package data;

import com.google.gson.JsonElement;
import util.ServerConstants;

import java.util.HashMap;
import java.util.Objects;

/**
 * Created by Razi on 2/23/2015.
 */
public class ObjectDiff extends HashMap<String, Object> {
    public ObjectDiff(String id)
    {
        super();
        this.put(ServerConstants.GAME_OBJECT_KEY_ID, id);
    }

    public boolean isChanged()
    {
        /*for(java.util.Map.Entry<String, Object> entry : this.entrySet()) {
            System.out.println(entry.getKey());
        }*/
        //System.out.println(this.size());
        return (this.size() > 1);
    }

    public void clearChanges()
    {
        String id = (String)this.get(ServerConstants.GAME_OBJECT_KEY_ID);
        this.clear();
        this.put(ServerConstants.GAME_OBJECT_KEY_ID, id);
    }

    @Override
    public ObjectDiff clone() {
        ObjectDiff result;
        try {
            result = (ObjectDiff)super.clone();
        } catch (Exception e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError(e);
        }
        return result;
    }
/*
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof ObjectDiff)) return false;
        return this.get(ServerConstants.GAME_OBJECT_KEY_ID).equals(((ObjectDiff) o).get(ServerConstants.GAME_OBJECT_KEY_ID));
    }

    @Override
    public int hashCode() {
        return this.get(ServerConstants.GAME_OBJECT_KEY_ID).hashCode();
    }*/
}
