package in.geekvalet.sevame.service;

import retrofit.client.Response;

/**
 * Created by gautam on 21/12/14.
 */
public class Util {
    public static boolean isSuccessful(Response response) {
        return response != null && response.getStatus() >= 200 && response.getStatus() <= 300;
    }
}
