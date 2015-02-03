package in.geekvalet.sevame;

import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;

import java.util.Date;

import in.geekvalet.sevame.service.MockSevaMeService;
import in.geekvalet.sevame.service.SevaMeService;
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
                    request.addHeader("X-Session-ID", authToken);
                }
            }
        };

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setConverter(new GsonConverter(gson))
                .setEndpoint(SevaMeService.BASE_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(requestInterceptor)
                .build();

        return restAdapter.create(SevaMeService.class);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
