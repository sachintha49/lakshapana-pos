package lk.thilina.pos_stationary.service;

import lk.thilina.pos_stationary.db.DBConnection;
import lk.thilina.pos_stationary.dto.CustomerDTO;
import lk.thilina.pos_stationary.exception.DuplicateIdentifierException;
import lk.thilina.pos_stationary.exception.FailedOperationException;
import lk.thilina.pos_stationary.exception.NotFountException;


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
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO customer (id,name,address) VALUES (?,?,?)");
            stmt.setString(1, customerDTO.getId());
            stmt.setString(2, customerDTO.getName());
            stmt.setString(3, customerDTO.getAddress());
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

    public void updateCustomer(CustomerDTO customerDTO) throws FailedOperationException, NotFountException {

        try {
            /* Methana ! operator eka danne update karanna issella customer kenek aniwa inna one nisa. cutomer kenek innawanam true return wena nisa beri welawakwath hitiye */
            if (!existCustomer(customerDTO.getId())){
                throw new NotFountException("There is no such customer associated with "+customerDTO.getId());
            }
            PreparedStatement pstm = connection.prepareStatement("UPDATE customer SET name=?, address=? WHERE id=?");
            pstm.setString(1,customerDTO.getName());
            pstm.setString(2,customerDTO.getAddress());
            pstm.setString(3,customerDTO.getId());
            pstm.executeUpdate();
        } catch (SQLException e) {
            throw new FailedOperationException("Failed to update the customer"+ customerDTO.getId(),e);
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
            return new CustomerDTO(id,rst.getString("name"),rst.getString("description"));
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
                customerList.add(new CustomerDTO(rst.getString("id"),rst.getString("name"),rst.getString("address")));
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
