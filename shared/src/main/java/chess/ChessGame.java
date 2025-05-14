package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    TeamColor teamTurn;
    ChessBoard board;

    public ChessGame() {
        teamTurn=TeamColor.WHITE;
        board=new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {teamTurn=team;}

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        // I will add code to check for check or check mate later, I just need to get the other code working :)
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessPiece thisPiece =  board.getPiece(startPosition);
        if(thisPiece==null || thisPiece.getTeamColor()!=teamTurn){return moves;};
        return thisPiece.pieceMoves(board, startPosition);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if(!validMoves(move.getStartPosition()).contains(move)){
            throw new InvalidMoveException("This piece cannot make this move at this time");
        }
        ChessPiece piece = board.getPiece(move.getStartPosition());
        board.addPiece(move.getEndPosition(), piece);
        piece.promotePiece(move.getPromotionPiece());
        board.addPiece(move.getStartPosition(), null);
        if(teamTurn==TeamColor.BLACK){teamTurn=TeamColor.WHITE;}
        else{teamTurn=TeamColor.BLACK;}
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition= new ChessPosition(0,0);
        ChessPosition currentPosition;
        ArrayList<ChessMove> allPossibleMovesList = new ArrayList<>();
        for(int i=1; i<=8; i++){
            for (int j=1; j<=8; j++){
                currentPosition= new ChessPosition(i,j);
                if(board.getPiece(currentPosition) == null){continue;}
                if (board.getPiece(currentPosition).getTeamColor() !=teamColor){
                    allPossibleMovesList.addAll(board.getPiece(currentPosition).pieceMoves(board, currentPosition));
                }
                else if (board.getPiece(currentPosition).getPieceType()== ChessPiece.PieceType.KING){
                   kingPosition=currentPosition;
                }
            }
        }
        for(ChessMove move:allPossibleMovesList){
            currentPosition= move.getEndPosition();
            if(currentPosition.equals(kingPosition)){
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        ChessPosition currentPosition;
        ArrayList<ChessMove> allPossibleMovesList = new ArrayList<>();
        for(int i=1; i<=8; i++){
            for (int j=1; j<=8; j++){
                currentPosition= new ChessPosition(i,j);
                if(board.getPiece(currentPosition) == null){continue;}
                if (board.getPiece(currentPosition).getTeamColor() !=teamColor){
                    allPossibleMovesList.addAll(board.getPiece(currentPosition).pieceMoves(board, currentPosition));
                }
            }
        }
        return !isInCheck(teamColor) && allPossibleMovesList.isEmpty();
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {this.board=board;}

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessGame chessGame)) {
            return false;
        }
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "teamTurn=" + teamTurn +
                ", board=" + board.toString() +
                '}';
    }
}
