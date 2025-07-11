package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import results.CreateResult;
import results.ListResult;
import results.LoginResult;
import results.RegisterResult;
import org.mindrot.jbcrypt.BCrypt;
import responseexception.ResponseException;
import dataaccess.DataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;

public class ChessService {

    public final DataAccess dataAccess;

    public ChessService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public RegisterResult register(String username, String password, String email) throws ResponseException {
        if(username==null || password==null || email==null){
            throw new ResponseException(400, "Error: bad request");
        }
        if(dataAccess.getUser(username) != null){
            throw new ResponseException(403, "Error: already taken");
        }
        else{
            password = encryptPassword(password);
            dataAccess.createUser(username, password, email);
            AuthData data = dataAccess.createAuth(username);
            return new RegisterResult(data.username(), data.authToken());
        }

    }

    public LoginResult login(String username, String password) throws ResponseException {
        if(username==null || password==null){
            throw new ResponseException(400, "Error: bad request");
        }
        UserData userData = dataAccess.getUser(username);
        if(userData==null || !verifyPassword(userData.password(), password)){
            throw new ResponseException(401, "Error: Unauthorized");
        }
        AuthData auth = dataAccess.createAuth(username);
        return new LoginResult(username, auth.authToken());
    }

    public boolean logout(String authID) throws ResponseException {
        if(dataAccess.getAuth(authID)==null){
            throw new ResponseException(401, "Error: Unauthorized");
        }
        dataAccess.deleteAuth(authID);
        return true;
    }

    public boolean clear() throws ResponseException {
        dataAccess.deleteAll();
        return true;
    }

    public CreateResult create(String authID, String gameName) throws ResponseException {
        if(gameName==null){
            throw new ResponseException(400, "Error: bad request");
        }
        if(dataAccess.getAuth(authID)==null){
            throw new ResponseException(401, "Error: Unauthorized");
        }
        GameData game =dataAccess.createGame(gameName);
        return new CreateResult(game.gameID());

    }

    public ListResult list(String authID) throws ResponseException {
        if(dataAccess.getAuth(authID)==null){
            throw new ResponseException(401, "Error: Unauthorized");
        }
        return new ListResult(dataAccess.listGames());
    }

    public boolean join(String color, int gameID, String authID) throws ResponseException{
        if(color!=null){color = color.toUpperCase();}
        if(color==null||(!color.equals("BLACK") && !color.equals("WHITE"))|| gameID == 0 || authID == null){
            throw new ResponseException(400, "Error: Bad Request");
        }
        AuthData auth = dataAccess.getAuth(authID);
        if(auth==null){
            throw new ResponseException(401, "Error: Unauthorized");
        }
        GameData game = dataAccess.getGame(gameID);
        if(game==null){throw new ResponseException(400, "Error: Bad Request");}
        if(color.equals("WHITE") && game.whiteUsername()!=null){throw new ResponseException(403, "Error: already taken");}
        if(color.equals("BLACK") && game.blackUsername()!=null){throw new ResponseException(403, "Error: already taken");}
        String username = auth.username();
        dataAccess.updateGame(color, gameID, username);
        return true;
    }

    public void leave(int gameID, String authID) throws ResponseException {
        GameData game = dataAccess.getGame(gameID);
        String username = getUser(authID);
        String color = null;
        if(game.blackUsername()!=null && game.blackUsername().equals(username)){color="BLACK";}
        else if(game.whiteUsername()!=null && game.whiteUsername().equals(username)){color="WHITE";}
        else{return;}
        dataAccess.updateGame(color, gameID, null);
    }

    public void updateMove(int gameID, ChessMove move) throws ResponseException {
        ChessGame game = getGame(gameID).game();
        try {
            game.makeMove(move);
        } catch (InvalidMoveException e) {
            throw new ResponseException(400, "You can't make this move");
        }
        dataAccess.updateMove(game, gameID);
    }

    public void resign(int gameID, String username) throws ResponseException{
        dataAccess.updateMove(null, gameID);
    }

    public GameData getGame(int gameID) throws ResponseException {
        return dataAccess.getGame(gameID);
    }

    public String getUser(String authID) throws ResponseException {
        if(dataAccess.getAuth(authID)==null){
            throw new ResponseException(401, "Error: Unauthorized");
        }
        return dataAccess.getAuth(authID).username();
    }

    private String encryptPassword(String clearTextPassword){
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
    }

    private boolean verifyPassword(String hashedPassword, String providedPassword){
        return BCrypt.checkpw(providedPassword, hashedPassword);
    }

}
