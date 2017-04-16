import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class OthelloAI extends AIClass {

    private static final int SEARCHDEPHT = 9;

    ArrayList<Integer> movesDone = new ArrayList<Integer>();
    ArrayList<Integer> possibleMoves;
    int move;
    OthelloBoard board;
    char token;
    char oppositeToken;

    int currentProcessingMove;
    //TODO change to better value deciding algorithm, currently minimized possible moves opponent
    Integer  bestMove = null;
    Integer bestMoveValue = -1;


    public OthelloAI(char c){
    	token = c;
    	oppositeToken = c == 'W' ? 'B' : 'W';
    	board = new OthelloBoard(token);
    }

    /**
     * Verwerkt eventueel de move van de tegenstander, berekend de move van de ai en verwerkt dan de move van de ai.
     * @param input De move van de tegenstander. (-1 = tegenstander doet geen move, dus wij beginnen en dit is zet 1)
     * @return move De move die de ai maakt.
     */
    @Override
    public synchronized int getNewMove(int input) {
        if(movesDone.contains(input) || movesDone.size() == 64){
            return -1;
        }
        if(input != -1){
            processMove(input, 'p');
        }
        move = calculateMove();
        processMove(move, 'c');
        return move;
    }

    /**
     * Berekend de nieuwe move.
     * @return newMove De move die de ai gaat zetten. Op dit moment volledig random
     */
    private int calculateMove(){
        bestMove = null;
        bestMoveValue = -1;
        possibleMoves = board.findPossibleMoves(token);
        if(possibleMoves.size() > 0){
            try {
                ArrayList<Integer> possiblemoves = (ArrayList<Integer>) board.findPossibleMoves(token).clone();
                for(Integer move: possiblemoves) {
                    OthelloBoard clone = (OthelloBoard) board.clone();
                    clone.flipPaths(move, token);

                    System.out.println("CLONE " + (board.getBoard() == clone.getBoard()));
                    System.out.println("CLONE2 " + (board.getBoard().get(0) == clone.getBoard().get(0)));
                    System.out.println("CLONE3 " + (board.findPossibleMoves(token) == clone.findPossibleMoves(token)));

                    currentProcessingMove = move;
                    calculateMove(clone, false, 0);
                }
                return bestMove;
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                return -1;
            }
        }else{
            return -1;
        }
    }

    private void calculateMove(OthelloBoard board, boolean myturn, int depth) throws CloneNotSupportedException {
        System.out.println("checking move");
        if (depth >= SEARCHDEPHT) {
            System.out.println("Now considering. " + currentProcessingMove);
            int opponentmoves = board.findPossibleMoves(oppositeToken).size();

            if (opponentmoves < bestMoveValue || bestMoveValue == -1) {
                bestMoveValue = opponentmoves;
                bestMove = currentProcessingMove;
                System.out.println("New best move " + currentProcessingMove);
            }
            return;
        }

        if (myturn) {
            ArrayList<Integer> possiblemoves = board.findPossibleMoves(token);

            // No possible moves, give turn to opponent
            if (possiblemoves.size() == 0) {
                calculateMove(board, false, ++depth);
            }

            for (Integer move: possiblemoves) {
                OthelloBoard clone = (OthelloBoard) board.clone();
                clone.flipPaths(move, token);
                calculateMove(clone, false, ++depth);
            }
        }
        else {
            ArrayList<Integer> possiblemoves = board.findPossibleMoves(oppositeToken);

            // No possible moves, give turn to opponent
            if (possiblemoves.size() == 0) {
                calculateMove(board, true, ++depth);
            }

            for (Integer move: possiblemoves) {
                OthelloBoard clone = (OthelloBoard) board.clone();
                clone.flipPaths(move, oppositeToken);
                calculateMove(clone, true, ++depth);
            }
        }
    }


    /**
     * Verwerkt een move
     * @param input De move die verwerkt moet worden
     * @param c	Het token van de speler die een move doet (tegenstander), c = computer (deze ai)
     */
    @Override
    public void processMove(int input, char c) {
    	movesDone.add(input);
    	if(c == 'p'){
            adaptBoard(input,oppositeToken);
    	} else{
    		adaptBoard(input,token);
    	}
    }

    /**
     * Method van de abstract superclass, deze past het bord aan.
     * @param input De move die verwerkt moet worden
     * @param c	Het token van de move die verwerkt moet worden 'W' = wit 'B' = zwart
     */
    private void adaptBoard(int input, char c){
        board.flipPaths(input, c);
    }

    /**
     * Print het bord
     */
    public void printBoard(){
        board.printBoard();
    }

    /**
     * Reset de ai
     */
    @Override
    void reset() {
        board.reset();
        movesDone.clear();
    }
    
    /**
     * Getter voor het bord
     */
    public OthelloBoard getBoard(){
    	return board;
    }

    public static void main(String[] args) {
        OthelloAI ai = new OthelloAI('W');
        ai.calculateMove();
    }
}