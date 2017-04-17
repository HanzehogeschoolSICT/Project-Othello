package control;
import ai.OthelloAI;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.OthelloBoard;
import model.OthelloCoordinate;
import server.ServerIn;
import server.ServerOut;

/**
 * Gebruikte bronnen:
 * Introduction to Java Programming.
 */
public class OTHGameController {
    private int check = 1;
    private OthelloAI othAI;
    private volatile OthelloBoard board;
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
    private ServerOut serverOut;
    private ServerIn sIn;
    private Cell[][] cell = new Cell[8][8];
    private int rowSelected;
    private int columnSelected;
    private String ownName;
    private String opponentName;
    private Character ownToken;
    private Character oppToken;
    private Window oldWindow;
    private String lastMsg = "";
    private String lastMove = "";
    private String lastTurn = "";
    private int finalMove;
    private boolean myTurn;
    private boolean withAI;
    private boolean tournament;
    private int moveToDo;
    private String gameResult;
    private long time;

    public OTHGameController() {
    }

    /**
     * Initializes naam, venster en AI.
     *
     * @param name   Spelernaam
     * @param window De Scene van het conenctie-venster
     * @throws InterruptedException Threads.
     */
    public void initData(String name, Window window) throws InterruptedException {
        ownName = name;
        oldWindow = window;
        ownNameLabel.setText(ownName);
        check = 1;
    }

    /**
     * Met AI? en of het een toernooi is? Lanceer daarna het spel!
     *
     * @param AI         De optie voor AI
     * @param tournement De optie voor toernooi
     */
    public void initSettings(boolean AI, boolean tournement) {
        withAI = AI;
        tournament = tournement;
        controlGame();
    }

    /**
     * Geef op en stop ControlGame.
     */
    @FXML
    public void doForfeit() {
        quitButton.setDisable(false);
        sendCommand("forfeit");
        check = 0;
    }

    /**
     * Geeft de objecten mee voor de verbinding met de server.
     *
     * @param conServerOut ServerOut
     * @param consIn       ServerIn
     */
    public void initModel(ServerOut conServerOut, ServerIn consIn) {
        serverOut = conServerOut;
        sIn = consIn;
    }

    /**
     * Update een label
     *
     * @param upLabel Label object
     * @param text    De text
     */
    private void updateLabel(Label upLabel, String text) {
        Platform.runLater(() -> upLabel.setText(text));
    }

    /**
     * Verstuur een commande naar de server
     *
     * @param cmd Het bericht
     */
    private void sendCommand(String cmd) {
        serverOut.sendToServer(cmd);
    }

    /**
     * Update het bord aan de hand van de opgeslagen coordinaten met de tokens
     */
    private void generateBoard() {
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

    /**
     * Bestuurd het spel, roept CheckColor en Playgame aan.
     */
    private void controlGame() {
        new Thread(() -> {
            while(true) {
                try {
                    while (check == 1) {
                        checkColor();
                    }
                    while (check == 2) {
                        playGame();
                    }
                    Thread.sleep(150);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Checkt welke kleur je bent en initialiseerd het bord en de AI met je eigen token.
     */
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

    /**
     * Maakt een nieuw AI-object aan met token en berekend optioneel de eerste move.
     * Maakt ook een lokaal bord aan voor de GUI.
     *
     * @param token Zwart of wit.
     */
    private void initBoard(char token) {
        ownToken = token;
        oppToken = token == 'W' ? 'B' : 'W';
        if (withAI) {
            othAI = new OthelloAI(token);
            if (ownToken == 'B') {
                moveToDo = othAI.getNewMove(-1);
            }
        }
        board = new OthelloBoard(token);
        generateBoard();
        check = 2;
    }

    /**
     * Deze functie kijkt wie er aan de beurt is en of het spel klaar is.
     */
    private void playGame() {
        String message = sIn.getMove();
        //if (!message.equals(lastMove)) {
            if (message.contains(opponentName)) {
                time = System.currentTimeMillis();
                processOpponentMove(message);
                generateBoard();
            }
       // }
        if (sIn.getTurn().contains("YOURTURN")) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            processOwnMove();
            generateBoard();
        }

        if (sIn.endOfGame()) {
            System.out.println(getGameResult());
            generateBoard();
            EoG.getEoMform(getGameResult());
            quitButton.setDisable(false);
            resetGame();
            System.out.println("Schermutseling is voorbij!");
        }
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Verwerkt de zet van de tegenstander.
     *
     * @param message Het bericht(Bijv: MOVE: 8)
     */
    private void processOpponentMove(String message) {
        updateLabel(statusLabel, opponentName + " is aan de beurt!");
        int move = parseMove(message);
        if (withAI) {
            moveToDo = othAI.getNewMove(move);
        }
        board.flipPaths(move, oppToken);
        generateBoard();
        lastMove = message;
    }

    /**
     * Verwerkt je eigen zet en krijgt een nieuwe move voor de AI
     */
    private void processOwnMove() {
        updateLabel(statusLabel, "Jij bent aan de beurt!");
        myTurn = true;
        sIn.resetTurn();
        if (withAI) {
            if (finalMove != moveToDo && moveToDo != -1) {
                board.flipPaths(moveToDo, ownToken);
                generateBoard();
                sendCommand("move " + moveToDo);
                System.out.println("Deze move nam seconden: " + (System.currentTimeMillis()-time)/1000);
                while (!sIn.endOfGame() && othAI.getBoard().findPossibleMoves(oppToken).size() == 0) {
                    moveToDo = othAI.getNewMove(-1);
                    if (moveToDo != -1) {
                        board.flipPaths(moveToDo, ownToken);
                        generateBoard();
                        System.out.println("Deze move nam seconden: " + (System.currentTimeMillis()-time)/1000);
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
        generateBoard();
    }

    /**
     * Verwerkt move-bericht van de server
     *
     * @param message Het bericht
     * @return de Move
     */
    private int parseMove(String message) {
        String msg = message.substring(message.indexOf("MOVE:") + 7, message.indexOf(", DETAILS:") - 1);
        return Integer.parseInt(msg);
    }

    /**
     * JavaFX initialize
     */
    @FXML
    private void initialize() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                gridPane.add(cell[i][j] = new Cell(i, j), j, i);
            }
        }
    }

    /**
     * Ruimt het bord op.
     */
    private void clearBoard() {
        Platform.runLater(() -> {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    cell[i][j].getChildren().clear();
                }
            }
        });
    }

    private void resetData() {
        oppToken = null;
        ownToken = null;
        ownName = "";
        opponentName = "";
        moveToDo=-1;
        lastMsg = "";
        lastMove=  "";
    }

    private void Exit() {
        check = 1;

        //clearBoard();
        Stage primaryStage = (Stage) oldWindow;
        primaryStage.show();
        //check = 1;
        //gridPane.getScene().getWindow().hide();
        Controller.newGame = true;
        Controller.challengeOpen = true;

    }

    /**
     * Reset alle waarden naar niks.
     */
    private void resetBoard() {
        Platform.runLater(() -> {
            resetData();
            if (withAI) {
                othAI.reset();
            }
            sIn.Reset();
            clearBoard();
            board.reset();
            if (tournament) {
                doQuit();
            }
            check=1;
            Stage primaryStage = (Stage) oldWindow;
            primaryStage.show();
            //check = 1;
            gridPane.getScene().getWindow().hide();
        });
    }

    private void resetGame(){
        check = 0;
        clearBoard();
        sIn.Reset();
        resetData();
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Controller.challengeOpen = true;
        check = 0;
        Platform.runLater(()->{
            Controller.newGame = true;
            Stage primaryStage = (Stage) oldWindow;
            primaryStage.show();
            //check = 1;
            gridPane.getScene().getWindow().hide();
        });
    }

    /**
     * Verlaat het spel en laat het connectie-venster zien!
     */
    @FXML
    public void doQuit() {
        sendCommand("forfeit");
        resetGame();
        //Exit();
    }

    /**
     * Stel het teken in
     *
     * @param row    de Row
     * @param column Colom
     * @param token  Token, ex Wit of Zwart
     */
    private void setTeken(int row, int column, char token) {
        lastMsg = "";
        lastMove = "";
        Platform.runLater(() -> {
            Circle steentje = new Circle(1000, 1000, 22.5);
            steentje.setStroke(Color.GREY);
            steentje.setFill(token == 'W' ? Color.WHITE : Color.BLACK);
            steentje.setStrokeWidth(3);
            cell[column][row].getChildren().add(steentje);
        });
        rowSelected = row;
        columnSelected = column;
    }

    /**
     * Maak een bericht voor de uitslag.
     *
     * @return gameResult Het bericht
     */
    private String getGameResult() {
        System.out.println(sIn.getMsg());

        if (sIn.getMsg().contains("SVR GAME WIN")) {
            gameResult = "Schermutseling gezegevierd! \n Wat een meesterlijke AI!";
            System.out.println(gameResult);
        } else if (sIn.getMsg().contains("SVR GAME LOSS")) {
            gameResult = "De nederlaag kwam hard aan, de schermutseling is verloren! \n Meedoen is belangrijker dan winnen.";
            System.out.println(gameResult);
        } else if (sIn.getMsg().contains("SVR GAME DRAW")) {
            gameResult = "Gelijk spelen is erger dan verliezen.";
            System.out.println(gameResult);
        }
        return gameResult;
    }

    /**
     * Inner-class voor de cells van de GridPane die de Steentjes houden.
     */
    private class Cell extends GridPane {
        private int row;
        private int column;

        private Cell(int row, int column) {
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
