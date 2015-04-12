package in.geekvalet.sevame.httpClient.Models;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by root on 4/11/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceProvider {

       @JsonIgnore
       private Object officeLocation;
       @JsonIgnore
       private  long created;
       private Object skills;

       @JsonIgnore
       private User user;
       private float experience;
    @JsonIgnore
       private boolean availability;

       @JsonIgnore
       private float  cost;
    @JsonIgnore
    private int dayEnd;
    @JsonIgnore
    private  int serviceRange;
    @JsonIgnore
       private float [] home_location;
    @JsonIgnore
       private int id;
    @JsonIgnore
       private int dayStart;
       @JsonIgnore
       private Object  details;

    public Object getSkills() {
        return skills;
    }

    public void setSkills(Object skills) {
        this.skills = skills;
    }

    public Object getDetails() {
        return details;
    }

    public void setDetails(Object details) {
        this.details = details;
    }

    public int getDayStart() {
        return dayStart;
    }

    public void setDayStart(int dayStart) {
        this.dayStart = dayStart;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float[] getHome_location() {
        return home_location;
    }

    public void setHome_location(float[] home_location) {
        this.home_location = home_location;
    }

    public int getServiceRange() {
        return serviceRange;
    }

    public void setServiceRange(int serviceRange) {
        this.serviceRange = serviceRange;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public int getDayEnd() {
        return dayEnd;
    }

    public void setDayEnd(int dayEnd) {
        this.dayEnd = dayEnd;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public float getExperience() {
        return experience;
    }

    public void setExperience(float experience) {
        this.experience = experience;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public Object getOfficeLocation() {
        return officeLocation;
    }

    public void setOfficeLocation(Object officeLocation) {
        this.officeLocation = officeLocation;
    }



}
