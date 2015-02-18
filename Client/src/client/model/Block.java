package client.model;

import model.Position;
import data.BlockData;

/**
 * Created by Razi on 2/13/2015.
 */
public class Block {

    private Position pos;
    private int height;
    private int resource;
    private String type;
    private String id;
    private int turn;

    Block(BlockData blockData) {
        this.pos = blockData.getPosition();
        this.height = blockData.getHeight();
        this.resource = blockData.getResource();
        this.turn = blockData.getTurn();
        this.type = blockData.getType();
        this.id = blockData.getId();
    }

    public void setChange(BlockData blockData) {
        this.pos = blockData.getPosition();
        this.height = blockData.getHeight();
        this.resource = blockData.getResource();
        this.turn = blockData.getTurn();
        this.type = blockData.getType();
        this.id = blockData.getId();
    }

    public Position getPos() {
        return pos;
    }

    public int getHeight() {
        return height;
    }

    public int getResource() {
        return resource;
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
