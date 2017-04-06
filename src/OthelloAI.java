import java.util.ArrayList;
import java.util.Random;

public class OthelloAI extends AIClass {

    ArrayList<Integer> movesDone = new ArrayList<Integer>();
    ArrayList<Integer> possibleMoves;
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
    public int getNewMove(int input) {

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
        possibleMoves = board.findPossibleMoves();
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

    public static void main(String[] args){
    	int move1;
    	int move2;
    	OthelloAI ai = new OthelloAI('B');
    	OthelloAI ai2 = new OthelloAI('W');
    	OthelloBoard board = ai.getBoard();
    	
    	System.out.println(board.findPossibleMoves());
    	board.printBoard();
    	System.out.println("");
    	
    	move1 = ai.getNewMove(-1);
    	System.out.println(move1);
    	
    	while(board.getBoard().size() < 60){
    		board.printBoard();
    		System.out.println("");
    		move2 = ai2.getNewMove(move1);
    		move1 = ai.getNewMove(move2);
    	}
    	board.printBoard();
    }
}