package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import requests.JoinRequest;
import responseexception.ResponseException;
import service.ChessService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private ChessService service = null;

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command.getAuthToken(), command.getGameID(), command.getRequest(), session);
            case LEAVE -> leave(command.getAuthToken(), command.getGameID(), command.getRequest(), session);
            //case MAKE_MOVE -> makeMove(action.visitorName());
        }
    }

    public void setService(ChessService service){
        this.service = service;
    }

    public void connect(String authToken, int gameID, Object request, Session session) throws IOException {
        if(!(request instanceof JoinRequest)){
            throw new IOException("Failed to join the game");
        }
        JoinRequest joinRequest = (JoinRequest) request;
        connections.add(authToken, gameID, session);
        GameData game = null;
        String message = null;
        try{
            game = service.getGame(gameID);
            message = joinMessage(joinRequest, game);
        }
        catch( ResponseException ex) {throw new IOException();}
        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(authToken, serverMessage);
    }

    private String joinMessage(JoinRequest request, GameData game) throws ResponseException {
        if(request.playerColor().equalsIgnoreCase("BLACK")){
            return game.blackUsername() + " has joined the game as black";
        }
        else if(request.playerColor().equalsIgnoreCase("WHITE")){
            return game.whiteUsername() + " has joined the game as white";
        }
        else{
            return service.getUser(request.authID()) + " is observing the game";
        }
    }

    public void leave(String authToken, int gameID, Object request, Session session) throws IOException {
        if(!JoinRequest.class.isInstance(request)){
            throw new IOException("Failed to leave the game");
        }
        JoinRequest leaveRequest = (JoinRequest) request;
        try{
        service.leave(leaveRequest.playerColor(), leaveRequest.gameID(), leaveRequest.authID());
        } catch (ResponseException e) {
            throw new IOException(e);
        }
        connections.remove(authToken);
        String message = null;
        try{
            message = service.getUser(leaveRequest.authID()) + " has left the game";
        }
        catch( ResponseException ex) {throw new IOException(ex);}
        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(authToken, serverMessage);
    }

}
