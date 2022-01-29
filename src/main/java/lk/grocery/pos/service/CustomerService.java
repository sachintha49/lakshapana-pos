package lk.grocery.pos.service;

import lk.grocery.pos.db.DBConnection;
import lk.grocery.pos.dto.CustomerDTO;
import lk.grocery.pos.exception.DuplicateIdentifierException;
import lk.grocery.pos.exception.FailedOperationException;
import lk.grocery.pos.exception.NotFountException;


import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerService {


    private Connection connection;

    {
        try {
            connection = DBConnection.getInstance().getConnection();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveCustomer(CustomerDTO customerDTO) throws DuplicateIdentifierException, FailedOperationException {
        try {
            if (existCustomer(customerDTO.getId())) {
                throw new DuplicateIdentifierException(customerDTO.getId() + " already exists");
            }
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO customer (id,name,address,credit_limit) VALUES (?,?,?,?)");
            stmt.setString(1, customerDTO.getId());
            stmt.setString(2, customerDTO.getName());
            stmt.setString(3, customerDTO.getAddress());
            stmt.setBigDecimal(4, customerDTO.getCreditLimit());
            stmt.executeUpdate();
        } catch (SQLException e) {
            /* methanin yawanne SQL exception ekak nisa meka user karana kattiyata eka wedak nehe eka api therena vidiyen kiyala thiyenawa*/
            throw new FailedOperationException("Failed to save the customer",e);
        }
    }

    /* DB eke customer kenek already innawada balanna check karanawa */
    public boolean existCustomer(String id) throws SQLException {
        PreparedStatement pstm = connection.prepareStatement("SELECT id FROM customer WHERE id=?");
        pstm.setString(1, id);
        return pstm.executeQuery().next();
    }

    public ResultSet customerLimit(String id) throws SQLException {
        PreparedStatement pstm = connection.prepareStatement("SELECT credit_limit FROM customer WHERE id=?");
        pstm.setString(1, id);
        return pstm.executeQuery();
    }

    public void updateCustomer(CustomerDTO customerDTO) throws FailedOperationException, NotFountException {

        try {
            /* Methana ! operator eka danne update karanna issella customer kenek aniwa inna one nisa. cutomer kenek innawanam true return wena nisa beri welawakwath hitiye */
            if (!existCustomer(customerDTO.getId())){
                throw new NotFountException("There is no such customer associated with "+customerDTO.getId());
            }
            PreparedStatement pstm = connection.prepareStatement("UPDATE customer SET name=?, address=? ,credit_limit = ? WHERE id=?");
            pstm.setString(1,customerDTO.getName());
            pstm.setString(2,customerDTO.getAddress());
            pstm.setBigDecimal(3,customerDTO.getCreditLimit());
            pstm.setString(4,customerDTO.getId());
            pstm.executeUpdate();
        } catch (SQLException e) {
            throw new FailedOperationException("Failed to update the customer"+ customerDTO.getId(),e);
        }

    }

    public BigDecimal updateCustomerCreditLimit(String customerCode, BigDecimal creditLimit) throws FailedOperationException {
        try {
            if (!existCustomer(customerCode)) {
                throw new NotFountException("There is no such customer associated with " + customerCode);
            }
            ResultSet rs= customerLimit(customerCode);
            BigDecimal limit = null;

            while (rs.next()){
                 limit = rs.getBigDecimal("credit_limit");
            }

            BigDecimal calc;
            calc = limit.add(creditLimit);

            System.out.println(calc);
            PreparedStatement pstm = connection.prepareStatement("UPDATE customer SET credit_limit = ? WHERE id=?");
            pstm.setBigDecimal(1,calc);
            pstm.setString(2,customerCode);
            pstm.executeUpdate();
            return calc;
        }
        catch (SQLException | NotFountException e) {
            throw new FailedOperationException("Failed to update the customer"+ customerCode,e);
        }


    }

    public void deleteCustomer(String id) throws NotFountException, FailedOperationException {
        try {
            if (!existCustomer(id)){
                throw new NotFountException("There is no such customer associated with "+id);
            }
            PreparedStatement pstm = connection.prepareStatement("DELETE FROM customer WHERE id=?");
            pstm.setString(1,id);
            pstm.executeUpdate();
        } catch (SQLException e) {
            throw new FailedOperationException("Failed to delete the customer"+id);
        }
    }

    public CustomerDTO findCustomer(String id) throws NotFountException, FailedOperationException {
        try {
            if(!existCustomer(id)){
                throw new NotFountException("There is no such customer associated with "+id);
            }

            PreparedStatement pstm = connection.prepareStatement("SELECT * FROM customer WHERE id=?");
            pstm.setString(1,id);
            ResultSet rst = pstm.executeQuery();
            rst.next();
            return new CustomerDTO(id,rst.getString("name"),rst.getString("description"),rst.getBigDecimal("credit_limit"));
        } catch (SQLException e) {
            throw new FailedOperationException("Failed to find the customer "+id,e);
        }
    }

    public List<CustomerDTO> findAllCustomer() throws FailedOperationException {
        try {
            List<CustomerDTO> customerList = new ArrayList<>();

            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT *  FROM customer");

            while (rst.next()){
                customerList.add(new CustomerDTO(rst.getString("id"),rst.getString("name"),rst.getString("address"),rst.getBigDecimal("credit_limit")));
            }

            return customerList;
        }catch (SQLException e){
            throw new FailedOperationException("Failed to find the customer ",e);
        }
    }

    public String generateNewCustomerId() throws FailedOperationException {
        try {
            ResultSet rst = connection.createStatement().executeQuery("SELECT * FROM customer ORDER BY id DESC LIMIT 1;");
            if (rst.next()){
                String id = rst.getString("id");
                int newCustomerID = Integer.parseInt(id.replace("C", "")) + 1; // 003 removes the C letter
                return String.format("C%03d",newCustomerID);
            }else{
                return "C001";
            }
        } catch (SQLException e) {
            throw new FailedOperationException("Failed to generate a new ID",e);
        }
    }

}
