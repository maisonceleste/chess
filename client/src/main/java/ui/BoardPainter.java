package ui;

import chess.ChessGame;
import model.GameData;


public class BoardPainter {
    private ChessGame game;
    private String boarders;
    private String dark;
    private String light;
    private String text;

    public BoardPainter(ChessGame game){
        this.game = game;
        this.boarders= EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
        this.dark = EscapeSequences.SET_BG_COLOR_BLACK;
        this.light = EscapeSequences.SET_BG_COLOR_WHITE;
        this.text = EscapeSequences.SET_TEXT_COLOR_BLACK;

    }

    public String drawWhiteView(){
        String result = "";
        result += endRows();
        result += game.toString() + "\n";
//        for(int i=8; i>0; i--){
//
//        }
        result+= endRows();
        return result;

    }

    public String drawBlackView(){
        String result = "";
        result += endRows();
        result += game.toString() + "\n";
//        for(int i=8; i>0; i--){
//
//        }
        result+= endRows();
        return result;
    }

    public void updateGame(ChessGame game){
        this.game = game;
    }

    private String endRows(){
        String row =  boarders + text +  EscapeSequences.EMPTY + "A B C D E F G H " + EscapeSequences.EMPTY;
        row+= EscapeSequences.RESET_BG_COLOR + "\n";
        return row;
    }

}
