package in.geekvalet.sevame.service;

import org.apache.http.HttpResponse;

import retrofit.client.Response;

/**
 * Created by gautam on 21/12/14.
 */
public class Util {
    public static boolean isSuccessful(Response response) {
        return response != null && response.getStatus() >= 200 && response.getStatus() <= 300;
    }

    public static boolean isRedirect(Response response) {
        return response.getStatus() >= 300 && response.getStatus() < 400;
    }

    public static boolean isSuccessful(HttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        return response != null && statusCode >= 200 && statusCode <= 300;
    }
}
