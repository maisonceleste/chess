package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
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
            case MAKE_MOVE -> makeMove(command.getAuthToken(), command.getGameID(), (ChessMove) command.getMove(), session);
            case RESIGN -> resign(command.getAuthToken(), command.getGameID(), session);
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
            session.getRemote().sendString(new Gson().toJson(serverMessage));
            return;
        }
        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(gameID, authToken, serverMessage);
        ServerMessage loadMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        session.getRemote().sendString(new Gson().toJson(loadMessage));
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
            session.getRemote().sendString(new Gson().toJson(serverMessage));
            return;
        }
        connections.remove(authToken, gameID);
        String message = null;
        try{
            message = service.getUser(authToken) + " has left the game";
        }
        catch( ResponseException ex) {throw new IOException(ex);}
        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(gameID, authToken, serverMessage);
    }

    public void resign(String authToken, int gameID, Session session) throws IOException {
        GameData game = null;
        String username;
        try{
            game = service.getGame(gameID);
            if(game.game() == null){
                throw new ResponseException(400, "The game is already over");
            }
            username = service.getUser(authToken);
            service.resign(gameID, username);
            game = service.getGame(gameID);
            if(!Objects.equals(game.whiteUsername(), username) && !Objects.equals(game.blackUsername(), username)){
                throw new ResponseException(400, "You are observer");
            }
        } catch (ResponseException e) {
            ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, null , "You can't resign");
            session.getRemote().sendString(new Gson().toJson(serverMessage));
            return;
        }
        String message = String.format("%s just moved resigned", username);
        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcastAll(gameID, serverMessage);
    }

    public void makeMove(String authToken, int gameID, ChessMove move, Session session) throws IOException {
        GameData game = null;
        try{
            game = service.getGame(gameID);
            if(game.game()==null){throw new ResponseException(400, "The game is over");}
            String username = service.getUser(authToken);
            service.updateMove(gameID, move);
            game = service.getGame(gameID);
            if(!Objects.equals(game.whiteUsername(), username) && !Objects.equals(game.blackUsername(), username)){
                throw new ResponseException(400, "You are observer");
            }
            if(checkTurn(username, game)){throw new ResponseException(400, "Not your turn");}

        } catch (ResponseException e) {
            ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, null , "Failed to implement move");
            session.getRemote().sendString(new Gson().toJson(serverMessage));
            return;
        }
        ServerMessage loadMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        connections.broadcastAll(gameID, loadMessage);
        ChessGame.TeamColor next = game.game().getTeamTurn();
        String last = (next == ChessGame.TeamColor.WHITE) ? game.blackUsername() : game.whiteUsername();
        ChessPiece.PieceType piece = game.game().getBoard().getPiece(move.getEndPosition()).getPieceType();
        String message = String.format("%s just moved %s. Next turn: %s", last, next, piece);
        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(gameID, authToken, serverMessage);
    }

    private boolean checkTurn(String username, GameData game){
        ChessGame.TeamColor currentTurn = game.game().getTeamTurn();
        if(currentTurn == ChessGame.TeamColor.WHITE){
            return Objects.equals(username, game.whiteUsername());
        }
        else{
            return Objects.equals(username, game.blackUsername());
        }
    }

}
