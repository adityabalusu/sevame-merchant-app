package in.geekvalet.sevame;

import android.content.Context;
import in.geekvalet.sevame.libs.KeyValueStore;
import in.geekvalet.sevame.model.ServiceProvider;

/**
 * Created by gautam on 23/6/14.
 */
public class DataStoreImpl implements DataStore {
    private final KeyValueStore keyValueStore;

    public DataStoreImpl(Context context) {
        this.keyValueStore = new KeyValueStore(context, "GlobalData");
    }

    @Override
    public void saveServiceProvider(ServiceProvider serviceProvider) {
        keyValueStore.write("serviceProviderId", serviceProvider.getId());
        keyValueStore.write("phoneNumber", serviceProvider.getPhoneNumber());
        keyValueStore.write("name", serviceProvider.getName());
    }

    @Override
    public ServiceProvider getServiceProvider() {
        String serviceProviderId = keyValueStore.getString("serviceProviderId", null);
        String phoneNumber = keyValueStore.getString("phoneNumber", null);
        String name = keyValueStore.getString("name", null);

        if(serviceProviderId == null || phoneNumber == null || name == null) {
            return null;
        }

        return new ServiceProvider(serviceProviderId, phoneNumber, name);
    }

    @Override
    public void saveAuthToken(String authToken) {
        keyValueStore.write("authToken", authToken);
    }

    @Override
    public String getAuthToken() {
        return keyValueStore.getString("authToken", null);
    }

    @Override
    public void saveGcmRegistrationId(String gcmRegistrationId) {
        keyValueStore.write("gcmRegistrationId", gcmRegistrationId);
    }

    @Override
    public String getGcmRegistrationId() {
        return keyValueStore.getString("gcmRegistrationId", null);
    }

    @Override
    public void saveAppVersion(Integer appVersion) {
        keyValueStore.write("appVersion", appVersion);
    }

    @Override
    public Integer getAppVersion() {
        return keyValueStore.getInt("appVersion", Integer.MIN_VALUE);
    }


}
