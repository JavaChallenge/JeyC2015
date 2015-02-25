package core.data;

import model.Position;
import util.ServerConstants;

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
        other.put(ServerConstants.BLOCK_KEY_RESOURCE_LONG, resource);
        other.put(ServerConstants.BLOCK_KEY_HEIGHT_LONG, height);
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
        other.put(ServerConstants.BLOCK_KEY_HEIGHT_LONG, height);
    }
    public void setResource(int resource) {
        other.put(ServerConstants.BLOCK_KEY_RESOURCE_LONG, resource);
    }

    public int getHeight() {
        return ((Double)other.get(ServerConstants.BLOCK_KEY_HEIGHT_LONG)).intValue();
    }
    public int getResource() {
        return ((Double)other.get(ServerConstants.BLOCK_KEY_RESOURCE_LONG)).intValue();
    }

    /*public void setHeight(int height) {
        other.height = height;
    }

    public void setresource(int resource) {
        other.resource = resource;
    }*/
}
