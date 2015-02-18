package core.model;

import core.Context;
import data.BlockData;
import data.StaticData;
import model.Position;
import util.ServerConstants;
import util.UID;

public class Block {


    private Context ctx;
    private int mX, mY;
    private Position mPos;
    private int mHeight;
    private int mResource;//TODO
    private String mType;
    private String mId;
    private int mTurn;
    private boolean isMovable;
    private Cell mCell;
    //private StaticGameObject mMyStaticObject;
    private BlockData mStaticData;


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

        /*mMyStaticObject = new StaticGameObject(turn, mId);
        mMyStaticObject.setPosition(new Position(mX,mY));
        HashMap<String,Object> otherDict = new HashMap<>();
        otherDict.put(BLOCK_KEY_HEIGHT, mHeight);
        otherDict.put(BLOCK_KEY_RESOURCE, mResource);
        mMyStaticObject.setOther(otherDict);*/
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
        this.mTurn = ctx.getTurn();
    }

    public Position getPos() {
        return mPos;
    }

    public int getResource() {
        return mResource;
    }
}
