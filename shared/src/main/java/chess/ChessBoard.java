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
        grid[0][0]= new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        grid[0][7]= new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        grid[0][1]= new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        grid[0][6]= new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        grid[0][2]= new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        grid[0][5]= new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        grid[0][3]= new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        grid[0][4]= new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        grid[7][0]= new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        grid[7][7]= new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        grid[7][1]= new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        grid[7][6]= new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        grid[7][2]= new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        grid[7][5]= new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        grid[7][3]= new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        grid[7][4]= new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        for (int j=0; j<8; j++){
            grid[1][j] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            grid[6][j] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }
        for(int i=2; i<6; i++){
            for (int j=0; j<8; j++){
                grid[i][j] = null;
            }
        }
    }

    @Override
    public String toString() {
        String output = "ChessBoard{ \n";
        for(int i=0; i<8; i++){
            for (int j=0; j<8; j++){
                if (grid[i][j] == null){output+="[ ]";}
                else{
                    output += "[" + grid[i][j].getPieceType().name().charAt(0) + "]";
                }
            }
            output += "\n";
        }
        return output;
    }

    @Override
    public boolean equals(Object compare) {
        if (compare == null || getClass() != compare.getClass()) {
            return false;
        }
        ChessBoard other = (ChessBoard) compare;
        for(int i=0; i<8; i++){
            for (int j=0; j<8; j++){
                if (!Objects.equals(grid[i][j], other.grid[i][j])){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(grid);
    }

    public ChessBoard deepCopy(){
        ChessBoard newBoard = new ChessBoard();
        for(int i=0; i<8; i++){
            for (int j=0; j<8; j++){
                newBoard.grid[i][j] = this.grid[i][j].deepCopy();
            }
        }
        return newBoard;
    }
}
