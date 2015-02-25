package core.data;

import model.Position;
import util.ServerConstants;

import java.util.HashMap;

/**
 * Created by Razi on 2/12/2015.
 */
public class CellData extends DynamicData {
    /*public class Other{
        int teamId;
        int energy;
    }*/
    //private Other other;

    public CellData(String id, Position position, String type, int teamId, int energy)
    {
        super(id, position, type);

        HashMap<String,Object> other = new HashMap<>();
        other.put(ServerConstants.CELL_KEY_ENERGY_LONG, energy);
        other.put(ServerConstants.GAME_OBJECT_KEY_TEAM_ID_LONG, teamId);
        this.other = other;

        //other = new Other();
        //other.teamId = teamId;
        //other.energy = energy;
    }

    public CellData(DynamicData dynamicData)
    {
        super(dynamicData);
    }
    public void setEnergy(int energy) {
        other.put(ServerConstants.CELL_KEY_ENERGY_LONG, energy);
    }

    public int getTeamId()
    {

        return ((Double) other.get(ServerConstants.GAME_OBJECT_KEY_TEAM_ID_LONG)).intValue();
    }
    public int getEnergy()
    {
        return ((Double)other.get(ServerConstants.CELL_KEY_ENERGY_LONG)).intValue();
    }
    /*public void setEnergy(int energy) {
        other.energy = energy;
    }

    public int getTeamId()
    {
        return other.teamId;
    }
    public int getEnergy()
    {
        return other.energy;
    }*/
}
