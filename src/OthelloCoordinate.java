
public class OthelloCoordinate {

	int x;
	int y;
	char token = '#';
	
	public OthelloCoordinate(int xIn, int yIn) {
		x = xIn;
		y = yIn;
	}
	
	public OthelloCoordinate(int index){
		x = index % 8;
		y = index / 8;
	}

	public void setToken(char c){
		token = c;
	}
	public char getToken(){
		return token;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public int getIndex(){
		return x + y * 8;
	}
	
	@Override
	public String toString(){
		return "x = " + x + " y = " + y + " token = " + token;
	}
}
