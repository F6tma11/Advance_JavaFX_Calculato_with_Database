/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fatma.calculator.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author elfrd
 */
public class DBConnection {
    
    private final String URL="jdbc:mysql://localhost:3306/calcolator";
    private final String username="root";
    private final String password="123456";
     public Connection connect() throws SQLException ,ClassNotFoundException{
         
         return DriverManager.getConnection(URL, username, password);
     }
    
}
