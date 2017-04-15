import java.util.ArrayList;
import java.util.Random;

public class OthelloAI extends AIClass {

    ArrayList<Integer> movesDone = new ArrayList<Integer>();
    ArrayList<Integer> possibleMoves;
    ArrayList<Integer> possibleOpponentMoves;
    int move;
    OthelloBoard board;
    char token;
    char oppositeToken;

    public OthelloAI(char c){
    	token = c;
    	oppositeToken = c=='W'? 'B' : 'W';
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
    public int calculateMove(){
        possibleMoves = board.findPossibleMoves(token);
        if(possibleMoves.size()>0){
            Random random = new Random();
            int newMove = possibleMoves.get(random.nextInt(possibleMoves.size()));
            return newMove;
        }else{
            return -1;
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
}