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

    public static Map load(Context ctx, String dir) throws IOException, IOException {


        String[] types = {
                ServerConstants.BLOCK_TYPE_NONE,
                ServerConstants.BLOCK_TYPE_NORMAL,
                ServerConstants.BLOCK_TYPE_MITOSIS,
                ServerConstants.BLOCK_TYPE_RESOURCE,
                ServerConstants.BLOCK_TYPE_IMPASSABLE
        };

        File file = new File(dir);
        String json = new String(Files.readAllBytes(file.toPath()), ServerConstants.MAP_FILE_ENCODING);
        System.out.println(json);
        FileStructure fs = new Gson().fromJson(json, FileStructure.class);

        // create map
        int w = fs.map.width, h = fs.map.height;
        Map map = new Map(ctx, w, h);
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                BlockStructure block = fs.map.blocks[j][i];
                int type = block.values[0].intValue();
                int height = block.values[1].intValue();
                int resource = block.values[2].intValue();
                boolean isMovable = !types[type].equals(ServerConstants.BLOCK_TYPE_IMPASSABLE);
                Block b = new Block(ctx, ServerConstants.TURN_MAKE_MAP, j, i, height, resource, types[type], isMovable);
                map.set(j, i, b);
            }
        }

        // create objects
        for (TypeStructure type : fs.objects) {
            ObjectStructure[] instances = type.instances;
            for (ObjectStructure instance : instances) {
                int x = instance.x, y = instance.y;
                int teamID = instance.values[0].intValue();
                int dof = instance.values[1].intValue();
                int energy = instance.values[2].intValue();
                int gainRate = instance.values[3].intValue();
//                dof = Constants.CELL_DEPTH_OF_FIELD;
//                gainRate = Constants.CELL_GAIN_RATE;
                Cell cell = new Cell(ctx, new Position(x, y), teamID, dof, energy, gainRate);
                ctx.addCell(cell);
            }
        }

        return map;
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
