package client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import network.data.ReceivedMessage;
import util.Log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Main controller. Controls execution of the program, e.g. checks time limit of
 * the client, handles incoming messages, controls network operations, etc.
 */
public class Controller {

    /**
     * Logging tag.
     */
    private static final String TAG = "Controller";

    /**
     * Connection details' file encoding.
     */
    private static String detailsEnc = "UTF-8";

    /**
     * Connection details.
     */
    private int port;
    private String host;
    private String token;
    private int retryDelay;

    /**
     * Player of the game.
     */
    private AI client;

    /**
     * Model of the game.
     */
    private Model model;

    /**
     * Client side network.
     */
    private Network network;

    /**
     * Terminator. Controller waits for this object to be notified. Then it will
     * be terminated.
     */
    private final Object terminator;

    /**
     * Name of the file which contains connection details.
     */
    private String settingsFile;

    /**
     * Timer used to limit execution of {@link AI#doTurn}.
     */
    private Timer timer;


    /**
     * Constructor.
     *
     * @param settingsFile    name of the file which contains connection details
     */
    public Controller(String settingsFile) {
        timer = new Timer();
        terminator = new Object();
        this.settingsFile = settingsFile;
    }

    /**
     * Starts a client by connecting to the server and sending a token.
     */
    public void start() {
        try {
            readClientData();
            model = new Model();
            client = new AI();
            network = new Network(this::handleMessage);
            network.setConnectionData(host, port, token);
            while (!network.isConnected()) {
                network.connect();
                Thread.sleep(retryDelay);
            }
            synchronized (terminator) {
                terminator.wait();
            }
            network.terminate();
        } catch (Exception e) {
            Log.i(TAG, "Error while starting client.", e);
        }
    }

    /**
     * Handles incoming message. This method will be called from
     * {@link client.Network} when a new message is received.
     *
     * @param msg    incoming message
     */
    private void handleMessage(ReceivedMessage msg) {
        switch (msg.name) {
            case "turn":
                handleTurnMessage(msg);
                break;
            case "init":
                handleInitMessage(msg);
                break;
            case "shutdown":
                handleShutdownMessage(msg);
                break;
            default:
                Log.i(TAG, "Undefined message received. " + msg.name);
                break;
        }
    }

    /**
     * Handles init message.
     *
     * @param msg    init message
     */
    private void handleInitMessage(ReceivedMessage msg) {
        model.handleInitMessage(msg);
    }

    /**
     * Handles turn message. Gives the message to the model and then executes
     * client's code to do next turn.
     *
     * @param msg    turn message
     */
    private void handleTurnMessage(ReceivedMessage msg) {
        model.handleTurnMessage(msg);
        doTurn();
    }

    /**
     * Handles shutdown message.
     *
     * @param msg    shutdown message
     */
    private void handleShutdownMessage(ReceivedMessage msg) {
        network.terminate();
        System.exit(0);
    }

    /**
     * Starts {@link AI#doTurn} with turn timeout.
     */
    private void doTurn() {
        Thread turn = new Thread() {
            @Override
            public void run() {
                // set timeout
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // interrupt turn thread
                        interrupt();
                        // send result of this turn
                        network.send(model.getClientTurn());
                    }
                }, model.getTurnTimeout());

                // do client's turn
                client.doTurn(model.getWorld());
            }
        };
        turn.start();
    }

    /**
     * Reads data of client from the file {@link #settingsFile}.
     *
     * @throws IOException if an I/O exception occurs during read of the file.
     */
    private void readClientData() throws IOException {
        byte fileData[] = Files.readAllBytes(Paths.get(settingsFile));
        String json = new String(fileData, detailsEnc);
        Gson gson = new Gson();
        JsonObject details = gson.fromJson(json, JsonObject.class);
        host = details.getAsJsonPrimitive("ip").getAsString();
        port = details.getAsJsonPrimitive("port").getAsInt();
        token = details.getAsJsonPrimitive("token").getAsString();
        retryDelay = details.getAsJsonPrimitive("retryDelay").getAsInt();
    }

}
