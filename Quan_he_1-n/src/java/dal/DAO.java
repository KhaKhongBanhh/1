/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Category;
import model.Product;

/**
 *
 * @author admin
 */
public class DAO extends DBContext{
    // Doc toan bo Category tu DB
    public List<Category> getAll(){
        List<Category> list = new ArrayList<>();
        String sql = "select * from Categories";
        try{
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                Category c = new Category(rs.getInt("id"),rs.getString("name"),rs.getString("describe"));
                list.add(c);
            }
        }catch(Exception e){
            System.out.println(e);
        }
        return list;  
    }
    
    // Doc tu bang products theo cid
    public List<Product> getProductsByCid(int cid){
        List<Product> list = new ArrayList<>();

        // Cách 1 : String sql = "select * from Products where cid = ?";
        String sql ="SELECT [id]\n" +
                    "      ,[name]\n" +
                    "      ,[price]\n" +
                    "      ,[quantity]\n" +
                    "      ,[image]\n" +
                    "      ,[cid]\n" +
                    "  FROM [dbo].[Products]\n" +
                    "  where cid = ?";
        try{
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, cid);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                Product p = new Product();
                
                p.setId(rs.getString("id"));
                p.setName(rs.getString("name"));
                p.setPrice(rs.getDouble("price"));
                p.setQuantity(rs.getInt("quantity"));
                p.setImage(rs.getString("image"));
                p.setReleaseDate(rs.getString("releaseDate"));
                p.setDescribe(rs.getString("describe"));
                
                Category c = getCategoryById(rs.getInt("cid"));
                p.setCategory(c);
                list.add(p);
            }
        }catch(Exception e){
            System.out.println(e);
        }
        return list;  
    }

    private Category getCategoryById(int aInt) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
