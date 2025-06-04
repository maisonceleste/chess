package service;

import Results.CreateResult;
import Results.ListResult;
import Results.LoginResult;
import Results.RegisterResult;
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

    private String encryptPassword(String clearTextPassword){
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
    }

    private boolean verifyPassword(String hashedPassword, String providedPassword){
        return BCrypt.checkpw(providedPassword, hashedPassword);
    }

}
