/**
 * 
 */
package com.sap.it.sr.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author I063098
 *
 */
public class DbAction {
    public static void main(String a[]){
        String driverClassName = "org.mariadb.jdbc.Driver";
        String url = "jdbc:mysql://pekitwin2008vm.pek.sap.corp:3306/srmreceive";

        Connection con = null;
        try {
            Class.forName(driverClassName);
            con = DriverManager.getConnection(url, "root", "Sybase123");
            // insert employee data from temporary table
            insertEmp(con);
            // update employee where employee full name is null
//            updateEmpName(con);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try{
                if(con != null) con.close();
            } catch (Exception ex){}
        }
    }
    
    public static void insertEmp(Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        PreparedStatement prSt = null;
        //The query can be update query or can be select query
        String query = "select * from tempempdata";
        boolean status = stmt.execute(query);
        if(status){
            //query is a select query.
            ResultSet rs = stmt.getResultSet();
            String sql = "INSERT INTO EMPLOYEE (BADGEID,EMPID,EMPNAME,COSTCENTER) VALUES (?,?,?,?)";
            while(rs.next()){
            	EmpInfo ei = LdapHelper.getEmployee(rs.getString(1), "I063098", "asdfJkl1");
                if (ei != null) {
                    prSt = con.prepareStatement(sql);
                    prSt.setString(1, rs.getString(2));
                    prSt.setString(2, rs.getString(1));
                    prSt.setString(3, ei.getName());
                    prSt.setString(4, ei.getCostCenter());
                    
                    prSt.executeUpdate();
                    
                    System.out.println("insert emp: " + rs.getString(1) + "_" + ei.getName());
                }
            }
            rs.close();
        }
    }
    
    public static void updateEmpName(Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        PreparedStatement prSt = null;
        //The query can be update query or can be select query
        String query = "select EMPID from employee where empname is null";
        boolean status = stmt.execute(query);
        if(status){
            //query is a select query.
            ResultSet rs = stmt.getResultSet();
            String sql = "UPDATE EMPLOYEE SET EMPNAME=? WHERE EMPID=?";
            while(rs.next()){
            	EmpInfo ei = LdapHelper.getEmployee(rs.getString(1), "I063098", "asdfJkl1");
                if (ei != null) {
                    prSt = con.prepareStatement(sql);
                    prSt.setString(2, rs.getString(1));
                    prSt.setString(1, ei.getName());
                    
                    prSt.executeUpdate();
                    
                    System.out.println("insert emp: " + rs.getString(1) + "_" + ei.getName());
                }
            }
            rs.close();
        }
    }
    
}
