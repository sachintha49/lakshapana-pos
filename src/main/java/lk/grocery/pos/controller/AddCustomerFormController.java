package lk.grocery.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.grocery.pos.dto.CustomerDTO;
import lk.grocery.pos.exception.DuplicateIdentifierException;
import lk.grocery.pos.exception.NotFountException;
import lk.grocery.pos.exception.FailedOperationException;
import lk.grocery.pos.service.CustomerService;
import lk.grocery.pos.tm.CustomerTM;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;

public class AddCustomerFormController {
    public AnchorPane root;
    public JFXTextField txtCustomerId;
    public JFXTextField txtCustomerName;
    public JFXTextField txtCustomerAddress;
    public TableView<CustomerTM> tblViewCustomer;
    public TableColumn colCustomerId;
    public TableColumn colCustomerName;
    public TableColumn colCustomerAddress;
    public JFXButton saveBtnId;
    public JFXButton deleteBtnId;
    public JFXButton btnAddNewCustomerID;
    public JFXTextField txtCustomerCreditLimit;
    public TableColumn colCustomerCreditLimit;
    /* ape class eka purawatama customerService class eka ona nisa methana declare karnawa*/
    CustomerService customerService = new CustomerService();

    public void initialize() throws FailedOperationException {
        tblViewCustomer.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tblViewCustomer.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblViewCustomer.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("address"));
        tblViewCustomer.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("credit_limit"));

       /* txtCustomerId.setEditable(false);
        txtCustomerName.setEditable(false);
        txtCustomerAddress.setEditable(false);*/

      /*  saveBtnId.setDisable(true);
        deleteBtnId.setDisable(true);*/
        initUI();

        /* Mokak hari item ekak table eken select wena kota me listener eka weda karanawa */
        tblViewCustomer.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            /* Null ta == kiyanne seletion ekak nehe kiyana eka*/
            deleteBtnId.setDisable(newValue == null);
            saveBtnId.setText(newValue != null ? "Update" : "Save");
            saveBtnId.setDisable(newValue == null);

            if (newValue != null) {
                txtCustomerId.setText(newValue.getId());
                txtCustomerName.setText(newValue.getName());
                txtCustomerAddress.setText(newValue.getAddress());
                txtCustomerCreditLimit.setText(newValue.getCredit_limit().toString());

                txtCustomerId.setDisable(false);
                txtCustomerName.setDisable(false);
                txtCustomerAddress.setDisable(false);
                txtCustomerCreditLimit.setDisable(false);

            }
        });
        /* address field eke idala enter hit karapu gaman btnSave button eka fire wenawa*/
        txtCustomerAddress.setOnAction(event -> saveBtnId.fire());

        loadAllCustomer();
    }

    private void loadAllCustomer() throws FailedOperationException {
        tblViewCustomer.getItems().clear();
        try {
            List<CustomerDTO> allCustomer = customerService.findAllCustomer();

/*            for (CustomerDTO customerDTO : allCustomer){
                CustomerTM customerTM = new CustomerTM(customerDTO.getId(), customerDTO.getName(), customerDTO.getAddress());
                tblViewCustomer.getItems().add(customerTM);
            }*/
            /* Uda ekama api stream API eka use karala gahanna puluwan */
           /* List<CustomerTM> customers = allCustomer.stream().map(dto -> new CustomerTM(dto.getId(), dto.getName(), dto.getAddress())).collect(Collectors.toList());
            tblViewCustomer.setItems(FXCollections.observableList(customers));*/

            /* eka piyawarakin agahannath puuwan*/
            customerService.findAllCustomer().forEach(dto -> tblViewCustomer.getItems().add(new CustomerTM(dto.getId(),dto.getName(),dto.getAddress(),dto.getCreditLimit().setScale(2))));
        } catch (FailedOperationException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
            throw e;
        }
    }

    public void btnBackFromAddCustomerFormOnAction(ActionEvent actionEvent) throws IOException {
        URL resource = this.getClass().getResource("/view/MainPageForm.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) this.root.getScene().getWindow();
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        Platform.runLater(() -> primaryStage.sizeToScene());
    }

    public void btnSaveOnAtion(ActionEvent actionEvent) throws FailedOperationException {

        String id = txtCustomerId.getText();
        String name = txtCustomerName.getText();
        String address = txtCustomerAddress.getText();
        String creditLimit = txtCustomerCreditLimit.getText().equals("") ? "0.00" : txtCustomerCreditLimit.getText();

        /* Regular expression eken kiyanne: capital A to Z and simple a - z akuru thiyenne puluwan saha space thiyenne puluwan and aduma tharame eka akurak hari thiyenna one*/
        if (!name.matches("[A-Za-z ]+")) {
            new Alert(Alert.AlertType.ERROR, "Invalid name!").show();
            txtCustomerName.requestFocus();
            return;
        } else if (!address.matches(".{3,}")) {
            new Alert(Alert.AlertType.ERROR, "Address should be least 3 character long").show();
            txtCustomerAddress.requestFocus();
            return;
        }

        try {
            if (saveBtnId.getText().equalsIgnoreCase("Save")) {
                /* Todo: we need to save this in our database first then only the table should be updated */
                try {
                    customerService.saveCustomer(new CustomerDTO(id,name,address,new BigDecimal(creditLimit)));
                    tblViewCustomer.getItems().add(new CustomerTM(id, name, address,new BigDecimal(creditLimit)));
                } catch (DuplicateIdentifierException e) {
                    new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
                }
            } else {
                /* Todo: first of all we need to update the DB, if that success */

                try {
                    customerService.updateCustomer(new CustomerDTO(id,name,address,new BigDecimal(creditLimit)));
                    CustomerTM selectedItem = tblViewCustomer.getSelectionModel().getSelectedItem();
                    selectedItem.setName(name);
                    selectedItem.setAddress(address);
                    selectedItem.setCredit_limit(new BigDecimal(creditLimit));
                    tblViewCustomer.refresh();
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

        btnAddNewCustomerID.fire();
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) throws FailedOperationException {

        try{
            customerService.deleteCustomer(tblViewCustomer.getSelectionModel().getSelectedItem().getId());
            tblViewCustomer.getItems().remove(tblViewCustomer.getSelectionModel().getSelectedItem());
            tblViewCustomer.getSelectionModel().clearSelection();
            initUI();
        } catch (NotFountException e) {
            e.printStackTrace(); /* This never gonna be happen with our UI design thiayana ekkenekma thama delte karanne nisa*/
        } catch (FailedOperationException e) {
             new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
             throw e;
        }
    }

    public void initUI() {
        txtCustomerId.clear();
        txtCustomerName.clear();
        txtCustomerAddress.clear();
        txtCustomerCreditLimit.clear();

        txtCustomerId.setDisable(true);
        txtCustomerName.setDisable(true);
        txtCustomerAddress.setDisable(true);
        txtCustomerCreditLimit.setDisable(true);

        txtCustomerId.setEditable(false);
        saveBtnId.setDisable(true);
        deleteBtnId.setDisable(true);

    }

    public void btnAddNewCustomerOnAction(ActionEvent actionEvent) throws FailedOperationException {
        txtCustomerId.clear();
        txtCustomerId.setText(generateNewId());
        txtCustomerName.clear();
        txtCustomerAddress.clear();
        txtCustomerCreditLimit.clear();
        txtCustomerName.requestFocus();
        saveBtnId.setText("Save");

        txtCustomerName.setDisable(false);
        txtCustomerAddress.setDisable(false);
        txtCustomerCreditLimit.setDisable(false);

        saveBtnId.setDisable(false);

        tblViewCustomer.getSelectionModel().clearSelection();
    }

    public String generateNewId() throws FailedOperationException {
        try {
            return customerService.generateNewCustomerId();
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
