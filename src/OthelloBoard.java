import java.util.ArrayList;


public class OthelloBoard implements Cloneable{
	ArrayList<OthelloCoordinate> board = new ArrayList<OthelloCoordinate>();
	private ArrayList<Integer> possibleMoves = new ArrayList<Integer>();
	char ownToken;
	
	OthelloBoard(char c){
		ownToken = c;
		reset();
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
     * voegt een coordinaat toe an het bord. Als het coordinaat al in het bord zit,
     *  dan verandert het het token naar het token van het nieuwe coordinaat.
     *  Werkt met x en y coordinaten
     * @param x X coordinaat van het coordinaat (0-7)
     * @param y	Y coordinaat van het coordinaat (0-7)
     * @param c De kleur van het coordinaat ('W' of 'B')
     */
	public void addCoordinate(int x, int y, char c){
		if(x >= 0 && x < 8 && y >= 0 && y < 8){
			OthelloCoordinate coord = new OthelloCoordinate(x,y);
			coord.setToken(c);
			if(!board.contains(coord)){
				board.add(coord);
			} else {
				OthelloCoordinate coord2 = getCoordinate(x, y);
				coord2.setToken(c);
			}
		}
	}
	
	/**
     * voegt een coordinaat toe an het bord. Als het coordinaat al in het bord zit,
     *  dan verandert het het token naar het token van het nieuwe coordinaat.
     *  Werkt met index
     * @param index Index van het coordinaat (0-63)
     * @param c De kleur van het coordinaat ('W' of 'B')
     */
	public void addCoordinate(int index, char c){
		if(index >= 0 && index <64){
			OthelloCoordinate coord = new OthelloCoordinate(index);
			coord.setToken(c);
			if(!hasCoord(coord)){
				board.add(coord);
			}else {
				OthelloCoordinate coord2 = getCoordinate(index % 8, index / 8);
				coord2.setToken(c);
			}
		}
	}
	
	/**
     * Returnt een coordinaat op een bepaald punt in het bord.
     * @param x X coordinaat van het coordinaat (0-7)
     * @param y	Y coordinaat van het coordinaat (0-7)
     * @return coord het coordinaat (return null als hij niet in het bord zit)
     */
	public OthelloCoordinate getCoordinate(int x, int y){
		for (OthelloCoordinate coord : board){
			if(coord.getX() == x && coord.getY() == y){
				return coord;
			}
		}
		return null;
	}

	/**
     * Returnt het bord
     * @return board Het bord
     */
	public ArrayList<OthelloCoordinate> getBoard(){
		return board;
	}
	
	/**
     * print het bord in een 2 dimensionale overzichtelijke manier
     */
	public void printBoard(){
		//System.out.println(board);
		for(int x = 0; x < 8; x++) {
    		String line = "";
    		
    		for(int y = 0; y < 8; y++) {
        		OthelloCoordinate coord = getCoordinate(x, y);
        		
        		if(coord == null) {
        			line+="#";
        		} else {
        			line+=coord.getToken();
        		}
        	}
    		
    		System.out.println(line);
    	}
	}

	/**
     * Reset het bord, voegt de standaard start steentjes toe
     */
	public void reset() {
		board.clear();
		addCoordinate(3,3,'W');
		addCoordinate(4,4,'W');
		addCoordinate(3,4,'B');
		addCoordinate(4,3,'B');	
	}
	
	/**
     * Geeft alle mogelijke moves voor een bepaalde kleur.
     * @param token De kleur die de zet gaat maken
     * @return possibleMoves Een lijst met mogelijke moves die de kleur token kan maken
     */
	synchronized ArrayList<Integer> findPossibleMoves(char token){
		possibleMoves.clear();
		for (OthelloCoordinate coord : board){
			searchSurroundingCorners(coord, token);
		}
		return possibleMoves;
	}

	/**
     * Zoekt door alle plekken om een bepaald punt heen om te kijken of het een mogelijke move is.
     * voegt alle mogelijke moves die geen duplicates zijn toe aan possibleMoves.
     * @param coord Het coordinaat uit het bord waar we recursief omheen gaan zoeken.
     * @param token De kleur die de zet gaat maken.
     */
	private void searchSurroundingCorners(OthelloCoordinate coord, char token) {
		int x = coord.getX();
		int y = coord.getY();
		OthelloCoordinate newCoord;
		for(int i=-1;i<=1;i++){
			for(int j=-1;j<=1;j++){
				if(!((i==0) && (j==0))){
					newCoord = new OthelloCoordinate(x+i,y+j);
					if(hasCoord(newCoord)){
						newCoord.setToken(getCoordinate(x+i,y+j).getToken());
					}
					if(isValid(newCoord, token) && !(possibleMoves.contains(newCoord.getIndex()))){
						possibleMoves.add(newCoord.getIndex());
					}
				}
			}
		}
	}

	/**
     * Geeft aan of een bepaalde move een een toegestane move is. Hiervoor moet het
     * coordinaat binnen het bord zitten, nog niet gevuld zijn en een mogelijk pad hebben.
     * @param coord Het coordinaat waarvan we kijken of hij toegestaan is
     * @param token De kleur die de zet gaat maken.
     * @return boolean true als de move toegestaan is, false als de move niet toegestaan is.
     */
	public boolean isValid(OthelloCoordinate coord, char token) {
		if(!isInBoard(coord)){
			return false;
		}
		if(hasCoord(coord)){
			return false;
		}
		return hasPossiblePath(coord, token);

	}

	/**
     * Kijkt voor een bepaald coordinaat of er een legaal pad is naar andere steentjes.
     * @param coord Het coordinaat waarvan we kijken of hij een pad heeft
     * @param token De kleur die de zet gaat maken.
     * @return boolean true als er minstens ��n pad gevonden is, false als er geen pad te vinden is.
     */
	private boolean hasPossiblePath(OthelloCoordinate coord, char token) {
		for(int i=-1;i<=1;i++){
			for(int j=-1;j<=1;j++){
				if(!((i==0) && (j==0))){
					if(checkDirection(i,j,coord, 0, token) != -1){
						return true;
					}
				}
			}
		}
		
		return false;
	}

	/**
     * Kijkt voor een bepaald coordinaat in een bepaalde richting of een pad te vinden is
     * @param i De x richting waarheen we gaan zoeken.
     * @param j De y richting waarheen we gaan zoeken.
     * @param coord Het coordinaat die we nu bekijken
     * @param depth De diepte van de recursie. Dit geeft aan hoe ver we al aan het zoeken zijn in een richting.
     * @param token De kleur die de zet gaat maken.
     * @return depth de diepte van de recursie, dit geeft aan hoe ver we al aan het zoeken zijn in een richting.
     *  Dit is nuttig om een path te flippen op het bord. Het geeft dus tegelijkertijd aan of de directie toegestaan is en hoe ver het pad uiteindelijk is.
     */
	private int checkDirection(int i, int j, OthelloCoordinate coord, int depth, char token) {
		OthelloCoordinate newCoord = getCoordinate(coord.getX()+i,coord.getY()+j);
		
		if(newCoord == null){
			return -1;
		}
		
		if(newCoord.getToken() == token && depth == 0){
			return -1;
		}
		if(newCoord.getToken() == token && depth > 0){
			return depth;
		}
		return checkDirection(i, j, newCoord, depth+1, token);
	}

	/**
     * Kijkt of een bepaald coordinaat in het bord gevuld is.
     * @param coordIn Het coordinaat.
     * @return boolean true als het coordinaat gevuld is, false als het coordinaat niet gevuld is.
     */
	private boolean hasCoord(OthelloCoordinate coordIn) {
		return getCoordinate(coordIn.getX(),coordIn.getY()) != null;

	}
	
	/**
     * Kijkt of een bepaald coordinaat binnen het bord zit.
     * @param coord Het coordinaat.
     * @return boolean true als het coordinaat binnen het bord zit, fals als het niet binnen het bord zit.
     */
	private boolean isInBoard(OthelloCoordinate coord){
		int x = coord.getX();
		int y = coord.getY();
		return ((x>=0 && x < 8) && (y>=0 && y < 8));
	}

	/**
     * Krijgt een move en kleur als input, en gaat dan alle mogelijke paden flippen.
     * Dit is het verwerken van een move in het bord.
     * @param input De move die verwerkt moet worden
     * @param token De kleur van de move die gezet wordt
     */
	public void flipPaths(int input, char token) {
		int depth;
		OthelloCoordinate coord = new OthelloCoordinate(input);
		for(int i=-1;i<=1;i++){
			for(int j=-1;j<=1;j++){
				if(!((i==0) && (j==0))){
					depth = getCorrectPathDepth(i,j,coord,token);
					if(depth > 0){
						flipFoundPath(coord, i,j,depth,token);
					}
				}
			}
		}
		
	}

	/**
     * Er is een pad gevonden en die wordt hier geflipt.
     * @param coord het coordinaat die nu geflipt wordt
     * @param i De x richting waarheen we bewegen.
     * @param j De y richting waarheen we bewegen.
     * @param depth de diepte van de recursie van getCorrectPathDepth. Dit is de lengte van het pad
     * @param token De kleur van de move die gezet wordt
     */
	private void flipFoundPath(OthelloCoordinate coord, int i, int j, int depth, char token) {
		OthelloCoordinate newCoord = coord;
		for(int n=0; n<=depth;n++){
			addCoordinate(newCoord.getIndex(), token);
			newCoord = new OthelloCoordinate(newCoord.getX()+i,newCoord.getY()+j);
			newCoord.setToken(token);
		}
		
	}

	/**
     * Maakt gebruik van checkdirection om de lengte van het pad te vinden.
     * De depth die daar gereturnt wordt geeft aan dat het pad toegestaan is als hij groter dan 0 is.
     * Ook geeft het de lengte van het pad aan.
     * @param i De x richting waarheen we bewegen.
     * @param j De y richting waarheen we bewegen.
     * @param coord het coordinaat van de move die gezet wordt
     * @param token De kleur van de move die gezet wordt
     * @return depth de diepte van het pad (en dus de lengte)
     */
	private int getCorrectPathDepth(int i, int j, OthelloCoordinate coord, char token) {
		return checkDirection(i,j,coord,0, token);
		
	}
}
