package client;

import Facade.ServerFacade;
import Requests.LoginRequest;
import Requests.RegisterRequest;
import Results.LoginResult;
import Results.RegisterResult;
import org.junit.jupiter.api.*;
import responseexception.ResponseException;
import server.Server;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ServerFacadeTests {

    private static Server server;
    private static String port;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        facade = new ServerFacade("http://localhost:" + port);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerPositive() throws ResponseException {
        RegisterRequest request = new RegisterRequest("newUsername", "newPassword", "newEmail");
        RegisterResult result = facade.registerUser(request);
        RegisterResult expected = new RegisterResult("newUsername", null);
        Assertions.assertEquals(expected.username(), result.username());
    }

    @Test
    public void registerNegative(){
        RegisterRequest request = new RegisterRequest(null, "newPassword", "newEmail");
        assertThrows(ResponseException.class, () -> facade.registerUser(request));
    }

    @Test
    public void logoutPositive() throws ResponseException {
        LoginRequest request = new LoginRequest("newUsername", "newPassword");
        LoginResult result = facade.loginUser(request);
        LoginResult expected = new LoginResult("newUsername", null);
        Assertions.assertEquals(expected.username(), result.username());
    }

    @Test
    public void loginNegative(){
        LoginRequest request = new LoginRequest(null, "newPassword");
        assertThrows(ResponseException.class, () -> facade.loginUser(request));
    }




}
