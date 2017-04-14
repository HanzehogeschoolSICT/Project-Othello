import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by stefa on 14-4-2017.
 */
public class EoG {



public static void getEoMform(String gameResult){
    Platform.runLater(() -> {
        try{
            FXMLLoader loader1 = new FXMLLoader();
            loader1.setLocation(ClassLoader.getSystemResource("EoMForm.fxml"));
            Stage EoMstage = new Stage();
            EoMstage.setScene(new Scene(loader1.load()));
            EoMController EoMcontroller = loader1.<EoMController>getController();
            EoMcontroller.initData(gameResult);
            EoMstage.show();
        }
        catch(Exception ex) {
        ex.printStackTrace();
        }

    });
    }
}
