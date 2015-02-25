package client.model;

import client.Model;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import data.ReceivedObjectDiff;
import model.Direction;
import model.GameEvent;
import model.Position;
import util.ServerConstants;

/**
 * Created by Razi on 2/12/2015.
 */
public class Cell {

    private Model mModel;
    final private String mId;
    private Position mPos;
    final private int mTeamId;
    private int mEnergy;

    private int depthOfField;
    private int jump;
    private int gainRate;
    private int attack;

    public Cell (Model model, ReceivedObjectDiff d) {
        mModel = model;
        JsonElement jId = d.get(ServerConstants.GAME_OBJECT_KEY_ID);
        mId = Model.getGson().fromJson(jId, String.class);
        JsonElement jTeamId = d.get(ServerConstants.GAME_OBJECT_KEY_TEAM_ID);
        mTeamId = Model.getGson().fromJson(jTeamId, Integer.class);
        this.setChange(d);
    }

    public void setChange(ReceivedObjectDiff d)
    {
        for(java.util.Map.Entry<String, JsonElement> entry : d.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            switch (key)
            {
                case ServerConstants.GAME_OBJECT_KEY_ID:
                    break;
                case ServerConstants.GAME_OBJECT_KEY_TYPE:
                    break;
                case ServerConstants.GAME_OBJECT_KEY_TEAM_ID:
                    break;
                case ServerConstants.GAME_OBJECT_KEY_POSITION:
                    this.mPos = Model.getGson().fromJson(value, Position.class);
                    break;
                case ServerConstants.CELL_KEY_ENERGY:
                    this.mEnergy = Model.getGson().fromJson(value, Integer.class);
                    break;
                case ServerConstants.CELL_KEY_DEPTH_OF_FIELD:
                    this.depthOfField = Model.getGson().fromJson(value, Integer.class);
                    break;
                case ServerConstants.CELL_KEY_JUMP:
                    this.jump= Model.getGson().fromJson(value, Integer.class);
                    break;
                case ServerConstants.CELL_KEY_GAIN_RATE:
                    this.gainRate = Model.getGson().fromJson(value, Integer.class);
                    break;
                case ServerConstants.CELL_KEY_ATTACK:
                    this.attack = Model.getGson().fromJson(value, Integer.class);
                    break;
                default:
                    break;
            }

        }
    }

    public void move(Direction direction)
    {
        GameEvent event = new GameEvent(GameEvent.TYPE_MOVE);
        event.setObjectId(mId);
        event.setArg(direction.toString(), GameEvent.ARG_INDEX_MOVE_DIRECTION);

        mModel.addEvent(event);
    }

    public void gainResource()
    {
        GameEvent event = new GameEvent(GameEvent.TYPE_GAIN_RESOURCE);
        event.setObjectId(mId);

        mModel.addEvent(event);
    }

    public void mitosis()
    {
        GameEvent event = new GameEvent(GameEvent.TYPE_MITOSIS);
        event.setObjectId(mId);

        mModel.addEvent(event);
    }

    public void attack(Direction direction)
    {
        GameEvent event = new GameEvent(GameEvent.TYPE_ATTACK);
        event.setObjectId(mId);
        event.setArg(direction.toString(), GameEvent.ARG_INDEX_ATTACK_DIRECTION);

        mModel.addEvent(event);
    }

    public String getId() {
        return mId;
    }

    public Position getPos() {
        return mPos;
    }

    public int getTeamId() {
        return mTeamId;
    }

    public int getEnergy() {
        return mEnergy;
    }

    public int getAttack() {
        return attack;
    }

    public int getGainRate() {
        return gainRate;
    }

    public int getJump() {
        return jump;
    }

    public int getDepthOfField() {
        return depthOfField;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Cell) {
            Cell c = (Cell) o;
            return mId.equals(c.mId);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return mId.hashCode();
    }

}
