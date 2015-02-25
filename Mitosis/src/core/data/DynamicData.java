package core.data;

import model.Position;
import util.ServerConstants;

import java.util.HashMap;

/**
 * Created by Razi on 2/12/2015.
 */
public class DynamicData {
    protected String id;
    protected Position position;
    protected String type;
    protected HashMap<String,Object> other;

    public DynamicData(DynamicData dynamicData)
    {
        this(dynamicData.id, dynamicData.position, dynamicData.type, dynamicData.other);
    }

    public DynamicData (String id, Position position, String type)
    {
        this.id = id;
        this. position = position;
        switch (type)
        {
            case ServerConstants.GAME_OBJECT_TYPE_CELL:
                type = ServerConstants.GAME_OBJECT_TYPE_CELL_LONG;
                break;
            default:
                break;
        }
        this.type = type;
    }
    public DynamicData (String id, Position position, String type, HashMap<String,Object> other)
    {
        this(id, position, type);
        this.other = other;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setOther(HashMap<String, Object> other) {
        this.other = other;
    }

    public String getId() {
        return id;
    }

    public Position getPosition() {
        return position;
    }

    public String getType() {
        return type;
    }
}
