package client.model;

import client.Model;
import com.google.gson.JsonElement;
import data.ReceivedObjectDiff;
import model.Position;
import util.ServerConstants;

/**
 * Created by Razi on 2/13/2015.
 */
public class Block {

    private Position pos;
    private int height;
    private int minHeight;
    private int resource;
    private String type;
    final private String id;
    private int turn;

    private int gainImprovementAmount;
    private int depthOfFieldImprovementAmount;
    private int jumpImprovementAmount;
    private int attackImprovementAmount;

    Block(ReceivedObjectDiff d) {
        JsonElement jId = d.get(ServerConstants.GAME_OBJECT_KEY_ID);
        id = Model.getGson().fromJson(jId, String.class);
        setChange(d);
    }

    public void setChange(ReceivedObjectDiff d)
    {
        JsonElement jType = d.get(ServerConstants.GAME_OBJECT_KEY_TYPE);
        if(jType != null) {
            type = Model.getGson().fromJson(jType, String.class);
        }
        for(java.util.Map.Entry<String, JsonElement> entry : d.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            switch (key)
            {
                case ServerConstants.GAME_OBJECT_KEY_ID:
                    //this.id = Model.getGson().fromJson(value, String.class);
                    break;
                case ServerConstants.GAME_OBJECT_KEY_POSITION:
                    this.pos = Model.getGson().fromJson(value, Position.class);
                    break;
                case ServerConstants.BLOCK_KEY_HEIGHT:
                    break;
                case ServerConstants.BLOCK_KEY_MIN_HEIGHT:
                    this.minHeight = Model.getGson().fromJson(value, Integer.class);
                    break;
                case ServerConstants.BLOCK_KEY_RESOURCE:
                    if(this.type.equals(ServerConstants.BLOCK_TYPE_RESOURCE)) {
                        this.resource = Model.getGson().fromJson(value, Integer.class);
                    }
                    else
                    {
                        this.resource = 0;
                    }
                    break;
                case ServerConstants.BLOCK_KEY_TURN:
                    this.turn = Model.getGson().fromJson(value, Integer.class);
                    break;
                case ServerConstants.BLOCK_KEY_TYPE:
                    this.type = Model.getGson().fromJson(value, String.class);
                    break;
                case ServerConstants.BLOCK_KEY_JUMP_IMP:
                    if(this.type.equals(ServerConstants.BLOCK_TYPE_MITOSIS)) {
                        this.jumpImprovementAmount = Model.getGson().fromJson(value, Integer.class);
                    }
                    else
                    {
                        this.jumpImprovementAmount = 0;
                    }
                    break;
                case ServerConstants.BLOCK_KEY_ATTACK_IMP:
                    if(this.type.equals(ServerConstants.BLOCK_TYPE_MITOSIS)) {
                        this.attackImprovementAmount = Model.getGson().fromJson(value, Integer.class);
                    }
                    else
                    {
                        this.attackImprovementAmount = 0;
                    }
                    break;
                case ServerConstants.BLOCK_KEY_DEPTH_OF_FIELD_IMP:
                    if(this.type.equals(ServerConstants.BLOCK_TYPE_MITOSIS)) {
                        this.depthOfFieldImprovementAmount = Model.getGson().fromJson(value, Integer.class);
                    }
                    else
                    {
                        this.depthOfFieldImprovementAmount = 0;
                    }
                    break;
                case ServerConstants.BLOCK_KEY_GAIN_RATE_IMP:
                    if(this.type.equals(ServerConstants.BLOCK_TYPE_MITOSIS)) {
                        this.gainImprovementAmount = Model.getGson().fromJson(value, Integer.class);
                    }
                    else
                    {
                        this.gainImprovementAmount = 0;
                    }
                    break;
                default:
                    break;
            }

        }
    }

    public Position getPos() {
        return pos;
    }

    public int getHeight() {
        return height;
    }

    public int getMinHeight() {
        return minHeight;
    }

    public int getResource() {
        if(this.type.equals(ServerConstants.BLOCK_TYPE_RESOURCE)) {
            return resource;
        }
        return 0;
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

    public int getGainImprovementAmount() {
        if(this.type.equals(ServerConstants.BLOCK_TYPE_MITOSIS))
        {
            return gainImprovementAmount;
        }
        return 0;
    }

    public int getDepthOfFieldImprovementAmount() {
        if(this.type.equals(ServerConstants.BLOCK_TYPE_MITOSIS))
        {
            return depthOfFieldImprovementAmount;
        }
        return 0;
    }

    public int getJumpImprovementAmount() {
        if(this.type.equals(ServerConstants.BLOCK_TYPE_MITOSIS))
        {
            return jumpImprovementAmount;
        }
        return 0;
    }

    public int getAttackImprovementAmount() {
        if(this.type.equals(ServerConstants.BLOCK_TYPE_MITOSIS))
        {
            return attackImprovementAmount;
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Block) {
            Block b = (Block) o;
            return pos.equals(b.pos);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return pos.hashCode();
    }

}
