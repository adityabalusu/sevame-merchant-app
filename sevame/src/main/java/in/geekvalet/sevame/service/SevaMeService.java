package in.geekvalet.sevame.service;

import in.geekvalet.sevame.model.Job;
import in.geekvalet.sevame.model.Service;
import in.geekvalet.sevame.model.Location;
import in.geekvalet.sevame.model.ServiceProvider;
import in.geekvalet.sevame.model.Skill;
import retrofit.client.Response;
import retrofit.http.*;

import java.util.List;
import java.util.Map;

/**
 * Created by gautam on 16/6/14.
 */
public interface SevaMeService {

    public static String BASE_URL = "http://accurox.com/api";

    public static class LoginRequest {
        public String accessToken;

        public LoginRequest(String accessToken) {
            this.accessToken = accessToken;
        }
    }

    public static class CreateServiceProviderRequest {

        public String name;

        public String phoneNumber;
        public Boolean initiateVerification;
        public String gcmRegistrationId;
    }
    public static class VerifyServiceProviderRequest {
        public String otp;

        public String authToken;
        public VerifyServiceProviderRequest(String authToken, String otp) {
            this.authToken = authToken;
            this.otp = otp;
        }
        public VerifyServiceProviderRequest() {
        }

    }

    public static class VerifyServiceProviderResponse {
        public Boolean verified;

        public VerifyServiceProviderResponse(Boolean verified) {
            this.verified = verified;
        }
    }

    public static class UpdateGcmRegistrationIdRequest {
        public final String gcmRegId;

        public UpdateGcmRegistrationIdRequest(String gcmRegId) {
            this.gcmRegId = gcmRegId;
        }
    }

    public static class ListResponse<T> {
        public Map<String, Object> meta;

        public List<T> objects;
    }

    public  static class UpdateServiceProviderRequest {
        public Map<String, List<Skill>> skills;

    }

    @POST("/serviceprovider/auth/google/")
    Response login(@Body LoginRequest loginRequest);

    @GET("/serviceprovider/{serviceProviderId}")
    Response authTest(@Path("serviceProviderId") String serviceProviderId);

    @POST("/serviceprovider/")
    ServiceProvider createServiceProvider(@Body CreateServiceProviderRequest body);

    @POST("/serviceprovider/{serviceProviderId}")
    ServiceProvider updateServiceProvider(@Path("serviceProviderId") String serviceProviderId,
                                          @Body UpdateServiceProviderRequest request);

    @GET("/service")
    ListResponse<Service> fetchServiceTypes();

    @PUT("/serviceprovider/{serviceProviderId}/job/{jobId}/reject")
    Response rejectJob(@Path("serviceProviderId") String serviceProviderId,
                       @Path("jobId") String jobId);

    @PUT("/serviceprovider/{serviceProviderId}/job/{jobId}/accept")
    Response acceptJob(@Path("serviceProviderId") String serviceProviderId,
                       @Path("jobId") String jobId);

    @POST("/serviceprovider/{serviceProviderId}/start_verification/")
    Response initiateServiceProviderVerification(@Path("serviceProviderId") String serviceProviderId);

    @POST("/job/jobId/start")
    Response startJob(@Path("jobId") String jobId);

    @POST("/job/jobId/stop")
    Response stopJob(@Path("jobId") String jobId);

    @POST("/serviceprovider/{serviceProviderId}/verify/")
    VerifyServiceProviderResponse verifyServiceProvider(@Path("accountId") String accountId, @Body VerifyServiceProviderRequest request);

    @POST("/serviceprovider/{serviceProviderId}/gcm")
    Response updateGcmRegistrationId(@Path("serviceProviderId") String serviceProviderId, @Body UpdateGcmRegistrationIdRequest registrationId);

    @POST("/serviceprovider/{serviceProviderId}/job?status=pending,started")
    ListResponse<Job> fetchAssignedJobs(@Path("serviceProviderId") String serviceProviderId);

    @POST("/serviceprovider/{serviceProviderId}/job?status=open")
    ListResponse<Job> fetchOpenJobs(@Path("serviceProviderId") String serviceProviderId);
}
