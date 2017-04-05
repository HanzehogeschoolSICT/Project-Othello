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
public class ChallengeController {
    @FXML private Button challengeButton;
    @FXML private Label challengeLabel;
    @FXML private GridPane gridPane;
    @FXML private Label ownNameLabel;
    @FXML private Label oppNameLabel;
    @FXML private Label turnLabel;

    private Model model;
    private ServerIn sIn;


    private Window oldWindow;

    private String oppName;

    private String challengeNr;

    private String gameType;








    public ChallengeController(){}

    public void initData(Model conModel, ServerIn consIn, Window window, String name,String number, String game) throws InterruptedException{
        oppName = name;
        model = conModel;
        sIn = consIn;
        oldWindow = window;
        gameType = game;
        challengeLabel.setText(oppName +  " Daagt jouw uit voor een potje " + gameType );
        challengeNr = number;
    }







    @FXML
    public void doAccept(){
        model.sendToServer("challenge accept " + challengeNr);
        challengeLabel.getScene().getWindow().hide();

    }




}
