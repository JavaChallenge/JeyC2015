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
    private static Gson gson = new Gson();
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
        ClientInitInfo initInfo = gson.fromJson(msg.args.get(0), ClientInitInfo.class);


        ReceivedObjectDiff[] mapData =  gson.fromJson(msg.args.get(1), ReceivedObjectDiff[].class);
        Map map = new Map(initInfo.getMapSize(), mapData);

        //TODO STATIC DIFF

        world = new World(this, initInfo, map);
    }

    public void handleTurnMessage(ReceivedMessage msg) {
        turnStartTime = System.currentTimeMillis();

        world.setTurn(gson.fromJson(msg.args.get(0), Integer.class));
        ReceivedClientTurnData clientTurnData = gson.fromJson(msg.args.get(1), ReceivedClientTurnData.class);

        //set statics
        clientTurnData.getStatics().forEach(world::setStaticChange);

        //set dynamics
        clientTurnData.getDynamics().forEach(world::setDynamicChange);

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

    public void addEvent(Event event) {
//        events.add(event);
        if(world.getMyCellsHashMap().get(event.getObjectId()) != null) {
            Message msg = new Message("event", new Event[]{event});
            sender.accept(msg);
        }
    }

    public World getWorld()
    {
        return world;
    }

    public static Gson getGson()
    {
        return gson;
    }

}
