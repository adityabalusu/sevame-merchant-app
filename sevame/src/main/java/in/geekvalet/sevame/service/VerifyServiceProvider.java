package in.geekvalet.sevame.service;

import in.geekvalet.sevame.Application;
import in.geekvalet.sevame.model.ServiceProvider;
import retrofit.RestAdapter;

import java.util.UUID;

/**
 * Created by gautam on 20/6/14.
 */
public class VerifyServiceProvider {
    private final ServiceProvider serviceProvider;
    private final String otp;
    private final SevaMeService sevaMeService;

    public VerifyServiceProvider(ServiceProvider serviceProvider, String otp) {
        this.serviceProvider = serviceProvider;
        this.otp = otp;
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(SevaMeService.BASE_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        sevaMeService = restAdapter.create(SevaMeService.class);
    }

    public Boolean invoke() {
        SevaMeService.VerifyServiceProviderRequest request = new SevaMeService.VerifyServiceProviderRequest(UUID.randomUUID().toString(), otp);

        SevaMeService.VerifyServiceProviderResponse response = sevaMeService.verifyServiceProvider(serviceProvider.getId(), request);

        if(response.verified) {
            Application.getDataStore().saveAuthToken(request.authToken);
        }

        return response.verified;
    }
}
