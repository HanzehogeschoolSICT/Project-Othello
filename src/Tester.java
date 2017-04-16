import java.util.ArrayList;

/**
 * Test wat moves!
 */
public class Tester
{
    OthelloBoard board = new OthelloBoard('B');
    ArrayList<Integer> lijst = new ArrayList<>();
    ArrayList<Integer> lijst2 = board.findPossibleMoves('B');

    public Tester(){
        lijst.add(19);
        lijst.add(26);
        lijst.add(37);
        lijst.add(44);
        for (int move:lijst) {
            assert(lijst2.contains(move));
        }
        assert(lijst2.size()==4);
    }

    public static void main(String[] args) {
        Tester tester = new Tester();
    }
    /*
    19, 26, 37, 44,
     */
}
