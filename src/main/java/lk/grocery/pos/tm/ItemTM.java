package lk.grocery.pos.tm;

import java.io.FileInputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Blob;

public class ItemTM implements Serializable {
    private String code;
    private String description;
    private BigDecimal unit_price;
    private int qty_on_hand;
    private String unit_type;
    private String filePath;

    public ItemTM() {
    }

    public ItemTM(String code, String description, BigDecimal unit_price, int qty_on_hand, String unit_type, String filePath) {
        this.code = code;
        this.description = description;
        this.unit_price = unit_price;
        this.qty_on_hand = qty_on_hand;
        this.unit_type = unit_type;
        this.filePath = filePath;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(BigDecimal unit_price) {
        this.unit_price = unit_price;
    }

    public int getQty_on_hand() {
        return qty_on_hand;
    }

    public void setQty_on_hand(int qty_on_hand) {
        this.qty_on_hand = qty_on_hand;
    }

    public String getUnit_type() {
        return unit_type;
    }

    public void setUnit_type(String unit_type) {
        this.unit_type = unit_type;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "ItemTM{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", unit_price=" + unit_price +
                ", qty_on_hand=" + qty_on_hand +
                ", unit_type='" + unit_type + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}
