import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * Created by stefa on 14-4-2017.
 */
public class EoG {



public static void getEoMform(String gameResult){
    Platform.runLater(()->{
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Spel beeindigd");
        alert.setHeaderText(gameResult);
        alert.setContentText("Echt fantastisch!!");
        alert.showAndWait();

    });
    }
}
