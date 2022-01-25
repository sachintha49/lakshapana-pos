package lk.grocery.pos.tm;

import java.io.Serializable;
import java.math.BigDecimal;

public class OrderItemDetailTM implements Serializable {
    private String itemCode;
    private String itemName;
    private double quantity;
    private BigDecimal unitPrice;
    private String unitType;
    private BigDecimal discount;
    private BigDecimal total;

    public OrderItemDetailTM() {
    }

    public OrderItemDetailTM(String itemCode, String itemName, double quantity, BigDecimal unitPrice, String unitType, BigDecimal discount, BigDecimal total) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.unitType = unitType;
        this.discount = discount;
        this.total = total;
    }
    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "OrderItemDetailTM{" +
                "itemCode='" + itemCode + '\'' +
                ", itemName='" + itemName + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", unitType='" + unitType + '\'' +
                ", discount=" + discount +
                ", total=" + total +
                '}';
    }
}
