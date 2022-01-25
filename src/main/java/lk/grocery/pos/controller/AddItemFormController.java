package lk.grocery.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lk.grocery.pos.exception.DuplicateIdentifierException;
import lk.grocery.pos.exception.NotFountException;
import lk.grocery.pos.dto.ItemDTO;
import lk.grocery.pos.exception.FailedOperationException;
import lk.grocery.pos.service.ItemService;
import lk.grocery.pos.tm.ItemTM;
import org.jfree.ui.ExtensionFileFilter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

public class AddItemFormController {
    public AnchorPane root;
    public JFXTextField txtItemCode;
    public JFXTextField txtItemDescription;
    public JFXTextField txtItemUnitPrice;
    public JFXButton saveBtnId;
    public JFXButton deleteBtnId;
    public TableView<ItemTM> tblViewItem;
    public TableColumn colItemCode;
    public TableColumn colItemDescription;
    public TableColumn colItemUnitPrice;
    public TableColumn colQuantityOnHand;
    public JFXTextField txtQuantityOnHand;
    public JFXButton btnAddNewItemID;
    public ComboBox cmbUnitType;
    public TableColumn colUnitType;
    public int fileLength;
    public TableColumn colImagePath;

    /* ape class eka purawatama customerService class eka ona nisa methana declare karnawa*/
    ItemService itemService = new ItemService();
    List<String> imageFile;

    public void initialize() throws FailedOperationException {
        tblViewItem.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("code"));
        tblViewItem.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tblViewItem.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("unit_price"));
        tblViewItem.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("qty_on_hand"));
        tblViewItem.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("unit_type"));

        imageFile = new ArrayList<>();
        imageFile.add("*.png");
        imageFile.add("*.jpg");
        imageFile.add("*.jpeg");

       /* txtCustomerId.setEditable(false);
        txtCustomerName.setEditable(false);
        txtCustomerAddress.setEditable(false);*/

      /*  saveBtnId.setDisable(true);
        deleteBtnId.setDisable(true);*/
        initUI();



        /* Mokak hari item ekak table eken select wena kota me listener eka weda karanawa */
        tblViewItem.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            /* Null ta == kiyanne seletion ekak nehe kiyana eka*/
            deleteBtnId.setDisable(newValue == null);
            saveBtnId.setText(newValue != null ? "Update" : "Save");
            saveBtnId.setDisable(newValue == null);

            if (newValue != null) {
                txtItemCode.setText(newValue.getCode());
                txtItemDescription.setText(newValue.getDescription());
                txtItemUnitPrice.setText(String.valueOf(newValue.getUnit_price()));
                txtQuantityOnHand.setText(String.valueOf(newValue.getQty_on_hand()));
                cmbUnitType.setValue(newValue.getUnit_type());
                txtItemCode.setDisable(false);
                txtItemDescription.setDisable(false);
                txtItemUnitPrice.setDisable(false);
                txtQuantityOnHand.setDisable(false);

            }
        });
        /* address field eke idala enter hit karapu gaman btnSave button eka fire wenawa*/
        /*txtCustomerAddress.setOnAction(event -> saveBtnId.fire());*/

        loadAllItem();

        cmbUnitType.getItems().addAll("none","Kg","l");
        cmbUnitType.getSelectionModel().selectFirst();
    }

    private void loadAllItem() throws FailedOperationException {
        tblViewItem.getItems().clear();
        try {
            List<ItemDTO> allItem = itemService.findAllItems();

/*            for (CustomerDTO customerDTO : allCustomer){
                CustomerTM customerTM = new CustomerTM(customerDTO.getId(), customerDTO.getName(), customerDTO.getAddress());
                tblViewCustomer.getItems().add(customerTM);
            }*/
            /* Uda ekama api stream API eka use karala gahanna puluwan */
           /* List<CustomerTM> customers = allCustomer.stream().map(dto -> new CustomerTM(dto.getId(), dto.getName(), dto.getAddress())).collect(Collectors.toList());
            tblViewCustomer.setItems(FXCollections.observableList(customers));*/

            /* eka piyawarakin agahannath puuwan*/
            List<ItemDTO> listItem = itemService.findAllItems();
            listItem.forEach(dto -> tblViewItem.getItems().add(new ItemTM(dto.getCode(),
                    dto.getDescription(),dto.getUnitPrice().setScale(2),dto.getQtyOnHand(),dto.getUnitType())));
        } catch (FailedOperationException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
            throw e;
        }
    }

    public void btnBackFromAddItemFormOnAction(ActionEvent actionEvent) throws IOException {
        URL resource = this.getClass().getResource("/view/MainPageForm.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) this.root.getScene().getWindow();
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        Platform.runLater(() -> primaryStage.sizeToScene());
    }

    public void btnSaveOnAtion(ActionEvent actionEvent) throws FailedOperationException, FileNotFoundException {
        String code = txtItemCode.getText();
        String unitType = cmbUnitType.getSelectionModel().getSelectedItem().toString();
        String description = txtItemDescription.getText();
        String unitPrice = txtItemUnitPrice.getText();
        String qtyOnHand = txtQuantityOnHand.getText();
        /* Regular expression eken kiyanne: capital A to Z and simple a - z akuru thiyenne puluwan saha space thiyenne puluwan and aduma tharame eka akurak hari thiyenna one*/
       /* if (!description.matches("[A-Za-z ]+")) {
            new Alert(Alert.AlertType.ERROR, "Invalid description!").show();
            txtItemDescription.requestFocus();
            return;
        } else if (!address.matches(".{3,}")) {
            new Alert(Alert.AlertType.ERROR, "Address should be least 3 character long").show();
            txtCustomerAddress.requestFocus();
            return;
        }*/

        try {
            if (saveBtnId.getText().equalsIgnoreCase("Save")) {
                /* Todo: we need to save this in our database first then only the table should be updated */
                try {
                    itemService.saveItem(new ItemDTO(code,description,new BigDecimal(unitPrice),Integer.parseInt(qtyOnHand),unitType));
                    tblViewItem.getItems().add(new ItemTM(code, description, new BigDecimal(unitPrice),Integer.parseInt(qtyOnHand),unitType));
                } catch (DuplicateIdentifierException e) {
                    new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
                }
            } else {
                /* Todo: first of all we need to update the DB, if that success */

                try {
                    itemService.updateItem(new ItemDTO(code, description, new BigDecimal(unitPrice),Integer.parseInt(qtyOnHand),unitType));
                    ItemTM selectedItem = tblViewItem.getSelectionModel().getSelectedItem();
                    selectedItem.setDescription(description);
                    selectedItem.setUnit_price(new BigDecimal(unitPrice));
                    selectedItem.setQty_on_hand(Integer.parseInt(qtyOnHand));
                    selectedItem.setUnit_type(unitType);
                    tblViewItem.refresh();
                } catch (FailedOperationException e) {
                    e.printStackTrace();
                } catch (NotFountException e) {
                    /* This never going to be happened*/
                    e.printStackTrace();
                }


            }
        }catch (FailedOperationException e){
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
            throw e;
        }

        btnAddNewItemID.fire();
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) throws FailedOperationException {

        try{
            itemService.deleteItem(tblViewItem.getSelectionModel().getSelectedItem().getCode());
            tblViewItem.getItems().remove(tblViewItem.getSelectionModel().getSelectedItem());
            tblViewItem.getSelectionModel().clearSelection();
            initUI();
        } catch (NotFountException e) {
            e.printStackTrace(); /* This never gonna be happen with our UI design thiayana ekkenekma thama delte karanne nisa*/
        } catch (FailedOperationException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
            throw e;
        }
    }

    public void initUI() {
        txtItemCode.clear();
        txtItemDescription.clear();
        txtItemUnitPrice.clear();
        txtQuantityOnHand.clear();

        txtItemCode.setDisable(true);
        txtItemDescription.setDisable(true);
        txtItemUnitPrice.setDisable(true);
        txtQuantityOnHand.setDisable(true);

        txtItemCode.setEditable(false);
        saveBtnId.setDisable(true);
        deleteBtnId.setDisable(true);

    }

    public void btnAddNewItemOnAction(ActionEvent actionEvent) throws FailedOperationException {
        txtItemCode.clear();
        txtItemCode.setText(generateNewId());
        txtItemDescription.clear();
        txtItemUnitPrice.clear();
        txtQuantityOnHand.clear();
        txtItemDescription.requestFocus();
        saveBtnId.setText("Save");

        txtItemDescription.setDisable(false);
        txtItemUnitPrice.setDisable(false);
        txtQuantityOnHand.setDisable(false);

        saveBtnId.setDisable(false);

        tblViewItem.getSelectionModel().clearSelection();
        txtItemDescription.requestFocus();
    }

    public String generateNewId() throws FailedOperationException {
        try {
            return itemService.generateNewItemId();
        } catch (FailedOperationException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
            throw e;
        }
      /*  if (tblViewCustomer.getItems().isEmpty()) {
            return "C001";
        } else {*/
        /* Get the last entered id from the table*/
        /* this ID can be started with captial C , C100, C001, C022, C1000*//*
            String id = tblViewCustomer.getItems().get(tblViewCustomer.getItems().size() - 1).getId(); // C003
            int newCustomerID = Integer.parseInt(id.replace("C", "")) + 1; // 003 removes the C letter

            return String.format("C%03d", newCustomerID);*/
    }

}
