package util;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
	private static final String fileName = "db.properties";
	
    public static Connection getConnection() {
    	Connection con = null;
        String connStr = null;
        try {
        	connStr = PropertyUtil.getPropertyString(fileName);
        } catch (IOException e) {
            System.out.println("Error reading db.properties.");
            e.printStackTrace();
        }
        if (connStr != null) {
            try {
            	con = DriverManager.getConnection(connStr);
            } catch (SQLException e) {
                System.out.println("Error connecting to DB.");
                e.printStackTrace();
            }
        }
        return con;
    }
}

            	


        	
        
          
              
