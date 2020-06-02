package ru.chat.client;

import ru.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Paths;


public class ChatView extends JFrame implements ActionListener {
    private static final String logPath = Paths.get(".").normalize().toAbsolutePath()
        + "/log/history.log";
    private static final int WIDTH = 400;
    private static final int HEIGHT = 300;

    private final JTextArea log = new JTextArea();

    private final JPanel panelBottom = new JPanel(new BorderLayout());
    private final JButton btnDisconnect = new JButton("Disconnect");
    private final JTextField tfMessage = new JTextField();
    private final JButton btnSend = new JButton("Send");

    private final JList<String> userList = new JList<>();
    ChatListener listener;

    protected ChatView(ChatListener listener) {
        this.listener = listener;
        Thread.setDefaultUncaughtExceptionHandler((Chat)listener);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(WIDTH, HEIGHT);
        log.setEditable(false);
        JScrollPane scrollog = new JScrollPane(log);
        JScrollPane scrollUser = new JScrollPane(userList);
        String[] users = {"user1", "user2", "user3", "user4", "user5",
            "user_with_an_exceptionally_long_name_in_this_chat"};
        userList.setListData(users);
        scrollUser.setPreferredSize(new Dimension(100, 0));
        btnSend.addActionListener(this);
        btnSend.setBackground(Color.green);
        btnDisconnect.setBackground(Color.RED);
        tfMessage.addActionListener(this);
        btnDisconnect.addActionListener(this);

        panelBottom.add(btnDisconnect, BorderLayout.WEST);
        panelBottom.add(tfMessage, BorderLayout.CENTER);
        panelBottom.add(btnSend, BorderLayout.EAST);

        add(scrollog, BorderLayout.CENTER);
        add(scrollUser, BorderLayout.EAST);
        add(panelBottom, BorderLayout.SOUTH);

        setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (source == tfMessage || source == btnSend) {
            if ("".equals(tfMessage.getText())) return;
            listener.onReadyToSendMsg(tfMessage.getText());
            tfMessage.setText(null);
            tfMessage.grabFocus();
        } else if (source == btnDisconnect) {
            listener.onDisconnect();
        }
        else {
            throw new RuntimeException("Unknown source: " + source);
        }
    }

    public void putMessage(String msg) {
        Utils.logging(logPath, msg);
        log.append(msg);
    }
}
