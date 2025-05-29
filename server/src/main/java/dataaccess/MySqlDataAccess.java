package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import responseexception.ResponseException;

import java.sql.SQLException;
import java.util.ArrayList;

public class MySqlDataAccess implements DataAccess{

    public MySqlDataAccess() throws ResponseException {
        configureDatabase();
    }

    @Override
    public void deleteAll() {

    }

    @Override
    public void resetGameID() {

    }

    @Override
    public AuthData createAuth(String username) {
        return null;
    }

    @Override
    public AuthData getAuth(String authID) {
        return null;
    }

    @Override
    public void deleteAuth(String authID) {

    }

    @Override
    public UserData createUser(String username, String password, String email) {
        return null;
    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public GameData createGame(String gameName) {
        return null;
    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public ArrayList<GameData> listGames() {
        return null;
    }

    @Override
    public GameData updateGame(String color, int gameID, String username) {
        return null;
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`username`),
              INDEX(password),
              INDEX(email)
            )ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
            ,
            """
            CREATE TABLE IF NOT EXISTS  games (
              `gameID` INT,
              `whiteUsername` varchar(256) NOT NULL,
              `blackUsername` varchar(256) NOT NULL,
              `gameName` varchar(256) NOT NULL,
              `game` blob,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`gameID`),
              INDEX(whiteUsername),
              INDEX(blackUsername),
              INDEX(gameName)
            )ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
            ,
            """
            CREATE TABLE IF NOT EXISTS  auths (
              `authID` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`authID`),
              INDEX(username)
            )ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws ResponseException{
        try{DatabaseManager.createDatabase();
            try (var conn = DatabaseManager.getConnection()) {
                for (var statement : createStatements) {
                    try(var preparedStatement = conn.prepareStatement(statement)) {
                        preparedStatement.executeUpdate();
                    }
                    catch(SQLException e){
                        System.err.println("Failed to execute statement:\n" + statement);
                    }
                }
            }
        } catch (SQLException | DataAccessException ex) {
            throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
