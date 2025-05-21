package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;

public class ChessService {

    private final DataAccess dataAccess;

    public ChessService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData register(String username, String password, String email){
        //use data access to search for username
        if(dataAccess.getUser(username) != null){
            throw new DataAccessException("This username is already taken :(");
        }
        else{
            dataAccess.createUser(username, password, email);
            dataAccess.createAuth(username);
        }

        //create auth data
        //return auth data
        return new AuthData("I haven't finished the function", "FAKEusername");
    }

}
