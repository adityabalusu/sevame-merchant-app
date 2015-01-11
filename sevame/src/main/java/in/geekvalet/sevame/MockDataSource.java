package in.geekvalet.sevame;

import in.geekvalet.sevame.model.ServiceProvider;

/**
 * Created by gautam on 22/12/14.
 */
public class MockDataSource implements DataStore{
    @Override
    public void saveServiceProvider(ServiceProvider serviceProvider) {

    }

    @Override
    public ServiceProvider getServiceProvider() {
        return new Fixtures().getServiceProvider();
    }

    @Override
    public void saveAuthToken(String authToken) {

    }

    @Override
    public String getAuthToken() {
        return null;
    }

    @Override
    public void saveGcmRegistrationId(String gcmRegistrationId) {

    }

    @Override
    public String getGcmRegistrationId() {
        return null;
    }

    @Override
    public void saveAppVersion(Integer appVersion) {

    }

    @Override
    public Integer getAppVersion() {
        return null;
    }
}
