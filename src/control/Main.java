package control;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import tests.Tester;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Tester tester = new Tester();
        Parent root = FXMLLoader.load(getClass().getResource("/view/connectForm.fxml"));
        primaryStage.setTitle("BKE/Othello Launcher");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                Platform.exit();
                System.exit(0);
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
