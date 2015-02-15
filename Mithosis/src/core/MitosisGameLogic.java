package core;

import core.model.*;
import core.model.Cell;
import core.model.Map;
import messages.*;
import util.Constants;
import server.Server;
import server.core.GameLogic;
import server.core.model.ClientInfo;
import server.core.model.Event;
import server.network.data.Message;

import java.io.IOException;
import java.util.*;

/**
 * Created by rajabzz on 2/2/15.
 */
public class MitosisGameLogic implements GameLogic {

    private final long GAME_LONG_TIME_TURN = 100; //TODO can change

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

        ctx = new Context(Constants.TURN_INIT, options[0], RESOURCE_PATH_CLIENTS);

        Map map = ctx.getMap();

        mTeams = ctx.getTeams();
    }

    @Override
    public void init() {

        ArrayList<String> teamsList = new ArrayList<>();
        for (int i = 0; i < mTeams.length; i++) {
            teamsList.add("team" + i);
        }

        ArrayList<String> viewsList = (ArrayList<String>) teamsList.clone();
        viewsList.add(Constants.VIEW_GLOBAL);

        Map map = ctx.getMap();

        MapSize mapSize = new MapSize();
        mapSize.setWidth(map.getWidth());
        mapSize.setHeight(map.getHeight());

        ArrayList<Object> unknownMap = new ArrayList<>();
        int height = map.getHeight();
        int width = map.getWidth();
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                HashMap<String, Object> blockMap = new HashMap<>();
                Block block = map.at(col, row);
                blockMap.put(Constants.GAME_OBJECT_KEY_ID, block.getId());
                blockMap.put(Constants.GAME_OBJECT_KEY_TYPE, Constants.BLOCK_TYPE_NONE);
                blockMap.put(Constants.GAME_OBJECT_KEY_TURN, Constants.TURN_WORLD_CREATION);
                blockMap.put(Constants.GAME_OBJECT_KEY_POSITION, new Position(block.getX(), block.getY()));
                HashMap<String,Object> otherDict = new HashMap<>();
                otherDict.put(Constants.BLOCK_KEY_HEIGHT, 0);
                otherDict.put(Constants.BLOCK_KEY_RESOURCE, 0);
                blockMap.put(Constants.GAME_OBJECT_KEY_OTHER, otherDict);
                unknownMap.add(blockMap);
            }
        }


        //Generate clients init message
        mClientsInitialMessages = new ArrayList<>();
        for (int t = 0; t < mTeams.length; t++)
        {

            //make info
            TeamInfo teamInfo = new TeamInfo(ctx.getClientsInfo()[t].getName(), ctx.getClientsInfo()[t].getID());

            HashMap<String, Object> info = new HashMap<>();
            info.put(Constants.INFO_KEY_TURN, ctx.getTurn());
            info.put(Constants.INFO_KEY_TEAMS, teamsList);
            info.put(Constants.INFO_KEY_YOUR_INFO, teamInfo);
            info.put(Constants.INFO_KEY_MAPSIZE, mapSize);


            //make static diff
            ArrayList<StaticGameObject> staticDiff = new ArrayList<>();

            //make client message
            Message clientMsg = new Message();
            clientMsg.setName(Message.NAME_INIT);
            Object[] args = {info, unknownMap, staticDiff};
            clientMsg.setArgs(args);
            mClientsInitialMessages.add(clientMsg);
        }

        //Generate UI init message

        //make info
        HashMap<String, Object> info = new HashMap<>();
        info.put(Constants.INFO_KEY_TURN, ctx.getTurn());
        info.put(Constants.INFO_KEY_TEAMS, teamsList);
        info.put(Constants.INFO_KEY_VIEWS, viewsList);
        info.put(Constants.INFO_KEY_MAPSIZE, mapSize);

        //make map
            // map is ready

        //make diff
        ArrayList<Object> uiDiff = new ArrayList<>();

            //Generate teams diff
            for (int t = 0; t < mTeams.length; t++)
            {
                HashMap<String,Object> viewDif = new HashMap<>();
                viewDif.put(Constants.VIEW,"team" + t);

                //calculate static diff for each team
                ArrayList<StaticGameObject> staticDiff = new ArrayList<>();


                viewDif.put(StaticGameObject.NAME, staticDiff);
                uiDiff.add(viewDif);
            }


            //Generate Global diff
            {
                HashMap<String, Object> viewDif = new HashMap<>();
                viewDif.put(Constants.VIEW, Constants.VIEW_GLOBAL);

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
        Object[] args = {info, unknownMap, uiDiff};
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
        ArrayList<GameEvent> mitosisEvents = new ArrayList<>();
        ArrayList<GameEvent> moveEvents = new ArrayList<>();
        ArrayList<GameEvent> gainResourceEvents = new ArrayList<>();

        Map map = ctx.getMap();



        if (clientsEvent != null || environmentEvent != null || terminalEvent != null) {
            if (clientsEvent != null)
            {
                for (int i = 0; i < mTeams.length; i++) {
                    if (clientsEvent[i] == null) continue;
                    for(int j = 0; j < clientsEvent[i].length; j++)
                    {
                        GameEvent event = new GameEvent(clientsEvent[i][j]);
                        //event.getGameObjectId() TODO CHECK OWNER
                        if(ctx.getDynamicObject(event.getObjectId()).getTeamId() != i)
                        {
                            continue;
                        }
                        event.setTeamId(i);
                        gameObjectEvents.put(event.getGameObjectId(), event);

                    }
                    //GameEvent[] teamEvent = (GameEvent[]) clientsEvent[i];
                    /*for (GameEvent event: teamEvent) {
                        event.setTeamId(i);
                        if (!gameObjectEvents.containsKey(event.getGameObjectId()))
                            gameObjectEvents.put(event.getGameObjectId(), event);
                    }*/
                }
            }
            if (environmentEvent != null)
            {
                for (GameEvent event: (GameEvent[])environmentEvent) {
                    event.setTeamId(-1);
                    gameObjectEvents.put(event.getGameObjectId(), event);
                }

            }
            if (terminalEvent != null)
            {
                //TODO
                /*for (GameEvent event: (GameEvent[])terminalEvent) {
                    event.setTeamId(-2);
                        gameObjectEvents.put(event.getGameObjectId(), event);
                }*/

            }

            Collection<GameEvent> currentTurnEventsList = gameObjectEvents.values();
            GameEvent[] currentTurnEvents = currentTurnEventsList.toArray(new GameEvent[currentTurnEventsList.size()]);

            for (GameEvent event: currentTurnEvents) {
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
                }
            }
        }

        Collections.shuffle(mitosisEvents);
        for (GameEvent event: mitosisEvents) {
            Cell cell = ctx.getCell(event.getGameObjectId());
            if (cell == null) continue;

            Block block = map.at(cell.getPos());
            if(!block.getType().equals(Constants.BLOCK_TYPE_MITOSIS))
            {
                continue;
            }
            cell.mitosis();
        }

        // handling move events
        Collections.shuffle(moveEvents);
        //System.out.println(moveEvents.size());
        ArrayList<GameEvent> validMoveEvents = new ArrayList<>();
        for (GameEvent event: moveEvents) {
            Cell cell = ctx.getCell(event.getGameObjectId());
            if (cell == null) continue;

            Direction dir = Direction.valueOf(event.getArgs()[GameEvent.ARG_INDEX_MOVE_DIRECTION]);
            Position nextPos = cell.getPos().getNextPos(dir);
            if(!ctx.checkBounds(nextPos))
            {
                continue;
            }
            Block block = map.at(nextPos);

            if (!block.isMovable()){
                continue; // if this block is not movable
            }
            //System.out.println(cell.getId());
            if (block.isEmpty()) {
                validMoveEvents.add(event);
                //cell.move(nextPos);
            }
        }
        for(GameEvent event: validMoveEvents)//TODO
        {
            Cell cell = ctx.getCell(event.getGameObjectId());

            Direction dir = Direction.valueOf(event.getArgs()[GameEvent.ARG_INDEX_MOVE_DIRECTION]);
            Position nextPos = cell.getPos().getNextPos(dir);
            Block block = map.at(nextPos);
            if (block.isEmpty()) {
                cell.move(nextPos);
            }
        }

        // handling gain resource events
        for (GameEvent event: gainResourceEvents) {
            //System.out.println("@gainResourceEvents for");
            Cell cell = ctx.getCell(event.getGameObjectId());
            if (cell == null) continue;

            Block block = map.at(cell.getPos());
            if(!block.getType().equals(Constants.BLOCK_TYPE_RESOURCE))
            {
                continue;
            }
            //System.out.println(block.getResource());
            if(block.getResource() > 0)
            {
                cell.gainResource();
            }
        }

        /*for (GameEvent event: gainResourceEvents) {
            Cell cell = null;
            for (Team team: mTeams) {
                if (team.getCellById(event.getGameObjectId()) != null)
                    cell = team.getCellById(event.getGameObjectId());
            }
            if (cell == null) continue;

            // check if the location of the cell is of type resource
            if (map.at(cell.getPos().x, cell.getPos().y).equals(Block.TYPE_RESOURCE))
                cell.gainResource();
        }*/
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
            ArrayList<StaticData> statics = new ArrayList<>();
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
                    dynamics.add(block.getCell().getDynamicData());
                }

                //add statics
                if(block.getLastChangeTurn() >= team.getLastVisitTurn(pos))
                {
                    statics.add(block.getStaticData());
                }

                //add transient


                //visit pos
                team.visitPosition(pos);
            }



            clientTurnData.setDynamics(dynamics);
            clientTurnData.setStatics(statics);
            clientTurnData.setTransients(transients);

            Object[] clientArgs = {ctx.getTurn(), clientTurnData};

            Message clientMsg = new Message();
            clientMsg.setName(Message.NAME_TURN);
            clientMsg.setArgs(clientArgs);
            mClientMessages.add(clientMsg);

            uiTurnData.setView("team" + team.getId());
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

        uiTurnData.setView( Constants.VIEW_GLOBAL);
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
}
