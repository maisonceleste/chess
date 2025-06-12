package client;

import chess.ChessGame;
import chess.ChessPosition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.BoardPainter;

public class BoardPainterTests {
    private  BoardPainter ui;

    @BeforeEach
    public void init(){
        ui = new BoardPainter(new ChessGame());
    }

    @Test
    public void drawWhite(){
        System.out.println(ui.drawWhiteView());
    }

    @Test
    public void drawBlack(){
        System.out.println(ui.drawBlackView());
    }

    @Test
    public void drawHighlightBlack(){
        System.out.println(ui.highlightMovesBlack(new ChessPosition(1,2)));
    }

    @Test
    public void drawHighlightWhite(){
        System.out.println(ui.highlightMovesWhite(new ChessPosition(7,2)));
    }
}
