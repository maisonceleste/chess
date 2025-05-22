package service;

import ResponseException.ResponseException;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;

public class ChessService {

    private final DataAccess dataAccess;

    public ChessService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData register(String username, String password, String email) throws ResponseException {
        //use data access to search for username
        if(dataAccess.getUser(username) != null){
            throw new ResponseException(403, "This username is already taken :(");
            //return new AuthData("authToken", "username already there");
        }
        else{
            dataAccess.createUser(username, password, email);
            return dataAccess.createAuth(username);
        }

        //create auth data
        //return auth data
        //return new AuthData("I haven't finished the function", "FAKEusername");
    }

}
