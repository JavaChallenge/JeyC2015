package core.data;

/**
 * Created by rajabzz on 2/3/15.
 */
public class StaticGameObject extends GameObject {
    public static final String NAME = "statics";

    private int turn;

    public void setId(String id) {
        this.id = id;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    StaticGameObject(int turn, String id)
    {
        this.turn = turn;
        this.id = id;
    }
}
