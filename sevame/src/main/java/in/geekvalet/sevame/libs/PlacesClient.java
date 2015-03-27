package in.geekvalet.sevame.libs;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by gautam on 2/6/14.
 */
public class PlacesClient {
    private static final String LOG_TAG = PlacesClient.class.getName();
    public static final String PLACES_API = "https://maps.googleapis.com/maps/api/place/search/json";

    public static class Place {
        private String id;
        private String icon;
        private String name;
        private String vicinity;
        private Double latitude;
        private Double longitude;

        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getIcon() {
            return icon;
        }
        public void setIcon(String icon) {
            this.icon = icon;
        }
        public Double getLatitude() {
            return latitude;
        }
        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }
        public Double getLongitude() {
            return longitude;
        }
        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getVicinity() {
            return vicinity;
        }
        public void setVicinity(String vicinity) {
            this.vicinity = vicinity;
        }

        static Place fromJson(JSONObject json) {
            try {
                Place result = new Place();
                JSONObject geometry = (JSONObject) json.get("geometry");
                JSONObject location = (JSONObject) geometry.get("location");
                result.setLatitude((Double) location.get("lat"));
                result.setLongitude((Double) location.get("lng"));
                result.setIcon(json.getString("icon"));
                result.setName(json.getString("name"));
                result.setVicinity(json.getString("vicinity"));
                result.setId(json.getString("id"));
                return result;
            } catch (JSONException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public String toString() {
            return "Place{" + "id=" + id + ", icon=" + icon + ", name=" + name + ", latitude=" + latitude + ", longitude=" + longitude + '}';
        }
    }


    private final String apiKey;

    public PlacesClient(String apikey) {
        this.apiKey = apikey;
    }

    public ArrayList<Place> findPlacesNear(double latitude, double longitude) {
        ArrayList<Place> places = new ArrayList<Place>();

        RestClient.QueryParameters queryParameters = new RestClient.QueryParameters().
                put("location", Double.toString(latitude) + "," + Double.toString(longitude)).
                put("radius", 10).
                put("sensor", true).
                put("key", apiKey);

        String jsonString = RestClient.get(PLACES_API, queryParameters);

        Log.i(LOG_TAG, "Results of places call" + jsonString);

        List<Json> results = new Json(jsonString).asMap().get("results").asList();

        for(Json result: results) {
            Map<String, Json> placeMap = result.asMap();
            String name = placeMap.get("name").asString();
            Map<String, Json> location = placeMap.get("geometry").asMap().get("location").asMap();

            Place place = new Place();
            place.setName(name);
            place.setLatitude(location.get("lat").asDouble());
            place.setLongitude(location.get("lng").asDouble());
            place.setId(placeMap.get("id").asString());
            place.setVicinity(placeMap.get("vicinity").asString());
            place.setIcon(placeMap.get("icon").asString());

            places.add(place);
        }

        return places;
    }
}
