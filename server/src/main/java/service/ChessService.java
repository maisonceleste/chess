package service;

import ResponseException.ResponseException;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;

public class ChessService {

    private final DataAccess dataAccess;

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
            dataAccess.createUser(username, password, email);
            AuthData data = dataAccess.createAuth(username);
            return new RegisterResult(data.username(), data.authToken());
        }

    }

    public LoginResult login(String username, String password) throws ResponseException {
        UserData userData = dataAccess.getUser(username);
        if(!userData.password().equals(password)){
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

    public boolean join(String color, int gameID, String authID) throws ResponseException {
        AuthData auth = dataAccess.getAuth(authID);
        if(auth==null){
            throw new ResponseException(401, "Error: Unauthorized");
        }
        String username = auth.username();
        dataAccess.updateGame(color, gameID, username);
        return true;
    }

}
