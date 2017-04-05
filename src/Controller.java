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

        }
    }


}
