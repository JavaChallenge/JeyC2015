package data;

import model.Position;

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

    StaticData (String id, int turn, Position position, String type)
    {
        this.id = id;
        this.turn = turn;
        this. position = position;
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
