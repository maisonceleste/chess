package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import responseexception.ResponseException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlDataAccess implements DataAccess{

    public MySqlDataAccess() throws ResponseException {
        configureDatabase();
    }

    @Override
    public void deleteAll() throws ResponseException {
        executeUpdate("TRUNCATE TABLE users;");
        executeUpdate("TRUNCATE TABLE games;");
        executeUpdate("TRUNCATE TABLE auths;");
    }

    @Override
    public void resetGameID() {
        return; //you don't need this for the database, but I put the function interface so my service tests could use it
    }

    @Override
    public AuthData createAuth(String username) throws ResponseException {
        String authID = UUID.randomUUID().toString();
        var statement = "INSERT INTO auths (authID, username) VALUES (?, ?)";
        AuthData newToken= new AuthData(authID, username);
        executeUpdate(statement, newToken.authToken(), newToken.username());
        return newToken;
    }

    @Override
    public AuthData getAuth(String authID) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authID, username FROM auths WHERE authID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String returnAuthID = rs.getString("authID");
                        String returnUsername = rs.getString("username");
                        return new AuthData(returnAuthID, returnUsername);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("DATABASE ERROR: Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void deleteAuth(String authID) throws ResponseException {
        var statement = "DELETE FROM auths WHERE authID=?";
        executeUpdate(statement, authID);
    }

    @Override
    public UserData createUser(String username, String password, String email) throws ResponseException {
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        executeUpdate(statement, username, password, email);
        return new UserData(username, password, email);
    }

    @Override
    public UserData getUser(String username) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String returnUsername = rs.getString("username");
                        String returnPassword = rs.getString("password");
                        String returnEmail = rs.getString("email");
                        return new UserData(returnUsername, returnPassword, returnEmail);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("DATABASE ERROR: Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public GameData createGame(String gameName) throws ResponseException {
        var statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES ( ?, ?, ?, ?)";
        ChessGame game = new ChessGame();
        var json = new Gson().toJson(game);
        int id = executeGameUpdate(statement, null, null, gameName, json);
        return new GameData(id, null, null, gameName, game);
    }

    @Override
    public GameData getGame(int gameID) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("DATABASE ERROR: Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        int returnGameID = rs.getInt("gameID");
        String whiteUsername = rs.getString("whiteUsername");
        String blackUsername = rs.getString("blackUsername");
        String gameName = rs.getString("gameName");
        var json = rs.getString("game");
        var game = new Gson().fromJson(json, ChessGame.class);
        return new GameData(returnGameID, whiteUsername, blackUsername, gameName, game);
    }

    @Override
    public ArrayList<GameData> listGames() throws ResponseException {
        var result = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("DATABASE ERROR: Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    @Override
    public GameData updateGame(String color, int gameID, String username) throws ResponseException {
        String column = color.equalsIgnoreCase("WHITE") ? "whiteUsername" : "blackUsername";
        var statement = "UPDATE games SET " + column + " = ? WHERE gameID = ?";
        int id = executeGameUpdate(statement, username, gameID);
        return getGame(gameID);
    }

    private void executeUpdate(String statement, Object... params) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case ChessGame p -> ps.setString(i + 1, p.toString());
                        case null -> ps.setNull(i + 1, NULL);
                        default -> {
                        }
                    }
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    rs.getInt(1);
                }

            }
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException(500, String.format("DATABASE ERROR: unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private int executeGameUpdate(String statement, Object... params) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`),
              INDEX(password),
              INDEX(email)
            )ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
            ,
            """
            CREATE TABLE IF NOT EXISTS  games (
              `gameID` INT NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256) NOT NULL,
              `game` TEXT DEFAULT NULL,
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
            throw new ResponseException(500, String.format("DATABASE ERROR: Unable to configure database: %s", ex.getMessage()));
        }
    }
}
