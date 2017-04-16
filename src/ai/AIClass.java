package ai;
public abstract class AIClass {

    abstract public int getNewMove(int input);
    abstract public void processMove(int input, char c);
    abstract void reset();


}