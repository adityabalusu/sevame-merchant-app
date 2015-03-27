package in.geekvalet.sevame;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;

import java.util.Date;

import in.geekvalet.sevame.service.MockSevaMeService;
import in.geekvalet.sevame.service.SevaMeService;
import in.geekvalet.sevame.service.util.RestEndpoints;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by gautam on 23/6/14.
 */
public class Application extends android.app.Application {
    private static final String LOG_TAG = Application.class.getName();
    private static Application instance;
    private static TaskManager taskManager;
    private static String SESSION_CONTEXT = "seva_me";
    public static Context getContext() {
        if(instance != null) {
            return instance.getApplicationContext();
        }

        return null;
    }

    public static DataStore getDataStore() {
        return new DataStoreImpl(instance.getApplicationContext());
    }

    public static TaskManager getTaskManager() {
        if(taskManager == null) {
            taskManager = new TaskManager();
        }

        return taskManager;
    }

    public static SevaMeService getSevaMeService() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .create();

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestInterceptor.RequestFacade request) {
                String authToken = Application.getDataStore().getAuthToken();
                if(authToken != null) {
                    request.addHeader("X-Session-Id", authToken);
                }
            }
        };

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setConverter(new GsonConverter(gson))
                .setEndpoint("https://sevame.in/api")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(requestInterceptor)
                .build();

        return restAdapter.create(SevaMeService.class);
        //return restAdapter.create(MockSevaMeService.class);

        //return new MockSevaMeService();
    }


    public static RestEndpoints getEndPointService(){
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .create();

        /* RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestInterceptor.RequestFacade request) {
                String authToken = Application.getDataStore().getAuthToken();
                if(authToken != null) {
                    request.addHeader("X-Session-Id", authToken);
                }
            }
        };*/

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setConverter(new GsonConverter(gson))
                .setEndpoint("http://localhost:8888/api")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                        //.setRequestInterceptor(requestInterceptor)
                .build();

        return restAdapter.create(RestEndpoints.class);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public SharedPreferences.Editor  getEditor() {

        SharedPreferences.Editor editor = getSharedPreferences(SESSION_CONTEXT, MODE_PRIVATE).edit();
        return  editor;
    }
}
