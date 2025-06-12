package server.websocket;

import com.google.gson.Gson;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import responseexception.ResponseException;
import service.ChessService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Objects;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private ChessService service = null;

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command.getAuthToken(), command.getGameID(), session);
            case LEAVE -> leave(command.getAuthToken(), command.getGameID(), session);
            //case MAKE_MOVE -> makeMove(action.visitorName());
        }
    }

    public void setService(ChessService service){
        this.service = service;
    }

    public void connect(String authToken, int gameID, Session session) throws IOException {
        connections.add(authToken, gameID, session);
        GameData game = null;
        String message = null;
        try{
            game = service.getGame(gameID);
            if(game == null) {throw new ResponseException(400, "Cannot access the game");}
            String username = service.getUser(authToken);
            if(username == null) {throw new ResponseException(400, "Unauthorized");}
            message = joinMessage(username, game);
        }
        catch( ResponseException ex) {
            ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, null , "Error; could not access game");
            connections.broadcastBack(authToken, serverMessage);
            return;
        }
        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(authToken, serverMessage);
        ServerMessage loadMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        connections.broadcastBack(authToken, loadMessage);
    }

    private String joinMessage(String username, GameData game) throws ResponseException {
        if(Objects.equals(game.blackUsername(), username)){
            return game.blackUsername() + " has joined the game as black";
        }
        else if(Objects.equals(game.whiteUsername(), username)){
            return game.whiteUsername() + " has joined the game as white";
        }
        else{
            return username + " is observing the game";
        }
    }

    public void leave(String authToken, int gameID, Session session) throws IOException {
        try{
        service.leave(gameID, authToken);
        } catch (ResponseException e) {
            ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, null , "Error; could not access game");
            connections.broadcastBack(authToken, serverMessage);
            return;
        }
        connections.remove(authToken);
        String message = null;
        try{
            message = service.getUser(authToken) + " has left the game";
        }
        catch( ResponseException ex) {throw new IOException(ex);}
        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(authToken, serverMessage);
//        message = "You have successfully left the game";
//        serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
//        connections.broadcastBack(authToken, serverMessage);
    }

}
