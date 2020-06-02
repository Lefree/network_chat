package ru.chat.client;

public interface ChatListener {

    public void onAuthorize(String host, int port, String username);
    public void onDisconnect();
    public void onReadyToSendMsg(String msg);
}