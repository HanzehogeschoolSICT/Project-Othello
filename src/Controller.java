import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller implements Runnable {
    public static Thread t;
    public static volatile boolean shouldStop = false;
    public static Boolean challengeOpen;
    public static Boolean newGame = false;
    private static Controller instance = null;
    @FXML
    private Button loginButton;
    @FXML
    private TextField nameInputField;
    @FXML
    private TextField ipInputField;
    @FXML
    private Button challengeButton;
    @FXML
    private RadioButton tttRadio;
    @FXML
    private RadioButton othRadio;
    @FXML
    private Button subscribeButton;
    @FXML
    private Label connectionLabel;
    @FXML
    private ComboBox playerBox;
    @FXML
    private Button refreshButton;
    @FXML
    private RadioButton botRadio;
    @FXML
    private Button logoutButton;
    private ServerOut serverOut = new ServerOut();
    private ServerIn sIn;
    private Boolean newChallenge;
    private String opponent;
    private String challengeNr;
    private String gameType;
    private Boolean subsribe = false;
    private Boolean newScrene = false;

    public Controller() {
        t = new Thread(this, "newGame Thread");
    }

    public void start() {
        t.start();
    }

    public void stop() {
        System.out.println("thread is closing.....");
        shouldStop = true;
    }

    public void run() {
        while (!shouldStop) {
            try {
                t.sleep(2000);
                if (newGame) {
                    System.out.println("thread is starting.....");
                    System.out.println("checking for new Game");
                    if (sIn.getMsg().contains("Tic-tac-")) {
                        drawBoard();
                        newGame = false;
                    }
                    if (sIn.getMsg().contains("Reversi")) {
                        drawBoard();
                        newGame = false;
                    }
                } else if (challengeOpen) {
                    challengeOpen = false;
                    System.out.println("checking for challenges.....");
                    getChallenge();
//
                }
            } catch (InterruptedException ex) {
                t.currentThread().interrupt();
            }


        }
    }

    @FXML
    private void initialize() {
    }

    @FXML
    public void doLogin() throws IOException {
        serverOut.connectToServer(nameInputField.getText(), ipInputField.getText());
        loginButton.setDisable(true);
        connectionLabel.setText("Verbonden met de Server, als " + nameInputField.getText());
        sIn = new ServerIn(serverOut.returnSocket().getInputStream());
        new Thread(sIn).start();
        serverOut.sendToServer("get playerlist");
        playerBox.setItems(sIn.returnOptions());
        newGame = true;
        challengeOpen = true;
        shouldStop = false;
        start();
    }

    public void getChallenge() {
        newChallenge = false;
        new Thread(() -> {
            newChallenge = true;
            while (newChallenge) {
                if (sIn.getChallenge()) {
                    newChallenge = false;
                    initChallenge();
                    showChallenge();
                }
            }
        }).start();

    }

    private void initChallenge(){
        opponent = sIn.getMsg().substring(sIn.getMsg().indexOf("CHALLENGER:") + 13, sIn.getMsg().indexOf("\", CHALLENGENUMBER:"));
        challengeNr = sIn.getMsg().substring(sIn.getMsg().indexOf("CHALLENGENUMBER:") + 18, sIn.getMsg().indexOf("\", GAMETYPE:"));
        gameType = sIn.getMsg().substring(sIn.getMsg().indexOf("GAMETYPE:") + 11, sIn.getMsg().length() - 2);
    }

    private void showChallenge(){
        Platform.runLater(() -> {
            try{
                FXMLLoader loader1 = new FXMLLoader();
                loader1.setLocation(ClassLoader.getSystemResource("challengeForm.fxml"));
                Stage challengeStage = new Stage();
                challengeStage.setScene(new Scene(loader1.load()));
                ChallengeController challengeController = loader1.<ChallengeController>getController();
                challengeController.initData(serverOut, sIn, loginButton.getScene().getWindow(), opponent, challengeNr, gameType);
                challengeStage.show();
            } catch (InterruptedException | IOException ex ){
                ex.printStackTrace();
            }
        });
    }

    public void drawBoard(){
        newScrene = false;
        newChallenge = false;
        new Thread(() -> {
            newScrene = true;
            while (newScrene) {
                if (sIn.getConnected()) {
                    Platform.runLater(() -> {
                        try {
                            boolean bot = false;
                            if(botRadio.isSelected()){bot=true;}
                            FXMLLoader loader ;
                            Stage gamestage = new Stage();

                            if(tttRadio.isSelected()) {
                                loader = new FXMLLoader(getClass().getResource("BKEgameForm.fxml"));
                                gamestage.setScene(new Scene(loader.load()));
                                BKEGameController gamecontroller = loader.<BKEGameController>getController();
                                gamecontroller.initData(serverOut, sIn, nameInputField.getText(), loginButton.getScene().getWindow(), bot);
                            } else if(othRadio.isSelected()) {
                                loader = new FXMLLoader(getClass().getResource("OTHgameForm.fxml"));
                                gamestage.setScene(new Scene(loader.load()));
                                OTHGameController gamecontroller = loader.<OTHGameController>getController();
                                gamecontroller.initModel(serverOut, sIn);
                                gamecontroller.initData(nameInputField.getText(), loginButton.getScene().getWindow(), bot);
                            }
                            changeViews(gamestage);
                        } catch(Exception ex) {
                            ex.printStackTrace();
                        }
                    });newScrene = false;
                }
            }
        }).start();

    }

    public void changeViews(Stage changeStage){
        Platform.runLater(() -> {
            loginButton.getScene().getWindow().setOnHidden(e -> {
                subscribeButton.setDisable(false);
                connectionLabel.setText("Match bezig of klaar!");
            });
            changeStage.show();
            loginButton.getScene().getWindow().hide();
        });
    }

    @FXML
    public void doRefreshPLR() {
        playerBox.getItems().clear();
        serverOut.sendToServer("get playerlist");
        playerBox.setItems(sIn.returnOptions());
    }

    @FXML
    void doChallenge() {
        if (tttRadio.isSelected()) {
            System.out.println("challenge " + playerBox.getValue() + " \"Tic-tac-toe\"");
            serverOut.sendToServer("challenge " + playerBox.getValue() + " \"Tic-tac-toe\"");
        } else if (othRadio.isSelected()) {
            System.out.println("challenge " + playerBox.getValue() + " \"Reversi\"");
            serverOut.sendToServer("challenge " + playerBox.getValue() + " \"Reversi\"");
        }
    }

    @FXML
    void doSubscribe() {
        newChallenge = false;
        subsribe = true;
        subscribeButton.setDisable(true);
        if (tttRadio.isSelected()) {
            serverOut.sendToServer("subscribe Tic-tac-toe");
            connectionLabel.setText(nameInputField.getText() + ": Aan het wachten op een tegenstander...");
        }
        if (othRadio.isSelected()) {
            serverOut.sendToServer("subscribe Reversi");
            connectionLabel.setText(nameInputField.getText() + ": Aan het wachten op een tegenstander...");
        }
        newChallenge = false;
    }

    @FXML
    void doLogout() {
        loginButton.setDisable(false);
        serverOut.sendToServer("bye");
    }


}
