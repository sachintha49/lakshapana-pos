package lk.grocery.pos.tm;

import java.io.Serializable;
import java.math.BigDecimal;

public class CustomerTM implements Serializable {
    String id;
    String name;
    String address;
    private BigDecimal credit_limit;


    public CustomerTM(String id, String name, String address,BigDecimal credit_limit) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.credit_limit = credit_limit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getCredit_limit() {
        return credit_limit;
    }

    public void setCredit_limit(BigDecimal credit_limit) {
        this.credit_limit = credit_limit;
    }

    @Override
    public String toString() {
        return "CustomerTM{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", credit_limit=" + credit_limit +
                '}';
    }
}
