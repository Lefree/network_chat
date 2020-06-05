package ru.chat.server.core;

import ru.chat.library.Library;
import ru.network.ServerSocketThread;
import ru.network.ServerSocketThreadListener;
import ru.network.SocketThread;
import ru.network.SocketThreadListener;

import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Vector;

public class Server implements ServerSocketThreadListener, SocketThreadListener {

    final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.y HH:mm:ss");
    ServerSocketThread server;
    ServerListener listener;
    Vector<SocketThread> connectedUsers = new Vector<>();

    public Server(ServerListener listener) {
        this.listener = listener;
    }

    public void start(int port) {
        if (server == null || !server.isAlive()) {
            server = new ServerSocketThread(this, "Chat server", port, 2000);
        } else {
            putLog("Server already started");
        }
    }

    public void stop() {
        if (server == null || !server.isAlive()) {
            putLog("Server is not running");
        } else {
            server.interrupt();
        }
    }

    private void putLog(String msg) {
        listener.onServerMessage(msg);
    }
    /**
     * Server Socket Thread Listener Methods
     */

    @Override
    public void onServerStart(ServerSocketThread thread) {
        Database.connect();
        putLog("Server started");
    }

    @Override
    public void onServerStop(ServerSocketThread thread) {
        putLog("Server stopped");
        Database.disconnect();
    }

    @Override
    public void onServerCreated(ServerSocketThread thread, ServerSocket server) {
        putLog("Server created");
    }

    @Override
    public void onSocketAccepted(ServerSocketThread thread, ServerSocket server, Socket socket) {
        putLog("Client connected");
        String name = "Socket thread " + socket.getInetAddress() + ":" + socket.getPort();
        new ClientThread(name, this, socket);
    }

    @Override
    public void onServerException(ServerSocketThread thread, Throwable throwable) {
        throwable.printStackTrace();
    }

    /**
     * Socket Thread Listener Methods
     */

    @Override
    public void onServerTimeoutException(ServerSocketThread thread, ServerSocket server) {
        putLog("Ping? Pong!");
    }

    @Override
    public void onSocketStart(SocketThread thread, Socket socket) {
        putLog("Client connected");
    }

    @Override
    public void onReceiveString(SocketThread thread, Socket socket, String msg) {
        ClientThread client = (ClientThread) thread;
        if (client.isAuthorized()) {
            handleAuthMessage(client, msg);
        } else {
            handleNotAuthMessage(client, msg);
        }
    }

    private void handleNotAuthMessage(ClientThread client, String msg) {
        String[] arr = msg.split(Library.DELIMITER);
        if (arr.length != 3 || !arr[0].equals(Library.AUTH_REQUEST)) {
            client.msgFormatError(msg);
            return;
        }
        String login = arr[1];
        String password = arr[2];
        String nickname = Database.getNickname(login, password);
        if (nickname == null) {
            client.authFail();
            return;
        }
        client.authAccept(nickname);
    }

    private void handleAuthMessage(ClientThread client, String msg) {
        sendToAllAuthorizedClients(client, msg);
    }

    private void sendToAllAuthorizedClients(ClientThread thread, String msg) {
        for(SocketThread th : connectedUsers) {
            ClientThread clientThread = (ClientThread) th;
            if (clientThread.isAuthorized())
                clientThread.sendMessage(String.format("%s:\n%s\n", thread.getNickname(), msg));
        }
    }

    @Override
    public void onSocketException(SocketThread thread, Throwable throwable) {
        throwable.printStackTrace();
        thread.close();
    }

    @Override
    public void onSocketReady(SocketThread thread, Socket socket) {
        connectedUsers.add(thread);
        putLog("Client ready to chat");
    }

    @Override
    public void onSocketStop(SocketThread thread) {
        connectedUsers.remove(thread);
        putLog("Client disconnected");
    }
}
