package in.geekvalet.sevame.service;

import in.geekvalet.sevame.Application;
import in.geekvalet.sevame.model.ServiceProvider;

/**
 * Created by gautam on 15/6/14.
 */
public class CreateServiceProvider {
    private static final String LOG_TAG = CreateServiceProvider.class.getName();

    private String name;
    private String phoneNumber;
    private SevaMeService sevaMeService;
    private Boolean initiateVerification = true;

    public CreateServiceProvider() {
        sevaMeService = Application.getSevaMeService();
    }
    public ServiceProvider invoke() {
        try {
            return tryInvoke();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private ServiceProvider tryInvoke() throws Exception {
        ServiceProvider serviceProvider = createServiceProvider();

        Application.getDataStore().saveServiceProvider(serviceProvider);

        if(serviceProvider != null) {
            initiateVerification(serviceProvider);
        }

        return serviceProvider;
    }

    private void initiateVerification(ServiceProvider serviceProvider) {
        sevaMeService.initiateServiceProviderVerification(serviceProvider.getId());
    }

    private ServiceProvider createServiceProvider() {

        SevaMeService.CreateServiceProviderRequest request = new SevaMeService.CreateServiceProviderRequest();

        request.initiateVerification = initiateVerification;
        request.name = name;
        request.phoneNumber = phoneNumber;
        request.gcmRegistrationId = Application.getDataStore().getGcmRegistrationId();

        return sevaMeService.createServiceProvider(request);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setInitiateVerification(boolean initiateVerification) {
        this.initiateVerification = initiateVerification;
    }
}
