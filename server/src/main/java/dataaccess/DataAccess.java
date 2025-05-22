package dataaccess;

import model.AuthData;
import model.UserData;

public interface DataAccess {
    AuthData createAuth(String username);

    AuthData getAuth(String authID);

    void deleteAuth(String authID);

    UserData createUser(String username, String password, String email);

    UserData getUser(String username);
}
