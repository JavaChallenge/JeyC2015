package core.model;

import core.Context;
import core.data.DynamicGameObject;
import core.data.CellData;
import core.data.DynamicData;
import core.data.ObjectDiff;
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
    private int energy;
    //private DynamicGameObject myDynamicGameObject;
    private CellData mDynamicData;
    private ObjectDiff[] diffsForAllViews;

    private int depthOfField;
    private int jump;
    private int gainRate;
    private int attack;

    public Cell (Context ctx, Position pos, int teamId, int dof, int energy, int gainRate, int jump, int attack) {
        super(teamId);
        this.ctx = ctx;
        id = UID.getUID();
        this.type = ServerConstants.GAME_OBJECT_TYPE_CELL;
        this.position = pos;
        depthOfField = dof;
        this.energy = energy;
        this.gainRate = gainRate;
        this.jump = jump;
        this.attack = attack;

        mDynamicData = new CellData(id, this.position, type, getTeamId(), this.energy);

        diffsForAllViews = new ObjectDiff[ctx.getViewsList().length];
        for(int i = 0; i < ctx.getViewsList().length; i++)
        {
            diffsForAllViews[i] =  new ObjectDiff(id);

            diffsForAllViews[i].put(ServerConstants.BLOCK_KEY_TYPE, type);
            diffsForAllViews[i].put(ServerConstants.CELL_KEY_ENERGY, this.energy);
            diffsForAllViews[i].put(ServerConstants.GAME_OBJECT_KEY_TEAM_ID, getTeamId());
            diffsForAllViews[i].put(ServerConstants.GAME_OBJECT_KEY_POSITION, this.position);
            if(i == getTeamId() || i == ctx.getGlobalViewIndex())
            {
                diffsForAllViews[i].put(ServerConstants.CELL_KEY_JUMP, this.jump);
                diffsForAllViews[i].put(ServerConstants.CELL_KEY_ATTACK, this.attack);
                diffsForAllViews[i].put(ServerConstants.CELL_KEY_GAIN_RATE, this.gainRate);
                diffsForAllViews[i].put(ServerConstants.CELL_KEY_DEPTH_OF_FIELD, this.depthOfField);
            }
        }
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

    public int getJump() {
        return jump;
    }

    public void setDepthOfField(int depthOfField) {

        this.depthOfField = depthOfField;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
        mDynamicData.setEnergy(energy);
        for(int i = 0; i < ctx.getViewsList().length; i++)
        {
            diffsForAllViews[i].put(ServerConstants.CELL_KEY_ENERGY, this.energy);
        }
    }

    public void setGainRate(int gainRate) {
        this.gainRate = gainRate;
    }

    @Override
    public void setPos(Position position)
    {
        this.position = position;
        mDynamicData.setPosition(position);
        for(int i = 0; i < ctx.getViewsList().length; i++)
        {
            diffsForAllViews[i].put(ServerConstants.GAME_OBJECT_KEY_POSITION, this.position);
        }
    }

    /*public void setTeamId(int teamId) {
        teamId = teamId;
    }*/


    public boolean mitosis() {
        //System.out.println("mitosis start");
        Block mitosisBlock;
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
            mitosisBlock = ctx.getMap().at(position);
            if(ctx.getMap().at(tPos).getType().equals(Constants.BLOCK_TYPE_IMPASSABLE))
            {
                continue;
            }
            int currentHeight = mitosisBlock.getHeight();
            int nextHeight = ctx.getMap().at(tPos).getHeight();
            int newJump = jump + mitosisBlock.getJumpImprovementAmount();
            if (nextHeight > currentHeight + newJump) {
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
        mitosisBlock = ctx.getMap().at(position);
        int energy = ServerConstants.CELL_MIN_ENERGY_FOR_MITOSIS;
        int secondEnergy = energy / 2;
        setEnergy(energy - secondEnergy);
        Cell newCell = new Cell
                (
                        ctx,
                        secondCellPos,
                        getTeamId(),
                        Math.min( depthOfField + mitosisBlock.getDepthOfFieldImprovementAmount(),ServerConstants.CELL_MAX_DEPTH_OF_FIELD),
                        secondEnergy,
                        Math.min(gainRate + mitosisBlock.getGainImprovementAmount(),ServerConstants.CELL_MAX_GAIN_RATE),
                        Math.min(jump + mitosisBlock.getJumpImprovementAmount(), ServerConstants.CELL_MAX_JUMP),
                        Math.min(attack + mitosisBlock.getAttackImprovementAmount(), ServerConstants.CELL_MAX_ATTACK)
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
        if(nextHeight > currentHeight +jump)
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
        else if(gainRate + energy > ServerConstants.CELL_MAX_ENERGY)
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

    public boolean attack(Cell cell) {
        //int attack = 20;//TODO
        cell.setEnergy(cell.getEnergy()-attack);
        if(cell.getEnergy() < 0)
        {
            ctx.addToDead(cell);
        }
        return true;
    }

    public DynamicData getDynamicData()
    {
        return mDynamicData;
    }

    public ObjectDiff getTeamViewDiffs(int teamId) {
        return diffsForAllViews[teamId];
    }

    public ObjectDiff getGlobalViewDiffs() {
        return diffsForAllViews[ctx.getGlobalViewIndex()];
    }

    public boolean isTMM(Cell cell) {
        return cell.getTeamId() == getTeamId();
    }

    public void die() {
        for(ObjectDiff d:diffsForAllViews)
        {
            d.clearChanges();
            d.put(ServerConstants.BLOCK_KEY_TYPE, ServerConstants.GAME_OBJECT_TYPE_DESTROYED);
        }
    }
}
