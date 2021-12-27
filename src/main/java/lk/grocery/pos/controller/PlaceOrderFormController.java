package lk.grocery.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.grocery.pos.util.PrintBillDetails;
import lk.grocery.pos.db.DBConnection;
import lk.grocery.pos.dto.ItemDTO;
import lk.grocery.pos.tm.OrderItemDetailTM;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.controlsfx.control.textfield.TextFields;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class PlaceOrderFormController {
    /*public TextField text;*/
    public AnchorPane root;
    public JFXTextField txtSelectItem;
    public Button txtAddAItemToBill;
    public TableView<OrderItemDetailTM> tblPlaceOrder;
    public TableColumn colItemCode;
    public TableColumn colItemName;
    public TableColumn colQty;
    public TableColumn colUnitPrice;
    public TableColumn colTotal;
    public TableColumn colDeleteBtn;
    public JFXTextField txtQty;
    public JFXTextField txtUnitPrice;
    public Label lblTotalPriceOfOrder;
    public Label lblOrderDate;
    public Label lblOrderId;
    public JFXTextField txtItemName;
    public Button btnAddItem;
    public TableColumn colDiscount;
    public JFXTextField txtDiscount;
    public Button btnPlaceOrder;
    public Label lblGrossAmount;
    public Label lblTotalDiscount;
    public JFXTextField txtCustomerCash;
    public JFXButton btnRefreshID;

    ItemDTO currentSelectedItem;
    ObservableList<OrderItemDetailTM> orderItemList = FXCollections.observableArrayList();


    public void initialize() {
        /* Autocomplete text box implementation*/
        final List<String> hitProducts = getHitProducts();

        TextFields.bindAutoCompletion(txtSelectItem, hitProducts);

        txtSelectItem.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {

                if (event.getCode() == KeyCode.ENTER) {
                    fillItemDetailInField(txtSelectItem.getText());
                    txtQty.requestFocus();
                } else if (event.getCode() == KeyCode.SPACE) {
                    txtCustomerCash.requestFocus();
                } else if (event.getCode() == KeyCode.BACK_SPACE) {
                    clearAllFields();
                }
            }
        });
        txtQty.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    txtDiscount.requestFocus();
                } else if (event.getCode() == KeyCode.SHIFT) {
                    txtSelectItem.requestFocus();
                    clearAllFields();
                }
            }
        });

        txtCustomerCash.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.SPACE) {
                    btnPlaceOrder.fire();
                } else if (event.getCode() == KeyCode.SHIFT) {
                    txtSelectItem.requestFocus();
                    clearAllFields();
                }
            }
        });
        txtDiscount.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    btnAddItem.fire();
                    txtSelectItem.requestFocus();
                } else if (event.getCode() == KeyCode.SHIFT) {
                    txtQty.requestFocus();
                }
            }
        });


        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            lblOrderDate.setText(javaDateFormat());
        }),
                new KeyFrame(Duration.seconds(10))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();

        /* Initialze table values*/
        tblPlaceOrder.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        tblPlaceOrder.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("itemName"));
        tblPlaceOrder.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("quantity"));
        tblPlaceOrder.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        tblPlaceOrder.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("discount"));
        tblPlaceOrder.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("total"));

        TableColumn<OrderItemDetailTM, Button> deleteBtn = (TableColumn<OrderItemDetailTM, Button>) tblPlaceOrder.getColumns().get(6);

        deleteBtn.setCellValueFactory(param -> {
            /*System.out.println(param);*/
            Button btnDelete = new Button("Delete");

            btnDelete.setOnAction(event -> {
                tblPlaceOrder.getItems().remove(param.getValue());
                tblPlaceOrder.getSelectionModel().clearSelection();
                calculateTotal();
                calculateGrossAmount();
                calculateTotalDiscount();
//                enableOrDisablePlaceOrderButton();
            });

            return new ReadOnlyObjectWrapper<>(btnDelete);
        });

        tblPlaceOrder.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedOrderDetail) -> {

            if (selectedOrderDetail != null) {
                /*btnAddItem.setText("Update");*/
                txtSelectItem.setText(selectedOrderDetail.getItemCode());
                txtItemName.setText(selectedOrderDetail.getItemName());
                txtQty.setText(String.valueOf(selectedOrderDetail.getQuantity()));
                txtUnitPrice.setText(String.valueOf(selectedOrderDetail.getUnitPrice()));
            } else {
               /* btnSave.setText("Add");
                cmbItemCode.setDisable(false);
                cmbItemCode.getSelectionModel().clearSelection();
                txtQty.clear();*/
            }

        });

        txtDiscount.setText("0.00");

        txtDiscount.setOnMouseClicked(e -> txtDiscount.selectAll());

        txtUnitPrice.setEditable(false);
        txtItemName.setEditable(false);

    }

    private int isAlreadyExists(String itemCode) {
        /*for (PlaceOrderTM tm : tableTm){
            if (itemCode.equals(tm.getCode())){
                return 1;
            }
        }*/
        for (int i = 0; i < orderItemList.size(); i++) {
            if (itemCode.equals(orderItemList.get(i).getItemCode())) {
                return i;
            }

        }
        return -1;
    }

    private void calculateTotal() {
        lblTotalPriceOfOrder.setText("" + tblPlaceOrder.getItems().stream().map(detail -> detail.getTotal())
                .reduce((accumulator, element) -> accumulator.add(element)).orElse(new BigDecimal(0)).setScale(2));
    }

    private void calculateTotalDiscount() {
        lblTotalDiscount.setText("- " + tblPlaceOrder.getItems().stream().map(detail -> detail.getDiscount())
                .reduce((accumulator, element) -> accumulator.add(element)).orElse(new BigDecimal(0)).setScale(2));
    }

    private void calculateGrossAmount() {
        lblGrossAmount.setText("" + tblPlaceOrder.getItems().stream().map(detail -> detail.getTotal().add(detail.getDiscount()))
                .reduce((accumulator, element) -> accumulator.add(element)).orElse(new BigDecimal(0)).setScale(2));
    }

    private void fillItemDetailInField(String itemCode) {
        if (itemCode.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please check the input is correct", ButtonType.OK).show();
            return;
        }
        try {
            final PreparedStatement prst = DBConnection.getInstance().getConnection().prepareStatement("SELECT * FROM item WHERE code = ?");
            prst.setString(1, itemCode);
            ResultSet rstSet = prst.executeQuery();
            rstSet.next();

            currentSelectedItem = new ItemDTO(rstSet.getString("code"),
                    rstSet.getString("description"),
                    rstSet.getBigDecimal("unit_price"),
                    rstSet.getInt("qty_on_hand"));

            txtItemName.setText(currentSelectedItem.getDescription());
            txtUnitPrice.setText(String.valueOf(currentSelectedItem.getUnitPrice()));
        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, "Please enter correct name of the item").show();
            e.printStackTrace();
        }
    }


    private List<String> getHitProducts() {
        List<String> list = new ArrayList<>();
        try {
            PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement("SELECT description FROM item");
            ResultSet rst = pstm.executeQuery();
            while (rst.next()) {
                list.add(rst.getString("description"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
//        List<String> list = ProductService.getSearchProduct(keyword);

        return list;
    }

    public void backToHomeOnAction(MouseEvent mouseEvent) throws IOException {
        URL resource = this.getClass().getResource("/view/MainPageForm.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) this.root.getScene().getWindow();
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        Platform.runLater(() -> primaryStage.sizeToScene());
    }

    public void clearAllFields() {

        txtSelectItem.clear();
        txtQty.clear();
        txtUnitPrice.clear();
        txtItemName.clear();
        txtDiscount.setText("0.00");
    }
public boolean validDiscount(){
        int qty = Integer.parseInt(txtQty.getText());
        double unitPrice = Double.parseDouble(txtUnitPrice.getText());
        double dis = Double.parseDouble(txtDiscount.getText());
        double itemPrice = unitPrice * qty;
        return dis < itemPrice ? false :true;
}

    public void btnAddItemToBillOnAtion(ActionEvent actionEvent) {
        if (txtDiscount.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Please enter discount").show();
            return;
        } else if (txtQty.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Please enter quantity").show();
            clearAllFields();
            return;
        } else if (txtQty.getText().trim().matches("^\\d+\\.\\d+") || !(Integer.parseInt(txtQty.getText().trim()) > 0)) {
            new Alert(Alert.AlertType.ERROR, "Please enter correct quantity").show();
            clearAllFields();
            return;
        } else if (new BigDecimal(txtDiscount.getText().trim()).compareTo(BigDecimal.ZERO) < 0) {
            new Alert(Alert.AlertType.ERROR, "Please enter correct discount").show();
            clearAllFields();
            return;
        }else if (validDiscount()){
            new Alert(Alert.AlertType.ERROR, "Please enter correct less discount").show();
            clearAllFields();
            return;
        }

        int rowIndex = isAlreadyExists(txtItemName.getText());

        if (rowIndex == -1) {
            OrderItemDetailTM orderItem = new OrderItemDetailTM();
            orderItem.setItemCode(currentSelectedItem.getCode());
            orderItem.setItemName(currentSelectedItem.getDescription());
            orderItem.setQuantity(Integer.parseInt(txtQty.getText().trim()));
            orderItem.setUnitPrice(currentSelectedItem.getUnitPrice().setScale(2));
            orderItem.setDiscount(new BigDecimal(txtDiscount.getText()).setScale(2));
            orderItem.setTotal(printTotalPerItem(txtQty.getText(),
                    String.valueOf(currentSelectedItem.getUnitPrice()), Double.parseDouble(txtDiscount.getText())));
//            String itemCode, String itemName, int quantity, BigDecimal unitPrice, double discount, BigDecimal total
            orderItemList.add(orderItem);

            tblPlaceOrder.setItems(orderItemList);
            clearAllFields();
        } else {
            new Alert(Alert.AlertType.WARNING, "The item is already existing", ButtonType.OK).show();

        }

        tblPlaceOrder.setItems(orderItemList);
        calculateTotal();
        calculateGrossAmount();
        calculateTotalDiscount();
    }

    private BigDecimal printTotalPerItem(String qty, String untPrice, double discount) {

        BigDecimal quantity = new BigDecimal(qty.trim());
        BigDecimal unitPrice = new BigDecimal(untPrice);
        BigDecimal itemTotal = quantity.multiply(unitPrice);
        final BigDecimal itemWithDiscount = itemTotal.subtract(BigDecimal.valueOf(discount));
        return itemWithDiscount.setScale(2);
    }

    public String javaDateFormat() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh.mm aa");
        String dateString = dateFormat.format(new Date());
        return dateString;
    }

    public void placeOrderBtnOnAction(ActionEvent actionEvent) throws JRException {
        if (txtCustomerCash.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please enter given cash!", ButtonType.OK).show();
            return;
        }
        BigDecimal customerCash = new BigDecimal(txtCustomerCash.getText().trim());
        BigDecimal netAmount = new BigDecimal(lblTotalPriceOfOrder.getText());
        if ((netAmount.compareTo(customerCash) == 1) && !(netAmount.compareTo(customerCash) == 0)) {
            new Alert(Alert.AlertType.WARNING, "Ask customer more money", ButtonType.OK).show();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to print this?", ButtonType.YES, ButtonType.CANCEL);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            List<PrintBillDetails> billItems = new ArrayList<>();

            for (OrderItemDetailTM item : tblPlaceOrder.getItems()) {

                String productDes = "[" + item.getQuantity() + " x " + item.getUnitPrice() + "]";
                if (item.getDiscount().compareTo(BigDecimal.ZERO) > 0) {
                    productDes += " - " + item.getDiscount();
                }

                billItems.add(new PrintBillDetails(item.getItemName(), item.getItemCode(), productDes, item.getTotal()));
            }
            JasperDesign jasperDesign = JRXmlLoader.load(this.getClass().getResourceAsStream("/report/pos-bill.jrxml"));
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);


            Map<String, Object> params = new HashMap<>();
            params.put("currentDate", javaDateFormat());
            params.put("grossTot", lblGrossAmount.getText());
            params.put("totDis", lblTotalDiscount.getText());
            params.put("cash", customerPaidCash());
            params.put("balance", calculateBalance());

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JRBeanCollectionDataSource(billItems));
            JasperViewer.viewReport(jasperPrint, false);
            //JasperPrintManager.printReport(jasperPrint, false);

            //tblPlaceOrder.getItems().clear();
        } else if (alert.getResult() == ButtonType.CANCEL) {
            System.out.println("return una");
            return;
        }

    }

    private String customerPaidCash() {
        BigDecimal customerCash = new BigDecimal(txtCustomerCash.getText().trim()).setScale(2);
        String cash = String.valueOf(customerCash);
        return cash;
    }

    private String calculateBalance() {
        BigDecimal customerCash = new BigDecimal(txtCustomerCash.getText().trim());
        BigDecimal netTotal = new BigDecimal(lblTotalPriceOfOrder.getText());
        BigDecimal balance = customerCash.subtract(netTotal);
        String balanceCusPay = String.valueOf(balance);
        return balanceCusPay;
    }

    public void btnRefreshOnAction(ActionEvent actionEvent) {
        clearAllFields();
        txtSelectItem.requestFocus();
        tblPlaceOrder.getItems().clear();
        lblGrossAmount.setText("00.00");
        lblTotalDiscount.setText("00.00");
        lblTotalPriceOfOrder.setText("00.00");
    }
}
