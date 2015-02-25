package core.model;

import core.Context;
import core.MitosisGameLogic;
import model.Position;
import server.core.model.ClientInfo;
import util.ServerConstants;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rajabzz on 2/2/15.
 */
public class Team {
    private Context ctx;
    private ClientInfo mInfo;
    //private ArrayList<Cell> mCells;
    private HashMap<String, Cell> mCells;
    private HashMap<String, Cell> lastVisibleCells;
    private HashMap<String, Cell> currentVisibleCells;
    private int mScore;
    int[][] viewHistory;
    //private int mId;

    public Team(Context ctx, ClientInfo clientInfo)
    {
        this.ctx = ctx;
        mInfo = clientInfo;
        //mCells = new ArrayList<>();
        mCells = new HashMap<>();

        lastVisibleCells = new HashMap<>();
        currentVisibleCells = new HashMap<>();
    }

    public void makeMap()
    {
        Map map = ctx.getMap();
        viewHistory = new int[map.getHeight()][map.getWidth()];
        for (int row = 0; row < map.getHeight(); row++) {
            for (int col = 0; col < map.getWidth(); col++) {
                viewHistory[row][col] = ServerConstants.TURN_TEAM_VIEW_HISTORY_DEFAULT;
                /*Block block = map.getBlocks()[row][col];
                if(!block.isEmpty()) {
                    Cell c = block.getCell();
                    if (c.getTeamId() == getId()) {
                        mCells.add(c);
                    }
                }*/
            }
        }
    }

    public Cell getCellById(String id) {//TODO order
        return mCells.get(id);
    }

    public ArrayList<Cell> getCells() {
        return new ArrayList<>(mCells.values());
    }
    public HashMap<String, Cell> getCellsHashMap() {
        return mCells;
    }

    public int getId() {
        return mInfo.getID();
    }

    public void addCell(Cell c) {
        mCells.put(c.getId(), c);
    }

    public void removeCell(String id)
    {
        mCells.remove(id);
    }

    public int getLastVisitTurn(Position pos)
    {
        return viewHistory[pos.getY()][pos.getX()];
    }

    public void visitPosition(Position pos)
    {
        viewHistory[pos.getY()][pos.getX()] = ctx.getTurn();
    }

    public void addToCurrentVisibleCells(Cell cell) {
        currentVisibleCells.put(cell.getId(), cell);
    }

    public boolean isOpp(Cell cell) {
        return cell.getTeamId() != mInfo.getID();
    }

    public Cell findInLastVisibleCells(String id) {
        return lastVisibleCells.get(id);
    }

    public HashMap<String, Cell> getLastVisibleCells() {
        return lastVisibleCells;
    }

    public Cell findInCurrentVisibleCells(String id) {
        return currentVisibleCells.get(id);
    }

    public HashMap<String, Cell> getCurrentVisibleCells() {
        return currentVisibleCells;
    }

    public void moveCurrentToLast() {
        lastVisibleCells = currentVisibleCells;
        currentVisibleCells = new HashMap<>();
    }

    public int getScore() {
        return mScore;
    }

    public void setScore(int mScore) {
        this.mScore = mScore;
    }

    public String getName()
    {
        return mInfo.getName();
    }
}
