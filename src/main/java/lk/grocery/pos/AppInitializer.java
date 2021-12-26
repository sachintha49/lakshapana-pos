package lk.grocery.pos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lk.grocery.pos.controller.MainPageFormController;

import java.io.IOException;

public class AppInitializer extends Application {
    MainPageFormController mainPage = new MainPageFormController();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/MainPageForm.fxml"));
        Scene mainScene = new Scene(root);
        primaryStage.setScene(mainScene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Java FX Backup POS");
        primaryStage.centerOnScreen();

        primaryStage.show();
    }
}
