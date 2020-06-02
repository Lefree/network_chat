package ru.chat.server.core;

import ru.network.ServerSocketThread;
import ru.network.ServerSocketThreadListener;
import ru.network.SocketThread;
import ru.network.SocketThreadListener;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server implements ServerSocketThreadListener, SocketThreadListener {

    ServerSocketThread server;
    Vector<SocketThread> connectedUsers = new Vector<>();

    public void start(int port) {
        if (server == null || !server.isAlive()) {
            server = new ServerSocketThread(this, "Chat server", port, 2000);
        } else {
            System.out.println("Server already started");
        }
    }

    public void stop() {
        if (server == null || !server.isAlive()) {
            System.out.println("Server is not running");
        } else {
            server.interrupt();
        }
    }

    private void putLog(String msg) {
        System.out.println(msg);
    }
    /**
     * Server Socket Thread Listener Methods
     */

    @Override
    public void onServerStart(ServerSocketThread thread) {
        putLog("Server started");
    }

    @Override
    public void onServerStop(ServerSocketThread thread) {
        putLog("Server stopped");
    }

    @Override
    public void onServerCreated(ServerSocketThread thread, ServerSocket server) {
        putLog("Server created");
    }

    @Override
    public void onSocketAccepted(ServerSocketThread thread, ServerSocket server, Socket socket) {
        putLog("Client connected");
        String name = "Socket thread " + socket.getInetAddress() + ":" + socket.getPort();
        new SocketThread(name, this, socket);
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
        connectedUsers.add(thread);
        putLog("Client connected");
    }

    @Override
    public void onReceiveString(SocketThread thread, Socket socket, String msg) {
        for(SocketThread th : connectedUsers)
            //TODO: заменить имя потока на нормальный юзернем
            th.sendMessage(String.format("%s:\n%s\n", thread.getName(), msg));
    }

    @Override
    public void onSocketException(SocketThread thread, Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onSocketReady(SocketThread thread, Socket socket) {
        putLog("Client ready to chat");
    }

    @Override
    public void onSocketStop(SocketThread thread) {
        connectedUsers.remove(connectedUsers.indexOf(thread));
        putLog("Client disconnected");
    }
}
