package model;

/**
 * Created by rajabzz on 2/2/15.
 */
public class GameEvent extends Event {
    public static final String TYPE_MOVE = "move";
    public static final String TYPE_ATTACK = "attack";
    public static final String TYPE_MITOSIS = "mitosis";
    public static final String TYPE_GAIN_RESOURCE = "gainResource";

    public static final int ARG_INDEX_MOVE_DIRECTION = 0;
    public static final int ARG_INDEX_ATTACK_DIRECTION = 0;

    public static final int ARGS_NUMBER_MOVE = 1;
    public static final int ARGS_NUMBER_ATTACK = 1;

    //private String type; // the type of the event
    //private String objectId; // to recognize which GameObject this event is for
    private int teamId; // tell which team this event is for
    //private String[] args; // arguments of the event

    public GameEvent(Event event)
    {
        type = event.getType();
        objectId = event.getObjectId();
        args = event.getArgs();
    }

    public GameEvent(String type){
        this.type = type;
        int argsLen = 0;
        switch (type){
            case TYPE_MOVE:
                argsLen = ARGS_NUMBER_MOVE;
                break;
            case TYPE_ATTACK:
                argsLen = ARGS_NUMBER_ATTACK;
                break;
            case TYPE_GAIN_RESOURCE:
                break;
            case TYPE_MITOSIS:
                break;
            default:
                break;
        }
        args = new String[argsLen];
    }

    public String getGameObjectId() {
        return objectId;
    }

    public String getType() {
        return type;
    }

    public int getTeamHid() {
        return teamId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    /*public void setType(String type) {
        this.type = type;
    }*/

    public void setTeamId(int teamHid) {
        teamId = teamHid;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArg(String value, int index)
    {
        args[index] = value;
    }
}
