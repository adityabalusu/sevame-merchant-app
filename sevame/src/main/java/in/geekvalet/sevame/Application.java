package in.geekvalet.sevame;

import android.content.Context;

import in.geekvalet.sevame.service.MockSevaMeService;
import in.geekvalet.sevame.service.SevaMeService;

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
        return new MockDataSource();
        //return new DataStoreImpl(instance.getApplicationContext());
    }

    public static TaskManager getTaskManager() {
        if(taskManager == null) {
            taskManager = new TaskManager();
        }

        return taskManager;
    }

    public static SevaMeService getSevaMeService() {
        return new MockSevaMeService();
//        RestAdapter restAdapter = new RestAdapter.Builder()
//                .setEndpoint(SevaMeService.BASE_URL)
//                .setLogLevel(RestAdapter.LogLevel.FULL)
//                .build();
//
//        return restAdapter.create(SevaMeService.class);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
