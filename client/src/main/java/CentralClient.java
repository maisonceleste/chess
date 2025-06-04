import model.GameData;
import responseexception.ResponseException;

import java.util.ArrayList;
import java.util.Arrays;

public class CentralClient implements Client {

    private final String authToken;
    private final ServerFacade server;
    private final String serverUrl;
    private final Repl repl;

    public CentralClient(String serverUrl, Repl repl, String authToken) {
        this.authToken = authToken;
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
                case "create" -> createGame(params);
                case "logout" -> logout();
                case "quit" -> quit();
                case "list" -> listGames();

                default -> throw new IllegalStateException("Unexpected value: " + cmd);
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    @Override
    public String help() {
        return """
                You are currently logged in.
                create <NAME> - to make a new game
                list - to list all games
                join <ID> [WHITE/BLACK] - to join and play a game
                observe <ID> - to observe a current game
                logout - to return to login menu
                quit - to leave the program
                help - to display possible commands
                """;
    }

    private String createGame(String... params) throws ResponseException{
        this.server.createGame(params[0], authToken);
        return "Successfully created the game, " + params[0];
    }

    private String logout() throws ResponseException{
        this.server.logoutUser(authToken);
        repl.changeState(Repl.State.PRELOGIN, serverUrl, null);
        return "Successfully logged out!";
    }

    private String listGames() throws ResponseException{
        ArrayList<GameData> result =this.server.listGames(authToken).games();
        String output="";
        for(int i=0; i<result.size(); i++){
            output += String.valueOf(i+1)+". ";
            output += "Name: " + result.get(i).gameName();
            output += " Black: " + result.get(i).blackUsername();
            output += " White: " + result.get(i).whiteUsername() + "\n";
        }
        return output;
    }

    @Override
    public String quit() throws ResponseException {
        try {
            logout();
        }
        catch(ResponseException ex) {
            throw new ResponseException(400, "Unable to log out. Quitting anyways,");
        }
        return "quit";
    }
}
