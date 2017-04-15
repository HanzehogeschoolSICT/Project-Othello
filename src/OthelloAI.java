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

    @Override
    public void processMove(int input, char c) {
    	movesDone.add(input);
    	if(c == 'p'){
            adaptBoard(input,oppositeToken);
    	} else{
    		adaptBoard(input,token);
    	}
    }

    private void adaptBoard(int input, char c){
        board.flipPaths(input, c);
    }

    public void printBoard(){
        board.printBoard();
    }

    @Override
    void reset() {
        board.reset();
        movesDone.clear();
    }
    
    public OthelloBoard getBoard(){
    	return board;
    }
}