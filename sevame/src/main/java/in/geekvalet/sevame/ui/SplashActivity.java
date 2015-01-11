package in.geekvalet.sevame.ui;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import in.geekvalet.sevame.Application;
import in.geekvalet.sevame.DataStore;
import in.geekvalet.sevame.service.GcmRefreshService;

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
            Intent intent = new Intent(SplashActivity.this, JobsActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(SplashActivity.this, SignupActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
