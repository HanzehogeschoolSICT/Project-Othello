import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

/**
 * TODO: Status labels updaten zonder dat de boel vastloopt.
 * Gebruikte bronnen: Introduction to Java Programming.
 */
public class BKEGameController {
    @FXML private Button quitButton;
    @FXML private Label statusLabel;
    @FXML private GridPane gridPane;
    @FXML private Label ownNameLabel;
    @FXML private Label oppNameLabel;
    @FXML private Label turnLabel;
    @FXML private Button forfeitButton;

    private Model model;
    public static ServerIn sIn;
    private Cell[][] cell = new Cell[3][3];
    private int rowSelected;
    private int columnSelected;

    private String ownName;
    private String opponentName;
    private String ownToken;
    private String oppToken;
    private Window oldWindow;

    private String lastMsg = "";
    private String lastMove = "";
    private String lastTurn = "";


    private boolean myTurn;
    private boolean withAI;
    private int moveToDo;
    int check = 1;

    public String gameStatus;

    public String gameResult;



    TicTacToeAI bkeAI = new TicTacToeAI();


    public BKEGameController(){}

    public void initData(Model conModel, ServerIn consIn, String name, Window window, boolean AI) throws InterruptedException{
        ownName = name;
        model = conModel;
        sIn = consIn;
        controlGame();
        oldWindow = window;
        ownNameLabel.setText(ownName);
        withAI = AI;
    }

    @FXML
    private void initialize(){
        for (int i=0; i<3; i++){
            for(int j = 0; j<3; j++){
                gridPane.add(cell[i][j] = new Cell(i, j), j, i);
            }
        }
    }

    public void resetBoard(){
        for (int i=0; i<3; i++){
            for(int j = 0; j<3; j++){
                cell[i][j].getChildren().clear();
            }
        }
        sIn.Reset();
        check = 1;
        opponentName = "";
        ownToken = "";
        oppToken = "";
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
                                ownToken = "X";
                                oppToken = "O";
                                moveToDo = bkeAI.getNewMove(-1);
                                Platform.runLater(() -> ownNameLabel.setText("Ik ben X"));
                                Platform.runLater(() -> oppNameLabel.setText(oppNameLabel.getText() + " is  O"));
                                Platform.runLater(() -> turnLabel.setText("Jij bent aan de beurt!!! hier zou die moeten beginnen"));

                                check = 2;
                                lastMsg = sIn.getMsg();
                            }
                            if (sIn.getMsg().contains("PLAYERTOMOVE: " + '"' + opponentName + '"')) {
                                opponentName = sIn.getMsg().substring(sIn.getMsg().indexOf("OPPONENT") + 11, sIn.getMsg().length() - 2);
                                System.out.println(opponentName);
                                ownToken = "O";
                                oppToken = "X";
                                Platform.runLater(() -> ownNameLabel.setText("Ik ben O"));
                                Platform.runLater(() -> oppNameLabel.setText(oppNameLabel.getText() + " is X"));
                                Platform.runLater(() -> turnLabel.setText("De tegenstander is aan de beurt!!!hier zou die moeten beginnen"));
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
                        Platform.runLater(() -> turnLabel.setText("Jij bent aan de beurt!!! deze veranderd"));
                        String msg = message;
                        msg = msg.substring(msg.indexOf("MOVE:") + 7, msg.length()-15);
                        int move = Integer.parseInt(msg);
                        Platform.runLater(() -> setTeken((move / 3), (move % 3), oppToken));
                        if(withAI) {
                            System.out.print("");
                            moveToDo = bkeAI.getNewMove(move);
                            //bkeAI.printBoard();
                        }
                        lastMove=message;
                    }}
                    if(!message.contains(opponentName)){
                        Platform.runLater(() -> turnLabel.setText("De tegenstander is aan de beurt!!! deze veranderd"));
                    }
                    if(sIn.endOfGame()){
                        //Platform.runLater(() -> subscribeButton.setDisable(false));
                        System.out.print("");
                        check=0;
                        Platform.runLater(() -> quit());
                        EoG.getEoMform(getGameResult());
                        Thread.currentThread().interrupt();
                    }
                    if(!lastTurn.equals(sIn.getTurn())){
//                        Platform.runLater(() -> turnLabel.setText("De tegenstander is aan de beurt!!! deze veranderd"));
                    if(sIn.getTurn().contains("YOURTURN")){
//                        Platform.runLater(() -> turnLabel.setText("Je bent aan de beurt! Snel, doe een zet!"));
                        myTurn = true;
                        if(withAI){
                            sIn.resetTurn();
                            Platform.runLater(() -> setTeken((moveToDo / 3), (moveToDo % 3), ownToken));
                            //System.out.println("DEZE MOVE VERNEUKT ALLES" + moveToDo);
                            model.sendToServer("move " + moveToDo);
                            //bkeAI.printBoard();
                        }
                        lastTurn=sIn.getTurn();

//                         FIXME Als je dit toevoegd gaat er iets heel fout.


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
    public void doQuit(){
        quit();


    }

    public void quit(){
        System.out.println("Hier doet die wat");
        resetBoard();
        bkeAI.reset();
        Stage primaryStage = (Stage)oldWindow;
        primaryStage.show();
//        model.sendToServer("forfeit");
        gridPane.
                getScene().getWindow().hide();
        Controller.newGame = true;
        Controller.shouldStop = false;
        Controller.challengeOpen = true;



    }

    @FXML
    public void doForfeit(){
        model.sendToServer("forfeit");
        check = 0;
    }

    public String getGameResult(){
        System.out.println(sIn.getMsg());

        if (sIn.getMsg().contains("SVR GAME WIN")){
            gameResult = "Gefeliciteerd," + " je hebt gewonnen";
            System.out.println(gameResult);
        }
        else if(sIn.getMsg().contains("SVR GAME LOSS")){
            gameResult = "Je zuigt";
            System.out.println(gameResult);
        }
        else if (sIn.getMsg().contains("SVR GAME DRAW")){
            gameResult = "gelijk spelen is erger dan verliezen";
            System.out.println(gameResult);
        }
        return gameResult;
    }

//    public void getEoMform(){
//
//        System.out.println(sIn.getMsg());
//
//        if (sIn.getMsg().contains("SVR GAME WIN")){
//            gameResult = "Gefeliciteerd," +
//                    " je hebt gewonnen";
//            System.out.println(gameResult);
//        }
//        else if(sIn.getMsg().contains("SVR GAME LOSS")){
//            gameResult = "Je zuigt";
//            System.out.println(gameResult);
//        }
//        else if (sIn.getMsg().contains("SVR GAME DRAW")){
//            gameResult = "gelijk spelen is erger dan verliezen";
//            System.out.println(gameResult);
//        }
//        Platform.runLater(() -> {
//            try{
//                FXMLLoader loader1 = new FXMLLoader();
//                loader1.setLocation(ClassLoader.getSystemResource("EoMForm.fxml"));
//                Stage EoMstage = new Stage();
//                EoMstage.setScene(new Scene(loader1.load()));
//                EoMController EoMcontroller = loader1.<EoMController>getController();
//                EoMcontroller.initData(sIn, gameResult);
//                EoMstage.show();
//
//            }catch(Exception ex) {
//                ex.printStackTrace();
//            }
//        });}

    private synchronized void setTeken(int row, int column, String token) {
        lastMsg = "";
        lastMove = "";
        Label label = new Label();
        //if(whoseTurn==0){label.setText("\n      X");}
        // else if(whoseTurn==1){label.setText("\n      O");}
        label.setText("\n      " + token);
        label.setFont(new Font("Arial", 30));
        label.setAlignment(Pos.CENTER);
        cell[row][column].getChildren().add(label);
        rowSelected = row;
        columnSelected = column;
        System.out.print("");



    }



    public class Cell extends GridPane {
        private String teken;
        private int row;
        private int column;

        public Cell(int row, int column){
            this.row = row;
            this.column = column;
            setStyle("-fx-border-color: black");
            this.setPrefSize(2000, 2000);
            this.setOnMouseClicked(e -> handleMouseClick());

        }

        private void handleMouseClick(){
            if(!withAI) {
                if (myTurn) {
                    Platform.runLater(() -> setTeken(row, column, ownToken));
                    model.sendToServer("move " + (row * 3 + column));
                    sIn.resetTurn();
                    myTurn = false;
                }
            }
        }




    }
}
