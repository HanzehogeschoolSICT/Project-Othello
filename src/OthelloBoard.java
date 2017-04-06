import java.util.ArrayList;


public class OthelloBoard {
	ArrayList<OthelloCoordinate> board = new ArrayList<OthelloCoordinate>();
	private ArrayList<Integer> possibleMoves = new ArrayList<Integer>();
	char token;
	
	OthelloBoard(char c){
		token = c;
		reset();		
	}
	
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
	public OthelloCoordinate getCoordinate(int x, int y){
		for (OthelloCoordinate coord : board){
			if(coord.getX() == x && coord.getY() == y){
				return coord;
			}
		}
		return null;
	}

	public ArrayList<OthelloCoordinate> getBoard(){
		return board;
	}
	
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

	public void reset() {
		board.clear();
		addCoordinate(3,3,'W');
		addCoordinate(4,4,'W');
		addCoordinate(3,4,'B');
		addCoordinate(4,3,'B');	
	}
	
	ArrayList<Integer> findPossibleMoves(){
		possibleMoves.clear();
		for (OthelloCoordinate coord : board){
			searchSurroundingCorners(coord);
		}
		return possibleMoves;
	}

	private void searchSurroundingCorners(OthelloCoordinate coord) {
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
					if(isValid(newCoord) && !(possibleMoves.contains(newCoord.getIndex()))){
						possibleMoves.add(newCoord.getIndex());
					}
				}
			}
		}
	}

	public boolean isValid(OthelloCoordinate coord) {
		if(!isInBoard(coord)){
			return false;
		}
		if(hasCoord(coord)){
			return false;
		}
		return hasPossiblePath(coord);

	}

	private boolean hasPossiblePath(OthelloCoordinate coord) {
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

	private int checkDirection(int i, int j, OthelloCoordinate coord, int depth, char c) {
		OthelloCoordinate newCoord = getCoordinate(coord.getX()+i,coord.getY()+j);
		
		if(newCoord == null){
			return -1;
		}
		
		if(newCoord.getToken() == c && depth == 0){
			return -1;
		}
		if(newCoord.getToken() == c && depth > 0){
			return depth;
		}
		return checkDirection(i, j, newCoord, depth+1, c);
	}

	private boolean hasCoord(OthelloCoordinate coordIn) {
		return getCoordinate(coordIn.getX(),coordIn.getY()) != null;

	}
	
	private boolean isInBoard(OthelloCoordinate coord){
		int x = coord.getX();
		int y = coord.getY();
		return ((x>=0 && x < 8) && (y>=0 && y < 8));
		
		
	}

	public void flipPaths(int input, char c) {
		int depth;
		OthelloCoordinate coord = new OthelloCoordinate(input);
		for(int i=-1;i<=1;i++){
			for(int j=-1;j<=1;j++){
				if(!((i==0) && (j==0))){
					depth = getCorrectPathDepth(i,j,coord,c);
					if(depth > 0){
						flipFoundPath(coord, i,j,depth,c);
					}
				}
			}
		}
		
	}

	private void flipFoundPath(OthelloCoordinate coord, int i, int j, int depth, char c) {
		OthelloCoordinate newCoord = coord;
		for(int n=0; n<=depth;n++){
			addCoordinate(newCoord.getIndex(), c);
			newCoord = new OthelloCoordinate(newCoord.getX()+i,newCoord.getY()+j);
			newCoord.setToken(c);
		}
		
	}

	private int getCorrectPathDepth(int i, int j, OthelloCoordinate coord, char c) {
		return checkDirection(i,j,coord,0, c);
		
	}
}
