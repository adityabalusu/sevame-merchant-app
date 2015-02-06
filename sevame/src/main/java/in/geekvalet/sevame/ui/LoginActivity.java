package in.geekvalet.sevame.ui;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.gson.Gson;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import in.geekvalet.sevame.Application;
import in.geekvalet.sevame.DataStore;
import in.geekvalet.sevame.R;
import in.geekvalet.sevame.model.ServiceProvider;
import in.geekvalet.sevame.service.SevaMeService;
import retrofit.client.Response;
import retrofit.client.Header;

public class LoginActivity extends FragmentActivity {

    private static final String LOG_TAG = LoginActivity.class.getName();
    static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 1001;
    static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1002;
    public static final String CLIENT_ID = "1003714066979-0jut8h3vr7kf3t44bia55k45nbpmv1pg.apps.googleusercontent.com";
    private static final String SCOPE = "audience:server:client_id:" + CLIENT_ID;

    private String mEmail;
    private Button loginButton;

    public class LoginTask extends AsyncTask<Void, Void, Void> {
        private static final String TAG = "TokenInfoTask";
        private static final String GIVEN_NAME_KEY = "given_name";
        private static final String FAMILY_NAME_KEY = "family_name";

        protected String mScope;
        protected String mEmail;

        LoginTask(String email, String scope) {
            this.mScope = scope;
            this.mEmail = email;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                login();
            } catch (IOException ex) {
                onError("Following Error occured, please try again. " + ex.getMessage(), ex);
            } catch (JSONException e) {
                onError("Bad response: " + e.getMessage(), e);
            }
            return null;
        }

        protected void onError(String msg, Exception e) {
            if (e != null) {
                Log.e(TAG, "Exception: ", e);
            }
            LoginActivity.this.show(msg);  // will be run in UI thread
        }

        /**
         * Get a authentication token if one is not available. If the error is not recoverable then
         * it displays the error message on parent activity.
         */
        protected String fetchToken() throws IOException {
            try {
                return GoogleAuthUtil.getToken(LoginActivity.this, mEmail, mScope);
            } catch (UserRecoverableAuthException userRecoverableException) {
                // GooglePlayServices.apk is either old, disabled, or not present, which is
                // recoverable, so we need to show the user some UI through the activity.
                LoginActivity.this.handleException(userRecoverableException);
            } catch (GoogleAuthException fatalException) {
                onError("Unrecoverable error " + fatalException.getMessage(), fatalException);
            }
            return null;
        }

        /**
         * Contacts the user info server to get the profile of the user and extracts the first name
         * of the user from the profile. In order to authenticate with the user info server the method
         * first fetches an access token from Google Play services.
         * @throws IOException if communication with user info server failed.
         * @throws JSONException if the response from the server could not be parsed.
         */
        private void login() throws IOException, JSONException {
            String token = fetchToken();

            removeSessionId();
            Response response = Application.getSevaMeService().login(new SevaMeService.LoginRequest(token));

            if(isSuccessful(response)) {
                saveSessionId(response);
                saveServiceProvider(response);
                onLoginSuccessful();
            } else {
                onLoginFail();
            }
        }

        private void removeSessionId() {
            Application.getDataStore().removeAuthToken();
        }

        private void saveServiceProvider(Response response) {
            String jsonString = null;
            try {
                jsonString = inputStreamToString(response.getBody().in());
                ServiceProvider serviceProvider  = new Gson().fromJson(jsonString, ServiceProvider.class);
                Application.getDataStore().saveServiceProvider(serviceProvider);
                Log.i(LOG_TAG, "Saved service provider with id " + serviceProvider.getId());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private String inputStreamToString(InputStream is) throws IOException {
            String line = "";
            StringBuilder total = new StringBuilder();

            // Wrap a BufferedReader around the InputStream
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            // Read response until the end
            while ((line = rd.readLine()) != null) {
                total.append(line);
            }

            // Return full string
            return total.toString();
        }


        private void saveSessionId(Response response) {
            if (response.getHeaders() != null) {
                for (Header header : response.getHeaders()) {
                    if (header.getName() != null && header.getName().equals("X-Session-Id")) {
                        saveAuthToken(header.getValue());
                    }
                }
            }
        }

        private boolean isSuccessful(Response response) {
            return response.getStatus() >= 200 && response.getStatus() < 300;
        }

        private void saveAuthToken(String token) {
            DataStore ds = Application.getDataStore();
            ds.saveAuthToken(token);
        }
    }

    private void onLoginFail() {
        show("Failed to login. Please try again later");
        loginButton.setEnabled(true);
    }

    private void onLoginSuccessful() {
        ServiceProvider serviceProvider = Application.getDataStore().getServiceProvider();

        if(!serviceProvider.isVerified()) {
            Intent intent = new Intent(this, VerifyMobileActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, JobsActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        //selectActivity();
        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickUserAccount();
            }
        });

        ServiceProvider serviceProvider = Application.getDataStore().getServiceProvider();
        if(serviceProvider != null) {
            loginButton.setEnabled(false);
            mEmail = serviceProvider.getEmail();
            show("Logging in as " + mEmail);
            login();
        }
    }

    private void pickUserAccount() {
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            // Receiving a result from the AccountPicker
            if (resultCode == RESULT_OK) {
                mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                // With the account name acquired, go get the auth token
                login();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "You must pick an account", Toast.LENGTH_LONG).show();
            }
        }else if ((requestCode == REQUEST_CODE_RECOVER_FROM_AUTH_ERROR ||
                requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR)
                && resultCode == RESULT_OK) {
            // Receiving a result that follows a GoogleAuthException, try auth again
            login();
        }

    }

    private void login() {
        if (mEmail == null) {
            pickUserAccount();
        } else {
            if (isDeviceOnline()) {
                new LoginTask(mEmail, SCOPE).execute();
            } else {
                Toast.makeText(this,"Device is not connected to internet", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public void show(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();            }
        });
    }

    public void handleException(final Exception e) {
        // Because this call comes from the AsyncTask, we must ensure that the following
        // code instead executes on the UI thread.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (e instanceof GooglePlayServicesAvailabilityException) {
                    // The Google Play services APK is old, disabled, or not present.
                    // Show a dialog created by Google Play services that allows
                    // the user to update the APK
                    int statusCode = ((GooglePlayServicesAvailabilityException)e)
                            .getConnectionStatusCode();
                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode,
                            LoginActivity.this,
                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                    dialog.show();
                } else if (e instanceof UserRecoverableAuthException) {
                    // Unable to authenticate, such as when the user has not yet granted
                    // the app access to the account, but the user can fix this.
                    // Forward the user to an activity in Google Play services.
                    Intent intent = ((UserRecoverableAuthException)e).getIntent();
                    startActivityForResult(intent,
                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                }
            }
        });
    }
}
