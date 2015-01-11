package in.geekvalet.sevame.model;

import java.io.Serializable;

/**
 * Created by gautam on 28/5/14.
 */
public class ServiceProvider implements Serializable {
    private String id;
    private String phoneNumber;
    private String name;

    public ServiceProvider(String id, String phoneNumber, String name) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
