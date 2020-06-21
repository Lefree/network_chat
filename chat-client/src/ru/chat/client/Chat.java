package ru.chat.client;

import ru.chat.library.Library;
import ru.network.SocketThread;
import ru.network.SocketThreadListener;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class Chat implements SocketThreadListener, ChatListener, Thread.UncaughtExceptionHandler {
    static LoginView loginView;
    static ChatView chatView;
    static JFrame activeView;
    private static final String WINDOW_TITLE = "Chat";

    SocketThread socketThread;
    Socket socket;

    public Chat() {
        Thread.setDefaultUncaughtExceptionHandler(this);
        loginView = new LoginView(this);
        activeView = loginView;
        activeView.setTitle(WINDOW_TITLE);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Chat();
            }
        });
    }

    private void setActiveView(JFrame view) {
        activeView.setVisible(false);
        activeView = view;
        activeView.setVisible(true);
    }

    protected boolean connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            socketThread = new SocketThread("Client", this, socket);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    protected void disconnect() {
        socketThread.close();
    }

    @Override
    public void onSocketStart(SocketThread thread, Socket socket) {
        if (chatView == null)
            chatView = new ChatView(this);
        setActiveView(chatView);

    }

    @Override
    public void onReceiveString(SocketThread thread, Socket socket, String msg) {
        String[] arrFromMsg = msg.split(Library.DELIMITER);
        String msgToLog = msg;
        switch (arrFromMsg[0]) {
            case Library.AUTH_ACCEPT:
                activeView.setTitle(WINDOW_TITLE + ": " + arrFromMsg[1]);
                msgToLog = arrFromMsg[1] + " joined to chat\n";
                break;
            case Library.MSG_FORMAT_ERROR:
                msgToLog = "Incorrect message: " + arrFromMsg[1] + "\n";
                break;
            case Library.TYPE_BROADCAST:
                chatView.putMessage(String.format("%s:\n%s\n", arrFromMsg[2], arrFromMsg[3]));
                break;
            case Library.USERS_LIST:
                String[] users = Arrays.copyOfRange(arrFromMsg, 1, arrFromMsg.length);
                ((ChatView)activeView).userList.setListData(users);
                return;
            default:
                throw new RuntimeException("Unknown msg type");
        }
    }

    @Override
    public void onSocketException(SocketThread thread, Throwable throwable) {
        thread.close();
    }

    @Override
    public void onSocketReady(SocketThread thread, Socket socket) {
        String login = loginView.getLogin();
        String password = loginView.getPassword();
        thread.sendMessage(Library.getAuthRequest(login, password));
    }

    @Override
    public void onSocketStop(SocketThread thread) {
        setActiveView(loginView);
        activeView.setTitle(WINDOW_TITLE);

    }

    @Override
    public void onAuthorize(String host, int port) {
        connect(host, port);
    }

    @Override
    public void onDisconnect() {
        disconnect();
    }

    @Override
    public void onUpdateNickname(String nickname) {
        socketThread.sendMessage(Library.getClientChangeName(nickname));
   }

    @Override
    public void onReadyToSendMsg(String msg) {
        socketThread.sendMessage(Library.getClientMsgBroadcast(msg));
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        throwable.printStackTrace();
        String msg;
        StackTraceElement[] ste = throwable.getStackTrace();
        msg = String.format("Exception in thread \"%s\" %s: %s\n\t at %s",
                thread.getName(), throwable.getClass().getCanonicalName(),
                throwable.getMessage(), ste[0]);
        JOptionPane.showMessageDialog(activeView, msg, "Exception", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }
}
