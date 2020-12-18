/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author atrin
 */
public class UserDatabase {
    private Connection conn;
    public final String user_table = "user_tb";
    
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private Statement statement;
    
    
    public Connection connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");     //Loading class `com.mysql.jdbc.Driver'. This is deprecated. The new driver class is `com.mysql.cj.jdbc.Driver'. The driver is automatically registered via the SPI and manual loading of the driver class is generally unnecessary.
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/chat_db", "root", "");
            System.out.println("Connect successfull");
        } catch (SQLException ex) {
            Logger.getLogger(UserDatabase.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error connection!");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(UserDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }
    
    public ResultSet getData() {
        try {
            statement = conn.createStatement(); //Create a Statement object to interact with the database
            resultSet = statement.executeQuery("SELECT * FROM " + user_table);
        } catch (SQLException ex) {
            Logger.getLogger(UserDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return resultSet;
    }
    
    private void showData() {
        resultSet = getData();
        try {
            while(resultSet.next()) {
                System.out.printf("%-15s %-4s", resultSet.getString(1), resultSet.getString(2));
                System.out.println("");
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(UserDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int insertUser(User user) {
        System.out.println("Before: name = " + user.name + " - pass = " + user.password);
        try {
            preparedStatement = conn.prepareCall("INSERT INTO " + user_table + " VALUES ('" + user.name + "', '" + user.password +"')");
            int kq = preparedStatement.executeUpdate();
            if(kq > 0) System.out.println("Insert successful!");
            System.out.println("After: name = " + user.name + " - pass = " + user.password);
            return kq;
        } catch (SQLException ex) {
            Logger.getLogger(UserDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }
    
    public int createUser(User user) {
        try {
            preparedStatement = conn.prepareStatement("INSERT INTO " + user_table + " VALUE(?,?);");
            preparedStatement.setString(1, user.name);
            preparedStatement.setString(2, user.password);
            return preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(UserDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }
    
    public int checkUser(String name, String password) {    //return 1 = account is correct
        try {
            preparedStatement = conn.prepareStatement("SELECT * FROM " + user_table + " WHERE name = '" + name + "' AND pass = '" + password +"'");
            resultSet = preparedStatement.executeQuery();
            
            if(resultSet.first()) {
                //user and pass is correct:
                return 1;
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDatabase.class.getName()).log(Level.SEVERE, null, ex);
            
        }
        return 0;
    }
    
    public void closeConnection() {
        try {
            if(resultSet!=null) resultSet.close();
            if(preparedStatement!=null) preparedStatement.close();
            if(statement!=null) statement.close();
            if(conn!=null) conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(UserDatabase.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("[UserDatabase.java] Error close connection");
        }
    }
    
    public static void main(String[] args) {
        UserDatabase ud = new UserDatabase();
        ud.connect();
        ud.showData();
        ud.closeConnection();
        
        System.out.println("============");
        ud.connect();
        ud.insertUser(new User("huy", "3"));
        ud.showData();
    }
}
