package clients;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import facade.ServerFacade;
import model.GameData;
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
    private ChessGame game;

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
                case "move" -> move(params);
                case "quit" -> quit();
                case "highlight" -> highlight(params);
                case "resign" -> resign();
                default -> throw new IllegalStateException("Uh-oh, that's not a valid command. Try one of these");
            };
        } catch (ResponseException ex) {
            return ex.getMessage() + help();
        }
    }

    @Override
    public String help(){
        return """
                You are currently playing chess
                redraw - to redraw the current state of the chess board
                leave - to exit the game
                move <start position> <end position> <promote piece> - to make a move
                resign - to forfeit the game
                highlight <position> - to show every possible move a piece can make
                help - to display possible commands
                """;
    }

    public void setGame(ChessGame.TeamColor color, String authToken, int gameID, ChessGame game){
        this.color = color ;
        this.authToken = authToken;
        this.gameID = gameID;
        this.game = game;
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

    public String leave() throws ResponseException {
        JoinRequest request = new JoinRequest(color.toString(), gameID, authToken);
        ws.leaveGame(authToken, gameID, request);
        repl.changeState(Repl.State.CENTRAL, this.serverUrl, authToken);
        return "You have left the game";
    }

    public String move(String... params) throws ResponseException {
        if(color != game.getTeamTurn()){throw new ResponseException(500, "It is not your turn");}
        ChessPosition startPosition = positionTranslator(params[0].toLowerCase());
        ChessPosition endPosition = positionTranslator(params[1].toLowerCase());
        ChessPiece.PieceType promotion = ChessPiece.PieceType.valueOf(params[2]);
        ChessMove move = new ChessMove(startPosition, endPosition, promotion);
        ws.makeMove(authToken, gameID, move);
        return "making move";
    }

    public String resign() throws ResponseException {
        ws.resign(authToken, gameID);
        return "You have resigned";
    }

    public String highlight(String... params) throws ResponseException {
        ChessPosition position = positionTranslator(params[0].toLowerCase());
        ChessPiece piece = game.getBoard().getPiece(position);
        if(piece == null){return redraw();}
        else if(color == ChessGame.TeamColor.BLACK){return ui.highlightMovesBlack(position);}
        else if(color == ChessGame.TeamColor.WHITE){return ui.highlightMovesWhite(position);}
        else{return redraw();}
    }

    private ChessPosition positionTranslator(String string) throws ResponseException {
        char letter = string.charAt(0);
        int col;
        switch (letter) {
            case 'a' -> col = 1;
            case 'b' -> col = 2;
            case 'c' -> col = 3;
            case 'd' -> col = 4;
            case 'e' -> col = 5;
            case 'f' -> col = 6;
            case 'g' -> col = 7;
            case 'h' -> col = 8;
            default -> throw new ResponseException(400, "Please select a valid chess position <letter><number>");
        }
        int row = string.charAt(1) - '0';
        return new ChessPosition(row, col);
    }

    @Override
    public String quit() throws ResponseException {
        return "you must leave the game before you can quit";
    }
}
