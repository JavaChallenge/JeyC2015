package client;


import client.model.Cell;
import client.model.Map;

import com.google.gson.JsonElement;
import data.*;
import util.ServerConstants;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Razi on 2/10/2015.
 */
public class World {

    private Model model;

    private String[] teams;
    private int myId;
    private String myName;

    private MapSize mapSize;
    private Map map;

    private int turn;

    private HashMap<String, Cell> allCells;
    private HashMap<String, Cell> myCells;
    private HashMap<String, Cell> enemyCells;

    private HashMap<String, Cell> allVisitedCells;
    private HashMap<String, Cell> invisibleCells;

    public World(Model model, ClientInitInfo initInfo, Map map)
    {
        this.model = model;
        this.teams = initInfo.getTeams();
        this.myId = initInfo.getYourInfo().getId();
        this.myName = initInfo.getYourInfo().getName();
        this.mapSize = initInfo.getMapSize();
        this.map = map;
        this.turn = initInfo.getTurn();

        allCells = new HashMap<>();
        myCells = new HashMap<>();
        enemyCells = new HashMap<>();

        allVisitedCells = new HashMap<>();
        invisibleCells = new HashMap<>();
    }

    public String[] getTeams() {
        return teams;
    }

    public int getMyId() {
        return myId;
    }

    public String getMyName() {
        return myName;
    }

    public MapSize getMapSize()
    {
        return mapSize;
    }

    public Map getMap() {
        return map;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }


    public void addCell(Cell cell)
    {
        allCells.put(cell.getId(), cell);
        allVisitedCells.put(cell.getId(), cell);
        if(cell.getTeamId() == myId) {
            myCells.put(cell.getId(), cell);
        }
        else
        {
            enemyCells.put(cell.getId(), cell);
        }
    }

    public void visibleCell(Cell cell)
    {
        allCells.put(cell.getId(), cell);
        allVisitedCells.put(cell.getId(), cell);
        if(cell.getTeamId() == myId) {
            myCells.put(cell.getId(), cell);
        }
        else
        {
            enemyCells.put(cell.getId(), cell);
        }
        invisibleCells.remove(cell.getId());
    }

    public void invisibleCell(Cell cell)
    {
        String id = cell.getId();
        invisibleCells.put(cell.getId(),cell);
        if(allCells.containsKey(id))
        {
            allCells.remove(id);
        }
        if(myCells.containsKey(id))
        {
            myCells.remove(id);
        }
        if(enemyCells.containsKey(id))
        {
            enemyCells.remove(id);
        }
    }

    public void killCell(Cell cell)
    {
        String id = cell.getId();
        if(allVisitedCells.containsKey(id))
        {
            allVisitedCells.remove(id);
        }
        if(allCells.containsKey(id))
        {
            allCells.remove(id);
        }
        if(myCells.containsKey(id))
        {
            myCells.remove(id);
        }
        if(enemyCells.containsKey(id))
        {
            enemyCells.remove(id);
        }
        if(invisibleCells.containsKey(id))
        {
            invisibleCells.remove(id);
        }
    }

    public ArrayList<Cell> getAllCells() {
        return new ArrayList<>(allCells.values());
    }

    public ArrayList<Cell> getMyCells() {
        return new ArrayList<>(myCells.values());
    }

    public ArrayList<Cell> getEnemyCells() {
        return new ArrayList<>(enemyCells.values());
    }

    public HashMap<String, Cell>getAllCellsHashMap()
    {
        return allCells;
    }

    public HashMap<String, Cell>getMyCellsHashMap()
    {
        return myCells;
    }
    public HashMap<String, Cell>getEnemyCellsHashMap()
    {
        return enemyCells;
    }


    public void setStaticChange(ReceivedObjectDiff d) {
        map.setChange(d);
    }

    public void setDynamicChange(ReceivedObjectDiff d)
    {

        JsonElement jId = d.get(ServerConstants.GAME_OBJECT_KEY_ID);
        String id = model.getGson().fromJson(jId, String.class);
        Cell c = allVisitedCells.get(id);
        if(c == null)
        {
            // if not exist
            Cell cell = new Cell(model, d);
            addCell(cell);
        }
        else
        {
            JsonElement jType = d.get(ServerConstants.GAME_OBJECT_KEY_TYPE);
            if(jType != null && model.getGson().fromJson(jType, String.class).equals(ServerConstants.GAME_OBJECT_TYPE_DESTROYED))
            {
                //cell is dead
                killCell(c);
            }
            else if(c.getTeamId() != getMyId())
            {
                //is opponent
                JsonElement jVis = d.get(ServerConstants.CELL_KEY_VISIBLE);
                if(jVis != null)
                {
                    int vis = model.getGson().fromJson(jVis, Integer.class);
                    if(vis == 0)
                    {
                        //cell exited from visible area
                        //TODO
                        invisibleCell(c);
                    }
                    else
                    {
                        //cell return to visible area
                        //TODO
                        visibleCell(c);
                        c.setChange(d);
                    }
                }
                else
                {
                    c.setChange(d);
                }
            }
            else
            {
                //is team mate
                c.setChange(d);
            }
        }
        /*Cell c = allCells.get(id);
        if(c == null)
        {
            Cell cell = new Cell(model, d);
            addCell(cell);
        }
        else
        {
            c.setChange(d);
        }*/
    }
}
