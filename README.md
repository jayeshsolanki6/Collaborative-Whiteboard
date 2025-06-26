# 🎨 Collaborative Whiteboard – Java Project

A real-time collaborative whiteboard application built using Java. Multiple users can draw, chat, and interact on a shared canvas with real-time updates.

## 👥 Team Members
- Aseem Nihal Dang  
- Jayesh Solanki  
- Titas Mandal

## 🚀 Overview
This project enables users to:
- Draw on a shared canvas in real-time
- Communicate via built-in live chat
- View active participants
- Log user activity into a MySQL database using JDBC

Built using Java Swing for the user interface and socket programming for real-time communication.

---

## 🛠 Features
- ✅ Real-time drawing sync across clients  
- ✅ Color palette for drawing  
- ✅ Eraser tool  
- ✅ Integrated chat panel  
- ✅ Online user list  
- ✅ JDBC integration for storing client names and login timestamps

---

## 💻 Tech Stack
- **Java**  
- **Swing** (for GUI)  
- **TCP Sockets** (for networking)  
- **Multithreading** (for handling multiple clients)  
- **MySQL + JDBC** (for database logging)

---

## 📦 Prerequisites
- Java 8 or higher  
- MySQL Server installed and running  
- MySQL JDBC Connector  
- Stable internet or LAN connection

---

## ⚙️ How to Run
1. Database Setup (JDBC)
- Add MySQL JDBC Connector
- Download from: https://dev.mysql.com/downloads/connector/j/
- Extract the .zip or .tar.gz and locate mysql-connector-java-<version>.jar.

🗃️ Create Database and Table
Open MySQL terminal or GUI and run:

sql
Copy code
```sh
CREATE DATABASE whiteboard;

USE whiteboard;

CREATE TABLE client_names (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    log_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```
### 💪 Server Setup
1. Compile the server code:
   ```sh
   javac server/WhiteboardServer.java
   ```
2. Run the server:
   ```sh
   java server.WhiteboardServer
   ```

### 👥 Client Setup
1. Compile the client code:
   ```sh
   javac client/WhiteboardClient.java
   ```
2. Launch a client:
   ```sh
   java client.WhiteboardClient
   ```
3. Enter your username when prompted to join the session.

---
## 🔧 Usage Guide
### 🌟 Drawing Features
- **Drawing**: Click and drag to draw on the canvas.
- **Change Color**: Click on any color button to switch colors.
- **Eraser Tool**: Click the "E" button to erase portions of your drawing.
- **Clear Canvas**: Implemented in future updates.

### 💬 Chat Features
- Type a message in the chat box and press **Enter** to send.
- Messages are visible to all connected users.

### 👤 User List
- View the list of online users on the right panel.

---
## 🔧 Known Limitations
❌ **No persistent storage** – Drawings disappear once the last client disconnects.  
❌ **No authentication** – Any user can join without verification.  
❌ **Basic error handling** – Limited handling of connection issues.  
