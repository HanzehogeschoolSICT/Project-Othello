package ai;
import java.util.ArrayList;
import java.util.Random;

public class TicTacToeAI extends AIClass {
    ArrayList<Integer> movesDone = new ArrayList<Integer>();
    int move;
    String board;

    public TicTacToeAI(){
        board = "#########";
    }

    @Override
    public int getNewMove(int input) {
        System.out.print("");
        if(movesDone.contains(input) || movesDone.size() == 9){
            return move;
        }
        if (input != -1){
            processMove(input, 'p');				//the control class is supposed to process the moves depending on if the server accepts them
        }
        move = calculateMove();
        processMove(move, 'c');
        return move;
    }

    public int calculateMove(){
        System.out.print("");
        Random random = new Random();
        int newMove = random.nextInt(9);
        while(movesDone.contains(newMove)){
            newMove = random.nextInt(9);
        }
        return newMove;
    }

    @Override
    public void processMove(int input, char c) {
        System.out.print("");
        movesDone.add(input);
        board = adaptBoard(input,c);
    }

    private String adaptBoard(int input, char c){
        System.out.print("");
        if(input<0 || input>8){
            return board;
        }
        char[] chars = board.toCharArray();
        chars[input] = c;
        return String.valueOf(chars);
    }
    public void printBoard(){
        System.out.println(board);
    }

    @Override
	public
    void reset() {
        board = "#########";
        movesDone.clear();
    }
}