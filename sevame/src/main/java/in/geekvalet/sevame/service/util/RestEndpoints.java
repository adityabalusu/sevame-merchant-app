package in.geekvalet.sevame.service.util;

import in.geekvalet.sevame.service.SevaMeService.LoginRequest;
import retrofit.http.POST;
import retrofit.client.Response;
import retrofit.http.*;

/**
 * Created by root on 3/22/15.
 */
public interface RestEndpoints {
   public static String BASE_URL= "http://localhost:8888/api";
   public  static String ROOT ="http://api.openweathermap.org/data/2.5";

    public static class WeatherResponse {
        private int cod;
        private String base;
        private Weather main;

        // default constructor, getters and setters
    }

    public static class Weather {
        private int id;
        private String main;
        private String description;

        // default constructor, getters and setters
    }


    @POST("/auth/google")
    Response login(@Body LoginRequest loginRequest);

    @GET("/test")
    Response test();

    @GET("/weather")
    void getWeather(@Query("q") String cityName);




}
