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

    public static String BASE_URL = "https://sevame.in/api";

    public static class LoginRequest {
        public String accessToken;

        public LoginRequest(String accessToken) {
            this.accessToken = accessToken;
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
        public String phoneNumber;
        public Map<String, List<Skill>> skills;

        public UpdateServiceProviderRequest setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }
    }

    @POST("/serviceprovider/auth/google/")
    Response login(@Body LoginRequest loginRequest);

    @GET("/serviceprovider/{serviceProviderId}")
    Response authTest(@Path("serviceProviderId") String serviceProviderId);

    @PUT("/serviceprovider/{serviceProviderId}")
    ServiceProvider updateServiceProvider(@Path("serviceProviderId") String serviceProviderId,
                                          @Body UpdateServiceProviderRequest request);

    @GET("/service")
    ListResponse<Service> fetchServiceTypes();

    @PUT("/job/{jobId}/reject")
    Response rejectJob(@Path("jobId") String jobId);

    @PUT("/job/{jobId}/accept")
    Response acceptJob(@Path("jobId") String jobId);

    @POST("/job/{jobId}/start")
    Response startJob(@Path("jobId") String jobId);

    @POST("/job/{jobId}/end")
    Response stopJob(@Path("jobId") String jobId);

    @POST("/serviceprovider/{serviceProviderId}/verify/")
    Response requestOTP(@Path("serviceProviderId") String serviceProviderId, @Query("phone_number") String phoneNumber);

    @POST("/serviceprovider/{serviceProviderId}/verify/")
    Response verifyServiceProvider(@Path("serviceProviderId") String serviceProviderId, @Query("otp") String otp);

    @POST("/serviceprovider/{serviceProviderId}/gcm")
    Response updateGcmRegistrationId(@Path("serviceProviderId") String serviceProviderId, @Body UpdateGcmRegistrationIdRequest registrationId);

    @GET("/serviceprovider/{serviceProviderId}/job?status=accepted,started")
    ListResponse<Job> fetchAssignedJobs(@Path("serviceProviderId") String serviceProviderId);

    @GET("/serviceprovider/{serviceProviderId}/job?status=assigned")
    ListResponse<Job> fetchOpenJobs(@Path("serviceProviderId") String serviceProviderId);
}
