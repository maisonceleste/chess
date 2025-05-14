package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor color;
    private ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;

    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;

    }

    public void promotePiece(ChessPiece.PieceType promotionPiece) {
        if(type != PieceType.PAWN || promotionPiece==null){return;}
        this.type = promotionPiece;

    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if(type==PieceType.KING){
            return new KingMovesCalculator().pieceMoves(board, myPosition, color);
        }
        else if(type==PieceType.BISHOP){
            return new BishopMovesCalculator().pieceMoves(board, myPosition, color);
        }
        else if(type==PieceType.ROOK){
            return new RookMovesCalculator().pieceMoves(board, myPosition, color);
        }
        else if(type==PieceType.QUEEN){
            return new QueenMovesCalculator().pieceMoves(board, myPosition, color);
        }
        else if(type==PieceType.KNIGHT){
            return new KnightMovesCalculator().pieceMoves(board, myPosition, color);
        }
        else if(type==PieceType.PAWN){
            return new PawnMovesCalculator().pieceMoves(board, myPosition, color);
        }

        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "color=" + color +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object comparePiece){
        if(!(comparePiece instanceof ChessPiece)){return false;}
        if(this == comparePiece){return true;}

        ChessPiece confirmedComparePiece = (ChessPiece) comparePiece;
        return this.getPieceType()==confirmedComparePiece.getPieceType() && this.getTeamColor()==confirmedComparePiece.getTeamColor();
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type);
    }
}
