package in.geekvalet.sevame.service;
import android.app.Activity;
import android.content.*;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import in.geekvalet.sevame.Application;
import in.geekvalet.sevame.DataStore;
import in.geekvalet.sevame.TaskManager;
import in.geekvalet.sevame.model.ServiceProvider;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: gautam
 * Date: 27/2/14
 * Time: 12:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class GcmRefreshService {
    private static final String LOG_TAG = GcmRefreshService.class.getName();
    public static final String PROPERTY_REG_ID = "gcmRegistrationId";
    public static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final GcmReceiver gcmReceiver;
    private final DataStore dataStore;
    private final Activity initiatingActivity;
    private final Boolean forceRefresh;

    String SENDER_ID = "589106308769";

    private final Context context;
    private SevaMeService sevaMeService;

    public class GcmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(LOG_TAG, "Received gcm " + intent.getAction() + " with type " + intent.getStringExtra("type") + " and action " + intent.getAction());

            if(intent.getAction().equals("com.google.android.c2dm.intent.REGISTRATION")) {

                String registrationId = intent.getStringExtra("registration_id");
                if (intent.getStringExtra("error") != null) {
                    Log.e(LOG_TAG, "Failed to get gcm: " + intent.getStringExtra("error"));
                } else if (intent.getStringExtra("unregistered") != null) {
                    Log.e(LOG_TAG, "Why did I just get unregistered?");
                } else {
                    Log.i(LOG_TAG, "Received registration id " + registrationId);

                    unregisterSelf();
                    storeRegistrationIdInBackground(registrationId);
                }
            }
        }

        private void unregisterSelf() {
            try {
                context.unregisterReceiver(this);
            } catch (Exception e) {
                Log.w(LOG_TAG, "Failed to unregister gcm receiver");
            }
        }
    }

    public GcmRefreshService(Activity activity, Boolean forceRefresh) {
        this.initiatingActivity = activity;
        this.forceRefresh = forceRefresh;
        this.context = Application.getContext();
        this.dataStore = Application.getDataStore();
        this.gcmReceiver = new GcmReceiver();
        sevaMeService = Application.getSevaMeService();
    }

    public GcmRefreshService(Activity activity) {
        this(activity, false);
    }

    public GcmRefreshService(Boolean forceRefresh) {
        this(null, forceRefresh);
    }

    public void invoke() {
        if (checkPlayServices()) {
            new TaskManager.Task() {
                @Override
                public Object run() {
                    if (forceRefresh || shouldRefresh()) {
                        register();
                    }

                    return null;
                }

                @Override
                public void onSuccess(Object result) {

                }

                @Override
                public void onFail(Throwable e) {

                }
            }.execute(Application.getTaskManager());
        } else {
            throw new RuntimeException("Could not register for GCM");
        }

    }

    private void register() {
        String registrationId = null;

        Log.i(LOG_TAG, "Attempting to register for gcm");

        try {
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);

            registrationId = gcm.register(SENDER_ID);
            Log.i(LOG_TAG, "Device registered, registration ID=" + registrationId);
        } catch (IOException ex) {
            Log.e(LOG_TAG, "Error :" + ex.getMessage());

            GCMRegistrar.checkDevice(context);
            GCMRegistrar.checkManifest(context);
            registrationId = GCMRegistrar.getRegistrationId(context);

            if (registrationId.equals("")) {
                IntentFilter iff = new IntentFilter();
                iff.addAction("com.google.android.c2dm.intent.REGISTRATION");

                context.registerReceiver(gcmReceiver, iff);
                GCMRegistrar.register(context, SENDER_ID);
            }
        }

        storeRegistrationId(registrationId);
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if(initiatingActivity != null) {
                if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                    if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                        GooglePlayServicesUtil.getErrorDialog(resultCode, initiatingActivity,
                                PLAY_SERVICES_RESOLUTION_REQUEST).show();
                    }
                } else {
                    Log.i(LOG_TAG, "This device is not supported.");
                }
            }

            return false;
        }
        return true;
    }

    private Boolean shouldRefresh() {
        String registrationId = dataStore.getGcmRegistrationId();
        if (registrationId == null || registrationId.isEmpty()) {
            return true;
        }

        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = dataStore.getAppVersion();
        int currentVersion = getAppVersion();
        if (registeredVersion != currentVersion) {
            Log.i(LOG_TAG, "App version changed.");
            return true;
        }

        Log.i(LOG_TAG, "Registration id found " + registrationId);
        return false;
    }

    private int getAppVersion() {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void storeRegistrationId(String registrationId) {
        if(registrationId != null && !registrationId.isEmpty()) {
            int appVersion = getAppVersion();
            ServiceProvider serviceProvider = dataStore.getServiceProvider();
            String authToken = dataStore.getAuthToken();

            if(serviceProvider != null && authToken != null) {
                Log.i(LOG_TAG, "Updating gcm registration id on the server");
                sevaMeService.updateGcmRegistrationId(serviceProvider.getId(), new SevaMeService.UpdateGcmRegistrationIdRequest(registrationId));
            }

            dataStore.saveGcmRegistrationId(registrationId);
            dataStore.saveAppVersion(appVersion);
        } else {
            Log.w(LOG_TAG, "Received empty/null registration id (Ignoring)");
        }
    }

    private void storeRegistrationIdInBackground(final String registrationId) {
        new TaskManager.Task() {
            @Override
            public Object run() {
                storeRegistrationId(registrationId);
                return null;
            }

            @Override
            public void onSuccess(Object result) {
            }

            @Override
            public void onFail(Throwable e) {
            }
        }.execute(Application.getTaskManager());
    }
}
