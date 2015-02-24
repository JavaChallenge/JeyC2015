package core.model;

import com.google.gson.Gson;
import core.Context;
import model.Position;
import util.Constants;
import util.ServerConstants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Map {
    private Context ctx;
    private int width, height;
    private Block[][] mBlocks;

    public Map(Context ctx, int w, int h) {
        this.ctx = ctx;
        width = w;
        height = h;
        mBlocks = new Block[h][w];
    }

    public static class ObjectStructure {
        public int x, y;
        public Number[] values;
    }

    public static class TypeStructure {
        public String name;
        public String[] keys;
        public Number[] defaults;
        public String coloring;
        public ObjectStructure[] instances;
    }

    public static class BlockStructure {
        public Number[] values;
    }

    public static class MapStructure {
        public int width, height;
        public String[] keys;
        public Number[] defaults;
        public String coloring;
        public BlockStructure[][] blocks;
    }

    public static class FileStructure {
        public MapStructure map;
        public TypeStructure[] objects;
    }

    /*public void load(String dir)
    {
        //TODO LOADING
        for(int i = 0; i < width; i++)
        {
            for(int j = 0; j < height; j++)
            {
                mBlocks[j][i] = new Block(ctx, Constants.TURN_MAKE_MAP, i, j, 1, 2, Constants.BLOCK_TYPE_MITOSIS, true);
            }
        }

        ctx.addCell((new Cell(ctx, new Position(width / 2, height / 2), 0, Constants.CELL_DEPTH_OF_FIELD, 10, Constants.CELL_GAIN_RATE)));
        ctx.addCell(new Cell(ctx,new Position(width / 2, height / 2 + 1), 0, Constants.CELL_DEPTH_OF_FIELD, 10, Constants.CELL_GAIN_RATE));
        //mBlocks[height/2][width/2].setCell(new Cell(ctx, new Position(width / 2, height / 2), 0, 2, 100, 20));
        //mBlocks[height/2 + 1][width/2].setCell(new Cell(ctx,new Position(width / 2, height / 2 + 1), 0, 2, 100, 20));
    }*/

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Block at(int x, int y) {
        return mBlocks[y][x];
    }

    public Block at(Position pos)
    {
        return mBlocks[pos.getY()][pos.getX()];
    }

    public void set(int x, int y, Block block) {
        mBlocks[y][x] = block;
    }

    public Block[][] getBlocks() {
        return mBlocks;
    }
}
