package lk.grocery.pos.controller;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BackgroundImage;
import javafx.stage.Stage;

import java.io.IOException;

public class MainPageFormController {

    public AnchorPane root;
    public ImageView btnAddCustomer;
    public ImageView btnAddItem;
    public ImageView btnPlaceOrder;

    public void initialize(){

    }


    public void addCustomerOnAction(MouseEvent mouseEvent) throws IOException {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/AddCustomerForm.fxml"))));
        //stage.setResizable(false);
        stage.centerOnScreen();
    }

    public void addItemOnAction(MouseEvent mouseEvent) throws IOException {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/AddItemForm.fxml"))));
        //stage.setResizable(false);
        stage.centerOnScreen();
    }

    public void placeOrderOnAction(MouseEvent mouseEvent) throws IOException {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/PlaceOrderForm.fxml"))));
        //stage.setResizable(false);
        stage.centerOnScreen();
    }

}
