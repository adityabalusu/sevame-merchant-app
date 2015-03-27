package in.geekvalet.sevame.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by gautam on 16/6/14.
 */
public class Location {
    private Double latitude;
    private Double longitude;

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location() {
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

    public LatLng asLatLng() {
        return new LatLng(latitude, longitude);
    }
}
