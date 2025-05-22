package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.Collection;

public interface DataAccess {

    void deleteAll();

    AuthData createAuth(String username);

    AuthData getAuth(String authID);

    void deleteAuth(String authID);

    UserData createUser(String username, String password, String email);

    UserData getUser(String username);

    GameData createGame(String gameName);

    GameData getGame(int gameID);

    ArrayList<GameData> listGames();

    GameData updateGame(String color, int gameID, String username);


}
