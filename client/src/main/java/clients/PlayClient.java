package clients;

import chess.ChessGame;
import facade.ServerFacade;
import repl.Repl;
import requests.JoinRequest;
import responseexception.ResponseException;
import ui.BoardPainter;
import websocket.WebSocketFacade;

import java.util.Arrays;

public class PlayClient implements Client {

    private final String serverUrl;
    private final ServerFacade server;
    private final Repl repl;
    private final WebSocketFacade ws;
    private ChessGame.TeamColor color;
    private String authToken;
    private int gameID;
    public BoardPainter ui;

    public PlayClient(String serverUrl, Repl repl) throws ResponseException {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.repl = repl;
        this.ws = new WebSocketFacade(serverUrl, repl);
        this.color = null;
        this.authToken = null;
        this.gameID = 0;
        this.ui = null;

    }

    @Override
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "help" -> help();
                case "redraw" -> redraw();
                case "leave" -> leave();
                default -> throw new IllegalStateException("Uh-oh, that's not a valid command. Try one of these");
            };
//        } catch (ResponseException ex) {
//            return ex.getMessage() + help();
        } finally {

        }
    }

    @Override
    public String help(){
        return """
                You are currently playing chess
                redraw - to redraw the current state of the chess board
                leave - to exit the game
                move <start position> <end position> - to make a move
                resign - to forfeit the game
                highlight <position> - to show every possible move a piece can make
                help - to display possible commands
                """;
    }

    public void setGame(ChessGame.TeamColor color, String authToken, int gameID){
        this.color = color ;;
        this.authToken = authToken;
        this.gameID = gameID;
    }

    public String connect(JoinRequest request) throws ResponseException {
        ws.joinGame(authToken, gameID, request);
        String board;
        if(color!=null && color.equals(ChessGame.TeamColor.BLACK)){board = ui.drawBlackView();}
        else{ board = ui.drawWhiteView();}
        return board + String.format("\nJoined the game as %s", request.playerColor());
    }

    public String redraw(){
        String board;
        if(color!=null && color.equals(ChessGame.TeamColor.BLACK)){board = ui.drawBlackView();}
        else{ board = ui.drawWhiteView();}
        return board;
    }

    @Override
    public String quit() throws ResponseException {
        return "you must leave the game before you can quit";
    }
}
