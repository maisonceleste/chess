package clients;

import facade.ServerFacade;
import repl.Repl;
import requests.LoginRequest;
import requests.RegisterRequest;
import results.LoginResult;
import results.RegisterResult;
import responseexception.ResponseException;

import java.util.Arrays;

public class PreLoginClient implements Client {

    private final String serverUrl;
    private final ServerFacade server;
    private final Repl repl;

    public PreLoginClient(String serverUrl, Repl repl){
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.repl = repl;
    }

    @Override
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "help" -> help();
                case "register" -> registerUser(params);
                case "login" -> loginUser(params);
                case "quit" -> quit();

                default -> throw new ResponseException(400, "Uh-oh, that's not a valid command. Try one of these \n" +help());
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }


    public String registerUser(String... params) throws ResponseException{
        if (params.length == 3){
            RegisterRequest request = new RegisterRequest(params[0], params[1], params[2]);
            RegisterResult result = server.registerUser(request);
            repl.changeState(Repl.State.CENTRAL, this.serverUrl, result.authToken());
            return "Registered new user, " + result.username();
        }
        throw new ResponseException(400, "Could not register new user. Try again"+ Arrays.toString(params));
    }

    public String loginUser(String... params) throws ResponseException{
        if (params.length == 2) {
            LoginRequest request = new LoginRequest(params[0], params[1]);
            LoginResult result = server.loginUser(request);
            repl.changeState(Repl.State.CENTRAL, this.serverUrl, result.authToken());
            return "Logged in, " + result.username();
        }
        throw new ResponseException(400, "Could not log in. Try login <USERNAME> <PASSWORD>");
    }

    @Override
    public String help(){
        return """
                You are currently logged out.
                register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                login <USERNAME> <PASSWORD> - to join a game and play
                quit - to leave the program
                help - to display possible commands
                """;
    }

    public String quit() throws ResponseException{
        return "quit";
    }


}
