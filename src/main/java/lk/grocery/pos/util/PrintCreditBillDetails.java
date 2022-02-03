package lk.grocery.pos.util;

import java.io.Serializable;
import java.math.BigDecimal;

public class PrintCreditBillDetails implements Serializable {
    private String productName;
    private String productCode;
    private String productDescription;
    private BigDecimal productTotal;
    private BigDecimal creditLimit;
    private String customerName;
    private BigDecimal totalCreditLimit;

    public PrintCreditBillDetails() {
    }

    public PrintCreditBillDetails(String productName, String productCode, String productDescription, BigDecimal productTotal, BigDecimal creditLimit, String customerName, BigDecimal totalCreditLimit) {
        this.productName = productName;
        this.productCode = productCode;
        this.productDescription = productDescription;
        this.productTotal = productTotal;
        this.creditLimit = creditLimit;
        this.customerName = customerName;
        this.totalCreditLimit = totalCreditLimit;
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

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getTotalCreditLimit() {
        return totalCreditLimit;
    }

    public void setTotalCreditLimit(BigDecimal totalCreditLimit) {
        this.totalCreditLimit = totalCreditLimit;
    }

    @Override
    public String toString() {
        return "PrintCreditBillDetails{" +
                "productName='" + productName + '\'' +
                ", productCode='" + productCode + '\'' +
                ", productDescription='" + productDescription + '\'' +
                ", productTotal=" + productTotal +
                ", creditLimit=" + creditLimit +
                ", customerName='" + customerName + '\'' +
                ", totalCreditLimit=" + totalCreditLimit +
                '}';
    }
}
