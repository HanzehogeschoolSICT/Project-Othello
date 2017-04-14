import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
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

    private Model model;
    private ServerIn sIn;


    private Window oldWindow;

    private String gamestatus;


    public EoMController() {
    }

    public void initData(Model conModel, ServerIn consIn, Window window, String gameresult) throws InterruptedException {
        model = conModel;
        sIn = consIn;
        oldWindow = window;
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