import Results.CreateResult;
import responseexception.ResponseException;

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
        return "creating the game " + params[0];
    }
}
