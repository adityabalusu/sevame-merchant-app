package in.geekvalet.sevame.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import in.geekvalet.sevame.Application;

/**
 * Created by gautam on 25/5/14.
 */
public class Job implements Serializable {
    private String id;
    private String request;
    private String appointmentTime;
    private String address;
    private Double[] location;
    private User user;
    private String status;

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Location getLocation() {
        return new Location(location[0], location[1]);
    }

    public void setLocation(Location location) {
        this.location = new Double[]{location.getLatitude(), location.getLongitude()};
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isStarted() {
        return status != null && status.equals("started");
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
