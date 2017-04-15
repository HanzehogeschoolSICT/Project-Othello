import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * TODO: Status labels updaten zonder dat de boel vastloopt. Gebruikte bronnen:
 * Introduction to Java Programming.
 */
public class OTHGameController {
	@FXML
	private Button quitButton;
	@FXML
	private Label statusLabel;
	@FXML
	private GridPane gridPane;
	@FXML
	private Label ownNameLabel;
	@FXML
	private Label oppNameLabel;
	@FXML
	private Label turnLabel;

	private Model model;
	private ServerIn sIn;
	private Cell[][] cell = new Cell[8][8];
	private int rowSelected;
	private int columnSelected;

	private String ownName;
	private String opponentName;
	private char ownToken;
	private char oppToken;
	private Window oldWindow;

	private String lastMsg = "";
	private String lastMove = "";
	private String lastTurn = "";

	private int finalMove;

	private boolean myTurn;
	private boolean withAI;
	private int moveToDo;
	int check = 1;
	OthelloAI othAI;
	volatile OthelloBoard board;

	public OTHGameController() {
	}

	public void initData(String name, Window window, boolean AI) throws InterruptedException {
		ownName = name;
		oldWindow = window;
		ownNameLabel.setText(ownName);
		withAI = AI;
		check = 1;
		controlGame();
	}

	@FXML
    public void doForfeit() {
        sendCommand("forfeit");
        check = 0;
    }
	
	public void initModel(Model conModel, ServerIn consIn) {
		model = conModel;
		sIn = consIn;
	}

	public void updateLabel(Label upLabel, String text) {
		Platform.runLater(() -> upLabel.setText(text));
	}

	public void sendCommand(String cmd) {
		model.sendToServer(cmd);
	}

	public void generateBoard() {
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				OthelloCoordinate coord;
				coord = board.getCoordinate(x, y);
				if (coord != null) {
					setTeken(coord.getX(), coord.getY(), coord.getToken());
				}
			}

		}
	}

	public void controlGame() {
		new Thread(() ->{
		try{
			while (check == 1) {
			checkColor();
		}
		while (check == 2) {
			playGame();
		}
	}catch(Exception ex){
		ex.printStackTrace();}
		}).start();
	}

	private void checkColor() {
		if (!sIn.getMsg().equals(lastMsg) && !sIn.getMsg().equals("")) {
			opponentName = sIn.getOppName();
			if (sIn.getMsg().contains("PLAYERTOMOVE: " + '"' + ownName + '"')) {
				initBoard('B');
				updateLabel(ownNameLabel, "Ik ben Zwart");
				updateLabel(oppNameLabel, opponentName + " is  Wit");
			}
			if (sIn.getMsg().contains("PLAYERTOMOVE: " + '"' + opponentName + '"')) {
				initBoard('W');
				updateLabel(ownNameLabel, "Ik ben Wit");
				updateLabel(oppNameLabel, opponentName + " is Zwart");
			}
			lastMsg = sIn.getMsg();
		}
	}

	private void initBoard(char token) {
		ownToken = token;
		oppToken = token == 'W' ? 'B' : 'W';
		if (withAI) {
			othAI = new OthelloAI(token);
			if(ownToken == 'B'){
				moveToDo = othAI.getNewMove(-1);
			}
		}
		board = new OthelloBoard(token);
		generateBoard();
		check = 2;
	}

	private void playGame() {
		String message = sIn.getMove();
		if (!message.equals(lastMove) ) {
			if (message.contains(opponentName)) {
				processOpponentMove(message);
			}
		}
		if (!lastTurn.equals(sIn.getTurn())) {
			if (sIn.getTurn().contains("YOURTURN")) {
				processOwnMove(message);
			}
		}
		if (sIn.endOfGame()) {
			resetBoard();
			System.out.println("Schermutseling is voorbij!");
		}
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void processOpponentMove(String message) {
			updateLabel(statusLabel, opponentName + " is aan de beurt!");
			int move = parseMove(message);
			moveToDo = othAI.getNewMove(move);
			board.flipPaths(move, oppToken);
			generateBoard();
			lastMove = message;
		}

	private void processOwnMove(String message) {
		updateLabel(statusLabel, "Jij bent aan de beurt!");
		myTurn = true;
		if (withAI) {
			sIn.resetTurn();
			if (finalMove != moveToDo && moveToDo != -1) {
				board.flipPaths(moveToDo, ownToken);
				sendCommand("move " + moveToDo);
				while(!sIn.endOfGame() && othAI.board.findPossibleMoves(oppToken).size()==0){
					moveToDo = othAI.getNewMove(-1);
					if(moveToDo!=-1){
						board.flipPaths(moveToDo, ownToken);
						sendCommand("move " + moveToDo);
					}
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			generateBoard();
			finalMove = moveToDo;
		}
		lastTurn = sIn.getTurn();
	}

	private int parseMove(String message) {
		String msg = message.substring(message.indexOf("MOVE:") + 7, message.indexOf(", DETAILS:") - 1);
		return Integer.parseInt(msg);
	}

	@FXML
	private void initialize() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				gridPane.add(cell[i][j] = new Cell(i, j), j, i);
			}
		}
	}

	public void resetBoard() {
		Platform.runLater(() -> {
			if (withAI) {
				othAI.reset();
			}
			board.reset();
			sIn.Reset();
			oppToken = ' ';
			ownToken = ' ';
			opponentName = "";
			check = 0;
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					//cell[i][j].getChildren().clear();
				}
			}
		});
	}

	@FXML
	public void doQuit() {
		sendCommand("forfeit");
		resetBoard();
		if (withAI) {
			othAI.reset();
		}
		sIn.Reset();
		model.sendToServer("subscribe Reversi");
		Stage primaryStage = (Stage) oldWindow;
		primaryStage.show();
		check = 1;
		gridPane.getScene().getWindow().hide();
	}

	private void setTeken(int row, int column, char token) {
		lastMsg = "";
		lastMove = "";
			Platform.runLater(() -> {
				Circle steentje = new Circle(1000, 1000, 22.5);
				steentje.setStroke(Color.GREY);
				steentje.setFill(token == 'W' ? Color.WHITE : Color.BLACK);
				steentje.setStrokeWidth(3);
				cell[row][column].getChildren().add(steentje);
			});
		rowSelected = row;
		columnSelected = column;
	}

	public class Cell extends GridPane {
		private int row;
		private int column;

		public Cell(int row, int column) {
			this.row = row;
			this.column = column;
			setStyle("-fx-border-color: grey");
			this.setPrefSize(2000, 2000);
			this.setOnMouseClicked(e -> handleMouseClick());

		}

		private void handleMouseClick() {
			if (!withAI) {
				OthelloCoordinate coord = new OthelloCoordinate((row * 8 + column));
				coord.setToken(ownToken);
				if (myTurn) {
					if (board.isValid(coord, ownToken)) {
						board.flipPaths((row * 8 + column), ownToken);
						generateBoard();
						sendCommand("move " + (row * 8 + column));
						sIn.resetTurn();
						myTurn = false;
					}
				}
			}
		}
	}
}
