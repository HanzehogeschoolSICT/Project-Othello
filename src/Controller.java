
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

import java.io.IOException;

public class Controller {
	@FXML
	private Button moveButton;
	@FXML
	private Button loginButton;
	@FXML
	private TextArea consoleArea;
	@FXML
	private TextField nameInputField;
	@FXML
	private TextField ipInputField;
	@FXML
	private TextField turnField;
	@FXML
	private Button quitButton;
	@FXML
	private Button challengeButton;
	@FXML
	private RadioButton tttRadio;
	@FXML
	private RadioButton othRadio;
	@FXML
	private TextField challengeField;
	@FXML
	private Button subscribeButton;
	@FXML
	private Label statusLabel;
	@FXML
	private GridPane gridPane;

	private Model model = new Model();
	private ServerIn sIn;
	private Cell[][] cell = new Cell[3][3];
	private int rowSelected;
	private int columnSelected;

	private String ownName;
	private String opponentName;
	private String ownToken;
	private String oppToken;

	TicTacToeAI bkeAI = new TicTacToeAI();

	public Controller() {
	}

	@FXML
	private void initialize() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				gridPane.add(cell[i][j] = new Cell(i, j), j, i);
			}
		}
	}

	@FXML
	public void doLogin() throws IOException {
		model.connectToServer(nameInputField.getText(), ipInputField.getText());
		ownName = nameInputField.getText();
		System.out.println(ownName);
		// Create a seperate thread to handle incoming responses
		sIn = new ServerIn(model.returnSocket().getInputStream());
		new Thread(sIn).start();
		loginButton.setDisable(true);
	}

	@FXML
	public void doSet() {
		model.sendToServer("move " + turnField.getText());
	}

	@FXML
	void doChallenge() {

	}

	@FXML
	void doSubscribe() throws Exception {
		if (tttRadio.isSelected()) {
			model.sendToServer("subscribe Tic-tac-toe");
			subscribeButton.setDisable(true);
			statusLabel.setText("Waiting for match");
			controlGame();
		}
		if (othRadio.isSelected()) {
			model.sendToServer("subscribe Tic-tac-toe");
			subscribeButton.setDisable(true);
		}

	}

	public void controlGame() throws InterruptedException {
		new Thread(() -> {
			try {
				boolean check = false;
				while (!check) {
					System.out.print("");
					if (!sIn.getMsg().equals("")) {
						if (sIn.getMsg().contains("PLAYERTOMOVE")) {
							opponentName = sIn.getMsg().substring(sIn.getMsg().indexOf("OPPONENT") + 11,
									sIn.getMsg().length() - 2);
							System.out.println(opponentName);
						}
						// System.out.println("bericht is:" + sIn.getMsg());
						if (sIn.getMsg().contains("PLAYERTOMOVE: " + '"' + ownName + '"')) {
							opponentName = sIn.getMsg().substring(sIn.getMsg().indexOf("OPPONENT") + 11,
									sIn.getMsg().length() - 2);
							System.out.println(opponentName);
							ownToken = "X";
							oppToken = "O";
							System.out.println("Eigen token:" + ownToken);
							System.out.println("Andere token:" + oppToken);
							check = true;
						}
						if (sIn.getMsg().contains("PLAYERTOMOVE: " + '"' + opponentName + '"')) {
							opponentName = sIn.getMsg().substring(sIn.getMsg().indexOf("OPPONENT") + 11,
									sIn.getMsg().length() - 2);
							System.out.println(opponentName);
							ownToken = "O";
							oppToken = "X";
							System.out.println("Eigen token:" + ownToken);
							System.out.println("Andere token:" + oppToken);
							check = true;
						}

					}
				}
				while (check) {
					System.out.println(sIn.getMove().contains("PLAYER: " + '"' + opponentName + '"'));
					System.out.println(sIn.getMove());
					System.out.println(opponentName);
					if (sIn.getMove().contains(opponentName)) {
						Platform.runLater(() -> statusLabel.setText("ZIJN BEURT OFZO"));
						String msg = sIn.getMove();
						System.out.println(msg);
						msg = msg.substring(msg.indexOf("MOVE:") + 7, msg.length() - 15);
						System.out.println(msg);
						int move = Integer.parseInt(msg);
						Platform.runLater(() -> setTeken((move / 3), (move % 3), oppToken));
					} else if (sIn.getMsg().contains("YOURTURN")) {
						Platform.runLater(() -> statusLabel.setText("jouwbeurt"));
					}
					Thread.sleep(100);
				}
				// Thread.sleep(100);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}).start();
	}

	@FXML
	public void doQuit() {
		model.sendToServer("quit");
		loginButton.setDisable(false);
		subscribeButton.setDisable(false);
	}

	private synchronized void setTeken(int row, int column, String token) {
		Label label = new Label();
		// if(whoseTurn==0){label.setText("\n X");}
		// else if(whoseTurn==1){label.setText("\n O");}
		label.setText("\n      " + token);
		label.setFont(new Font("Arial", 30));
		label.setAlignment(Pos.CENTER);
		cell[row][column].getChildren().add(label);
		rowSelected = row;
		columnSelected = column;
	}

	public class Cell extends GridPane {
		private String teken;
		private int row;
		private int column;

		public Cell(int row, int column) {
			this.row = row;
			this.column = column;
			setStyle("-fx-border-color: black");
			this.setPrefSize(2000, 2000);
			this.setOnMouseClicked(e -> handleMouseClick());

		}

		private void handleMouseClick() {
			Platform.runLater(() -> setTeken(row, column, ownToken));
			model.sendToServer("move " + (row * 3 + column));
		}

	}
}
