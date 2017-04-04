public abstract class AIClass {
    protected String board;

    abstract public int getNewMove(int input);
    abstract public void processMove(int input, char c);
    abstract void reset();


}