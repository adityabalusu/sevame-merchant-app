package in.geekvalet.sevame.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import in.geekvalet.sevame.Application;
import in.geekvalet.sevame.DataStore;
import in.geekvalet.sevame.R;
import in.geekvalet.sevame.model.Service;
import in.geekvalet.sevame.model.ServiceProvider;
import in.geekvalet.sevame.service.GcmRefreshService;
import in.geekvalet.sevame.service.SevaMeService;
import in.geekvalet.sevame.service.SevaMeService.ListResponse;
import in.geekvalet.sevame.service.Util;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SplashActivity extends FragmentActivity {

    private static final String LOG_TAG = SplashActivity.class.getName();
    private Button goButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        //selectActivity();
        doInBackground();
        addListenerOnButton();

        //new GcmRefreshService(this).invoke();
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
                } else if(serviceProvider.getSkills() == null || serviceProvider.getSkills().isEmpty()) {
                    Intent intent = new Intent(SplashActivity.this, SelectSkillSetActivity.class);
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

    //mamu's Code

    protected void doInBackground() {
        SevaMeService service =  Application.getSevaMeService();
        //ListResponse<Service> serviceRespList = service.fetchServiceTypes();
       //List<Service> serviceList = serviceRespList.objects;
        //List<Service> serviceList
        Spinner dropdown = (Spinner)findViewById(R.id.spinner1);
        List<String> sList = new ArrayList<String>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sList);
 /*       for (Service serv : serviceList) {
            Log.i("Test Mamu", serv.getName());
            //adapter.add(serv.getName());
            sList.add(serv.getName());
        }*/
        sList.add("Plumbing");
        dropdown.setAdapter(adapter);
    }

    public void addListenerOnButton(){
        goButton = (Button) findViewById(R.id.screen1_go_button);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openLogin = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(openLogin);
            }
        });
    }


}
