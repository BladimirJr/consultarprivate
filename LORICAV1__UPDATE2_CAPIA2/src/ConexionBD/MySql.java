/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ConexionBD;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.FileInputStream;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author auxsistemas3
 */
public class MySql {
    
        Connection conexion;
        String url="jdbc:mysql://localhost:3306/";
        String Usuario="root";
        String Contrasena="syd123";
        String Bd="mydb";
        String driver ="com.mysql.jdbc.Driver"; 
        private Statement sentencias;
        
        public MySql(){
        
        }
        
        public Connection conectar()
        {
            try {
                Class.forName(driver);
                conexion=DriverManager.getConnection(url+Bd,Usuario,Contrasena);
                sentencias = conexion.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                System.out.println("Se conecto");
            } catch (ClassNotFoundException | SQLException ex) {
                System.out.println("No conecto");
                Logger.getLogger(MySql.class.getName()).log(Level.SEVERE, null, ex);
               
            }
            
            return conexion;
        }
        
        public void desconectar()
        {
            try {
                conexion.close();
                System.out.println("Se desconecto");
            } catch (SQLException ex) {
                Logger.getLogger(MySql.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public ResultSet consulta(String consulta) {
        try {
            java.sql.ResultSet resultado = sentencias.executeQuery(consulta);
            return resultado;
        } catch (java.sql.SQLException ex) {
            //Log.escribirLog(ex);
           // Log.escribirLog(ex.getMessage());
            if (ex.getErrorCode() == 1064) {
                //JOptionPane.showMessageDialog(null, "Error de sintaxis\n" + ex.getMessage(), "Error", 0);
            } else {
                //JOptionPane.showMessageDialog(null, "Error:\n" + ex.getMessage(), "Error", 0);
            }
            return null;
        }
    }
}
