package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.*;

public class MemoryDataAccess implements DataAccess{

    private int nextGameID = 1;
    final private HashMap<String, UserData> users = new HashMap<>();
    final private HashMap<String, AuthData> authCodes = new HashMap<>();
    final private HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public void deleteAll() {
        users.clear();
        authCodes.clear();
        games.clear();
    }

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
    public void deleteAuth(String authID){
        authCodes.remove(authID);
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

    @Override
    public GameData createGame(String gameName) {
        GameData game = new GameData(nextGameID++,null, null, gameName, new ChessGame());
        games.put(game.gameID(), game);
        return game;
    }

    @Override
    public ArrayList<GameData> listGames() {
        return new ArrayList<>(games.values());
    }

    @Override
    public GameData updateGame(String color, int gameID, String username){
        GameData oldGame = games.get(gameID);
        GameData newGame;
        if(color == "WHITE"){
            newGame = new GameData(oldGame.gameID(), null, username, oldGame.gameName(), oldGame.game());
        }
        else{
            newGame = new GameData(oldGame.gameID(), username, null, oldGame.gameName(), oldGame.game());
        }
        games.remove(gameID);
        games.put(gameID, newGame);
        return newGame;
    }
}
