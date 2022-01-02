package lk.grocery.pos.service;

import lk.grocery.pos.db.DBConnection;
import lk.grocery.pos.dto.ItemDTO;
import lk.grocery.pos.exception.DuplicateIdentifierException;
import lk.grocery.pos.exception.NotFountException;
import lk.grocery.pos.exception.FailedOperationException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemService {
    private Connection connection;

    {
        try {
            connection = DBConnection.getInstance().getConnection();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveItem(ItemDTO itemDTO) throws DuplicateIdentifierException, FailedOperationException {
        try {
            if (existItem(itemDTO.getCode())) {
                throw new DuplicateIdentifierException(itemDTO.getCode() + " already exists");
            }
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO item (code,description,unit_price,qty_on_hand,unit_type) VALUES (?,?,?,?,?)");
            stmt.setString(1, itemDTO.getCode());
            stmt.setString(2, itemDTO.getDescription());
            stmt.setBigDecimal(3, itemDTO.getUnitPrice());
            stmt.setInt(4, itemDTO.getQtyOnHand());
            stmt.setString(5,itemDTO.getUnitType());
            stmt.executeUpdate();
        } catch (SQLException e) {
            /* methanin yawanne SQL exception ekak nisa meka user karana kattiyata eka wedak nehe eka api therena vidiyen kiyala thiyenawa*/
            throw new FailedOperationException("Failed to save the item ",e);
        }
    }

    /* DB eke customer kenek already innawada balanna check karanawa */
    public boolean existItem(String itemCode) throws SQLException {
        PreparedStatement pstm = connection.prepareStatement("SELECT code FROM item WHERE code=?");
        pstm.setString(1, itemCode);
        return pstm.executeQuery().next();
    }

    public void updateItem(ItemDTO itemDTO) throws FailedOperationException, NotFountException {

        try {
            /* Methana ! operator eka danne update karanna issella customer kenek aniwa inna one nisa. cutomer kenek innawanam true return wena nisa beri welawakwath hitiye */
            if (!existItem(itemDTO.getCode())){
                throw new NotFountException("There is no such item associated with "+itemDTO.getCode());
            }
            PreparedStatement pstm = connection.prepareStatement("UPDATE item SET description=?, unit_price=?, qty_on_hand=?, unit_type=? WHERE code=?");
            pstm.setString(1,itemDTO.getDescription());
            pstm.setBigDecimal(2,itemDTO.getUnitPrice());
            pstm.setInt(3,itemDTO.getQtyOnHand());
            pstm.setString(4,itemDTO.getUnitType());
            pstm.setString(5,itemDTO.getCode());
            pstm.executeUpdate();
        } catch (SQLException e) {
            throw new FailedOperationException("Failed to update the item"+ itemDTO.getCode(),e);
        }

    }

    public void deleteItem(String code) throws NotFountException, FailedOperationException {
        try {
            if (!existItem(code)){
                throw new NotFountException("There is no such item associated with "+code);
            }
            System.out.println("methanata enawa");
            PreparedStatement pstm = connection.prepareStatement("DELETE FROM item WHERE code=?");
            pstm.setString(1,code);
            pstm.executeUpdate();
        } catch (SQLException e) {
            throw new FailedOperationException("Failed to delete the item "+code);
        }
    }

    public ItemDTO findItem(String code) throws NotFountException, FailedOperationException {
        try {
            if(!existItem(code)){
                throw new NotFountException("There is no such item associated with "+code);
            }

            PreparedStatement pstm = connection.prepareStatement("SELECT * FROM item WHERE code=?");
            pstm.setString(1,code);
            ResultSet rst = pstm.executeQuery();
            rst.next();
            return new ItemDTO(code,rst.getString("description"),rst.getBigDecimal("unit_price"),rst.getInt("qty_on_hand"),rst.getString("unit_type"));
        } catch (SQLException e) {
            throw new FailedOperationException("Failed to find the item "+code,e);
        }
    }

    public List<ItemDTO> findAllItems() throws FailedOperationException {
        try {
            List<ItemDTO> itemList = new ArrayList<>();

            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT *  FROM item");

            while (rst.next()){
                itemList.add(new ItemDTO(rst.getString("code"),rst.getString("description"),rst.getBigDecimal("unit_price"),rst.getInt("qty_on_hand"),rst.getString("unit_type")));
            }

            return itemList;
        }catch (SQLException e){
            throw new FailedOperationException("Failed to find the item ",e);
        }
    }

    public String generateNewItemId() throws FailedOperationException {
        try {
            ResultSet rst = connection.createStatement().executeQuery("SELECT * FROM item ORDER BY code DESC LIMIT 1;");
            if (rst.next()){
                String code = rst.getString("code");
                int newItemID = Integer.parseInt(code.replace("I", "")) + 1; // 003 removes the C letter
                return String.format("I%03d",newItemID);
            }else{
                return "I001";
            }
        } catch (SQLException e) {
            throw new FailedOperationException("Failed to generate a new ID",e);
        }
    }
}
