package service;

import ResponseException.ResponseException;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

public class ChessService {

    private final DataAccess dataAccess;

    public ChessService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public RegisterResult register(String username, String password, String email) throws ResponseException {
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

}
