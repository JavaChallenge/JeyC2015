package core.model;

import java.util.HashMap;

/**
 * Created by rajabzz on 2/3/15.
 */
public class GameObject {

    protected String id;
    protected Position position;
    protected String type;
    protected HashMap<String,Object> other;

    public GameObject() {

    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public HashMap<String,Object> getOther() {
        return other;
    }

    public String getType() {
        return type;
    }

    public Position getPos() {
        return position;
    }

    public void setPos(Position position) {
        this.position = position;
    }

    public void setOther(HashMap<String,Object> other) {
        this.other = other;
    }

    public void setType(String type) {
        this.type = type;
    }
}
