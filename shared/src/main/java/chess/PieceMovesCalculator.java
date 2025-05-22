package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public interface PieceMovesCalculator {
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor color);

    public default boolean outOfBounds(ChessPosition position){
        return position.getColumn() > 8 || position.getColumn() <= 0 || position.getRow() > 8 || position.getRow() <= 0;
    }

    public default boolean canCapture(ChessBoard board, ChessPosition position, ChessGame.TeamColor color){
        return board.getPiece(position)!= null && board.getPiece(position).getTeamColor().equals(color);
    }

    public default ArrayList<ChessMove> scanPattern(ChessBoard board, ChessPosition pstn, ChessGame.TeamColor color, int rowPatt, int columnPatt){
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessPosition newPosition = new ChessPosition(pstn.getRow() + rowPatt, pstn.getColumn() + columnPatt);
        while(true){
            if(outOfBounds(newPosition) || canCapture(board, newPosition, color)){break;}
            moves.add(new ChessMove(pstn, newPosition, null));
            if(board.getPiece(newPosition)!= null && !board.getPiece(newPosition).getTeamColor().equals(color)){break;}
            newPosition = new ChessPosition(newPosition.getRow() + rowPatt, newPosition.getColumn() + columnPatt);
        }
        return moves;
    }
}

class KingMovesCalculator implements PieceMovesCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor color){
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessMove current;
        ChessPosition newPosition;
        for(int i=-1; i<=1; i++) {
            for (int j = -1; j <= 1; j++) {
                newPosition = new ChessPosition(position.getRow()+i, position.getColumn()+j);
                if (outOfBounds(newPosition)){
                    continue;
                }
                current= new ChessMove(position, newPosition, null);
                if(current.getStartPosition().equals(current.getEndPosition())){
                    continue;
                }
                if(board.getPiece(newPosition)!= null && board.getPiece(newPosition).getTeamColor().equals(color)){
                    continue;
                }
                moves.add(current);
            }
        }
        return moves;
    }

}

class BishopMovesCalculator implements PieceMovesCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        moves.addAll(scanPattern(board, position, color, 1, 1));
        moves.addAll(scanPattern(board, position, color, 1, -1));
        moves.addAll(scanPattern(board, position, color, -1, -1));
        moves.addAll(scanPattern(board, position, color, -1, 1));
        return moves;
    }
}

class RookMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        moves.addAll(scanPattern(board, position, color, 1, 0));
        moves.addAll(scanPattern(board, position, color, -1, 0));
        moves.addAll(scanPattern(board, position, color, 0, -1));
        moves.addAll(scanPattern(board, position, color, 0, 1));
        return moves;
    }
}

class QueenMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        moves.addAll(scanPattern(board, position, color, 1, 0));
        moves.addAll(scanPattern(board, position, color, -1, 0));
        moves.addAll(scanPattern(board, position, color, 0, -1));
        moves.addAll(scanPattern(board, position, color, 0, 1));
        moves.addAll(scanPattern(board, position, color, 1, 1));
        moves.addAll(scanPattern(board, position, color, 1, -1));
        moves.addAll(scanPattern(board, position, color, -1, -1));
        moves.addAll(scanPattern(board, position, color, -1, 1));
        return moves;
    }
}

class KnightMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        ArrayList<ChessPosition> endPositions = new ArrayList<>(Arrays.asList(new ChessPosition(row+1, col+2),
                new ChessPosition(row+1, col-2), new ChessPosition(row-1, col+2), new ChessPosition(row-1, col-2),
                new ChessPosition(row+2, col+1), new ChessPosition(row+2, col-1), new ChessPosition(row-2, col+1),
                new ChessPosition(row-2, col-1)));
        for(ChessPosition newPosition: endPositions){
            if(!outOfBounds(newPosition) && (board.getPiece(newPosition)== null || !board.getPiece(newPosition).getTeamColor().equals(color))){
                moves.add(new ChessMove(position, newPosition, null));
            }
        }
        return moves;
    }
}

class PawnMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int direction = 1;
        if (color == ChessGame.TeamColor.BLACK){direction = -1;}
        //diagonal moves
        ChessPosition newPosition = new ChessPosition(position.getRow()+direction, position.getColumn()+1);
        if (!outOfBounds(newPosition) && canCapture(board, newPosition, color)) {
            moves.addAll(promotionGenerator(new ChessMove(position, newPosition, null), color));
        }
        newPosition = new ChessPosition(position.getRow()+direction, position.getColumn()-1);
        if (!outOfBounds(newPosition) && canCapture(board, newPosition, color)) {
            moves.addAll(promotionGenerator(new ChessMove(position, newPosition, null), color));
        }
        //forward moves
        newPosition = new ChessPosition(position.getRow()+direction, position.getColumn());
        if(board.getPiece(newPosition) == null){
            moves.addAll(promotionGenerator(new ChessMove(position, newPosition, null), color));
            if((position.getRow()==2 && color== ChessGame.TeamColor.WHITE) || (position.getRow()==7 && color== ChessGame.TeamColor.BLACK)){
                newPosition = new ChessPosition(newPosition.getRow()+direction, newPosition.getColumn());
                if(board.getPiece(newPosition) == null) {moves.add(new ChessMove(position, newPosition, null));}
            }
        }
        return moves;
    }

    @Override
    public boolean canCapture(ChessBoard board, ChessPosition position, ChessGame.TeamColor color){
        return board.getPiece(position)!= null && !board.getPiece(position).getTeamColor().equals(color);
    }

    private Collection<ChessMove> promotionGenerator(ChessMove move, ChessGame.TeamColor color){
        ArrayList<ChessMove> moves = new ArrayList<>();
        int finalRow = move.getEndPosition().getRow();
        if((finalRow==8 && color==ChessGame.TeamColor.WHITE) || (finalRow==1 && color==ChessGame.TeamColor.BLACK)){
            moves.add(new ChessMove(move.getStartPosition(), move.getEndPosition(), ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(move.getStartPosition(), move.getEndPosition(), ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(move.getStartPosition(), move.getEndPosition(), ChessPiece.PieceType.KNIGHT));
            moves.add(new ChessMove(move.getStartPosition(), move.getEndPosition(), ChessPiece.PieceType.ROOK));
        }
        else{
            moves.add(move);
        }
        return moves;
    }
}