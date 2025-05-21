package dataaccess;

import model.AuthData;

public interface DataAccess {
    AuthData createAuth(String username);
}
