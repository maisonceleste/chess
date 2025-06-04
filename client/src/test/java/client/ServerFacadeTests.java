package client;

import Facade.ServerFacade;
import Requests.RegisterRequest;
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


}
