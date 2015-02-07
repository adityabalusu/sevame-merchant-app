package in.geekvalet.sevame.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import in.geekvalet.sevame.Application;
import in.geekvalet.sevame.DataStore;
import in.geekvalet.sevame.model.ServiceProvider;
import in.geekvalet.sevame.service.GcmRefreshService;
import in.geekvalet.sevame.service.Util;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SplashActivity extends FragmentActivity {

    private static final String LOG_TAG = SplashActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectActivity();

        new GcmRefreshService(this).invoke();
    }

    private void selectActivity() {
        DataStore dataStore = Application.getDataStore();

        if(dataStore.getAuthToken() != null) {
            validateAuthToken();
        } else {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void validateAuthToken() {
        new AsyncTask<Object, Object, Response>() {

            @Override
            protected Response doInBackground(Object[] objects) {
                ServiceProvider serviceProvider = Application.getDataStore().getServiceProvider();

                if(serviceProvider == null) {
                    return null;
                }

                try {
                    return Application.getSevaMeService().authTest(serviceProvider.getId());
                } catch (RetrofitError error) {
                    return error.getResponse();
                }
            }

            @Override
            protected void onPostExecute(Response response) {
                ServiceProvider serviceProvider = Application.getDataStore().getServiceProvider();

                if(response == null || Util.isRedirect(response) || response.getStatus() == 403 || serviceProvider == null) {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else if(!serviceProvider.isVerified()) {
                    Intent intent = new Intent(SplashActivity.this, VerifyMobileActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(SplashActivity.this, JobsActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }.execute();
    }
}
