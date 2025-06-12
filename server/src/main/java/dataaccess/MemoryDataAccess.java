package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import responseexception.ResponseException;

import java.util.*;

public class MemoryDataAccess implements DataAccess{

    private int nextGameID = 1;
    final public HashMap<String, UserData> users = new HashMap<>();
    final public HashMap<String, AuthData> authCodes = new HashMap<>();
    final public HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public void resetGameID(){nextGameID=1;}

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
    public GameData getGame(int gameID){
        return games.get(gameID);
    }

    @Override
    public ArrayList<GameData> listGames() {
        return new ArrayList<>(games.values());
    }

    @Override
    public GameData updateGame(String color, int gameID, String username) {
        GameData oldGame = games.get(gameID);
        GameData newGame;
        if(Objects.equals(color, "WHITE")){
            newGame = new GameData(oldGame.gameID(), username, oldGame.blackUsername(), oldGame.gameName(), oldGame.game());
        }
        else{
            newGame = new GameData(oldGame.gameID(), oldGame.whiteUsername(), username, oldGame.gameName(), oldGame.game());
        }
        games.remove(gameID);
        games.put(gameID, newGame);
        return newGame;
    }

    @Override
    public void updateMove(ChessGame newGame, int gameID) throws ResponseException {
        return;
    }
}
