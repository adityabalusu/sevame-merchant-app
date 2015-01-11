package in.geekvalet.sevame.model;

import java.util.List;

/**
 * Created by gautam on 13/12/14.
 */
public class Service {
    private String name;
    private List<String> skills;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }
}
