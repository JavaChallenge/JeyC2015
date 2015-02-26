package core;

import com.google.gson.JsonObject;
import core.data.*;
import core.model.*;
import model.*;
import core.model.Map;
import data.*;
import util.Json;
import util.ServerConstants;
import server.Server;
import server.core.GameLogic;
import server.core.model.ClientInfo;
import model.Event;
import network.data.Message;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;

/**
 * Created by rajabzz on 2/2/15.
 */
public class MitosisGameLogic implements GameLogic {

    private final long GAME_LONG_TIME_TURN;

    private static final Charset CONFIG_ENCODING = Charset.forName("UTF-8");
    private static final String RESOURCE_PATH_GAME = "resources/mitosis/game.conf";
    private static final String RESOURCE_PATH_CLIENTS = "resources/mitosis/clients.conf";


    private Context ctx;
    private Team[] mTeams;



    private ArrayList<Message> mClientMessages;
    private Message mUIMessage;

    private ArrayList<Message> mClientsInitialMessages;
    private Message mUIInitialMessage;

    public static void main(String[] args) {
        Server server = new Server(options -> new MitosisGameLogic(options));
        server.start();
    }

    public MitosisGameLogic(String[] options) throws IOException {
        super();

        String gameConfig = new String(Files.readAllBytes(new File(RESOURCE_PATH_GAME).toPath()), CONFIG_ENCODING);
        GAME_LONG_TIME_TURN = Json.GSON.fromJson(gameConfig, JsonObject.class).get("turn").getAsLong();

        ctx = new Context(ServerConstants.TURN_INIT, options[0], RESOURCE_PATH_CLIENTS);

        mTeams = ctx.getTeams();
    }

    @Override
    public void init() {

        ArrayList<String> teamsList = new ArrayList<>();
        for (int i = 0; i < mTeams.length; i++) {
            teamsList.add("team" + i);
        }

        /*ArrayList<String> viewsList = (ArrayList<String>) teamsList.clone();
        viewsList.add(ServerConstants.VIEW_GLOBAL);*/

        Map map = ctx.getMap();

        MapSize mapSize = new MapSize();
        mapSize.setWidth(map.getWidth());
        mapSize.setHeight(map.getHeight());

        ArrayList<Object> uiUnknownMap = new ArrayList<>();
        int height = map.getHeight();
        int width = map.getWidth();
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                HashMap<String, Object> blockMap = new HashMap<>();
                Block block = map.at(col, row);
                blockMap.put(ServerConstants.GAME_OBJECT_KEY_ID_LONG, block.getId());
                blockMap.put(ServerConstants.GAME_OBJECT_KEY_TYPE_LONG, ServerConstants.BLOCK_TYPE_NONE_LONG);
                blockMap.put(ServerConstants.GAME_OBJECT_KEY_TURN_LONG, ServerConstants.TURN_WORLD_CREATION);
                blockMap.put(ServerConstants.GAME_OBJECT_KEY_POSITION_LONG, new Position(block.getX(), block.getY()));
                HashMap<String,Object> otherDict = new HashMap<>();
                otherDict.put(ServerConstants.BLOCK_KEY_HEIGHT_LONG, 0);
                otherDict.put(ServerConstants.BLOCK_KEY_RESOURCE_LONG, 0);
                blockMap.put(ServerConstants.GAME_OBJECT_KEY_OTHER_LONG, otherDict);
                uiUnknownMap.add(blockMap);
            }
        }

        ArrayList<Object> clientUnknownMap = new ArrayList<>();
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                //HashMap<String, Object> blockMap = new HashMap<>();
                Block block = map.at(col, row);
                ObjectDiff blockMap = new ObjectDiff(block.getId());
                //blockMap.put(ServerConstants.GAME_OBJECT_KEY_ID, block.getId());
                blockMap.put(ServerConstants.GAME_OBJECT_KEY_TYPE, ServerConstants.BLOCK_TYPE_NONE);
                blockMap.put(ServerConstants.GAME_OBJECT_KEY_TURN, ServerConstants.TURN_WORLD_CREATION);
                //blockMap.put(ServerConstants.GAME_OBJECT_KEY_POSITION, new Position(block.getX(), block.getY()));
                blockMap.put(ServerConstants.GAME_OBJECT_KEY_POSITION, block.getPos());

                clientUnknownMap.add(blockMap);
            }
        }


        //Generate clients init message
        mClientsInitialMessages = new ArrayList<>();
        for (int t = 0; t < mTeams.length; t++)
        {

            //make info
            TeamInfo teamInfo = new TeamInfo(ctx.getClientsInfo()[t].getName(), ctx.getClientsInfo()[t].getID());

            HashMap<String, Object> info = new HashMap<>();
            info.put(ServerConstants.INFO_KEY_TURN, ctx.getTurn());
            info.put(ServerConstants.INFO_KEY_TEAMS, teamsList);
            info.put(ServerConstants.INFO_KEY_YOUR_INFO, teamInfo);
            info.put(ServerConstants.INFO_KEY_MAP_SIZE, mapSize);
            info.put(ServerConstants.INFO_KEY_HEIGHT_COEFFICIENT, ServerConstants.BLOCK_HEIGHT_COEFFICIENT);


            //make static diff
            ArrayList<StaticGameObject> staticDiff = new ArrayList<>();

            //make client message
            Message clientMsg = new Message();
            clientMsg.setName(Message.NAME_INIT);
            Object[] args = {info, clientUnknownMap, staticDiff};
            clientMsg.setArgs(args);
            mClientsInitialMessages.add(clientMsg);
        }

        //Generate UI init message

        //make info
        HashMap<String, Object> info = new HashMap<>();
        info.put(ServerConstants.INFO_KEY_TURN, ctx.getTurn());
        info.put(ServerConstants.INFO_KEY_TEAMS, teamsList);
        info.put(ServerConstants.INFO_KEY_VIEWS, ctx.getViewsList());
        info.put(ServerConstants.INFO_KEY_MAP_SIZE, mapSize);
        info.put(ServerConstants.INFO_KEY_HEIGHT_COEFFICIENT, ServerConstants.BLOCK_HEIGHT_COEFFICIENT);

        //make map
            // map is ready

        //make diff
        ArrayList<Object> uiDiff = new ArrayList<>();

            //Generate teams diff
            for (int t = 0; t < mTeams.length; t++)
            {
                HashMap<String,Object> viewDif = new HashMap<>();
                viewDif.put(ServerConstants.VIEW, ctx.getTeamViewNameById(t));

                //calculate static diff for each team
                ArrayList<StaticGameObject> staticDiff = new ArrayList<>();


                viewDif.put(StaticGameObject.NAME, staticDiff);
                uiDiff.add(viewDif);
            }


            //Generate Global diff
            {
                HashMap<String, Object> viewDif = new HashMap<>();
                viewDif.put(ServerConstants.VIEW, ctx.getGlobalViewName());

                //calculate static diff for global view
                ArrayList<StaticData> staticDiff = new ArrayList<>();

                for (int row = 0; row < height; row++) {
                    for (int col = 0; col < width; col++) {
                        Block block = map.at(col, row);
                        staticDiff.add(block.getStaticData());
                    }
                }

                viewDif.put(StaticGameObject.NAME, staticDiff);
                uiDiff.add(viewDif);
            }

        //make ui message
        Message uiInitMsg = new Message();
        uiInitMsg.setName(Message.NAME_INIT);
        Object[] args = {info, uiUnknownMap, uiDiff};
        uiInitMsg.setArgs(args);

        mUIInitialMessage = uiInitMsg;

    }

    @Override
    public Message getUIInitialMessage() {
        return mUIInitialMessage;
    }

    @Override
    public Message[] getClientInitialMessages() {
        return mClientsInitialMessages.toArray(new Message[mClientsInitialMessages.size()]);
    }

    @Override
    public ClientInfo[] getClientInfo() {
        return ctx.getClientsInfo();
    }

    @Override
    public void simulateEvents(Event[] terminalEvent, Event[] environmentEvent, Event[][] clientsEvent) {
        HashMap<String, GameEvent> gameObjectEvents = new HashMap<>();
        ArrayList<GameEvent> moveEvents = new ArrayList<>();
        ArrayList<GameEvent> attackEvents = new ArrayList<>();
        ArrayList<GameEvent> mitosisEvents = new ArrayList<>();
        ArrayList<GameEvent> gainResourceEvents = new ArrayList<>();

        Map map = ctx.getMap();

        ctx.getDeadCells().clear();
        /*for(java.util.Map.Entry entry : ctx.getDeadCells().entrySet())
        {

        }*/


        if (clientsEvent != null || environmentEvent != null || terminalEvent != null) {
            if (clientsEvent != null) {
                for (int i = 0; i < mTeams.length; i++) {
                    if (clientsEvent[i] == null)
                        continue;
                    for(int j = 0; j < clientsEvent[i].length; j++) {
                        try {
                            GameEvent event = new GameEvent(clientsEvent[i][j]);
                            //event.getGameObjectId() TODO CHECK OWNER
                            String id = event.getGameObjectId();
                            if (ctx.getDynamicObject(id) == null || ctx.getDynamicObject(id).getTeamId() != i) {
                                continue;
                            }
                            event.setTeamId(i);
                            gameObjectEvents.put(event.getGameObjectId(), event);
                        } catch (Exception ignored) {
                        }
                    }
                    //GameEvent[] teamEvent = (GameEvent[]) clientsEvent[i];
                    /*for (GameEvent event: teamEvent) {
                        event.setTeamId(i);
                        if (!gameObjectEvents.containsKey(event.getGameObjectId()))
                            gameObjectEvents.put(event.getGameObjectId(), event);
                    }*/
                }
            }
            if (environmentEvent != null) {
                for (GameEvent event : (GameEvent[])environmentEvent) {
                    event.setTeamId(-1);
                    gameObjectEvents.put(event.getGameObjectId(), event);
                }
            }
            if (terminalEvent != null) {
                //TODO
                /*for (GameEvent event: (GameEvent[])terminalEvent) {
                    event.setTeamId(-2);
                        gameObjectEvents.put(event.getGameObjectId(), event);
                }*/
            }

            Collection<GameEvent> currentTurnEventsList = gameObjectEvents.values();
            for (GameEvent event : currentTurnEventsList) {
                try {
                    switch (event.getType()) {
                        case GameEvent.TYPE_MOVE:
                            moveEvents.add(event);
                            break;

                        case GameEvent.TYPE_GAIN_RESOURCE:
                            gainResourceEvents.add(event);
                            break;

                        case GameEvent.TYPE_MITOSIS:
                            mitosisEvents.add(event);
                            break;
                        case GameEvent.TYPE_ATTACK:
                            attackEvents.add(event);
                    }
                } catch (Exception ignored) {
                }
            }
        }

        Collections.shuffle(mitosisEvents);
        for (GameEvent event : mitosisEvents) {
            try {
                Cell cell = ctx.getCell(event.getGameObjectId());
                if (cell == null)
                    continue;
                Block block = map.at(cell.getPos());
                if (!block.getType().equals(ServerConstants.BLOCK_TYPE_MITOSIS)) {
                    continue;
                }
                cell.mitosis();
            } catch (Exception ignored) {
            }
        }


        ctx.clearDeadCells();
        for (GameEvent event: attackEvents) {
            try {
                Cell cell = ctx.getCell(event.getGameObjectId());
                if (cell == null)
                    continue;
                Direction dir = Direction.valueOf(event.getArgs()[GameEvent.ARG_INDEX_ATTACK_DIRECTION]);
                Position nextPos = cell.getPos().getNextPos(dir);
                Block block = map.at(nextPos);
                if (block.isEmpty())
                    continue;
                //if(block.getCell().isTMM(cell)) continue;
                cell.attack(block.getCell());
            } catch (Exception ignored) {
            }
        }
        for (java.util.Map.Entry entry : ctx.getDeadCells().entrySet()) {
            Cell cell = (Cell)entry.getValue();
            ctx.killCell(cell);
        }

        // handling move events
//        Collections.shuffle(moveEvents);
        //System.out.println(moveEvents.size());
        ArrayList<GameEvent> validMoveEvents = new ArrayList<>();
//        HashMap<Cell, GameEvent> validMoveEvents = new HashMap<>();

        for (GameEvent event : moveEvents) {
            try {
                Cell cell = ctx.getCell(event.getGameObjectId());
                if (cell == null)
                    continue;
                Direction dir = Direction.valueOf(event.getArgs()[GameEvent.ARG_INDEX_MOVE_DIRECTION]);
                Position pos = cell.getPos();
                Position nextPos = pos.getNextPos(dir);
                if (!ctx.checkBounds(nextPos) || !ctx.checkBounds(pos))
                    continue;
                Block block = map.at(pos);
                Block nextBlock = map.at(nextPos);
                int currentHeight = block.getHeight();
                int nextHeight = nextBlock.getHeight();
                if (nextHeight > currentHeight + cell.getJump()) {
                    continue;
                }
//                System.out.printf("jump:%d, from:%d, to:%d%n", cell.getJump(), nextHeight);
                if (!nextBlock.isMovable())
                    continue; // if this block is not movable
                //System.out.println(cell.getId());
                //if (nextBlock.isEmpty())
                validMoveEvents.add(event);
//            validEvents.add(new EventValidationCell())
                //cell.move(nextPos);
            } catch (Exception ignored) {
            }
        }

//        System.out.println("valid move events: " + validMoveEvents.size());

        int n = validMoveEvents.size();
        Cell[] cells = new Cell[n];
        Position[] starts = new Position[n];
        Position[] ends = new Position[n];

        for (int i = 0; i < n; i++) {
            GameEvent event = validMoveEvents.get(i);
            cells[i] = ctx.getCell(event.getGameObjectId());
            Direction dir = Direction.valueOf(event.getArgs()[GameEvent.ARG_INDEX_MOVE_DIRECTION]);
            starts[i] = cells[i].getPos();
            ends[i] = starts[i].getNextPos(dir);
        }

//        ArrayList<EventValidationCell> validEvents = new ArrayList<>();

        boolean[] validationResult = ctx.getEvMap().validate(validMoveEvents);

        for (int i = 0; i < n; i++) {
            if (validationResult[i]) {
                map.at(starts[i]).removeCell();
//                cell.move(cell.getPos().getNextPos(dir));
            }
        }

        for (int i = 0; i < n; i++) {
            if (validationResult[i]) {
                map.at(ends[i]).setCell(cells[i]);
                cells[i].setPos(ends[i]);
            }
        }
//
//        for (GameEvent event: validMoveEvents) {
//            Cell cell = ctx.getCell(event.getGameObjectId());
//            Direction dir = Direction.valueOf(event.getArgs()[GameEvent.ARG_INDEX_MOVE_DIRECTION]);
//            Position pos = cell.getPos();
//            Position nextPos = pos.getNextPos(dir);
//            Block nextBlock = map.at(nextPos);
//            if (nextBlock.isEmpty())
//                cell.move(nextPos);
//        }

        // handling gain resource events
        for (GameEvent event: gainResourceEvents) {
            try {
                //System.out.println("@gainResourceEvents for");
                Cell cell = ctx.getCell(event.getGameObjectId());
                if (cell == null)
                    continue;
                Block block = map.at(cell.getPos());
                if (!block.getType().equals(ServerConstants.BLOCK_TYPE_RESOURCE)) {
                    continue;
                }
                //System.out.println(block.getResource());
                if (block.getResource() > 0) {
                    cell.gainResource();
                }
            } catch (Exception ignored) {
            }
        }

        for(Team team : mTeams) {
            int score = 0;
            for (Cell cell : team.getCells()) {
                score += cell.getEnergy();
            }
            team.setScore(score);
        }
        ctx.incTurn();
    }

    @Override
    public void generateOutputs() {
        Map map = ctx.getMap();

        mClientMessages = new ArrayList<>();

        ArrayList<Object> uiDataList = new ArrayList<>();
        //uiDataList.add(mTeams);
        Object[] uiArgs = {ctx.getTurn(), uiDataList};

        //Generate client and team view output
        for (Team team: mTeams) {
            ArrayList<DynamicData> dynamics = new ArrayList<>();
            ArrayList<ObjectDiff> dynamicDiffs = new ArrayList<>();
            ArrayList<StaticData> statics = new ArrayList<>();
            ArrayList<ObjectDiff> staticDiffs = new ArrayList<>();
            ArrayList<Transient> transients = new ArrayList<>();

            ClientTurnData clientTurnData = new ClientTurnData();
            UITurnData uiTurnData = new UITurnData();


            //TODO fill dynamics, statics and transients
            HashSet<Position> visiblePos = new HashSet();
            for (Cell cell: team.getCells()) {
                ctx.getAllPositionsCanSee(visiblePos, cell.getPos(), cell.getDepthOfField());
            }

            for(Position pos : visiblePos)
            {
                Block block = map.at(pos);

                //add dynamics
                if(!block.isEmpty())
                {
                    Cell cell = block.getCell();
                    team.addToCurrentVisibleCells(cell);
                    dynamics.add(cell.getDynamicData());
                    ObjectDiff objectDiff = cell.getTeamViewDiffs(team.getId());
                    if(team.isOpp(cell) && team.findInLastVisibleCells(cell.getId()) == null)
                    {
                        objectDiff.put(ServerConstants.CELL_KEY_VISIBLE, 1);
                    }
                    if(objectDiff.isChanged())
                    {
                        dynamicDiffs.add(objectDiff.clone());
                        objectDiff.clearChanges();
                    }
                }

                //add statics
                if(block.getLastChangeTurn() >= team.getLastVisitTurn(pos))
                {
                    statics.add(block.getStaticData());
                    ObjectDiff objectDiff = block.getTeamViewDiffs(team.getId());
                    if(objectDiff.isChanged())
                    {
                        staticDiffs.add(objectDiff.clone());
                        objectDiff.clearChanges();
                    }
                }

                //add transient


                //visit pos
                team.visitPosition(pos);
            }
//            System.out.println("team " + team.getId() + " current : " + team.getCurrentVisibleCells().size() + " last : " + team.getLastVisibleCells().size());
            for(java.util.Map.Entry entry : team.getLastVisibleCells().entrySet())
            {
                String id = (String)entry.getKey();
                if(team.findInCurrentVisibleCells(id) == null)
                {
                    Cell cell = (Cell) entry.getValue();
                    if(ctx.getDeadCells().get(id) == null)
                    {
                        // a live cell -> should be opponent
                        if(!team.isOpp(cell))
                        {
//                            System.out.println("not dead! not opp!");
                            //What?!
                        }
                        else
                        {
                            //out of view
                            ObjectDiff objectDiff = new ObjectDiff(id);
                            objectDiff.put(ServerConstants.CELL_KEY_VISIBLE, 0);
                            dynamicDiffs.add(objectDiff);
                        }
                    }
                    else
                    {
                        // a dead cell -> team mate or opponent
//                        System.out.println("dead!");
                        ObjectDiff objectDiff = cell.getTeamViewDiffs(team.getId());
                        dynamicDiffs.add(objectDiff.clone());
                        objectDiff.clearChanges();
                    }
                }
            }
            team.moveCurrentToLast();

            clientTurnData.setDynamics(dynamicDiffs);
            clientTurnData.setStatics(staticDiffs);
            clientTurnData.setTransients(transients);

            Object[] clientArgs = {ctx.getTurn(), clientTurnData};

            Message clientMsg = new Message();
            clientMsg.setName(Message.NAME_TURN);
            clientMsg.setArgs(clientArgs);
            mClientMessages.add(clientMsg);

            uiTurnData.setView(ctx.getTeamViewNameById(team.getId()));
            uiTurnData.setDynamics(dynamics);
            uiTurnData.setStatics(statics);
            uiTurnData.setTransients(transients);

            uiDataList.add(uiTurnData);

            /*objects.put(VIEW, "team" + i);
            allObjects.add(objects);*/
        }

        ArrayList<DynamicData> dynamics = new ArrayList<>();
        ArrayList<StaticData> statics = new ArrayList<>();
        ArrayList<Transient> transients = new ArrayList<>();

        UITurnData uiTurnData = new UITurnData();

        //Generate global view outputs
        //TODO fill dynamics, statics and transients
        for (int row = 0; row < map.getHeight(); row++) {
            for (int col = 0; col < map.getWidth(); col++) {
                Block block = map.getBlocks()[row][col];

                //add dynamics
                if(!block.isEmpty())
                {
                    dynamics.add(block.getCell().getDynamicData());
                }

                //add statics
                if(block.getLastChangeTurn() + 1 == ctx.getTurn())
                {
                    statics.add(block.getStaticData());
                }

                //add transient

            }
        }

        uiTurnData.setView( ctx.getGlobalViewName());
        uiTurnData.setDynamics(dynamics);
        uiTurnData.setStatics(statics);
        uiTurnData.setTransients(transients);

        uiDataList.add(uiTurnData);

        mUIMessage = new Message(Message.NAME_TURN, uiArgs);
    }

    @Override
    public Message getUIMessage() {
        return mUIMessage;
    }

    public Message getStatusMessage() {

        HashMap<String, Integer>scores =  new HashMap<>();
        for(Team team : mTeams)
        {
            scores.put(team.getName(), team.getScore());
        }
        Object[] args = new Object[] {scores};

        Message status = new Message(Message.NAME_STATUS, args);
        return status;
    }

    @Override
    public Message[] getClientMessages() {
        return mClientMessages.toArray(new Message[mClientMessages.size()]);
    }

    @Override
    public Event[] makeEnvironmentEvents() {
        /*ArrayList<GameEvent> events = new ArrayList<>();
        Random rnd = new Random();
        for(java.util.Map.Entry<String, Cell> entry : allCells.entrySet())
        {
            Cell c = entry.getValue();
            GameEvent event = new GameEvent(GameEvent.TYPE_MOVE);
            Direction dir = Direction.values()[rnd.nextInt(6)];
            event.setObjectId(c.getId());
            event.setArg(dir.toString(),GameEvent.ARG_INDEX_MOVE_DIRECTION);
            events.add(event);
        }
        GameEvent [] eventsArray = new GameEvent[events.size()];
        eventsArray = events.toArray(eventsArray);
        return eventsArray;*/
        return null;
    }

    @Override
    public boolean isGameFinished() {
        return ctx.getTurn() >= GAME_LONG_TIME_TURN;
    }

    @Override
    public void terminate()
    {
        if(mTeams.length > 0) {
            for (int i = 0; i < mTeams.length; i++) {
                if(i > 0)
                {
                    System.out.print(", ");
                }
                System.out.print(mTeams[i].getScore());
            }
            System.out.println();
        }
    }
}
