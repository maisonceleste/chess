package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import responseexception.ResponseException;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;


public class ServiceUnitTest {
    static final ChessService Service = new ChessService(new MemoryDataAccess());

    @BeforeEach
    void clear() {
        Service.dataAccess.deleteAll();
        Service.dataAccess.resetGameID();
    }

    @Test
    void registerUser() throws ResponseException {
        RegisterResult result = Service.register("correctUsername", "correctPassword", "correctEmail");

        RegisterResult expectedResult = new RegisterResult("correctUsername", "random auth code");

        assertEquals(expectedResult.username(), result.username());
        assertNotNull(result.authToken());
    }

    @Test
    void registerBadUser() throws ResponseException {
        Service.register("correctUsername", "correctPassword", "correctEmail");
        assertThrows(ResponseException.class, () -> Service.register("correctUsername", "evil", "Evil"));
        assertThrows(ResponseException.class, () -> Service.register(null, "evil", "superEvil"));
    }

    @Test
    void loginUser() throws ResponseException{
        Service.register("correctUsername", "correctPassword", "correctEmail");
        LoginResult result = Service.login("correctUsername", "correctPassword");

        LoginResult expectedResult = new LoginResult("correctUsername", "random auth token");

        assertEquals(expectedResult.username(), result.username());
        assertNotNull(result.authToken());
    }

    @Test
    void loginBadUser() throws ResponseException{
        Service.register("correctUsername", "correctPassword", "correctEmail");
        assertThrows(ResponseException.class, () -> Service.login("correctUsername", "evil"));
        assertThrows(ResponseException.class, () -> Service.login(null, "correctPassword"));
    }

    @Test
    void logoutUser() throws ResponseException{
        RegisterResult registration =Service.register("correctUsername", "correctPassword", "correctEmail");
        String authToken = registration.authToken();
        Service.logout(authToken);
        assertNull(Service.dataAccess.getAuth(authToken));
    }

    @Test
    void logoutBadUser() throws ResponseException{
        RegisterResult registration =Service.register("correctUsername", "correctPassword", "correctEmail");
        assertThrows(ResponseException.class, () -> Service.logout("FALSE authToken"));
    }

    @Test
    void createGame() throws ResponseException{
        RegisterResult registration =Service.register("correctUsername", "correctPassword", "correctEmail");
        String authToken = registration.authToken();
        CreateResult result = Service.create(authToken, "testGame");
        assertEquals(1, result.gameID());
    }

    @Test
    void createBadGame() throws ResponseException{
        RegisterResult registration =Service.register("correctUsername", "correctPassword", "correctEmail");
        String authToken = registration.authToken();
        assertThrows(ResponseException.class, () -> Service.create("FALSE authToken", "testGame"));
        assertThrows(ResponseException.class, () -> Service.create(authToken , null));
    }

    @Test
    void listGames() throws ResponseException {
        RegisterResult registration =Service.register("correctUsername", "correctPassword", "correctEmail");
        String authToken = registration.authToken();
        Service.create(authToken, "testGame1");
        Service.create(authToken, "testGame2");
        ListResult result =Service.list(authToken);
        assertEquals(2, result.games().size());
    }

    @Test
    void badListGames() throws ResponseException {
        RegisterResult registration =Service.register("correctUsername", "correctPassword", "correctEmail");
        String authToken = registration.authToken();
        Service.create(authToken, "testGame1");
        Service.create(authToken, "testGame2");
        assertThrows(ResponseException.class, () -> Service.list("bad auth token"));
    }

    @Test
    void joinGame() throws ResponseException {
        RegisterResult registration =Service.register("correctUsername", "correctPassword", "correctEmail");
        String authToken = registration.authToken();
        Service.create(authToken, "testGame1");
        assert(Service.join("WHITE", 1, authToken));
    }

    @Test
    void badJoinGame() throws ResponseException {
        RegisterResult registration =Service.register("correctUsername", "correctPassword", "correctEmail");
        String authToken = registration.authToken();
        Service.create(authToken, "testGame1");
        Service.join("WHITE", 1, authToken);
        assertThrows(ResponseException.class, () -> Service.join("WHITE", 1, authToken));
    }

    @Test
    void testClear() throws ResponseException {
        RegisterResult registration =Service.register("correctUsername", "correctPassword", "correctEmail");
        String authToken = registration.authToken();
        Service.create(authToken, "testGame1");
        Service.clear();
        assertNull(Service.dataAccess.getUser("correctUsername"));
        assertNull(Service.dataAccess.getAuth(authToken));
        assertNull(Service.dataAccess.getGame(1));
    }

}
