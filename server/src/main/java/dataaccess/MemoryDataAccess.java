package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryDataAccess implements DataAccess{

    final private HashMap<String, UserData> users = new HashMap<>();
    final private HashMap<String, AuthData> authCodes = new HashMap<>();
    final private HashMap<String, GameData> games = new HashMap<>();

    @Override
    public AuthData createAuth(String username){
        String authID = UUID.randomUUID().toString();
        AuthData newToken= new AuthData(authID, username);
        authCodes.put(authID, newToken);
        return newToken;
    }

    @Override
    public AuthData getAuth(String authID){
        return authCodes.get(authID);
    }

    @Override
    public UserData createUser(String username, String password, String email){
        UserData newUser = new UserData(username, password, email);
        users.put(username, newUser);
        return newUser;
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }
}
