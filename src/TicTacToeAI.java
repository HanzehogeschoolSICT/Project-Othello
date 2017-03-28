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
		if(movesDone.contains(input)){
			return -1;
		}
		//processMove(input,'p');				the control class is supposed to process the moves depending on if the server accepts them
		Random random = new Random();
		move = random.nextInt(9);
		while(movesDone.contains(move)){
			move = random.nextInt(9);
		}
		//processMove(move, 'ç');
		return move;
	}

	@Override
	public void processMove(int input, char c) {
		movesDone.add(input);
		super.board = adaptBoard(input,c);
	}
	
	private String adaptBoard(int input, char c){
		if(input<0 || input>=8){
	        return super.board;
	    }
	    char[] chars = super.board.toCharArray();
	    chars[input] = c;
	    return String.valueOf(chars);  
	}
	public void printBoard(){
		System.out.println(super.board);
	}
	
	/* test code
	public static void main(String [ ] args) {
		TicTacToeAI ai = new TicTacToeAI();
		ai.printBoard();
		System.out.println(ai.getNewMove(1));
		ai.printBoard();
		System.out.println(ai.getNewMove(5));
		ai.printBoard();
	}
	*/
}
