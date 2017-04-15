import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
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
public class ChallengeController {
    @FXML private Button challengeButton;
    @FXML private Label challengeLabel;

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
    public void doAccept() throws IOException{
        model.sendToServer("challenge accept " + challengeNr);
        Stage primaryStage = (Stage)oldWindow;
        primaryStage.show();
        challengeLabel.getScene().getWindow().hide();



//        FXMLLoader loader2 = new FXMLLoader();
//        loader2.setLocation(ClassLoader.getSystemResource("connectForm.fxml"));
//        Controller controller  = loader2.<Controller>getController();
//        controller.drawBoard();
    }




}
