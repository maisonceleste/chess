package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private final ChessPiece [][] grid = new ChessPiece[8][8];

    public ChessBoard() {
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {

        grid[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {

        return grid[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {

        for(int i=0; i<8; i++){
            for (int j=0; j<8; j++){
                grid[i][j] = null;
            }
        }
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "grid=" + Arrays.toString(grid) +
                '}';
    }

    @Override
    public boolean equals(Object compare) {
        if (compare == null || getClass() != compare.getClass()) {
            return false;
        }
        ChessBoard other = (ChessBoard) compare;
        return Objects.deepEquals(grid, other.grid);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(grid);
    }
}
