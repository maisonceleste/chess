package server;

import com.google.gson.Gson;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import service.ChessService;
import spark.*;

public class Server {

    ChessService service;

    public Server() {
        this.service = new ChessService(new MemoryDataAccess());
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private String register(Request req, Response res) {
        //I think I need to turn it back into json to send to client but for now I'm going to leave it :)
        var data = new Gson().fromJson(req.body(), RegisterRequest.class);
        String username = data.username();
        String password = data.password();
        String email = data.email();
        AuthData authToken= service.register(username, password, email);
        res.type("application/json");
        return authToken.authToken();

    }
}
