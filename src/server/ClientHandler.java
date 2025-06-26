package server;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(),  true);

            username = in.readLine(); // First message is username
            if (username == null) {
                throw new IOException("Username not received.");
            }

            // Logging Username in Database
            logClientName(username);

            WhiteboardServer.broadcast("CHAT " + username + " joined the chat.");
            WhiteboardServer.broadcastUserList(); // Send updated list

            String message;
            while ((message = in.readLine()) != null) {
                WhiteboardServer.broadcast(message);
            }
        } catch (IOException e) {
            System.err.println("Client disconnected: " + e.getMessage());
        } finally {
            WhiteboardServer.removeClient(this);
            try {
                if (socket != null) socket.close();
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }
     // JDBC: Logging Client Name And Time
    private void logClientName(String clientName) {
        String jdbcURL = "jdbc:mysql://localhost:3306/whiteboard";
        String dbUser = "root";
        String dbPassword = "aseem";
        String sql = "INSERT INTO client_names (name, log_time) VALUES (?, NOW())";

        try (Connection conn = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, clientName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error logging client name: " + e.getMessage());
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}
