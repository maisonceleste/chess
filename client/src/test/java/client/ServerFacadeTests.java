package client;

import Facade.ServerFacade;
import Requests.JoinRequest;
import Requests.LoginRequest;
import Requests.RegisterRequest;
import Results.CreateResult;
import Results.ListResult;
import Results.LoginResult;
import Results.RegisterResult;
import model.GameData;
import org.junit.jupiter.api.*;
import responseexception.ResponseException;
import server.Server;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ServerFacadeTests {

    private static Server server;
    private static String port;
    private static ServerFacade facade;
    private static String username;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        facade = new ServerFacade("http://localhost:" + port);
        System.out.println("Started test HTTP server on " + port);
        username = "nerUsername"+port;

    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void aregisterPositive() throws ResponseException {
        RegisterRequest request = new RegisterRequest(username, "newPassword", "newEmail");
        RegisterResult result = facade.registerUser(request);
        RegisterResult expected = new RegisterResult(username, null);
        Assertions.assertEquals(expected.username(), result.username());
    }

    @Test
    public void aregisterNegative(){
        RegisterRequest request = new RegisterRequest(null, "newPassword", "newEmail");
        assertThrows(ResponseException.class, () -> facade.registerUser(request));
    }

    @Test
    public void loginPositive() throws ResponseException {
        LoginRequest request = new LoginRequest(username, "newPassword");
        LoginResult result = facade.loginUser(request);
        LoginResult expected = new LoginResult(username, null);
        Assertions.assertEquals(expected.username(), result.username());
    }

    @Test
    public void loginNegative(){
        LoginRequest request = new LoginRequest(null, "newPassword");
        assertThrows(ResponseException.class, () -> facade.loginUser(request));
    }

    @Test
    public void logoutPositive() throws ResponseException {
        LoginRequest request = new LoginRequest(username, "newPassword");
        LoginResult result = facade.loginUser(request);
        Assertions.assertDoesNotThrow(() -> facade.logoutUser(result.authToken()));
    }

    @Test
    public void logoutNegative() throws ResponseException {
        LoginRequest request = new LoginRequest(username, "newPassword");
        LoginResult result = facade.loginUser(request);
        Assertions.assertThrows(ResponseException.class, () -> facade.logoutUser("Fake token"));
    }

    @Test
    public void createPositive() throws ResponseException{
        LoginRequest request = new LoginRequest(username, "newPassword");
        String  auth = facade.loginUser(request).authToken();
        CreateResult result = facade.createGame("newGame"+port, auth);
        assertNotEquals(0, result.gameID());
    }

    @Test
    public void createNegative() throws ResponseException{
        LoginRequest request = new LoginRequest(username, "newPassword");
        Assertions.assertThrows(ResponseException.class, () -> facade.createGame(null, "Fake auth"));
    }

    @Test
    public void listPositive() throws ResponseException{
        LoginRequest request = new LoginRequest(username, "newPassword");
        String  auth = facade.loginUser(request).authToken();
        ListResult result = facade.listGames(auth);
    }

    @Test
    public void listNegative() throws ResponseException{
        LoginRequest request = new LoginRequest(username, "newPassword");
        Assertions.assertThrows(ResponseException.class, () -> facade.listGames( "Fake auth"));
    }

    @Test
    public void joinPositive() throws ResponseException{
        LoginRequest request = new LoginRequest(username, "newPassword");
        String  auth = facade.loginUser(request).authToken();
        CreateResult result = facade.createGame("testGame"+port, auth);
        JoinRequest join = new JoinRequest("BLACK", result.gameID(), auth);
        Assertions.assertDoesNotThrow(() -> facade.joinGame(join));
    }

    @Test
    public void joinNegative() throws ResponseException{
        LoginRequest request = new LoginRequest(username, "newPassword");
        String  auth = facade.loginUser(request).authToken();
        ArrayList<GameData> result = facade.listGames(auth).games();
        JoinRequest join = new JoinRequest(null, result.getFirst().gameID(), auth);
        Assertions.assertThrows(ResponseException.class, () -> facade.joinGame(join));
    }

}
