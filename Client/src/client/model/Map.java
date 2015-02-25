package client.model;

import client.Model;
import com.google.gson.JsonElement;
import data.ReceivedObjectDiff;
import model.Position;
import data.MapSize;
import util.ServerConstants;

import java.util.HashMap;

/**
 * Created by Razi on 2/13/2015.
 */
public class Map {
    private Block[][] blocks;
    private HashMap<String, Block> allBlocks;


    public Map(MapSize mapSize, ReceivedObjectDiff[] mapData) {
        blocks = new Block[mapSize.getHeight()][mapSize.getWidth()];
        allBlocks = new HashMap<>();
        for(ReceivedObjectDiff s : mapData)
        {
            JsonElement jType = s.get(ServerConstants.BLOCK_KEY_TYPE);
            String type = Model.getGson().fromJson(jType, String.class);
            if(isBlockType(type)) {
                Block b = new Block(s);
                Position pos = b.getPos();
                blocks[pos.getY()][pos.getX()] = b;
                allBlocks.put(b.getId(), b);
            }
        }
    }

    public boolean setChange(ReceivedObjectDiff d)
    {
        JsonElement jId = d.get(ServerConstants.GAME_OBJECT_KEY_ID);
        String id = Model.getGson().fromJson(jId, String.class);
        Block b = allBlocks.get(id);
        if(b == null)
        {
            //nothing
        }
        else {
            b.setChange(d);
        }
        return true;
    }

    public boolean isBlockType(String type)
    {
        switch (type)
        {
            case ServerConstants.BLOCK_TYPE_NONE:
            case ServerConstants.BLOCK_TYPE_NORMAL:
            case ServerConstants.BLOCK_TYPE_MITOSIS:
            case ServerConstants.BLOCK_TYPE_RESOURCE:
            case ServerConstants.BLOCK_TYPE_IMPASSABLE:
                return true;
            default:
                //nothing yet !
                return false;
        }
    }

    public Block at(Position pos)
    {
        return blocks[pos.getY()][pos.getX()];
    }

    public Block at(int x, int y)
    {
        return blocks[y][x];
    }
}
