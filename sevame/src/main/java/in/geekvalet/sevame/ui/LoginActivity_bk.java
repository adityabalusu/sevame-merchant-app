/*
package in.geekvalet.sevame.ui;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.*;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.common.api.GoogleApiClient.ServerAuthCodeCallbacks;
import com.google.android.gms.plus.model.people.Person;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import in.geekvalet.sevame.Application;
import in.geekvalet.sevame.DataStore;
import in.geekvalet.sevame.R;
import in.geekvalet.sevame.model.ServiceProvider;
import in.geekvalet.sevame.service.SevaMeService;
import in.geekvalet.sevame.service.Util;
import in.geekvalet.sevame.service.util.RestEndpoints;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.client.Header;

public class LoginActivity extends FragmentActivity implements ConnectionCallbacks, OnConnectionFailedListener
        //GoogleApiClient.ServerAuthCodeCallbacks {
{
    private static final String LOG_TAG = LoginActivity.class.getName();
    static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 1001;
    static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1002;
    public static final String CLIENT_ID = "388912374776-4shaubvgqe5om4c7r4c9ubi7v1cm1m4m.apps.googleusercontent.com";

   // private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";

    private static final String SCOPE = "oauth2:server:client_id:388912374776-4shaubvgqe5om4c7r4c9ubi7v1cm1m4m.apps.googleusercontent.com:api_scope:https://www.googleapis.com/auth/userinfo.profile";


    private static final String WEB_CLIENT_ID = "WEB_CLIENT_ID";

    // Base URL for your token exchange server, no trailing slash.
    private static final String SERVER_BASE_URL = "https://sevame.in/api";

    // URL where the client should GET the scopes that the server would like granted
    // before asking for a serverAuthCode
    private static final String EXCHANGE_TOKEN_URL = SERVER_BASE_URL + "/exchangetoken";

    // URL where the client should POST the serverAuthCode so that the server can exchange
    // it for a refresh token,
    private static final String SELECT_SCOPES_URL = SERVER_BASE_URL + "/selectscopes";

    //private static final String SCOPE ="oauth2:server:client_id:+CLIENT_ID+:https://www.googleapis.com/auth/plus.login";

    private String mEmail;
    //Google Signin
    private Button signInButton;
    //Normal login
    private Button loginButton;



    //Mamu
    private static final int RC_SIGN_IN = 0;
   // Google client to communicate with Google
    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private boolean signedInUser;
    private ConnectionResult mConnectionResult;
    private boolean mSignInClicked;

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
    */
/*        try {
                login();
            } catch (IOException ex) {
                onError("Following Error occured, please try again. " + ex.getMessage(), ex);
            } catch (JSONException e) {
                onError("Bad response: " + e.getMessage(), e);
            }*//*

            return null;
        }

        protected void onError(String msg, Exception e) {
            if (e != null) {
                Log.e(TAG, "Exception: ", e);
            }
            LoginActivity.this.show(msg);  // will be run in UI thread
        }

        */
/**
         * Get a authentication token if one is not available. If the error is not recoverable then
         * it displays the error message on parent activity.
         *//*

        protected String fetchToken() throws IOException {
            try {

                String token = GoogleAuthUtil.getToken(LoginActivity.this, mEmail, mScope);
                return token;
            } catch (UserRecoverableAuthException userRecoverableException) {
                // GooglePlayServices.apk is either old, disabled, or not present, which is
                // recoverable, so we need to show the user some UI through the activity.
                LoginActivity.this.handleException(userRecoverableException);
            } catch (GoogleAuthException fatalException) {
                onError("Unrecoverable error " + fatalException.getMessage(), fatalException);
            }
            return null;
        }

        */
/**
         * Contacts the user info server to get the profile of the user and extracts the first name
         * of the user from the profile. In order to authenticate with the user info server the method
         * first fetches an access token from Google Play services.
         *
         * @throws IOException   if communication with user info server failed.
         * @throws JSONException if the response from the server could not be parsed.
         *//*

        private void login() throws IOException, JSONException {
            String token = fetchToken();

            removeSessionId();

            Response response;
            try {
                if (token != null) {

                  */
/* *//*
*/
/* RestAdapter restAdapter = new RestAdapter.Builder().
                            setEndpoint("http://localhost/").build();*//*

                    //RestEndpoints service = restAdapter.create(RestEndpoints.class);
                    //response = service.login(new SevaMeService.LoginRequest(token));
                    //response = service.test();
                    response = Application.getSevaMeService().login(new SevaMeService.LoginRequest(token));
                    //saveSessionId(response);
                    //saveServiceProvider(response);
                    onLoginSuccessful();
                }
            } catch (Exception error) {
                //String errorRep = error.getResponse();
                //Log.e("Error", error.getLocalizedMessage());
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
                ServiceProvider serviceProvider = new Gson().fromJson(jsonString, ServiceProvider.class);
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

        private void saveAuthToken(String token) {
            DataStore ds = Application.getDataStore();
            ds.saveAuthToken(token);
        }
    }

    private void onLoginFail() {
        show("Failed to login. Please try again later");
        signInButton.setEnabled(true);
    }

    private void onLoginSuccessful() {
        ServiceProvider serviceProvider = Application.getDataStore().getServiceProvider();

        if (!serviceProvider.isVerified()) {
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
        signInButton = (Button) findViewById(R.id.signin_button);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pickUserAccount();
                signInWithGplus();
             }
        });

        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openMainActivity = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(openMainActivity);
            }
        });

        ServiceProvider serviceProvider = Application.getDataStore().getServiceProvider();
        if (serviceProvider != null) {
            loginButton.setEnabled(false);
            mEmail = serviceProvider.getEmail();
            show("Logging in as " + mEmail);
            login();
        }

        //Login button
        mGoogleApiClient = buildGoogleApiClient();
    }

    //mamus Final Code
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
     }
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }





    */
/*private void pickUserAccount() {
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }*//*


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

        } else if ((requestCode == REQUEST_CODE_RECOVER_FROM_AUTH_ERROR ||
                requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR)
                && resultCode == RESULT_OK) {
            // Receiving a result that follows a GoogleAuthException, try auth again
            login();
        }
        else if (requestCode == RC_SIGN_IN) {
            if (resultCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }

    }

    private void login() {
        if (mEmail == null || mEmail.isEmpty()) {
            //pickUserAccount();
        } else {
            if (isDeviceOnline()) {
                new LoginTask(mEmail, SCOPE).execute();
            } else {
                Toast.makeText(this, "Device is not connected to internet", Toast.LENGTH_LONG).show();
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
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
            }
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
                    int statusCode = ((GooglePlayServicesAvailabilityException) e)
                            .getConnectionStatusCode();
                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode,
                            LoginActivity.this,
                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                    dialog.show();
                } else if (e instanceof UserRecoverableAuthException) {
                    // Unable to authenticate, such as when the user has not yet granted
                    // the app access to the account, but the user can fix this.
                    // Forward the user to an activity in Google Play services.
                    Intent intent = ((UserRecoverableAuthException) e).getIntent();
                    startActivityForResult(intent,
                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                }
            }
        });
    }

    //Google + APK client
    private GoogleApiClient buildGoogleApiClient() {
        // When we build the GoogleApiClient we specify where connected and
        // connection failed callbacks should be returned, which Google APIs our
        // app uses and which OAuth 2.0 scopes our app requests.
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN);


        return builder.build();

    }

    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    */
/*@Override
    public CheckResult onCheckServerAuthorization(String s, Set<Scope> scopes) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(SELECT_SCOPES_URL);
        HashSet<Scope> serverScopeSet = new HashSet<Scope>();

        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            int responseCode = httpResponse.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(httpResponse.getEntity());

            if (responseCode == 200) {
                String[] scopeStrings = responseBody.split(" ");
                for (String scope : scopeStrings) {
                    Log.i("TAG", "Server Scope: " + scope);
                    serverScopeSet.add(new Scope(scope));
                }
            } else {
                Log.e("TAG", "Error in getting server scopes: " + responseCode);
            }

        } catch (ClientProtocolException e) {
            Log.e("TAG", "Error in getting server scopes.", e);
        } catch (IOException e) {
            Log.e("TAG", "Error in getting server scopes.", e);
        }

        // This tells GoogleApiClient that the server needs a new serverAuthCode with
        // access to the scopes in serverScopeSet.  Note that we are not asking the server
        // if it already has such a token because this is a sample application.  In reality,
        // you should only do this on the first user sign-in or if the server loses or deletes
        // the refresh token.
        return CheckResult.newAuthRequiredResult(serverScopeSet);

    }

    @Override
    public boolean onUploadServerAuthCode(String s, String s2) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(EXCHANGE_TOKEN_URL);

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("serverAuthCode", s));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            final String responseBody = EntityUtils.toString(response.getEntity());
            Log.i("TAG", "Code: " + statusCode);
            Log.i("TAG", "Resp: " + responseBody);

        *//*
*/
/*    // Show Toast on UI Thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, responseBody, Toast.LENGTH_LONG).show();
                }
            });*//*
*/
/*
            return (statusCode == 200);
        } catch (ClientProtocolException e) {
            Log.e("TAG", "Error in auth code exchange.", e);
            return false;
        } catch (IOException e) {
            Log.e("TAG", "Error in auth code exchange.", e);
            return false;
        }
    }*//*


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            return;
        }
        if (!mIntentInProgress) {
            // store mConnectionResult
            mConnectionResult = result;
            if (signedInUser) {
               resolveSignInError();
            }
        }

    }


    */
/**
     * Sign-in into google
     * *//*

    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }



}
*/
