/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Drivers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import utilities.DBConnectionUtil.ConnectionUtil;
import api.adapters.DatabaseAdapter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import utilities.DBConnectionUtil.PreparedStatementUtil;

/**
 *
 * @author darod
 */
public class DBDriver {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        DatabaseAdapter adapter = new DatabaseAdapter();
        Connection con = ConnectionUtil.conDB();
        //PreparedStatement preparedStatement = con.prepareStatement("CREATE TABLE users (uuid VARCHAR(36), firstName VARCHAR(50), lastName VARCHAR(50), street VARCHAR(20), city VARCHAR(20), state VARCHAR(20), zipcode VARCHAR(5), email VARCHAR(50), password VARCHAR(50))");
        //PreparedStatement preparedStatement2 = con.prepareStatement("select * from users");
        //UUID uuid = UUID.randomUUID();
        String uuid = "5ac1d0ac-7999-4951-aee0-3d69d1b10308";
        Map<String, String> userInformation = new HashMap<>();
        
        //System.out.println(adapter.queryForAttribute(, "zipcode"));
        //System.out.println(adapter.updateInformation(uuid, "city", "Charlotte"));
        //System.out.println(adapter.queryForAttribute(uuid, "city"));


        //PreparedStatement.executeUpdate();
//        ResultSet resultSet = preparedStatement2.executeQuery();
//
//        if (!resultSet.next()) {
//            System.out.println("No results found");
//        } else {
//     
//                System.out.println(resultSet.getString(1) + " " + resultSet.getString(2));
            
        }
    }

