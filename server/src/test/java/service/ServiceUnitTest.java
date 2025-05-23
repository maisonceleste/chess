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
    static final ChessService service = new ChessService(new MemoryDataAccess());

    @BeforeEach
    void clear() {
        service.dataAccess.deleteAll();
        service.dataAccess.resetGameID();
    }

    @Test
    void registerUser() throws ResponseException {
        RegisterResult result = service.register("correctUsername", "correctPassword", "correctEmail");

        RegisterResult expectedResult = new RegisterResult("correctUsername", "random auth code");

        assertEquals(expectedResult.username(), result.username());
        assertNotNull(result.authToken());
    }

    @Test
    void registerBadUser() throws ResponseException {
        service.register("correctUsername", "correctPassword", "correctEmail");
        assertThrows(ResponseException.class, () -> service.register("correctUsername", "evil", "Evil"));
        assertThrows(ResponseException.class, () -> service.register(null, "evil", "superEvil"));
    }

    @Test
    void loginUser() throws ResponseException{
        service.register("correctUsername", "correctPassword", "correctEmail");
        LoginResult result = service.login("correctUsername", "correctPassword");

        LoginResult expectedResult = new LoginResult("correctUsername", "random auth token");

        assertEquals(expectedResult.username(), result.username());
        assertNotNull(result.authToken());
    }

    @Test
    void loginBadUser() throws ResponseException{
        service.register("correctUsername", "correctPassword", "correctEmail");
        assertThrows(ResponseException.class, () -> service.login("correctUsername", "evil"));
        assertThrows(ResponseException.class, () -> service.login(null, "correctPassword"));
    }

    @Test
    void logoutUser() throws ResponseException{
        RegisterResult registration =service.register("correctUsername", "correctPassword", "correctEmail");
        String authToken = registration.authToken();
        service.logout(authToken);
        assertNull(service.dataAccess.getAuth(authToken));
    }

    @Test
    void logoutBadUser() throws ResponseException{
        RegisterResult registration =service.register("correctUsername", "correctPassword", "correctEmail");
        assertThrows(ResponseException.class, () -> service.logout("FALSE authToken"));
    }

    @Test
    void createGame() throws ResponseException{
        RegisterResult registration =service.register("correctUsername", "correctPassword", "correctEmail");
        String authToken = registration.authToken();
        CreateResult result = service.create(authToken, "testGame");
        assertEquals(1, result.gameID());
    }

    @Test
    void createBadGame() throws ResponseException{
        RegisterResult registration =service.register("correctUsername", "correctPassword", "correctEmail");
        String authToken = registration.authToken();
        assertThrows(ResponseException.class, () -> service.create("FALSE authToken", "testGame"));
        assertThrows(ResponseException.class, () -> service.create(authToken , null));
    }

    @Test
    void listGames() throws ResponseException {
        RegisterResult registration =service.register("correctUsername", "correctPassword", "correctEmail");
        String authToken = registration.authToken();
        service.create(authToken, "testGame1");
        service.create(authToken, "testGame2");
        ListResult result =service.list(authToken);
        assertEquals(2, result.games().size());
    }

    @Test
    void badListGames() throws ResponseException {
        RegisterResult registration =service.register("correctUsername", "correctPassword", "correctEmail");
        String authToken = registration.authToken();
        service.create(authToken, "testGame1");
        service.create(authToken, "testGame2");
        assertThrows(ResponseException.class, () -> service.list("bad auth token"));
    }

    @Test
    void joinGame() throws ResponseException {
        RegisterResult registration =service.register("correctUsername", "correctPassword", "correctEmail");
        String authToken = registration.authToken();
        service.create(authToken, "testGame1");
        assert(service.join("WHITE", 1, authToken));
    }

    @Test
    void badJoinGame() throws ResponseException {
        RegisterResult registration =service.register("correctUsername", "correctPassword", "correctEmail");
        String authToken = registration.authToken();
        service.create(authToken, "testGame1");
        service.join("WHITE", 1, authToken);
        assertThrows(ResponseException.class, () -> service.join("WHITE", 1, authToken));
    }

    @Test
    void testClear() throws ResponseException {
        RegisterResult registration =service.register("correctUsername", "correctPassword", "correctEmail");
        String authToken = registration.authToken();
        service.create(authToken, "testGame1");
        service.clear();
        assertNull(service.dataAccess.getUser("correctUsername"));
        assertNull(service.dataAccess.getAuth(authToken));
        assertNull(service.dataAccess.getGame(1));
    }

}
