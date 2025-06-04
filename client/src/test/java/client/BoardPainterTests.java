package client;

import chess.ChessGame;
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
}
