package core;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import core.model.*;
import core.model.Map;
import model.*;
import server.core.model.ClientInfo;
import util.ServerConstants;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

/**
 * Created by Razi on 2/12/2015.
 */
public class Context {

    private int turn;


    private ClientInfo[] clientsInfo;
    private Map map;
    private Team[] teams;

    private HashMap<String, DynamicGameObject> allDynamicObjects;
    private HashMap<String, Cell> allCells;

    private ArrayList<Position[]> relatedVisiblePositionsFromOdd;
    private ArrayList<Position[]> relatedVisiblePositionsFromEven;

    ArrayList<Direction> directions;


    Context(int turn, String mapDir, String clientIntoDir) throws IOException {
        this.turn = turn;
        allDynamicObjects = new HashMap<>();
        allCells = new HashMap<>();

        relatedVisiblePositionsFromEven = new ArrayList<>();
        relatedVisiblePositionsFromOdd = new ArrayList<>();

        //load clients
        Gson gson = new Gson();
        try {
            File file = new File(clientIntoDir);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            clientsInfo = gson.fromJson(bufferedReader, ClientInfo[].class);
        } catch (FileNotFoundException notFound) {
            throw new RuntimeException("mitosis/clients config file not found");
        } catch (JsonParseException e) {
            throw new RuntimeException("mitosis/clients file does not meet expected syntax");
        }

        //make teams
        teams = new Team[clientsInfo.length];
        for (int i = 0; i < teams.length; i++) {
            clientsInfo[i].setID(i);
            teams[i] = new Team(this, clientsInfo[i]);
        }

        //load map
        loadMap(mapDir);
        //map = Map.load(this, mapDir);

        //make teams map
        for (int i = 0; i < teams.length; i++) {
            teams[i].makeMap();
        }
    }

    private void loadMap(String dir) throws IOException {
        String[] types = {
                ServerConstants.BLOCK_TYPE_NONE,
                ServerConstants.BLOCK_TYPE_NORMAL,
                ServerConstants.BLOCK_TYPE_MITOSIS,
                ServerConstants.BLOCK_TYPE_RESOURCE,
                ServerConstants.BLOCK_TYPE_IMPASSABLE
        };

        File file = new File(dir);
        String json = new String(Files.readAllBytes(file.toPath()), ServerConstants.MAP_FILE_ENCODING);
        //System.out.println(json);
        Map.FileStructure fs = new Gson().fromJson(json, Map.FileStructure.class);

        // create map
        int w = fs.map.width, h = fs.map.height;
        map = new Map(this, w, h);
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                Map.BlockStructure block = fs.map.blocks[i][j];
                int type = block.values[0].intValue();
                int height = block.values[1].intValue();
                int resource = block.values[2].intValue();
                boolean isMovable = !types[type].equals(ServerConstants.BLOCK_TYPE_IMPASSABLE);
                Block b = new Block(this, ServerConstants.TURN_MAKE_MAP, j, i, height, resource, types[type], isMovable);
                map.set(j, i, b);
            }
        }

        // create objects
        for (Map.TypeStructure type : fs.objects) {
            Map.ObjectStructure[] instances = type.instances;
            for (Map.ObjectStructure instance : instances) {
                int x = instance.x, y = instance.y;
                int teamID = instance.values[0].intValue();
                int dof = instance.values[1].intValue();
                int energy = instance.values[2].intValue();
                int gainRate = instance.values[3].intValue();
                Cell cell = new Cell(this, new Position(x, y), teamID, dof, energy, gainRate);
                addCell(cell);
            }
        }
    }

    public int getTurn() {
        return turn;
    }

/*    void setTurn(int turn) {
        this.turn = turn;
    }*/

    /*void setMap(Map map) {
        this.map = map;
    }*/

    /*void newMap(String dir){



    }*/

    /*void setTeams(Team[] teams)
    {
        this.teams = teams;
    }*/

    public ClientInfo[] getClientsInfo() {
        return clientsInfo;
    }

    public Team[] getTeams() {
        return teams;
    }

    public Map getMap(){
        return map;
    }

    void incTurn()
    {
        turn++;
    }

    /*HashMap<String, Cell> getAllCells() {
        return allCells;
    }*/

    public DynamicGameObject getDynamicObject(String id) {
        return allDynamicObjects.get(id);
    }

    public Cell getCell(String id)
    {
        return allCells.get(id);
    }

    public boolean addCell(Cell cell) {
        //checking
        if (!cell.getType().equals(ServerConstants.GAME_OBJECT_TYPE_CELL)) {
            return false;
        }
        if (!checkBounds(cell.getPos())) {
            return false;
        }
        Block block = map.at(cell.getPos());
        if (!block.isEmpty()) {
            return false;
        }

        //add to map
        block.setCell(cell);

        //add to allCells
        allCells.put(cell.getId(), cell);

        //add to allDynamicObjects
        allDynamicObjects.put(cell.getId(), cell);

        //add to team
        teams[cell.getTeamId()].addCell(cell);

        return true;
    }

    private void calculateRelationVisiblePos(int depthOfField) {
        if (relatedVisiblePositionsFromOdd.size() != relatedVisiblePositionsFromEven.size()) {
            System.out.println("error!! calculateRelationVisiblePos!");
        }
        if (relatedVisiblePositionsFromOdd.size() == 0) {
            Position[] positions = new Position[1];
            positions[0] = new Position(0,0);
            relatedVisiblePositionsFromEven.add(positions);
            relatedVisiblePositionsFromOdd.add(positions);
        }
        int currentSize = relatedVisiblePositionsFromOdd.size();
        if (depthOfField < currentSize) {
            return;
        }
        Position startPos = new Position(0,0);
        Direction[] directions = {Direction.SOUTH_WEST, Direction.SOUTH, Direction.SOUTH_EAST, Direction.NORTH_EAST, Direction.NORTH, Direction.NORTH_WEST};
        for (int i = 0; i < currentSize; i++) {
            startPos = startPos.getNextPos(Direction.NORTH);
        }


        for(int i = currentSize; i < depthOfField + 1 ; i++)
        {
            Position[] evenPositions = new Position[i * 6];
            Position[] oddPositions = new Position[i * 6];
            int index = 0;
            for (Direction d : directions) {
                for(int j = 0; j < i; j++) {
                    evenPositions[index] = new Position(startPos);
                    oddPositions[index] = new Position(startPos.getX(),-startPos.getY());
                    startPos = startPos.getNextPos(d);
                    index++;
                }
            }
            relatedVisiblePositionsFromEven.add(evenPositions);
            relatedVisiblePositionsFromOdd.add(oddPositions);
            startPos = startPos.getNextPos(Direction.NORTH);
        }
    }

    public ArrayList<Position[]> getRelatedVisiblePositions(Position pos, int depthOfField)
    {
        if(depthOfField + 1 > relatedVisiblePositionsFromOdd.size() || depthOfField + 1 > relatedVisiblePositionsFromEven.size())
        {
            calculateRelationVisiblePos(depthOfField);
        }

        if(pos.getX() % 2 == 1)
        {
            return relatedVisiblePositionsFromOdd;
        }
        return relatedVisiblePositionsFromEven;
    }

    public void getAllPositionsCanSee (HashSet<Position> positionsCanSee, Position pos, int depthOfField) {
        if(positionsCanSee == null)
        {
            positionsCanSee = new HashSet<>();
        }

        ArrayList<Position[]> relatedPositions = getRelatedVisiblePositions(pos, depthOfField);
        for(Position[] positions : relatedPositions)
        {
            for(Position p : positions)
            {
                Position newPos = new Position(p.getX() + pos.getX(), p.getY() + pos.getY());
                if(checkBounds(newPos))
                {
                    positionsCanSee.add(map.at(newPos).getPos());
                }
            }
        }

        /*for(int i = depthOfField; i > 0; i--)
        {
            for(int j = i - depthOfField; j < depthOfField - i + 1; j++)
            {
                if(checkBounds(new Position(pos.getX() + j, pos.getY() + i))) {
                    positionsCanSee.add(map.at(pos.getX() + j, pos.getY() + i).getPos());
                }
            }
        }
        for(int i = 0; i >= -depthOfField; i--)
        {
            for(int j = -depthOfField; j <= depthOfField; j++)
            {
                if(checkBounds(new Position(pos.getX() + j, pos.getY() + i))) {
                    positionsCanSee.add(map.at(pos.getX() + j, pos.getY() + i).getPos());
                }
            }
        }*/
        /*for(int i = depthOfField; i >= 0; i--)
        {
            for(int j = 0; j 6)
        }*/
        /*--depthOfField;
        for (Direction dir: Direction.values()) {
            Position currentPos = pos.getNextPos(dir);
            if (depthOfField == 0) {
                positionsCanSee.add(currentPos);
                return;
            }
            getAllPositionsCanSee(positionsCanSee, currentPos, depthOfField);
        }*/
    }

    public boolean checkBounds(Position position) {
        return position.getX() >= 0 && position.getX() < map.getWidth()
                && position.getY() >= 0 && position.getY() < map.getHeight();
    }

    public ArrayList<Direction> getShuffledListOfDirections()
    {
        if(directions == null) {
            directions = new ArrayList<Direction>(Arrays.asList(Direction.values()));
        }
        Collections.shuffle(directions);
        return directions;
    }
}
