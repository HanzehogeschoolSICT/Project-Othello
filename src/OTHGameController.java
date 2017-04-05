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


    private boolean myTurn;
    private boolean withAI;
    private int moveToDo;
    int check = 1;
    OthelloAI othAI;
    OthelloBoard board;

    public OTHGameController(){}

    public void initData(Model conModel, ServerIn consIn, String name, Window window, boolean AI) throws InterruptedException{
        ownName = name;
        model = conModel;
        sIn = consIn;
        controlGame();
        oldWindow = window;
        ownNameLabel.setText(ownName);
        withAI = AI;

    }

    public void generateBoard(){
        for(int x = 0; x < 8; x++) {
            String line = "";
            for(int y = 0; y < 8; y++) {
                OthelloCoordinate coord = board.getCoordinate(x, y);
                if(coord == null) {
                    line+="";
                } else {
                    setTeken(x, y,coord.getToken());
                }
            }

        }
    }

    public void controlGame() throws InterruptedException{
        new Thread(() -> {
            try{
                while(check==1){
                    System.out.print("");
                    if(!sIn.getMsg().equals(lastMsg)) {
                        if (!sIn.getMsg().equals("")) {
                            if (sIn.getMsg().contains("PLAYERTOMOVE")) {
                                opponentName = sIn.getMsg().substring(sIn.getMsg().indexOf("OPPONENT") + 11, sIn.getMsg().length() - 2);
                                Platform.runLater(() -> oppNameLabel.setText(opponentName));
                                lastMsg = sIn.getMsg();
                            }
                            if (sIn.getMsg().contains("PLAYERTOMOVE: " + '"' + ownName + '"')) {
                                opponentName = sIn.getMsg().substring(sIn.getMsg().indexOf("OPPONENT") + 11, sIn.getMsg().length() - 2);
                                System.out.println(opponentName);
                                ownToken = 'W';
                                oppToken = 'B';
                                if(withAI){
                                    othAI = new OthelloAI('W');
                                } else {
                                    board = new OthelloBoard('W');
                                }
                                generateBoard();
                                //moveToDo = othAI.getNewMove(-1);
                                Platform.runLater(() -> ownNameLabel.setText("Ik ben X"));
                                Platform.runLater(() -> oppNameLabel.setText(oppNameLabel.getText() + " is  O"));
                                check = 2;
                                lastMsg = sIn.getMsg();
                            }
                            if (sIn.getMsg().contains("PLAYERTOMOVE: " + '"' + opponentName + '"')) {
                                opponentName = sIn.getMsg().substring(sIn.getMsg().indexOf("OPPONENT") + 11, sIn.getMsg().length() - 2);
                                System.out.println(opponentName);
                                ownToken = 'B';
                                oppToken = 'W';
                                myTurn = true;
                                if(withAI){
                                    othAI = new OthelloAI('B');
                                } else {
                                    board = new OthelloBoard('B');
                                }
                                generateBoard();
                                Platform.runLater(() -> ownNameLabel.setText("Ik ben O"));
                                Platform.runLater(() -> oppNameLabel.setText(oppNameLabel.getText() + " is X"));
                                check = 2;
                                lastMsg = sIn.getMsg();
                            }

                        }
                    }
                }
                System.out.print("");
                while(check==2) {
                    String message = sIn.getMove();
                    if(!message.equals(lastMove)){
                        if(message.contains(opponentName)){
                            Platform.runLater(() -> statusLabel.setText("Tegenstander is aan de beurt, berijdt je voor!"));
                            String msg = message;
                            msg = msg.substring(msg.indexOf("MOVE:") + 7, msg.length()-15);
                            int move = Integer.parseInt(msg);
                            board.addCoordinate(move, oppToken);
                            generateBoard();
                            //Platform.runLater(() -> setTeken((move / 8), (move % 8), oppToken));
                            if(withAI) {
                                System.out.print("");
                                moveToDo = othAI.getNewMove(move);
                                //bkeAI.printBoard();
                            }
                            lastMove=message;
                        }}
                    if(sIn.eogMsg()){
                        //Platform.runLater(() -> subscribeButton.setDisable(false));
                        System.out.print("");
                        check=0;
                        Thread.currentThread().interrupt();
                    }
                    if(!lastTurn.equals(sIn.getTurn())){
                        if(sIn.getTurn().contains("YOURTURN")){
                            myTurn = true;
                            if(withAI){
                                sIn.resetTurn();
                                //Platform.runLater(() -> setTeken((moveToDo / 8), (moveToDo % 8), ownToken));
                                //System.out.println("DEZE MOVE VERNEUKT ALLES" + moveToDo);
                                model.sendToServer("move " + moveToDo);
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
                        Thread.sleep(1250);
                    } else{
                        Thread.sleep(100);
                    }
                }
                //Thread.sleep(100);
            } catch (Exception ex){
                ex.printStackTrace();
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

    @FXML
    public void doQuit(){
        //resetBoard();
        //othAI.reset();
        Stage primaryStage = (Stage)oldWindow;
        primaryStage.show();
        model.sendToServer("forfeit");
        gridPane.getScene().getWindow().hide();
    }

    private synchronized void setTeken(int row, int column, char token) {
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
        System.out.print("");

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
            if(!withAI) {
                if (myTurn) {
                    board.addCoordinate(row,column,ownToken);
                    generateBoard();
                //Platform.runLater(() -> setTeken(row, column, ownToken));
                model.sendToServer("move " + (row * 8 + column));
                sIn.resetTurn();
                myTurn = false;
                }
            }
        }


    }
}
