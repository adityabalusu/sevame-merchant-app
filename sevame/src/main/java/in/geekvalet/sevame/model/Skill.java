package in.geekvalet.sevame.model;

/**
 * Created by gautam on 21/12/14.
 */
public class Skill {
    private String name;
    private Boolean inspection;

    public Skill(String name, Boolean inspection) {
        this.name = name;
        this.inspection = inspection;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getInspection() {
        return inspection;
    }

    public void setInspection(Boolean inspection) {
        this.inspection = inspection;
    }
}
