package chess;

import java.util.ArrayList;
import java.util.Collection;

public interface PieceMovesCalculator {
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor color);

    public default boolean outOfBounds(ChessPosition position){
        return position.getColumn() > 8 || position.getColumn() <= 0 || position.getRow() > 8 || position.getRow() <= 0;
    }

    public default boolean canCapture(ChessBoard board, ChessPosition position, ChessGame.TeamColor color){
        return board.getPiece(position)!= null && board.getPiece(position).getTeamColor().equals(color);
    }

    public default ArrayList<ChessMove> scanPattern(ChessBoard board, ChessPosition position, ChessGame.TeamColor color, int rowPattern, int columnPattern){
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessPosition newPosition = new ChessPosition(position.getRow() + rowPattern, position.getColumn() + columnPattern);
        while(true){
            if(outOfBounds(newPosition) || canCapture(board, newPosition, color)){break;}
            moves.add(new ChessMove(position, newPosition, null));
            if(board.getPiece(newPosition)!= null && !board.getPiece(newPosition).getTeamColor().equals(color)){break;}
            newPosition = new ChessPosition(newPosition.getRow() + rowPattern, newPosition.getColumn() + columnPattern);
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

    private boolean allowedMove(ChessBoard board, ChessMove move){
        return true;
    }
}

class BishopMovesCalculator implements PieceMovesCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessMove current;
        moves.addAll(scanPattern(board, position, color, 1, 1));
        moves.addAll(scanPattern(board, position, color, 1, -1));
        moves.addAll(scanPattern(board, position, color, -1, -1));
        moves.addAll(scanPattern(board, position, color, -1, 1));
        return moves;
    }
}

//class KnightMovesCalculator implements PieceMovesCalculator {
//    @Override
//    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
//        ArrayList<ChessMove> moves = new ArrayList<>();
//        ChessMove current;
//        ChessPosition newPosition = new ChessPosition(position.getRow(), position.getColumn() + 1);
//        while(true){
//            if(outOfBounds(newPosition) || canCapture(board, newPosition, color)){break;}
//            moves.add(new ChessMove(position, newPosition, null));
//            if(board.getPiece(newPosition)!= null && !board.getPiece(newPosition).getTeamColor().equals(color)){break;}
//            newPosition = new ChessPosition(newPosition.getRow(), newPosition.getColumn() + 1);
//        }
//    }
//}