import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller implements Runnable{
    @FXML private Button loginButton;
    @FXML private TextField nameInputField;
    @FXML private TextField ipInputField;
    @FXML private Button challengeButton;
    @FXML private RadioButton tttRadio;
    @FXML private RadioButton othRadio;
    @FXML private Button subscribeButton;
    @FXML private Label connectionLabel;
    @FXML private ComboBox playerBox;
    @FXML private Button refreshButton;
    @FXML private RadioButton botRadio;
    @FXML private Button logoutButton;

    public static Thread t;
    public static volatile boolean shouldStop = false;

    private static Controller instance = null;
    private Model model = new Model();
    private ServerIn sIn;

    private Boolean newChallenge;
    public static Boolean challengeOpen;

    private String opponent;
    private String challengeNr;
    private String gameType;

    private Boolean subsribe = false;
    private Boolean newScrene = false;

    public static Boolean newGame = false;

    public Controller(){
        t = new Thread(this, "newGame Thread");
    }

    public void start(){
        t.start();
    }

    public void stop(){
        System.out.println("thread is closing.....");
        shouldStop = true;
    }

    public void run() {
        while (!shouldStop) {
            try{
            t.sleep(2000);
            if (newGame == true) {
                System.out.println("thread is starting.....");
                System.out.println("checking for new Game");
                if(sIn.getMsg().contains("Tic-tac-")){
                    drawBoard();
                    newGame = false;
                }
                if(sIn.getMsg().contains("Reversi")){
                    drawOthello();
                    newGame = false;
                }
            }
            else if (challengeOpen == true) {
                challengeOpen = false;
                System.out.println("checking for challenges.....");
                getChallenge();
//
            }}catch (InterruptedException ex){
                t.currentThread().interrupt();
            }


        }
    }





    @FXML private void initialize(){
    }

    @FXML public void doLogin() throws IOException {
        model.connectToServer(nameInputField.getText(), ipInputField.getText());
        loginButton.setDisable(true);
        connectionLabel.setText("Verbonden met de Server, als " + nameInputField.getText());
        sIn = new ServerIn(model.returnSocket().getInputStream());
        new Thread(sIn).start();
        model.sendToServer("get playerlist");
        playerBox.setItems(sIn.returnOptions());

        newGame = true;
        challengeOpen = true;
        shouldStop = false;
        start();

//        getChallenge();
//        drawBoard();
    }

    public void getChallenge(){
        newChallenge = false;
        new Thread(() -> {
            newChallenge = true;
            while (newChallenge){
                if (sIn.getChallenge()){

                    newChallenge = false;
                    Platform.runLater(() -> {
                        try {
                            System.out.print("");
                            if(!sIn.getMsg().equals("")) {
                                opponent = sIn.getMsg().substring(sIn.getMsg().indexOf("CHALLENGER:") + 13, sIn.getMsg().indexOf("\", CHALLENGENUMBER:"));
                                challengeNr = sIn.getMsg().substring(sIn.getMsg().indexOf("CHALLENGENUMBER:") + 18, sIn.getMsg().indexOf("\", GAMETYPE:"));
                                gameType = sIn.getMsg().substring(sIn.getMsg().indexOf("GAMETYPE:") + 11, sIn.getMsg().length() - 2);
                                System.out.println(opponent);
                                System.out.println(challengeNr);
                                System.out.println(gameType);
                                FXMLLoader loader1 = new FXMLLoader();
                                loader1.setLocation(ClassLoader.getSystemResource("challengeForm.fxml"));
                                Stage challengeStage = new Stage();
                                challengeStage.setScene(new Scene(loader1.load()));
                                ChallengeController challengeController = loader1.<ChallengeController>getController();
                                challengeController.initData(model, sIn, loginButton.getScene().getWindow(), opponent, challengeNr, gameType);
                                challengeStage.show();
//                            loginButton.getScene().getWindow().hide();
                            }
                        } catch(Exception ex) {
                            ex.printStackTrace();
                        }
                    });newChallenge = false;

                }
            }
        }).start();

    }

    public void drawBoard(){
        newScrene = false;
//        t.stop();
        new Thread(() -> {
            newScrene = true;
            while (newScrene) {
                if (sIn.getConnected()) {
                    Platform.runLater(() -> {
                        try {
                            boolean bot = false;
                            if(botRadio.isSelected()){bot=true;}
                            System.out.print("");
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("BKEgameForm.fxml"));
                            Stage gamestage = new Stage();
                            gamestage.setScene(new Scene(loader.load()));
                            BKEGameController gamecontroller = loader.<BKEGameController>getController();
                            gamecontroller.initData(model, sIn, nameInputField.getText(), loginButton.getScene().getWindow(), bot);
                            loginButton.getScene().getWindow().setOnHidden(e -> {
                                subscribeButton.setDisable(false);
                                connectionLabel.setText("Match bezig of klaar!");
                            });
                            gamestage.show();
                            loginButton.getScene().getWindow().hide();

                        } catch(Exception ex) {
                            ex.printStackTrace();
                        }
                    });newScrene = false;
                }
            }
        }).start();

    }

    public void drawOthello(){
        new Thread(() -> {
            boolean newScrene = true;
            while (newScrene) {
                if (sIn.getConnected()) {
                    Platform.runLater(() -> {
                        try {
                            boolean bot = false;
                            if(botRadio.isSelected()){bot=true;}
                            System.out.print("");
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("OTHgameForm.fxml"));
                            Stage gamestage = new Stage();
                            gamestage.setScene(new Scene(loader.load()));
                            OTHGameController gamecontroller = loader.<OTHGameController>getController();
                            gamecontroller.initModel(model, sIn);
                            gamecontroller.initData(nameInputField.getText(), loginButton.getScene().getWindow(), bot);
                            loginButton.getScene().getWindow().setOnHidden(e -> {
                                subscribeButton.setDisable(false);
                                connectionLabel.setText("Match bezig of klaar!");
                            });
                            gamestage.show();
                            loginButton.getScene().getWindow().hide();
                        } catch(Exception ex) {
                            ex.printStackTrace();
                        }
                    });newScrene = false;
                }
            }
        }).start();
    }

    @FXML public void doRefreshPLR(){
        playerBox.getItems().clear();
        model.sendToServer("get playerlist");
        playerBox.setItems(sIn.returnOptions());
//        getChallenge();
//        drawBoard();

    }
    @FXML void doChallenge(){
        //TODO Niet hardcoden
        if(tttRadio.isSelected()) {
            System.out.println("challenge " + playerBox.getValue() + " \"Tic-tac-toe\"");
            model.sendToServer("challenge " + playerBox.getValue() + " \"Tic-tac-toe\"");
        }
        else if(othRadio.isSelected()){
            System.out.println("challenge " + playerBox.getValue() + " \"Reversi\"");
            model.sendToServer("challenge " + playerBox.getValue() + " \"Reversi\"");
        }
//        drawBoard();
    }

    @FXML void doSubscribe() {
        newChallenge = false;
        subsribe = true;
        if(tttRadio.isSelected()){
            model.sendToServer("subscribe Tic-tac-toe");
            subscribeButton.setDisable(true);
            connectionLabel.setText(nameInputField.getText() + ": Aan het wachten op een tegenstander...");
//            drawBoard();
            subsribe = false;

        }
        if(othRadio.isSelected()){
            model.sendToServer("subscribe Reversi");
            subscribeButton.setDisable(true);
            connectionLabel.setText(nameInputField.getText() + ": Aan het wachten op een tegenstander...");
            newChallenge = false;

        }
    }
    @FXML void doLogout(){
        loginButton.setDisable(false);
        model.sendToServer("bye");
//        shouldStop = true;

    }



}
