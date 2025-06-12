package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;

import static ui.EscapeSequences.*;


public class BoardPainter {
    private ChessGame game;
    private ChessBoard board;
    private String boarders;
    private String dark;
    private String light;
    private String text;

    public BoardPainter(ChessGame game){
        this.game = game;
        this.board = game.getBoard();
        this.boarders= EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
        this.dark = EscapeSequences.SET_BG_COLOR_BLACK;
        this.light = EscapeSequences.SET_BG_COLOR_WHITE;
        this.text = EscapeSequences.SET_TEXT_COLOR_BLACK;

    }

    public String drawWhiteView(){
        String result = "";
        result += endRowsWhite();
        for(int i=8; i>=1; i--){
            result+= boarders + i;
            for(int j=1; j<=8; j++){
                String background= squareColor(i,j);

                result+= background;
                result+= getSymbol(background, i, j);
            }
            result+= boarders + text + i + RESET_BG_COLOR+ "\n";
        }
        result+= endRowsWhite();
        return result+RESET_TEXT_COLOR;

    }

    public String drawBlackView(){
        String result = "";
        result += endRowsBlack();
        for(int i=1; i<9; i++){
            result+= boarders + i;
            for(int j=8; j>=1; j--){
                String background= squareColor(i,j);
                result+= background;
                result+= getSymbol(background, i, j);
            }
            result+= boarders + text + i + RESET_BG_COLOR+ "\n";
        }
        result+= endRowsBlack();
        return result+RESET_TEXT_COLOR;
    }

    public String highlightMovesBlack(ChessPosition startPosition){
        String result = "";
        result += endRowsBlack();
        for(int i=1; i<9; i++){
            result+= boarders + i;
            for(int j=8; j>=1; j--){
                String background= squareColor(i,j);
                result+= background;
                result+= getSymbol(background, i, j);
            }
            result+= boarders + text + i + RESET_BG_COLOR+ "\n";
        }
        result+= endRowsBlack();
        return result+RESET_TEXT_COLOR;
    }


    public void updateGame(ChessGame game){
        this.board = game.getBoard();
    }

    private String endRowsWhite(){
        String row =  boarders + text + "  A  B  C  D  E  F  G  H  " ;
        row+= RESET_BG_COLOR + "\n";
        return row;
    }

    private String endRowsBlack(){
        String row =  boarders + text + "  H  G  F  E  D  C  B  A  " ;
        row+= RESET_BG_COLOR + "\n";
        return row;
    }

    private String squareColor(int i, int j){
        if(i%2 == j%2f){
            return dark;
        }
        else{
            return light;
        }
    }

    private String getSymbol(String background, int i, int j){
        ChessPiece piece = board.getPiece(new ChessPosition(i, j));
        if (piece==null){return EMPTY;}
        ChessPiece.PieceType type = piece.getPieceType();
        ChessGame.TeamColor color = piece.getTeamColor();
        switch(type){
            case ROOK:
                if(color == ChessGame.TeamColor.WHITE){ return SET_TEXT_COLOR_RED+WHITE_ROOK;}
                else{return SET_TEXT_COLOR_BLUE+BLACK_ROOK;}
            case BISHOP:
                if(color == ChessGame.TeamColor.WHITE){ return SET_TEXT_COLOR_RED+WHITE_BISHOP;}
                else{return SET_TEXT_COLOR_BLUE+BLACK_BISHOP;}
            case KNIGHT:
                if(color == ChessGame.TeamColor.WHITE){ return SET_TEXT_COLOR_RED+WHITE_KNIGHT;}
                else{return SET_TEXT_COLOR_BLUE+BLACK_KNIGHT;}
            case PAWN:
                if(color == ChessGame.TeamColor.WHITE){ return SET_TEXT_COLOR_RED+WHITE_PAWN;}
                else{return SET_TEXT_COLOR_BLUE+BLACK_PAWN;}
            case KING:
                if(color == ChessGame.TeamColor.WHITE){ return SET_TEXT_COLOR_RED+WHITE_KING;}
                else{return SET_TEXT_COLOR_BLUE+BLACK_KING;}
            case QUEEN:
                if(color == ChessGame.TeamColor.WHITE){ return SET_TEXT_COLOR_RED+WHITE_QUEEN;}
                else{return SET_TEXT_COLOR_BLUE+BLACK_QUEEN;}
            default:
                return EMPTY;
        }
    }
}
