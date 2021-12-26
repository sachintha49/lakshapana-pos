package lk.grocery.pos.util;

import java.io.Serializable;
import java.math.BigDecimal;

public class PrintBillDetails implements Serializable {
    private String productName;
    private String productCode;
    private String productDescription;
    private BigDecimal productTotal;

    public PrintBillDetails() {
    }

    public PrintBillDetails(String productName, String productCode, String productDescription, BigDecimal productTotal) {
        this.productName = productName;
        this.productCode = productCode;
        this.productDescription = productDescription;
        this.productTotal = productTotal;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public BigDecimal getProductTotal() {
        return productTotal;
    }

    public void setProductTotal(BigDecimal productTotal) {
        this.productTotal = productTotal;
    }

    @Override
    public String toString() {
        return "PrintBillDetails{" +
                "productName='" + productName + '\'' +
                ", productCode='" + productCode + '\'' +
                ", productDescription='" + productDescription + '\'' +
                ", productTotal=" + productTotal +
                '}';
    }
}
