package lk.grocery.pos.dto;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Blob;

public class ItemDTO implements Serializable {
    private String code;
    private String description;
    private BigDecimal unitPrice;
    private int qtyOnHand;
    private String unitType;

    public ItemDTO() {
    }

    public ItemDTO(String code, String description, BigDecimal unitPrice, int qtyOnHand, String unitType) {
        this.code = code;
        this.description = description;
        this.unitPrice = unitPrice;
        this.qtyOnHand = qtyOnHand;
        this.unitType = unitType;
    }

    @Override
    public String toString() {
        return "ItemDTO{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", unitPrice=" + unitPrice +
                ", qtyOnHand=" + qtyOnHand +
                ", unitType='" + unitType + '\'' +
                '}';
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQtyOnHand() {
        return qtyOnHand;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setQtyOnHand(int qtyOnHand) {
        this.qtyOnHand = qtyOnHand;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }
}
