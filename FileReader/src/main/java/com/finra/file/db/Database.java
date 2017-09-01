package com.finra.file.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.finra.file.vo.Employee;

// H2 In-Memory Database Example shows about storing the database contents into memory. 

public class Database {

    private static final String DB_DRIVER = "org.h2.Driver";
    private static final String DB_CONNECTION = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";

   

    public static void insertEmployee(Employee emp) throws SQLException {
        Connection connection = getDBConnection();
     
        PreparedStatement insertStatement = null;
       

      
        String InsertQuery = "INSERT INTO EMPLOYEE" + "(id, name, title) values" + "(?,?,?)";
       

        try {
            
            insertStatement = connection.prepareStatement(InsertQuery);
            insertStatement.setInt(1,emp.getId() );
            insertStatement.setString(2, emp.getName());
            insertStatement.setString(3, emp.getTitle());
            insertStatement.executeUpdate();
            insertStatement.close();

           

            connection.commit();
        } catch (SQLException e) {
            System.out.println("Exception Message " + e.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }

    public static void selectEmployees() throws SQLException {
        Connection connection = getDBConnection();
        Statement stmt = null;
        PreparedStatement selectQuery = null;
        String SelectQuery = "select * from EMPLOYEE";
        try {
        	selectQuery = connection.prepareStatement(SelectQuery);
             ResultSet rs = selectQuery.executeQuery();
             
             while (rs.next()) {
                 System.out.println("Id " + rs.getInt("id") + " Name " + rs.getString("name")+ " Title " + rs.getString("title"));
             }
             selectQuery.close();
        } catch (SQLException e) {
            System.out.println("Exception Message " + e.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }
    
    
    public static void createTable() throws SQLException {
        Connection connection = getDBConnection();
        
        PreparedStatement ps = null;
        String CreateQuery = "CREATE TABLE EMPLOYEE(id int primary key, name varchar(100), title varchar(25))";
        try {
        	connection.setAutoCommit(false);

            ps = connection.prepareStatement(CreateQuery);
            ps.executeUpdate();
            ps.close();
            connection.commit();
        } catch (SQLException e) {
            System.out.println("Exception Message " + e.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }

    private static Connection getDBConnection() {
        Connection dbConnection = null;
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
            return dbConnection;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return dbConnection;
    }
}
