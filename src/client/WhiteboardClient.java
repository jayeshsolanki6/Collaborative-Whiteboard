package client;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class WhiteboardClient {
    private Socket socket;
    private PrintWriter out;
    private JTextArea chatArea;
    private JTextField chatField;
    private DefaultListModel<String> clientListModel;
    private DrawPanel drawPanel;

    public WhiteboardClient(String serverAddress, int port, String userName) {
        try {
            socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);

            JFrame frame = new JFrame("Collaborative Whiteboard - " + userName);
            frame.setSize(800, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel mainPanel = new JPanel(new BorderLayout());

            // Whiteboard drawing panel
            drawPanel = new DrawPanel(out);
            mainPanel.add(drawPanel, BorderLayout.CENTER);

            // Side Panel (Chat + Clients)
            JPanel sidePanel = new JPanel(new BorderLayout());
            sidePanel.setPreferredSize(new Dimension(200, 0));

            // Client List Panel
            clientListModel = new DefaultListModel<>(); //dynamic data storage
            JList<String> clientList = new JList<>(clientListModel);
            clientList.setBorder(BorderFactory.createTitledBorder("Online Users"));
            sidePanel.add(new JScrollPane(clientList), BorderLayout.NORTH);

            // Chat History
            chatArea = new JTextArea();
            chatArea.setEditable(false);
            chatArea.setLineWrap(true);
            chatArea.setBorder(BorderFactory.createTitledBorder("Chat"));
            sidePanel.add(new JScrollPane(chatArea), BorderLayout.CENTER);

            // Chat Input Box
            chatField = new JTextField();
            chatField.addActionListener(e -> sendMessage(userName));
            sidePanel.add(chatField, BorderLayout.SOUTH);

            mainPanel.add(sidePanel, BorderLayout.EAST);
            frame.add(mainPanel);
            frame.setVisible(true);

            // Start listening for messages
            new Thread(this::listenForMessages).start();

            // Send join notification
            out.println(userName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String userName) {
        String message = chatField.getText().trim();
        if (!message.isEmpty()) {
            out.println("CHAT " + userName + ": " + message);
            chatField.setText("");
        }
    }

    private void listenForMessages() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("CHAT ")) {
                    String chatMessage = line.substring(5);
                    SwingUtilities.invokeLater(() -> {
                        chatArea.append(chatMessage + "\n");
                        chatArea.setCaretPosition(chatArea.getDocument().getLength()); // Auto-scroll
                    });
                } else if (line.startsWith("DRAW ")) {
                    drawPanel.processDrawMessage(line.substring(5));
                } else if (line.startsWith("USERLIST ")) {
                    String[] users = line.substring(9).split(","); // Split by comma
                    SwingUtilities.invokeLater(() -> {
                        clientListModel.clear();
                        for (String user : users) {
                            if (!user.isEmpty()) {
                                clientListModel.addElement(user); // username added to clientlist
                            }
                        }
                    });
                }
            }
        } catch (IOException e) {
            SwingUtilities.invokeLater(() -> {
                chatArea.append("Disconnected from server.\n");
            });
        } finally {
            try {
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        String serverAddress = "localhost";
        int port = 5000;
        String userName = JOptionPane.showInputDialog("Enter your name:");
        if (userName != null && !userName.trim().isEmpty()) {
            new WhiteboardClient(serverAddress, port, userName);
        }
    }
}
