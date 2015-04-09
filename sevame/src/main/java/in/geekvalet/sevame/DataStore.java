package in.geekvalet.sevame;

import in.geekvalet.sevame.libs.KeyValueStore;
import in.geekvalet.sevame.model.ServiceProvider;

/**
 * Created by gautam on 22/12/14.
 */
public interface DataStore {
    void saveServiceProvider(ServiceProvider serviceProvider);

    KeyValueStore getKeyStore();

    ServiceProvider getServiceProvider();

    void saveAuthToken(String authToken);

    String getAuthToken();

    void saveGcmRegistrationId(String gcmRegistrationId);

    String getGcmRegistrationId();

    void saveAppVersion(Integer appVersion);

    Integer getAppVersion();

    void removeAuthToken();
}
