package model;

import model.Position;

import java.util.HashMap;

/**
 * Created by rajabzz on 2/3/15.
 */
public class Transient {
    public static final String NAME = "transient";


    private int turn;
    private int duration;
    private Position position;
    private String type;
    private HashMap<String,Object> other;

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
