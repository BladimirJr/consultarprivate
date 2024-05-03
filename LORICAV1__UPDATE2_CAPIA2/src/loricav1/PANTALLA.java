/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loricav1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import ConexionBD.MySql;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author auxsistemas3
 */
public class PANTALLA extends javax.swing.JFrame {

    
    DefaultTableModel model;
    private String epsu1;
    private String regimen;
    
    /**
     * Creates new form PANTALLA
     */
    public PANTALLA() {
    initComponents();
    setTitle("CONSULTA MEDICAMENTOS POR PACIENTE");
    setSize(800, 900);
    EPSCUADRO.setEnabled(false);
    NOMCUADRO.setEnabled(false);
    SEXOCUADRO.setEnabled(false);
    TICUADRO.setEnabled(false);
    REGIMEN.setEnabled(false);
    FECHACUADRO.setEnabled(false);
    TABLA.setEnabled(false);
    
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Centrar el frame en la pantalla
    setLocationRelativeTo(null);

    BUSCARCUADRO.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                buscarUsuario(BUSCARCUADRO.getText());
            }
        }
    });

    BUSCARMED.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent f) {
            if (f.getKeyCode() == KeyEvent.VK_ENTER) {
                buscarMedicamento(BUSCARMED.getText());
            }
        }
    });
    buscarMedicamento("");
    
    
    
}

    
    public void cargar(String epsUsuario) {
  
    String[] titulos = {"Descripcion"};
    DefaultTableModel model = new DefaultTableModel(null, titulos);
    
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.1.35:3306/mydb", "root", "syd123")) {
      
        String sql = "SELECT idMed, Descripcion FROM medicamentos WHERE EPS = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, epsUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // String idMed = rs.getString("idMed");
                    String Descripcion = rs.getString("Descripcion");
                    model.addRow(new Object[]{ Descripcion});
                }
                TABLA.setModel(model);
                
            }
        } finally {
        conn.close();
        // System.out.println("Desconecto 2");
        }
        
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "Error al cargar datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    private void limpiarCampos(){
        
        TICUADRO.setText("");
        SEXOCUADRO.setText("");
        FECHACUADRO.setText("");
        NOMCUADRO.setText("");
        EPSCUADRO.setText("");
        REGIMEN.setText("");
        BUSCARMED.setText("");
        LISTA.setModel(new DefaultListModel<>());
        TABLA.setModel(new DefaultTableModel());
    }
    private void limpiarJList() {
    LISTA.setModel(new DefaultListModel<>());
}

    private void buscarUsuario(String documento) {
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.1.35:3306/mydb", "root", "syd123")) {
        String sql = "SELECT regimen, EPS, tipodoc, id, apellido1, apellido2, nombre1, nombre2, sexo, fechanaci " +
                     "FROM usuarios " +
                     "WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, documento);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Concatenar nombres y apellidos
                    String nombres = rs.getString("nombre1") + " " + rs.getString("nombre2");
                    String apellidos = rs.getString("apellido1") + " " + rs.getString("apellido2");

                    // Asignar valores a los campos de texto
                    TICUADRO.setText(rs.getString("tipodoc"));
                    SEXOCUADRO.setText(rs.getString("sexo"));
                    FECHACUADRO.setText(rs.getString("fechanaci"));
                    NOMCUADRO.setText(apellidos + "  " + nombres);
                    String eps = rs.getString("EPS");
                    String reg = rs.getString("regimen");
                    cargar(eps);
                    switch (eps) {
                        case "MS":
                            EPSCUADRO.setText("MUTUAL SER");
                            break;
                        case "CC":
                            EPSCUADRO.setText("CAJA COPI");
                            break;
                        case "NE":
                            EPSCUADRO.setText("NUEVA EPS");
                            break;
                        case "CS":
                            EPSCUADRO.setText("COOSALUD");
                            break;
                        default:
                            EPSCUADRO.setText("Valor desconocido");
                            break;
                    }
                    epsu1 = eps;
                    switch (reg) {
                        case "SUB":
                            REGIMEN.setText("SUBSIDIADO");
                            break;
                        case "CONT":
                            REGIMEN.setText("CONTRIBUTIVO");
                            break;
                        default:
                            REGIMEN.setText("Valor desconocido");
                            break;
                    }
                    // Limpiar el JList antes de agregar nuevos elementos
                    limpiarJList();
                    // Aquí agregamos la búsqueda de las columnas REF
                    String refSql = "SELECT REF FROM usuarios WHERE id = ?";
                    try (PreparedStatement refStmt = conn.prepareStatement(refSql)) {
                        refStmt.setString(1, documento);
                        try (ResultSet refRs = refStmt.executeQuery()) {
                            // Creamos un modelo para el JList
                            DefaultListModel<String> listModel = new DefaultListModel<>();
                            while (refRs.next()) {
                                String ref = refRs.getString("REF");
                                // Aplicamos las transformaciones necesarias con un if-else
                                if (ref.equals("CONPM")) {
                                    ref = "CONT REC Y PM";
                                } else if (ref.equals("SUBPM")) {
                                    ref = "SUB REC Y PM";
                                } else if (ref.equals("MEDSUB")) {
                                    ref = "MED-SUB";
                                } else if (ref.equals("MEDCON")) {
                                    ref = "MED CONT";
                                } else if (ref.equals("STMED")) {
                                    ref = "MEDI-SUB";
                                } else if (ref.equals("NEMED")) {
                                    ref = "MEDI-SUB";
                                } else if (ref.equals("CCMED")) {
                                    ref = "MEDI-SUB";
                                } else if (ref.equals("CSMED")) {
                                    ref = "MEDI-SUB";
                                } else if (ref.equals("CSCONT")) {
                                    ref = "CONT";
                                } else if (ref.equals("CSCONTP")) {
                                    ref = "CONT-PORT";
                                }
                                
                                listModel.addElement(ref);
                            }
                            // Asignamos el modelo al JList
                            LISTA.setModel(listModel);
                        }
                    }
                } else {
                    // Limpiar los campos si no se encuentra el usuario
                    limpiarCampos();
                    JOptionPane.showMessageDialog(this, "No se encontraron resultados para el número de documento ingresado.", "Mensaje", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error al buscar usuario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}



    
        

    private void buscarMedicamento(String nombreMed) {
    // Realizar la conexión a la base de datos
        
        
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.1.35:3306/mydb", "root", "syd123")) {
        String sql = "SELECT idMed, Descripcion FROM medicamentos WHERE Descripcion LIKE ? AND EPS = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Establecer los parámetros con el nombre del medicamento y la EPS
            stmt.setString(1, "%" + nombreMed + "%");
            stmt.setString(2, epsu1); // Utilizar el valor de eps1 como parámetro para comparar con la EPS de la tabla
            try (ResultSet rs = stmt.executeQuery()) {
                // Limpiar el modelo de la tabla antes de agregar los nuevos resultados
                DefaultTableModel model = (DefaultTableModel) TABLA.getModel();
                model.setRowCount(0);
                while (rs.next()) {
                    //String idMed = rs.getString("idMed");
                    String Descripcion = rs.getString("Descripcion");
                    model.addRow(new Object[]{ Descripcion});
                }
                TABLA.setModel(model); // Establecer el modelo actualizado en la tabla

            }
        } finally {
        conn.close();
        // System.out.println("Desconecto 2");
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error al buscar medicamento: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}




    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        NOMCUADRO = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        TICUADRO = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        FECHACUADRO = new javax.swing.JTextField();
        SEXOCUADRO = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        BUSCARCUADRO = new javax.swing.JTextField();
        EPSCUADRO = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        BUSCARMED = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        TABLA = new javax.swing.JTable();
        REGIMEN = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jScrollPane2 = new javax.swing.JScrollPane();
        LISTA = new javax.swing.JList();
        jLabel9 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        NOMCUADRO.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        NOMCUADRO.setDisabledTextColor(new java.awt.Color(0, 0, 0));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("BUSCAR ID :");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Nombre completo:");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("T. Doc:");

        TICUADRO.setDisabledTextColor(new java.awt.Color(0, 0, 0));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setText("Fecha Nacimiento:");

        FECHACUADRO.setDisabledTextColor(new java.awt.Color(0, 0, 0));

        SEXOCUADRO.setDisabledTextColor(new java.awt.Color(0, 0, 0));

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setText("Sexo:");

        BUSCARCUADRO.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        BUSCARCUADRO.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BUSCARCUADROKeyPressed(evt);
            }
        });

        EPSCUADRO.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        EPSCUADRO.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        EPSCUADRO.setBorder(null);
        EPSCUADRO.setDisabledTextColor(new java.awt.Color(0, 0, 0));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel6.setText("Buscar Producto:");

        BUSCARMED.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BUSCARMEDActionPerformed(evt);
            }
        });

        TABLA.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Descripcion"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(TABLA);
        if (TABLA.getColumnModel().getColumnCount() > 0) {
            TABLA.getColumnModel().getColumn(0).setResizable(false);
            TABLA.getColumnModel().getColumn(0).setPreferredWidth(200);
        }

        REGIMEN.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        REGIMEN.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        REGIMEN.setText(" ");
        REGIMEN.setToolTipText("");
        REGIMEN.setBorder(null);
        REGIMEN.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        REGIMEN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                REGIMENActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel7.setText("Eps:");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel8.setText("Regimen:");

        LISTA.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jScrollPane2.setViewportView(LISTA);

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel9.setText("PM & MED:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(BUSCARMED, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(EPSCUADRO, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7)
                                    .addComponent(NOMCUADRO, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(BUSCARCUADRO, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel2))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(22, 22, 22)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel8)
                                            .addComponent(REGIMEN, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel3)
                                            .addComponent(TICUADRO, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(27, 27, 27)
                                                .addComponent(SEXOCUADRO, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(FECHACUADRO, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(31, 31, 31)
                                                .addComponent(jLabel5)
                                                .addGap(18, 18, 18)
                                                .addComponent(jLabel4)))))
                                .addGap(39, 39, 39)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 235, Short.MAX_VALUE)))
                        .addContainerGap())))
            .addComponent(jSeparator2)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BUSCARCUADRO, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(TICUADRO, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(NOMCUADRO, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(FECHACUADRO, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(SEXOCUADRO, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(14, 14, 14)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8))
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(REGIMEN, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                            .addComponent(EPSCUADRO))
                        .addGap(18, 18, 18)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BUSCARMED, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 507, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BUSCARCUADROKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BUSCARCUADROKeyPressed
        // TODO add your handling code here:
           
    }//GEN-LAST:event_BUSCARCUADROKeyPressed

    private void BUSCARMEDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BUSCARMEDActionPerformed
BUSCARMED.getDocument().addDocumentListener(new DocumentListener() {
    @Override
    public void insertUpdate(DocumentEvent e) {
        actualizarBusqueda();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        actualizarBusqueda();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        // No es necesario hacer nada en este caso
    }

    // Método para realizar la búsqueda y actualizar la tabla
    private void actualizarBusqueda() {
        String textoBusqueda = BUSCARMED.getText(); // Obtener el texto actual del campo de texto
        buscarMedicamento(textoBusqueda); // Realizar la búsqueda de medicamentos con el texto actual
    }
});

        // TODO add your handling code here:
    }//GEN-LAST:event_BUSCARMEDActionPerformed

    private void REGIMENActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_REGIMENActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_REGIMENActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PANTALLA.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PANTALLA.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PANTALLA.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PANTALLA.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PANTALLA().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField BUSCARCUADRO;
    private javax.swing.JTextField BUSCARMED;
    private javax.swing.JTextField EPSCUADRO;
    private javax.swing.JTextField FECHACUADRO;
    private javax.swing.JList LISTA;
    private javax.swing.JTextField NOMCUADRO;
    private javax.swing.JTextField REGIMEN;
    private javax.swing.JTextField SEXOCUADRO;
    javax.swing.JTable TABLA;
    private javax.swing.JTextField TICUADRO;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    // End of variables declaration//GEN-END:variables
}
