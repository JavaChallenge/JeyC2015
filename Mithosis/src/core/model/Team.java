package core.model;

import core.Context;
import core.MitosisGameLogic;
import server.core.model.ClientInfo;
import util.Constants;

import java.util.ArrayList;

/**
 * Created by rajabzz on 2/2/15.
 */
public class Team {
    private Context ctx;
    private ClientInfo mInfo;
    private ArrayList<Cell> mCells;
    private int mScore;
    int[][] viewHistory;
    //private int mId;

    public Team(Context ctx, ClientInfo clientInfo)
    {

        this.ctx = ctx;
        mInfo = clientInfo;
        mCells = new ArrayList<>();

    }

    public void makeMap()
    {
        Map map = ctx.getMap();
        viewHistory = new int[map.getHeight()][map.getWidth()];
        for (int row = 0; row < map.getHeight(); row++) {
            for (int col = 0; col < map.getWidth(); col++) {
                viewHistory[row][col] = Constants.TURN_TEAM_VIEW_HISTORY_DEFAULT;
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
        for (Cell cell: mCells) {
            if (cell.getId().equals(id))
                return cell;
        }
        return null;
    }

    public ArrayList<Cell> getCells() {
        return mCells;
    }

    public int getId() {
        return mInfo.getID();
    }

    public void addCell(Cell c) {
        mCells.add(c);
    }

    public int getLastVisitTurn(Position pos)
    {
        return viewHistory[pos.getY()][pos.getX()];
    }

    public void visitPosition(Position pos)
    {
        viewHistory[pos.getY()][pos.getX()] = ctx.getTurn();
    }
}
