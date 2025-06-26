package server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class WhiteboardServer {
    private static final int PORT = 5000;
    private static Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();

    public static void main(String[] args) {
        System.out.println("Server started on port " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler client = new ClientHandler(socket);
                clients.add(client);
                new Thread(client).start();
                broadcastUserList(); // Immediately update user list
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Broadcasts a message to all connected clients
    static synchronized void broadcast(String message) {
        for (ClientHandler client : clients) {
            try {
                client.sendMessage(message);
            } catch (Exception e) {
                System.err.println("Failed to send to client: " + e.getMessage());
            }
        }
    }
 // Sends the updated list of connected users to all clients
    static synchronized void broadcastUserList() {
        List<String> usernames = new ArrayList<>();
        for (ClientHandler client : clients) {
            if (client.getUsername() != null) {
                usernames.add(client.getUsername());
            }
        }
        String userListMessage = "USERLIST " + String.join(",", usernames); // Use comma as separator
        broadcast(userListMessage);
    }
// Removes a client when they disconnect and updates the active user list
    static synchronized void removeClient(ClientHandler client) {
        if (clients.remove(client)) {
            broadcast("CHAT " + client.getUsername() + " left the chat.");
            broadcastUserList();
        }
    }
}

