package model;

public class OthelloCoordinate implements Cloneable {

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

	/**
     * Stelt de kleur van het coordinaat in
     * @param c De kleur van het coordinaat ('W' of 'B')
     */
	public void setToken(char c){
		token = c;
	}
	
	/**
     * geeft de kleur van het coordinaat
     * return token De kleur van het coordinaat ('W' of 'B')
     */
	public char getToken(){
		return token;
	}
	
	/**
     * geeft het x coordinaat van het coordinaat
     * @return x Het x coordinaat (0-7)
     */
	public int getX(){
		return x;
	}
	
	/**
     * geeft het y coordinaat van het coordinaat
     * @return y Het y coordinaat (0-7)
     */
	public int getY(){
		return y;
	}
	
	/**
     * geeft de index van het coordinaat
     * @return index De index van het coordinaat (0-63)
     */
	public int getIndex(){
		return x + y * 8;
	}
	
	/**
     * Print het coordinaat op een nette manier.
     * @return String alle info van het coordinaat op een nette manier geprint.
     */
	@Override
	public String toString(){
		return "x = " + x + " y = " + y + " token = " + token;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		OthelloCoordinate newcoord = new OthelloCoordinate(x,y);
		newcoord.setToken(token);
		return newcoord;
	}
}
