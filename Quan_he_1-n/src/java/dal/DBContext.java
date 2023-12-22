
package dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBContext {
    protected Connection connection;
    public DBContext()
    {
        try {
            // Edit URL , username, password to authenticate with your MS SQL Server
            String username = "sa";
            String password = "traungo456"; 
            String url = "jdbc:sqlserver://localhost:1433;databaseName= Trading2022;user=sa;password=traungo456;integratedSecurity=false;trustServerCertificate=true;";
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(url);
        }
        catch (ClassNotFoundException | SQLException ex)
        {
            System.out.println(ex);
        }
    }
}
