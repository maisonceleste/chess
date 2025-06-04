package service;

import results.CreateResult;
import results.ListResult;
import results.LoginResult;
import results.RegisterResult;
import dataaccess.MemoryDataAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import responseexception.ResponseException;

import static org.junit.jupiter.api.Assertions.*;


public class ServiceUnitTest {
    static final ChessService SERVICE = new ChessService(new MemoryDataAccess());

    @BeforeEach
    void clear() throws ResponseException {
        SERVICE.dataAccess.deleteAll();
        SERVICE.dataAccess.resetGameID();
    }

    @Test
    void registerUser() throws ResponseException {
        RegisterResult result = SERVICE.register("correctUsername", "correctPassword", "correctEmail");

        RegisterResult expectedResult = new RegisterResult("correctUsername", "random auth code");

        assertEquals(expectedResult.username(), result.username());
        assertNotNull(result.authToken());
    }

    @Test
    void registerBadUser() throws ResponseException {
        SERVICE.register("correctUsername", "correctPassword", "correctEmail");
        assertThrows(ResponseException.class, () -> SERVICE.register("correctUsername", "evil", "Evil"));
        assertThrows(ResponseException.class, () -> SERVICE.register(null, "evil", "superEvil"));
    }

    @Test
    void loginUser() throws ResponseException{
        SERVICE.register("correctUsername", "correctPassword", "correctEmail");
        LoginResult result = SERVICE.login("correctUsername", "correctPassword");

        LoginResult expectedResult = new LoginResult("correctUsername", "random auth token");

        assertEquals(expectedResult.username(), result.username());
        assertNotNull(result.authToken());
    }

    @Test
    void loginBadUser() throws ResponseException{
        SERVICE.register("correctUsername", "correctPassword", "correctEmail");
        assertThrows(ResponseException.class, () -> SERVICE.login("correctUsername", "evil"));
        assertThrows(ResponseException.class, () -> SERVICE.login(null, "correctPassword"));
    }

    @Test
    void logoutUser() throws ResponseException{
        RegisterResult registration = SERVICE.register("correctUsername", "correctPassword", "correctEmail");
        String authToken = registration.authToken();
        SERVICE.logout(authToken);
        assertNull(SERVICE.dataAccess.getAuth(authToken));
    }

    @Test
    void logoutBadUser() throws ResponseException{
        RegisterResult registration = SERVICE.register("correctUsername", "correctPassword", "correctEmail");
        assertThrows(ResponseException.class, () -> SERVICE.logout("FALSE authToken"));
    }

    @Test
    void createGame() throws ResponseException{
        RegisterResult registration = SERVICE.register("correctUsername", "correctPassword", "correctEmail");
        String authToken = registration.authToken();
        CreateResult result = SERVICE.create(authToken, "testGame");
        assertEquals(1, result.gameID());
    }

    @Test
    void createBadGame() throws ResponseException{
        RegisterResult registration = SERVICE.register("correctUsername", "correctPassword", "correctEmail");
        String authToken = registration.authToken();
        assertThrows(ResponseException.class, () -> SERVICE.create("FALSE authToken", "testGame"));
        assertThrows(ResponseException.class, () -> SERVICE.create(authToken , null));
    }

    @Test
    void listGames() throws ResponseException {
        RegisterResult registration = SERVICE.register("correctUsername", "correctPassword", "correctEmail");
        String authToken = registration.authToken();
        SERVICE.create(authToken, "testGame1");
        SERVICE.create(authToken, "testGame2");
        ListResult result = SERVICE.list(authToken);
        assertEquals(2, result.games().size());
    }

    @Test
    void badListGames() throws ResponseException {
        RegisterResult registration = SERVICE.register("correctUsername", "correctPassword", "correctEmail");
        String authToken = registration.authToken();
        SERVICE.create(authToken, "testGame1");
        SERVICE.create(authToken, "testGame2");
        assertThrows(ResponseException.class, () -> SERVICE.list("bad auth token"));
    }

    @Test
    void joinGame() throws ResponseException {
        RegisterResult registration = SERVICE.register("correctUsername", "correctPassword", "correctEmail");
        String authToken = registration.authToken();
        SERVICE.create(authToken, "testGame1");
        assert(SERVICE.join("WHITE", 1, authToken));
    }

    @Test
    void badJoinGame() throws ResponseException {
        RegisterResult registration = SERVICE.register("correctUsername", "correctPassword", "correctEmail");
        String authToken = registration.authToken();
        SERVICE.create(authToken, "testGame1");
        SERVICE.join("WHITE", 1, authToken);
        assertThrows(ResponseException.class, () -> SERVICE.join("WHITE", 1, authToken));
    }

    @Test
    void testClear() throws ResponseException {
        RegisterResult registration = SERVICE.register("correctUsername", "correctPassword", "correctEmail");
        String authToken = registration.authToken();
        SERVICE.create(authToken, "testGame1");
        SERVICE.clear();
        assertNull(SERVICE.dataAccess.getUser("correctUsername"));
        assertNull(SERVICE.dataAccess.getAuth(authToken));
        assertNull(SERVICE.dataAccess.getGame(1));
    }

}
