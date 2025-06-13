package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, ConcurrentHashMap<String, Connection>> connections = new ConcurrentHashMap<>();

    public void add(String authToken, int gameID, Session session) {
        var connection = new Connection(authToken, gameID,  session);
        if(!connections.containsKey(gameID)){
            connections.put(gameID, new ConcurrentHashMap<String, Connection>());
        }
        connections.get(gameID).put(authToken, connection);
    }

    public void remove(String authToken, int gameID) {
        var gameConnections = connections.get(gameID);
        if (gameConnections != null) {
            gameConnections.remove(authToken);
            if (gameConnections.isEmpty()) {
                connections.remove(gameID);
            }
        }
    }

    public void broadcast(int gameID, String excludeAuthToken, ServerMessage serverMessage) throws IOException {
        var removeList = new ArrayList<Connection>();
        String message = new Gson().toJson(serverMessage);
        for (var c : connections.get(gameID).values()) {
            if (c.session.isOpen()) {
                if (!c.authToken.equals(excludeAuthToken)) {
                    c.send(message);
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.authToken);
        }
    }

    public void broadcastAll(int gameID, ServerMessage serverMessage) throws IOException {
        var removeList = new ArrayList<Connection>();
        String message = new Gson().toJson(serverMessage);
        for (var c : connections.get(gameID).values()) {
            if (c.session.isOpen()) {
                    c.send(message);
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.authToken);
        }
    }

//    public void broadcastBack(String authToken, ServerMessage serverMessage) throws IOException {
//        String message = new Gson().toJson(serverMessage);
//        var connection = connections.get(authToken);
//        if (connection.session.isOpen()) {
//            connection.send(message);
//        }
//
//    }
}
