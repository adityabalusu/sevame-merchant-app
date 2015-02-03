package in.geekvalet.sevame.model;

import java.io.Serializable;

/**
 * Created by gautam on 28/5/14.
 */
public class ServiceProvider implements Serializable {
    private String id;
    private String phoneNumber;
    private String name;
    private String email;
    private Boolean verified;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isVerified() {
        if(verified == null) {
            return false;
        }

        return verified;
    }
}
