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

    private final JPanel nickNamePanel = new JPanel(new GridLayout(1, 3));
    private final JLabel updateNicknameLabel = new JLabel("Update nick");
    private final JTextField nickname = new JTextField(new String());
    private final JButton updateNickname = new JButton("Change");
    private final JPanel panelBottom = new JPanel(new BorderLayout());
    private final JButton btnDisconnect = new JButton("Disconnect");
    private final JTextField tfMessage = new JTextField();
    private final JButton btnSend = new JButton("Send");

    protected final JList<String> userList = new JList<>();
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
        userList.setListData(new String[0]);
        scrollUser.setPreferredSize(new Dimension(100, 0));
        btnSend.addActionListener(this);
        btnSend.setBackground(Color.green);
        btnDisconnect.setBackground(Color.RED);
        tfMessage.addActionListener(this);
        btnDisconnect.addActionListener(this);
        updateNickname.addActionListener(this);

        nickNamePanel.add(updateNicknameLabel);
        nickNamePanel.add(nickname);
        nickNamePanel.add(updateNickname);

        panelBottom.add(btnDisconnect, BorderLayout.WEST);
        panelBottom.add(tfMessage, BorderLayout.CENTER);
        panelBottom.add(btnSend, BorderLayout.EAST);

        add(nickNamePanel, BorderLayout.NORTH);
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
        } else if (source == updateNickname) {
            listener.onUpdateNickname(nickname.getText());
            nickname.setText("");
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
