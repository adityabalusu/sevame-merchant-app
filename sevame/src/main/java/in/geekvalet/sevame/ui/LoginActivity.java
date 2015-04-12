package in.geekvalet.sevame.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceActivity;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.*;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import in.geekvalet.sevame.Application;
import in.geekvalet.sevame.R;
import in.geekvalet.sevame.libs.KeyValueStore;

public class LoginActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener,ServerAuthCodeCallbacks{

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;
    private String SERVER_CLIENT_ID = "388912374776-ag2qu3vvvumlim94tpkqs3e6jn4munmg.apps.googleusercontent.com";

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    private boolean mIntentInProgress;
    private ConnectionResult mConnectionResult;
    private boolean mSignInClicked;

 ;
    private SignInButton btnGplusLogin;
    private ProgressDialog pDialog;

    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        // Initializing google plus api client
        mGoogleApiClient  = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .requestServerAuthCode(SERVER_CLIENT_ID, this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();

        pDialog = new ProgressDialog(getApplicationContext());
        pDialog.setMessage("Please Wait");


        btnGplusLogin = (SignInButton) findViewById(R.id.sign_in_button);
        btnGplusLogin.setSize(SignInButton.SIZE_WIDE);
        btnGplusLogin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.sign_in_button && !mGoogleApiClient.isConnecting()) {
                    mSignInClicked = true;
                    mGoogleApiClient.connect();
                }
                //signInWithGplus();
            }
        });

    /*    btnSignIn = (Button) findViewById(R.id.login_button);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/


    }

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


    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;

        pDialog.dismiss();
        Toast.makeText(this, "Successfully logged in", Toast.LENGTH_LONG).show();
        Intent openMainActivity = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(openMainActivity);
        // Get user's information
        //getProfileInformation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {


        if (!mIntentInProgress) {
            if (mSignInClicked && result.hasResolution()) {
                // The user has already clicked 'sign-in' so we attempt to resolve all
                // errors until the user is signed in, or they cancel.
                try {
                    result.startResolutionForResult(this, RC_SIGN_IN);
                    //resolveSignInError();
                    mIntentInProgress = true;
                } catch (IntentSender.SendIntentException e) {
                    // The intent was canceled before it was sent.  Return to the default
                    // state and attempt to connect to get an updated ConnectionResult.
                    mIntentInProgress = false;
                    mGoogleApiClient.connect();
                }
            }
        }



    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {

    //    pDialog = new ProgressDialog(getApplicationContext());
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
  /*              if(pDialog != null){
                    pDialog.show();
                }*/
//                pDialog.setMessage("Please wait ...");
  //              pDialog.show();
            }
        }
    }


    /**
     * Sign-in into google
/*     * *//*
    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }*/

    /**
     * Method to resolve any signin errors
     * */
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

   /* @Override
    public CheckResult onCheckServerAuthorization(String s, Set<Scope> scopes) {
        return null;
    }

    @Override
    public boolean onUploadServerAuthCode(String s, String s2) {
        return false;
    }*/




    @Override
    public CheckResult onCheckServerAuthorization(String idToken, Set<Scope> scopeSet) {

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet;
        HttpResponse httpResponse;
        int responseCode;
        String responseBody;

    /*
     * Get a list of scopes required by the server and check whether the
     * server has a valid refresh token for the user.
     */
    /*    HashSet<Scope> serverScopeSet = new HashSet<Scope>();
        Boolean serverHasToken = false;
        try {
        String id = URLEncoder.encode(idToken, "utf-8");
        String url = "https://example.com/checkscopesandtoken";
        HttpPost httpPost = new HttpPost(url);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("idToken", idToken));
            nameValuePairs.add(new BasicNameValuePair("scopes", serverScopeSet.toString()));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            httpResponse = httpClient.execute(httpPost);
            responseCode = httpResponse.getStatusLine().getStatusCode();
            responseBody = EntityUtils.toString(httpResponse.getEntity());
            JSONObject json = new JSONObject(responseBody);
            if (responseCode == 200) {
                String[] scopeStrings = json.getString("scopes");
                for (String scope : scopeStrings) {
                    Log.i("Auth", "Server Scope: " + scope);
                    serverScopeSet.add(new Scope(scope));
                }
                serverHasToken = json.getBoolean("has_refresh_token");
            } else {
                Log.e("Auth", "Error in getting refresh token status: " + responseCode);
            }
        } catch (ClientProtocolException e) {
            Log.e("TAG", "Error in getting refresh token status.", e);
        } catch (Exception  e) {
            Log.e("TAG", "Error in getting refresh token status.", e);
        }catch (UnsupportedEncodingException  e) {
            Log.e("TAG", "Error in getting refresh token status.", e);
        }catch (Exception  e) {
            Log.e("TAG", "Error in getting refresh token status.", e);
        }*/

    /*
     * If the server doesn't have a valid refresh token, tell the
     * GoogleApiClient to retrieve a new authorization code for the scopes.
     */
        /*if (!serverHasToken) {*/

        HashSet<Scope> serverScopeSet = new HashSet<Scope>();
        serverScopeSet.add(new Scope("https:/" +
                "/www.googleapis.com/auth/userinfo.profile"));
            return CheckResult.newAuthRequiredResult(serverScopeSet);
    /*    } else {
            // The server has a valid refresh token, so a new authorization code
            // is not necessary.
            return CheckResult.newAuthNotRequiredResult();
        }*/
    }


    @Override
    public boolean onUploadServerAuthCode(String idToken, String serverAuthCode) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("https://sevame.in/api/auth/google?user_type=service_provider");

        try {
           /* List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            //nameValuePairs.add(new BasicNameValuePair("idToken", idToken));
            nameValuePairs.add(new BasicNameValuePair("access_token", serverAuthCode));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs))*/;
            //
            String json = "{\"access_token\":\""+idToken+"\"}";
            StringEntity params =new StringEntity(json);
            httpPost.setEntity(params);
            HttpResponse response = httpClient.execute(httpPost);
            Header [] header = response.getHeaders("X-Session-Id");
            String sessionId = header[0].getValue();
            int statusCode = response.getStatusLine().getStatusCode();
            final String responseBody = EntityUtils.toString(response.getEntity());
            KeyValueStore keyValueStore = Application.getDataStore().getKeyStore();
            keyValueStore.write("service_provider", responseBody);
            Application.getDataStore().saveAuthToken(sessionId);
            Log.i("TAG", "Code: " + statusCode);
            Log.i("TAG", "Resp: " + responseBody);

            return (statusCode == 200);
        } catch (ClientProtocolException e) {
            Log.e("TAG", "Error in auth code exchange.", e);
            return false;
        } catch (IOException e) {
            Log.e("TAG", "Error in auth code exchange.", e);
            return false;
        }
    }
}
