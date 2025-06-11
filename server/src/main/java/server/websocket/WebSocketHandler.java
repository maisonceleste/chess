package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import requests.JoinRequest;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command.getAuthToken(), command.getGameID(), command.getRequest(), session);
            //case MAKE_MOVE -> makeMove(action.visitorName());
        }
    }

    public void connect(String authToken, int gameID, Object request, Session session) throws IOException {
        if(!JoinRequest.class.isInstance(request)){
            throw new IOException("Failed to join the game");
        }
        JoinRequest joinRequest = (JoinRequest) request;
        connections.add(authToken, gameID, session);
        var message = "Someone joined the game but I'm not telling you who because I haven't coded it yet";
        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(authToken, serverMessage);
    }


}
