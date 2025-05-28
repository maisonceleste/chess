package server;

import dataaccess.MySqlDataAccess;
import responseexception.ResponseException;
import com.google.gson.Gson;
import dataaccess.MemoryDataAccess;
import service.*;
import spark.*;

public class Server {

    ChessService service;

    public Server(){
        try {
            this.service = new ChessService(new MySqlDataAccess());
        } catch (ResponseException e) {
            System.out.println("Could not open MySQL server :( use memory instead");
            this.service = new ChessService(new MemoryDataAccess());;
        }
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.delete("/db", this::clear);
        Spark.post("/game", this::create);
        Spark.get("/game", this::list);
        Spark.put("/game", this::join);
        Spark.exception(ResponseException.class, this::exceptionHandler);
        //This line initializes the server and can be removed once you have a functioning endpoint

        Spark.awaitInitialization();
        return Spark.port();
    }

    private void exceptionHandler(ResponseException exception, Request req, Response res){
        res.status(exception.statusCode());
        res.body(exception.toJson());
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private String register(Request req, Response res) throws ResponseException{
        //I think I need to turn it back into json to send to client but for now I'm going to leave it :)
        var data = new Gson().fromJson(req.body(), RegisterRequest.class);
        String username = data.username();
        String password = data.password();
        String email = data.email();
        RegisterResult result = service.register(username, password, email);
        res.type("application/json");
        return new Gson().toJson(result);
    }

    private String login(Request req, Response res) throws ResponseException {
        var data = new Gson().fromJson(req.body(), LoginRequest.class);
        String username = data.username();
        String password = data.password();
        LoginResult result = service.login(username, password);
        res.type("application/json");
        return new Gson().toJson(result);
    }

    private String logout(Request req, Response res) throws ResponseException {
        var data = req.headers("Authorization");
        service.logout(data);
        res.type("application/json");
        return "{}";
    }

    private String clear(Request req, Response res) throws ResponseException {
        service.clear();
        res.type("application/json");
        return "{}";
    }

    private String create(Request req, Response res) throws ResponseException{
        var auth = req.headers("Authorization");
        var data = new Gson().fromJson(req.body(), CreateRequest.class);
        String name = data.gameName();
        CreateResult result = service.create(auth, name);
        res.type("application/json");
        return new Gson().toJson(result);
    }

    private String list(Request req, Response res) throws ResponseException{
        var auth = req.headers("Authorization");
        ListResult result = service.list(auth);
        res.type("application/json");
        return new Gson().toJson(result);
    }

    private String join(Request req, Response res) throws ResponseException{
        var auth = req.headers("Authorization");
        var data = new Gson().fromJson(req.body(), JoinRequest.class);
        String playerColor = data.playerColor();
        int gameID = data.gameID();
        service.join(playerColor, gameID, auth);
        res.type("application/json");
        return "{}";
    }
}
