package core.model;

import core.Context;
import data.CellData;
import data.DynamicData;
import model.Direction;
import model.Position;
import util.Constants;
import util.ServerConstants;
import util.UID;

import java.util.ArrayList;

/**
 * Created by rajabzz on 2/2/15.
 */
public class Cell extends DynamicGameObject {
    public static final int MIN_ENERGY_FOR_MITOSIS = 50; //TODO can change later

    private Context ctx;
    //private String id;
    //private Position pos;
    //final private int teamId;
    private int depthOfField;
    private int energy;
    private int gainRate;
    //private DynamicGameObject myDynamicGameObject;
    private CellData mDynamicData;

    public Cell (Context ctx, Position pos, int teamId, int dof, int energy, int gainRate) {
        super(teamId);
        this.ctx = ctx;
        id = UID.getUID();
        this.type = ServerConstants.GAME_OBJECT_TYPE_CELL;
        this.position = pos;
        depthOfField = dof;
        this.energy = energy;
        this.gainRate = gainRate;

        mDynamicData = new CellData(id, this.position, type, this.teamId, this.energy);
    }

    public void addEnergy(int amount) {
        energy += amount;
    }

    /*public Cell() {
        id = UUID.randomUUID();
    }*/


    public int getEnergy() {
        return energy;
    }

    public int getDepthOfField() {
        return depthOfField;
    }

    public int getGainRate() {
        return gainRate;
    }

    public void setDepthOfField(int depthOfField) {

        this.depthOfField = depthOfField;
    }

    public void setEnergy(int energy) {
        mDynamicData.setEnergy(energy);
        this.energy = energy;
    }

    public void setGainRate(int gainRate) {
        this.gainRate = gainRate;
    }

    /*public void setTeamId(int teamId) {
        teamId = teamId;
    }*/


    public boolean mitosis() {
        //System.out.println("mitosis start");
        if(energy < ServerConstants.CELL_MIN_ENERGY_FOR_MITOSIS)
        {
            return false;
        }
        Position secondCellPos = null;
        ArrayList<Direction> directions = ctx.getShuffledListOfDirections();
        for(Direction d : directions)
        {
            Position tPos = position.getNextPos(d);
            if(!ctx.checkBounds(tPos))
            {
                continue;
            }
            int currentHeight = ctx.getMap().at(position).getHeight();
            int nextHeight = ctx.getMap().at(tPos).getHeight();
            if (nextHeight > currentHeight + 1) {
                continue;
            }
            if(ctx.getMap().at(tPos).isEmpty())
            {
                secondCellPos = tPos;
                break;
            }
        }
        if(secondCellPos == null) {
            return false;
        }

        int energy = ServerConstants.CELL_MIN_ENERGY_FOR_MITOSIS;
        int secondEnergy = energy / 2;
        setEnergy(energy - secondEnergy);
        Cell newCell = new Cell
                (
                        ctx,
                        secondCellPos,
                        teamId,
                        depthOfField,
                        secondEnergy,
                        gainRate
                );
        ctx.addCell(newCell);
        //System.out.println("mitosis end");
        return true;
    }

    public boolean move(Position nextPos) {//TODO
        if(!ctx.checkBounds(nextPos)) {
            return false;
        }
        int nextHeight = ctx.getMap().at(nextPos).getHeight();
        int currentHeight = ctx.getMap().at(position).getHeight();
        if(nextHeight > currentHeight +1)
        {
            return false;
        }
        if (ctx.checkBounds(position)) {
            Block block = ctx.getMap().at(position);
            block.removeCell();
        }
        Block block = ctx.getMap().at(nextPos);
        setPos(nextPos);
        block.setCell(this);
        return true;
    }

    @Override
    public void setPos(Position position)
    {
        mDynamicData.setPosition(position);
        this.position = position;
    }

    public boolean gainResource () {
        //System.out.println("gain start");
        //System.out.println(ctx.getMap().at(position).getResource());
        //System.out.println(energy);
        if(energy >= ServerConstants.CELL_MAX_ENERGY)
        {
            return false;
        }
        Block block = ctx.getMap().at(position);
        if(block.getResource() <= 0)
        {
            return false;
        }
        int blockResource = block.getResource();
        int gain;
        if( blockResource < gainRate) {
            gain = blockResource;
        }
        else if(gainRate + blockResource > ServerConstants.CELL_MAX_ENERGY)
        {
            gain = ServerConstants.CELL_MAX_ENERGY - energy;
        }
        else{
            gain = gainRate;
        }
        block.setResource(blockResource - gain);
        setEnergy(energy + gain);
        //System.out.println("gain end");
        return true;
    }

    public DynamicData getDynamicData()
    {
        return mDynamicData;
    }
}
