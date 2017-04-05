import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller {
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

    private Model model = new Model();
    private ServerIn sIn;

    private Boolean newChallenge;

    private String opponent;
    private String challengeNr;
    private String gameType;

    public Controller(){}

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
        new Thread(() -> {
            newChallenge = true;
            while (newChallenge){
                if (sIn.getChallenge()){
                    Platform.runLater(() -> {
                        try {
                            System.out.print("");
                            opponent = sIn.getMsg().substring(sIn.getMsg().indexOf("CHALLENGER:")+ 13, sIn.getMsg().indexOf("\", CHALLENGENUMBER:"));
                            challengeNr = sIn.getMsg().substring(sIn.getMsg().indexOf("CHALLENGENUMBER:")+18, sIn.getMsg().indexOf("\", GAMETYPE:"));
                            gameType = sIn.getMsg().substring(sIn.getMsg().indexOf("GAMETYPE:")+11, sIn.getMsg().length()-2);
                            System.out.println(opponent);
                            System.out.println(challengeNr);
                            System.out.println(gameType);
                            FXMLLoader loader1 = new FXMLLoader();
                            loader1.setLocation(ClassLoader.getSystemResource("challengeForm.fxml"));
                            Stage challengeStage = new Stage();
                            challengeStage.setScene(new Scene(loader1.load()));
                            ChallengeController challengeController = loader1.<ChallengeController>getController();
                            challengeController.initData(model,sIn,loginButton.getScene().getWindow(),opponent, challengeNr, gameType);
                            challengeStage.show();
                            loginButton.getScene().getWindow().hide();
                        } catch(Exception ex) {
                            ex.printStackTrace();
                    }
                });newChallenge = false;
            }
        }
    }).start();

    }

    @FXML public void doRefreshPLR(){

    }
    @FXML void doChallenge(){
        //TODO Niet hardcoden
        System.out.println("challenge " + playerBox.getValue() + " \"Tic-tac-toe\"");
        model.sendToServer("challenge " + playerBox.getValue() + " \"Tic-tac-toe\"");
    }

    @FXML void doSubscribe() {
        if(tttRadio.isSelected()){
            model.sendToServer("subscribe Tic-tac-toe");
            subscribeButton.setDisable(true);
            connectionLabel.setText(nameInputField.getText() + ": Aan het wachten op een tegenstander...");
            newChallenge = false;
            new Thread(() -> {
                boolean newScrene = true;
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
        if(othRadio.isSelected()){
            model.sendToServer("subscribe Reversi");
            subscribeButton.setDisable(true);
            connectionLabel.setText(nameInputField.getText() + ": Aan het wachten op een tegenstander...");
            newChallenge = false;
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
    }


}
