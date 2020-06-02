package ru.chat.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView extends JFrame implements ActionListener {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 200;

    private final JTextField tfIPAddress = new JTextField("127.0.0.1");
    private final JTextField tfPort = new JTextField("8000");
    private final JCheckBox cbAlwaysOnTop = new JCheckBox("Always on top");
    private final JTextField tfLogin = new JTextField("Alex");
    private final JPasswordField tfPassword = new JPasswordField("123");
    private final JButton  btnLogin = new JButton("Login");

    private final JLabel ipLabel = new JLabel("Host address");
    private final JLabel portLabel = new JLabel("Port");
    private final JLabel loginLabel = new JLabel("Login");
    private final JLabel passwordLabel = new JLabel("Password");

    private final JPanel panel = new JPanel(new GridLayout(4, 2));
    private final JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
    ChatListener listener;

    protected LoginView(ChatListener listener) {
        this.listener = listener;
        Thread.setDefaultUncaughtExceptionHandler((Chat)listener);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setSize(WIDTH, HEIGHT);
        setBackground(Color.LIGHT_GRAY);

        panel.add(ipLabel);
        panel.add(tfIPAddress);
        panel.add(portLabel);
        panel.add(tfPort);
        panel.add(loginLabel);
        panel.add(tfLogin);
        panel.add(passwordLabel);
        panel.add(tfPassword);
        bottomPanel.add(cbAlwaysOnTop);
        btnLogin.setBackground(Color.GREEN);
        btnLogin.addActionListener(this);
        bottomPanel.add(btnLogin);
        add(panel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (source == btnLogin) {
            listener.onAuthorize(tfIPAddress.getText(),
                    Integer.parseInt(tfPort.getText()), tfLogin.getText());
        }
        else if (source == cbAlwaysOnTop) {
            setAlwaysOnTop(cbAlwaysOnTop.isSelected());
        }
        else {
            throw new RuntimeException("Unknown source: " + source);
        }
    }
}
