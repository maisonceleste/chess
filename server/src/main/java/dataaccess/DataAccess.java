package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import responseexception.ResponseException;

import java.util.ArrayList;
import java.util.Collection;

public interface DataAccess {

    void deleteAll() throws ResponseException;

    void resetGameID();

    AuthData createAuth(String username) throws ResponseException;

    AuthData getAuth(String authID) throws ResponseException;

    void deleteAuth(String authID) throws ResponseException;

    UserData createUser(String username, String password, String email) throws ResponseException;

    UserData getUser(String username) throws ResponseException;

    GameData createGame(String gameName);

    GameData getGame(int gameID);

    ArrayList<GameData> listGames();

    GameData updateGame(String color, int gameID, String username);


}
