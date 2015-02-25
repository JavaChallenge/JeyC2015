package core.data;

import model.Position;
import util.ServerConstants;

import java.util.HashMap;

/**
 * Created by Razi on 2/12/2015.
 */
public class StaticData {
    protected String id;
    protected int turn;
    protected Position position;
    protected String type;
    protected HashMap<String,Object> other;

    public StaticData(StaticData staticData)
    {
        this(staticData.id, staticData.turn, staticData.position, staticData.type, staticData.other);
    }

    public StaticData(String id, int turn, Position position, String type)
    {
        this.id = id;
        this.turn = turn;
        this. position = position;
        switch (type)
        {
            case ServerConstants.BLOCK_TYPE_NONE:
                type = ServerConstants.BLOCK_TYPE_NONE_LONG;
                break;
            case ServerConstants.BLOCK_TYPE_IMPASSABLE:
                type =ServerConstants.BLOCK_TYPE_IMPASSABLE_LONG;
                break;
            case ServerConstants.BLOCK_TYPE_MITOSIS:
                type =ServerConstants.BLOCK_TYPE_MITOSIS_LONG;
                break;
            case ServerConstants.BLOCK_TYPE_NORMAL:
                type = ServerConstants.BLOCK_TYPE_NORMAL_LONG;
                break;
            case ServerConstants.BLOCK_TYPE_RESOURCE:
                type = ServerConstants.BLOCK_TYPE_RESOURCE_LONG;
                break;
            default:
                break;
        }
        this.type = type;
    }
    StaticData (String id, int turn, Position position, String type, HashMap<String,Object> other)
    {
        this(id, turn, position, type);
        this.other = other;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setOther(HashMap<String, Object> other) {
        this.other = other;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public int getTurn() {
        return turn;
    }

    public Position getPosition() {
        return position;
    }
}
