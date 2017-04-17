package control;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import server.ServerIn;
import server.ServerOut;

import java.io.IOException;
import java.util.Optional;

public class Controller implements Runnable {
    private static Thread t;
    public static volatile boolean shouldStop = false;
    public static Boolean challengeOpen;
    public static Boolean newGame = false;
    private static Controller instance = null;

    @FXML
    private Button loginButton;
    @FXML
    private TextField nameInputField;
    @FXML
    private ComboBox ipInputField;
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
    @FXML
    private CheckBox battleBox;

    private ServerOut serverOut = new ServerOut();
    private ServerIn sIn;
    private Boolean newChallenge;
    private String opponent;
    private String challengeNr;
    private String gameType;
    private Boolean subsribe = false;
    private Boolean newScrene = false;

    private FXMLLoader loader;
    private Stage gamestage = new Stage();

    public Controller() {
        t = new Thread(this, "newGame Thread");
    }

    private void start() {
        t.start();
    }

    public void stop() {
        System.out.println("thread is closing.....");
        shouldStop = true;
    }

    public void run() {
        while (!shouldStop) {
            try {
                t.sleep(500);
                if (newGame) {
                    if (sIn.getMsg().contains("Tic-tac-")) {
                        drawBoard();
                        newGame = false;
                    }
                    if (sIn.getMsg().contains("Reversi")) {
                        drawBoard();
                        newGame = false;
                        System.out.println("KLAAAARRRRRKLAAAARRRRRKLAAAARRRRRKLAAAARRRRRKLAAAARRRRRKLAAAARRRRRKLAAAARRRRRKLAAAARRRRRKLAAAARRRRRKLAAAARRRRRKLAAAARRRRRKLAAAARRRRR");
                    }
                } else if (challengeOpen && !battleBox.isSelected()) {
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
        ipInputField.getItems().addAll(
                "localhost",
                "145.33.225.170",
                "koekjesclan.nl"
        );
    }

    @FXML
    public void doLogin() throws IOException {
        serverOut.connectToServer(nameInputField.getText(), ipInputField.getValue().toString());
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

    private void getChallenge() {
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

    private void initChallenge() {
        opponent = sIn.getMsg().substring(sIn.getMsg().indexOf("CHALLENGER:") + 13, sIn.getMsg().indexOf("\", CHALLENGENUMBER:"));
        challengeNr = sIn.getMsg().substring(sIn.getMsg().indexOf("CHALLENGENUMBER:") + 18, sIn.getMsg().indexOf("\", GAMETYPE:"));
        gameType = sIn.getMsg().substring(sIn.getMsg().indexOf("GAMETYPE:") + 11, sIn.getMsg().length() - 2);
    }

    private void showChallenge() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Uitdaging!");
            alert.setHeaderText(opponent + " daagt je uit, accepteer je?");
            alert.setContentText("Game: " + gameType);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                serverOut.sendToServer("challenge accept " + challengeNr);
            } else {
                sIn.resetChallenge();
                challengeOpen = true;

            }
        });
    }

    private void startGame(String game, boolean bot) throws Exception {
        if (game.equals("ttt")) {
            loader = new FXMLLoader(getClass().getResource("/view/BKEgameForm.fxml"));
            gamestage.setScene(new Scene(loader.load()));
            BKEGameController gamecontroller = loader.<BKEGameController>getController();
            gamecontroller.initData(serverOut, sIn, nameInputField.getText(), loginButton.getScene().getWindow(), bot);
        } else if (game.equals("oth")) {

//            OTHGameController gamecontroller = new OTHGameController();
//            loader = new FXMLLoader(getClass().getResource("/view/OTHgameForm.fxml"));
//            loader.setController(gamecontroller);
//            loader.<OTHGameController>getController().initModel(serverOut, sIn);
//            loader.<OTHGameController>getController().initData(nameInputField.getText(), loginButton.getScene().getWindow());
//            loader.<OTHGameController>getController().initSettings(bot, battleBox.isSelected());
//            gamestage.setScene(new Scene(loader.load()));


            loader = new FXMLLoader(getClass().getResource("/view/OTHgameForm.fxml"));
            gamestage.setScene(new Scene(loader.load()));
            OTHGameController gamecontroller = loader.<OTHGameController>getController();
            System.out.println(gamecontroller);
            gamecontroller.initModel(serverOut, sIn);
            gamecontroller.initData(nameInputField.getText(), loginButton.getScene().getWindow());
            gamecontroller.initSettings(bot, battleBox.isSelected());
        }
    }

    private void drawBoard() {
        new Thread(() -> {
            newScrene = true;
            while (newScrene) {
                if (sIn.getConnected()) {
                    Platform.runLater(() -> {
                        try {
                            if (tttRadio.isSelected()) {
                                startGame("ttt", botRadio.isSelected());
                            } else if (othRadio.isSelected()) {
                                startGame("oth", botRadio.isSelected());
                            }
                            changeViews(gamestage);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                    newScrene = false;
                }
            }
            System.out.println("----------------------------------------------------------------------");
        }).start();
    }

    private void changeViews(Stage changeStage) {
        Platform.runLater(() -> {
            loginButton.getScene().getWindow().setOnHidden(e -> {
                subscribeButton.setDisable(false);
                connectionLabel.setText("Match bezig of klaar!");
            });
            changeStage.setTitle("LEKKER SPELEN!");
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
        }
        if (othRadio.isSelected()) {
            serverOut.sendToServer("subscribe Reversi");
        }
        connectionLabel.setText(nameInputField.getText() + ": Aan het wachten op een tegenstander...");
        newChallenge = false;
    }

    @FXML
    void doLogout() {
        loginButton.setDisable(false);
        serverOut.sendToServer("bye");
    }



}
