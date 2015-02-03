package in.geekvalet.sevame.service;

import java.util.ArrayList;
import java.util.List;

import in.geekvalet.sevame.Fixtures;
import in.geekvalet.sevame.model.Job;
import in.geekvalet.sevame.model.ServiceProvider;
import in.geekvalet.sevame.model.Service;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Path;

/**
 * Created by gautam on 13/12/14.
 */
public class MockSevaMeService implements SevaMeService {

    @Override
    public Response login(@Body LoginRequest loginRequest) {
        return null;
    }

    @Override
    public Response authTest(@Path("serviceProviderId") String serviceProviderId) {
        return null;
    }

    @Override
    public ServiceProvider createServiceProvider(@Body CreateServiceProviderRequest body) {
        return null;
    }

    @Override
    public ServiceProvider updateServiceProvider(@Path("serviceProviderId") String serviceProviderId, @Body UpdateServiceProviderRequest request) {
        return null;
    }

    @Override
    public ListResponse<Service> fetchServiceTypes() {
        List<Service> services = new ArrayList<Service>();

        Service service = new Service();
        List<String> skills = new ArrayList<String>();

        skills.add("Tap Repair");
        skills.add("Piping");
        skills.add("Sink Repair");
        skills.add("Flush Repair");
        skills.add("Commode Repair");

        skills.add("Tap Installation");
        skills.add("Piping");
        skills.add("Sink Installation");
        skills.add("Flush Installation");
        skills.add("Commode Installation");

        service.setName("Plumbing");
        service.setSkills(skills);
        services.add(service);

        service = new Service();
        skills = new ArrayList<String>();

        skills.add("Bulb fitting");
        skills.add("Fan fitting");
        skills.add("Geyser repair");
        skills.add("Geyser fitting");
        skills.add("Switch fitting");

        service.setName("Electrical work");
        service.setSkills(skills);
        services.add(service);

        ListResponse<Service> response = new ListResponse<Service>();
        response.objects = services;
        return response;
    }

    @Override
    public Response rejectJob(@Path("serviceProviderId") String serviceProviderId, @Path("jobId") String jobId) {
        return null;
    }

    @Override
    public Response acceptJob(@Path("serviceProviderId") String serviceProviderId, @Path("jobId") String jobId) {
        return null;
    }

    @Override
    public Response initiateServiceProviderVerification(@Path("serviceProviderId") String serviceProviderId) {
        return null;
    }

    @Override
    public Response startJob(@Path("jobId") String jobId) {
        return null;
    }

    @Override
    public Response stopJob(@Path("jobId") String jobId) {
        return null;
    }

    @Override
    public VerifyServiceProviderResponse verifyServiceProvider(@Path("accountId") String accountId, @Body VerifyServiceProviderRequest request) {
        return null;
    }

    @Override
    public Response updateGcmRegistrationId(@Path("serviceProviderId") String serviceProviderId, @Body UpdateGcmRegistrationIdRequest registrationId) {
        return null;
    }

    @Override
    public ListResponse<Job> fetchAssignedJobs(@Path("serviceProviderId") String serviceProviderId) {
        Fixtures fixtures = new Fixtures();
        List<Job> jobs = new ArrayList<Job>();

        jobs.add(fixtures.getJob1());
        jobs.add(fixtures.getJob2());
        jobs.add(fixtures.getJob3());
        jobs.add(fixtures.getJob4());
        jobs.add(fixtures.getJob5());

        ListResponse<Job> response = new ListResponse<Job>();
        response.objects = jobs;

        return response;
    }

    @Override
    public ListResponse<Job> fetchOpenJobs(@Path("serviceProviderId") String serviceProviderId) {
        Fixtures fixtures = new Fixtures();
        List<Job> jobs = new ArrayList<Job>();

        jobs.add(fixtures.getJob1());
        jobs.add(fixtures.getJob2());
        jobs.add(fixtures.getJob3());
        jobs.add(fixtures.getJob4());
        jobs.add(fixtures.getJob5());

        ListResponse<Job> response = new ListResponse<Job>();
        response.objects = jobs;

        return response;
    }

}
