import java.util.ArrayList;
import java.util.Random;

public class TicTacToeAI extends AIClass {
    ArrayList<Integer> movesDone = new ArrayList<Integer>();
    int move;

    public TicTacToeAI(){
        super.board = "#########";
    }

    @Override
    public int getNewMove(int input) {
        if(movesDone.contains(input) || movesDone.size() == 9){
            return -1;
        }
        if (input != -1){
        	processMove(input, 'p');				//the control class is supposed to process the moves depending on if the server accepts them
        }
        move = calculateMove();
        processMove(move, 'c');
        return move;
    }
    
    public int calculateMove(){
		Random random = new Random();
        int newMove = random.nextInt(9);
        while(movesDone.contains(newMove)){
            newMove = random.nextInt(9);
        }
        return newMove;
	}

    @Override
    public void processMove(int input, char c) {
        movesDone.add(input);
        super.board = adaptBoard(input,c);
    }

    private String adaptBoard(int input, char c){
        if(input<0 || input>8){
            return super.board;
        }
        char[] chars = super.board.toCharArray();
        chars[input] = c;
        return String.valueOf(chars);
    }
    public void printBoard(){
        System.out.println(super.board);
    }

	@Override
	void reset() {
		super.board = "#########";
		movesDone.clear();
	}
}