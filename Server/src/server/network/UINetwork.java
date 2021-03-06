package server.network;

import network.data.Message;
import network.JsonSocket;
import util.Log;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * {@link server.network.UINetwork} is a server which is responsible for sending
 * UI data to the <code>node.js</code> client.
 * <p>
 * When a client is connected to the server, it sends a token and waits for the
 * initial message which contains necessary data of beginning of the game.
 * <p>
 * Messages are sent using methods {@link #sendBlocking} or {@link #sendNonBlocking}.
 * <p>
 * The communications are one sided, i.e. everything which is sent by client is
 * ignored by the server.
 */
public final class UINetwork extends NetServer {

    /**
     * Logging tag
     */
    private static String TAG = "UINetwork";

    /**
     * token of the server
     */
    private final String mToken;

    /**
     * current client of the server
     */
    private JsonSocket mClient;

    /**
     * Lock for {@link #mClient}
     */
    private final Lock mClientLock;

    /**
     * Notifies waiters when a new client is connected.
     */
    private final Object clientNotifier;

    /**
     * thread executor which is used to accept clients
     */
    private ExecutorService executor;

    /**
     * Thread executor which is used to send messages.
     */
    private ExecutorService sendExecutor;

    /**
     * Initializes the class and starts sending messages to clients.
     * If there is no client at the time of sending, the message will be
     * thrown away.
     *
     * @param token    token of the server
     * @see #sendBlocking
     * @see #sendNonBlocking
     * @see #hasClient
     * @see #waitForClient
     * @see #waitForNewClient
     */
    public UINetwork(String token) {
        mToken = token;
        clientNotifier = new Object();
        mClientLock = new ReentrantLock(true);
    }

    /**
     * Sends a message to the client.
     * Caller method will be blocked until the message is sent.
     *
     * @param   msg     message to send
     */
    public void sendBlocking(Message msg) {
        try {
            mClient.send(msg);
        } catch (IOException e) {
            Log.d(TAG, "Message sending failure.", e);
        }
    }

    /**
     * Sends a message to the client.
     * Caller method wont be blocked.
     *
     * @param   msg       message to send
     * @see #sendBlocking
     */
    public void sendNonBlocking(Message msg) {
        sendExecutor.submit(() -> sendBlocking(msg));
    }

    /**
     * Creates a new thread to verify the client by taking a token.
     *
     * @param client    a {@link network.JsonSocket} which is connected
     * @see server.network.NetServer#accept
     */
    @Override
    protected void accept(JsonSocket client) {
        executor.submit(() -> {
            boolean valid = false;
            try {
                valid = verifyClient(client);
            } catch (Exception e) {
                valid = false;
            }
            if (valid) {
                changeClient(client);
            } else {
                Log.i(TAG, "Client rejected.");
                try {
                    client.close();
                } catch (Exception ignored) {
                }
            }
        });
    }

    /**
     * Verifies the client by taking a token.
     *
     * @param client    client
     * @throws Exception if verification is failed
     * @see #accept
     */
    private boolean verifyClient(JsonSocket client) throws Exception {
        Future<Message> futureMessage
                = executor.submit(() -> client.get(Message.class));
        Message token = futureMessage.get(1000, TimeUnit.SECONDS);
        return token != null && "token".equals(token.name) && token.args != null
                && token.args.length >= 1 && mToken.equals(token.args[0]);
    }

    /**
     * Changes current client to the specified client.
     * It actually closes the previous client (if exists) and then creates a
     * thread for the new one.
     *
     * @param client    new client
     * @see #verifyClient
     */
    private void changeClient(JsonSocket client) {
        mClientLock.lock();
        try {
            // close previous socket
            if (mClient != null)
                mClient.close();
        } catch (Exception e) {
            Log.i(TAG, "Socket closing failure.", e);
        } finally {
            // change the client
            mClient = client;
            // notify waiting threads
            synchronized (clientNotifier) {
                clientNotifier.notifyAll();
            }
            mClientLock.unlock();
        }
    }

    @Override
    public synchronized void listen(int port) {
        executor = Executors.newCachedThreadPool();
        sendExecutor = Executors.newSingleThreadExecutor();
        super.listen(port);
    }

    @Override
    public synchronized void terminate() {
        super.terminate();
        executor.shutdown();
        executor = null;
        sendExecutor.shutdown();
        sendExecutor = null;
    }

    /**
     * Returns true if any client is connected and verified by the server.
     *
     * @return true if there is any clients
     */
    public boolean hasClient() {
        return mClient != null;
    }

    /**
     * Caller will be blocked until a client is connected.
     * If currently a client is connected, returns without waiting.
     *
     * @throws InterruptedException if the current thread is interrupted.
     */
    public void waitForClient() throws InterruptedException {
        synchronized (clientNotifier) {
            if (hasClient())
                return;
            clientNotifier.wait();
        }
    }

    /**
     * Caller will be blocked until a client is connected or the
     * timeout is reached.
     * If currently a client is connected, returns without waiting.
     *
     * @param timeout   timeout in seconds
     * @throws InterruptedException if the current thread is interrupted.
     */
    public void waitForClient(long timeout) throws InterruptedException {
        synchronized (clientNotifier) {
            if (hasClient())
                return;
            clientNotifier.wait(timeout);
        }
    }

    /**
     * Caller will be blocked until a <b>new</b> client is connected.
     *
     * @throws InterruptedException if the current thread is interrupted.
     */
    public void waitForNewClient() throws InterruptedException {
        synchronized (clientNotifier) {
            clientNotifier.wait();
        }
    }

    /**
     * Caller will be blocked until a <b>new</b> client is connected or the
     * timeout is reached.
     *
     * @param timeout   timeout in seconds
     * @throws InterruptedException if the current thread is interrupted.
     */
    public void waitForNewClient(long timeout) throws InterruptedException {
        synchronized (clientNotifier) {
            clientNotifier.wait(timeout);
        }
    }

}