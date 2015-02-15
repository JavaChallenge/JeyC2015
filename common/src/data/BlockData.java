package data;

import model.Position;
import util.Constants;

import java.util.HashMap;

/**
 * Created by Razi on 2/12/2015.
 */
public class BlockData extends StaticData {
    /*public class Other{
        int height;
        int resource;
    }*/
    //private Other other;

    public BlockData(String id, int turn, Position position, String type, int height, int resource)
    {
        super(id, turn, position,type);

        HashMap<String,Object> other = new HashMap<>();
        other.put(Constants.BLOCK_KEY_RESOURCE, resource);
        other.put(Constants.BLOCK_KEY_HEIGHT, height);
        this.other = other;

        /*other = new Other();
        other.height = height;
        other.resource = resource;*/
    }

    public BlockData(StaticData staticData)
    {
        super(staticData);
    }

    public void setHeight(int height) {
        other.put(Constants.BLOCK_KEY_HEIGHT, height);
    }
    public void setResource(int resource) {
        other.put(Constants.BLOCK_KEY_RESOURCE, resource);
    }

    public int getHeight() {
        return ((Double)other.get(Constants.BLOCK_KEY_HEIGHT)).intValue();
    }
    public int getResource() {
        return ((Double)other.get(Constants.BLOCK_KEY_RESOURCE)).intValue();
    }

    /*public void setHeight(int height) {
        other.height = height;
    }

    public void setresource(int resource) {
        other.resource = resource;
    }*/
}
