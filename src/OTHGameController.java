import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * TODO: Status labels updaten zonder dat de boel vastloopt.
 * Gebruikte bronnen: Introduction to Java Programming.
 */
public class OTHGameController {
    @FXML private Button quitButton;
    @FXML private Label statusLabel;
    @FXML private GridPane gridPane;
    @FXML private Label ownNameLabel;
    @FXML private Label oppNameLabel;
    @FXML private Label turnLabel;

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

    public OTHGameController(){}

    public void initData(String name, Window window, boolean AI) throws InterruptedException{
        ownName = name;
        oldWindow = window;
        ownNameLabel.setText(ownName);
        withAI = AI;
        controlGame();
    }
    public void initModel(Model conModel, ServerIn consIn){
        model = conModel;
        sIn = consIn;
    }

    public void initToken(char owntok, char opptok){
        ownToken = owntok;
        oppToken = opptok;
    }

    public void updateLabel(Label upLabel, String text){
        Platform.runLater(() -> upLabel.setText(text));
    }

    public void sendCommand(String cmd){
        model.sendToServer(cmd);
    }

    public void generateBoard(){
        for(int x = 0; x < 8; x++) {
            for(int y = 0; y < 8; y++) {
                OthelloCoordinate coord;
                if(withAI){
                    coord = othAI.board.getCoordinate(x, y);
                } else{
                    coord = board.getCoordinate(x, y);
                }
                if(coord != null) {
                    setTeken(coord.getX(), coord.getY(),coord.getToken());
                }
            }

        }
    }

    public void initOpponentData(){
        if (sIn.getMsg().contains("PLAYERTOMOVE")) {
            opponentName = sIn.getMsg().substring(sIn.getMsg().indexOf("OPPONENT") + 11, sIn.getMsg().length() - 2);
            updateLabel(oppNameLabel, opponentName);
            lastMsg = sIn.getMsg();
        }
    }



    public void controlGame() throws InterruptedException{
        new Thread(() -> {
            try{
                while(check==1){
                    if(!sIn.getMsg().equals(lastMsg)) {
                        if (!sIn.getMsg().equals("")) {
                            initOpponentData();

                            if (sIn.getMsg().contains("PLAYERTOMOVE: " + '"' + ownName + '"')) {
                                opponentName = sIn.getMsg().substring(sIn.getMsg().indexOf("OPPONENT") + 11, sIn.getMsg().length() - 2);
                                //System.out.println(opponentName);
                                initToken('B', 'W');
                                if(withAI){
                                    othAI = new OthelloAI('B');
                                    moveToDo = othAI.getNewMove(-1);
                                } else {
                                    board = new OthelloBoard('B');
                                }
                                generateBoard();
                                updateLabel(ownNameLabel, "Ik ben Zwart");
                                updateLabel(oppNameLabel, oppNameLabel.getText() + " is  Wit");
                                check = 2;
                                lastMsg = sIn.getMsg();
                            }
                            if (sIn.getMsg().contains("PLAYERTOMOVE: " + '"' + opponentName + '"')) {
                                opponentName = sIn.getMsg().substring(sIn.getMsg().indexOf("OPPONENT") + 11, sIn.getMsg().length() - 2);
                                //System.out.println(opponentName);
                                initToken('W', 'B');
                               // myTurn = true;
                                if(withAI){
                                    othAI = new OthelloAI('W');
                                } else {
                                    board = new OthelloBoard('W');
                                }
                                generateBoard();
                                updateLabel(ownNameLabel, "Ik ben Wit");
                                updateLabel(oppNameLabel, opponentName+" is Zwart");
                                check = 2;
                                lastMsg = sIn.getMsg();
                            }

                        }
                    }
                }
                //System.out.print("");
                while(check==2) {
                    String message = sIn.getMove();
                    if(sIn.eogMsg()){
                        if(message.contains(opponentName)){
                           String msg = message.substring(message.indexOf("MOVE:") + 7, message.indexOf(", DETAILS:")-1);
                            int move = Integer.parseInt(msg);
                            othAI.board.flipPaths(move, oppToken);
                       }
                        //Platform.runLater(() -> subscribeButton.setDisable(false));
                       // System.out.print("");
                        check=0;
                        generateBoard();
                        resetBoard();
                        //Thread.currentThread().interrupt();
                    }
                    if(!message.equals(lastMove) && check==2){
                        if(message.contains(opponentName)){
                            updateLabel(statusLabel, "Tegenstander is aan de beurt, berijdt je voor!");
                            String msg = message.substring(message.indexOf("MOVE:") + 7, message.indexOf(", DETAILS:")-1);
                            int move = Integer.parseInt(msg);
                            //Platform.runLater(() -> setTeken((move / 8), (move % 8), oppToken));
                            if(withAI) {
                                generateBoard();
                               // System.out.print("DIT IS DE MOVE DIE ALLES VERKLOOT: " + move);
                                //othAI.board.flipPaths(move, oppToken);
                                moveToDo = othAI.getNewMove(move);
                                generateBoard();
                                //bkeAI.printBoard();
                            } else{
                                generateBoard();
                                board.flipPaths(move, oppToken);
                                generateBoard();

                            }
                            lastMove=message;
                        }}
                    if(!lastTurn.equals(sIn.getTurn()) && check==2){
                        if(sIn.getTurn().contains("YOURTURN")){
                            myTurn = true;
                            if(withAI){
                                sIn.resetTurn();
                                //Platform.runLater(() -> setTeken((moveToDo / 8), (moveToDo % 8), ownToken));
                                //System.out.println("DEZE MOVE VERNEUKT ALLES" + moveToDo);
                                generateBoard();
                                if(finalMove!=moveToDo && moveToDo!=-1){
                                    sendCommand("move " + moveToDo);
                                }
                                finalMove = moveToDo;
                                //sendCommand("move " + moveToDo);

                                //bkeAI.printBoard();
                            }
                            lastTurn=sIn.getTurn();
                            /**
                             * FIXME Als je dit toevoegd gaat er iets heel fout.
                             * Platform.runLater(() -> statusLabel.setText("Je bent aan de beurt! Snel, doe een zet!"));
                             */
                        }} else if(sIn.getTurn().contains("YOURTURN") ){
                        myTurn = true;
                    }
                    if(withAI){
                        Thread.sleep(300);
                    } else{
                        Thread.sleep(100);
                    }
                }
                //Thread.sleep(100);
            } catch (Exception ex){
                ex.printStackTrace();
           // }
            }
        }).start();

    }

    @FXML
    private void initialize(){
        for (int i=0; i<8; i++){
            for(int j = 0; j<8; j++){
                gridPane.add(cell[i][j] = new Cell(i, j), j, i);
            }
        }
    }

    public void resetBoard() {
        Platform.runLater(() -> {
            if (withAI) {
                othAI.reset();
                othAI.board.reset();
            }
            sIn.Reset();
            oppToken = ' ';
            ownToken = ' ';
            opponentName = "";
            check = 1;
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    cell[i][j].getChildren().clear();
                }
            }
            initialize();
        });
    }

    @FXML
    public void doQuit(){
        sendCommand("forfeit");
        resetBoard();
        if(withAI){
            othAI.reset();
            othAI.board.reset();
        }
        sIn.Reset();
        model.sendToServer("subscribe Reversi");
        Stage primaryStage = (Stage)oldWindow;
        primaryStage.show();
        check=1;
        gridPane.getScene().getWindow().hide();
    }

    private void setTeken(int column, int row, char token) {
        lastMsg = "";
        lastMove = "";
        //Label label = new Label();
        //if(whoseTurn==0){label.setText("\n      X");}
        // else if(whoseTurn==1){label.setText("\n      O");}
        //label.setText(token);
        if(token=='B'){
            Platform.runLater(() -> cell[row][column].setStyle("-fx-background-color: black"));
        } else if(token=='W'){
            Platform.runLater(() -> cell[row][column].setStyle("-fx-background-color: white"));
        }
        //label.setFont(new Font("Arial", 30));
        //label.setAlignment(Pos.CENTER);
        //cell[row][column].getChildren().add(label);
        rowSelected = row;
        columnSelected = column;
        //System.out.print("");

    }

    public class Cell extends GridPane {
        private int row;
        private int column;

        public Cell(int row, int column){
            this.row = row;
            this.column = column;
            setStyle("-fx-border-color: grey");
            this.setPrefSize(2000, 2000);
            this.setOnMouseClicked(e -> handleMouseClick());

        }

        private void handleMouseClick(){
            //if(!withAI) {
                OthelloCoordinate coord = new OthelloCoordinate((row * 8 + column));
                coord.setToken(ownToken);
                //if (myTurn) {
                    if(othAI.board.isValid(coord)){
                        othAI.board.flipPaths((row * 8 + column),ownToken);
                        generateBoard();
                        sendCommand("move " + (row * 8 + column));
                        sIn.resetTurn();
                        myTurn = false;
                    }
                //}
            //}
        }


    }
}
