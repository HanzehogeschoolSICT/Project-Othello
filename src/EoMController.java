import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Window;

import java.io.IOException;

/**
 * Created by stefa on 10-4-2017.
 */
public class EoMController {
    @FXML
    private Button EoMbutton;
    @FXML
    private Label eomLabel;

    private ServerOut serverOut;
    private ServerIn sIn;


    private Window oldWindow;

    private String gamestatus;


    public EoMController() {
    }

    public void initData(String gameresult) throws InterruptedException {


        gamestatus = gameresult;
        eomLabel.setText(gamestatus);

    }


    @FXML
    public void close() throws IOException {
//        Stage primaryStage = (Stage) oldWindow;
//        primaryStage.show();
        eomLabel.getScene().getWindow().hide();


    }
}