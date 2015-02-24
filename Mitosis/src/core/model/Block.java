package core.model;

import core.Context;
import data.BlockData;
import data.ObjectDiff;
import data.StaticData;
import model.Position;
import util.ServerConstants;
import util.UID;

public class Block {


    private Context ctx;
    private int mX, mY;
    private Position mPos;
    private int mHeight;
    private int mResource;
    private String mType;
    private String mId;
    private int mTurn;
    private boolean isMovable;
    private Cell mCell;
    //private StaticGameObject mMyStaticObject;
    private BlockData mStaticData;
    private ObjectDiff[] diffsForAllViews;


    public Block(Context ctx, int turn, int x, int y) {
        this(ctx, turn, x, y, 0, 0, ServerConstants.BLOCK_TYPE_NONE, true);
    }

    public Block(Context ctx, int turn, int x, int y, int height, int resource) {
        this(ctx, turn, x, y, height, resource, ServerConstants.BLOCK_TYPE_NONE, true);
    }

    public Block(Context ctx, int turn, int x, int y, int height, int resource, String type) {
        this(ctx, turn, x, y, height, resource, type, true);
    }

    public Block(Context ctx, int turn, int x, int y, int height, int resource, String type, boolean isMovable) {
        this.ctx = ctx;
        mId = UID.getUID();
        mTurn = turn;
        mX = x;
        mY = y;
        mPos = new Position(x, y);
        mHeight = height;
        mResource = resource;
        mType = type;
        this.isMovable = isMovable;

        mStaticData = new BlockData(mId,turn,mPos, type, height, resource);

        diffsForAllViews = new ObjectDiff[ctx.getViewsList().length];
        for(int i = 0; i < ctx.getViewsList().length; i++)
        {
            diffsForAllViews[i] =  new ObjectDiff(mId);

            diffsForAllViews[i].put(ServerConstants.BLOCK_KEY_TYPE, mType);
            diffsForAllViews[i].put(ServerConstants.GAME_OBJECT_KEY_POSITION, mPos);
            diffsForAllViews[i].put(ServerConstants.BLOCK_KEY_TURN, mTurn);
            diffsForAllViews[i].put(ServerConstants.BLOCK_KEY_MIN_HEIGHT, mHeight);
            if(mType.equals(ServerConstants.BLOCK_TYPE_RESOURCE)) {
                diffsForAllViews[i].put(ServerConstants.BLOCK_KEY_RESOURCE, mResource);
            }
            if(mType.equals(ServerConstants.BLOCK_TYPE_RESOURCE))
            {
                //TODO
            }

        }
    }

    public int getX() {
        return mX;
    }

    public int getY() {
        return mY;
    }

    public void setCell(Cell cell) {
        mCell = cell;
    }

    public void removeCell()
    {
        mCell = null;
    }

    public Cell getCell() {
        return mCell;
    }

    public boolean isEmpty () {
        return mCell == null;
    }

    public String getType() {
        return mType;
    }

    public int getHeight() {
        return mHeight;
    }

    public String getId() {
        return mId;
    }

    public boolean isMovable() {
        return isMovable;
    }

    public StaticData getStaticData()
    {
        return mStaticData;
    }

    public int getLastChangeTurn()
    {
        return mTurn;
    }

    public void setResource(int resource)
    {
        mResource = resource;
        mStaticData.setResource(resource);
        mStaticData.setTurn(ctx.getTurn());

        for(int i = 0; i < ctx.getViewsList().length; i++)
        {
            diffsForAllViews[i].put(ServerConstants.BLOCK_KEY_RESOURCE, resource);
            diffsForAllViews[i].put(ServerConstants.BLOCK_KEY_TURN, ctx.getTurn());
        }

        this.mTurn = ctx.getTurn();
    }

    public Position getPos() {
        return mPos;
    }

    public int getResource() {
        return mResource;
    }
    public ObjectDiff getTeamViewDiffs(int teamId) {
        return diffsForAllViews[teamId];
    }

    public ObjectDiff getGlobalViewDiffs() {
        return diffsForAllViews[ctx.getGlobalViewIndex()];
    }
}
