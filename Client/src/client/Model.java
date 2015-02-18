package client;

import client.model.Map;
import com.google.gson.Gson;
import data.*;
import model.Event;
import network.data.Message;
import network.data.ReceivedMessage;
import util.ServerConstants;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Consumer;

/**
 * Model contains data which describes current state of the game.
 */
public class Model {

    private long turnTimeout = 400;
    private long turnStartTime;
    private LinkedBlockingDeque<Event> eventsToSend;
    private Consumer<Message> sender;
    private World world;

    public Model(Consumer<Message> sender) {
        this.sender = sender;
        eventsToSend = new LinkedBlockingDeque<>();
    }

    public void handleInitMessage(ReceivedMessage msg) {
        Gson gson = new Gson();
        ClientInitInfo initInfo = gson.fromJson(msg.args.get(0), ClientInitInfo.class);

        StaticData[] mapData =  gson.fromJson(msg.args.get(1), StaticData[].class);
        Map map = new Map(initInfo.getMapSize(), mapData);

        //TODO STATIC DIFF

        world = new World(this, initInfo, map);
    }

    public void handleTurnMessage(ReceivedMessage msg) {
        turnStartTime = System.currentTimeMillis();
        Gson gson = new Gson();

        world.setTurn(gson.fromJson(msg.args.get(0), Integer.class));
        ClientTurnData clientTurnData = gson.fromJson(msg.args.get(1), ClientTurnData.class);

        //set statics
        clientTurnData.getStatics().forEach(world::setStaticChange);

        //set dynamics
        world.clearDynamics();
        for(DynamicData d : clientTurnData.getDynamics()) {
            if(d.getType().equals(ServerConstants.GAME_OBJECT_TYPE_CELL)) {
                CellData cd = new CellData(d);
                world.addCell(cd);
            } else {
                //nothing yet!
            }
        }

        //set transients    TODO
    }

    public long getTurnTimeout() {
        return turnTimeout;
    }

    public long getTurnTimePassed() {
        return System.currentTimeMillis() - turnStartTime;
    }

    public long getTurnRemainingTime() {
        return turnTimeout - getTurnTimePassed();
    }

//    public Message getClientTurn() {
//        Event[] tEvents = eventsToSend.toArray(new Event[events.size()]);
//        return new Message("event", tEvents);
//        /*
//        Object [] args = new Object[1];
//        args[0] = gameEvents;
//        return new Message("event", args);
//         */
//    }

    public void addEvent(Event event) {
//        events.add(event);
        Message msg = new Message("event", new Event[] {event});
        sender.accept(msg);
    }

    public World getWorld()
    {
        return world;
    }

}
