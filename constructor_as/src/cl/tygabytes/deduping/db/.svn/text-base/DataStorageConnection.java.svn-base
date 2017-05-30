/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.tygabytes.deduping.db;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author claudio
 */
public class DataStorageConnection {

    private static DataStorageConnection instancia;
    private static String[] parametros;
    public Connection conn = null;

    private DataStorageConnection(String[] params) {

        //Get connection
        try {
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setUser(params[0]);
            dataSource.setPassword(params[1]);
            dataSource.setDatabaseName(params[2]);
            dataSource.setServerName(params[3]);
            dataSource.setPort(Integer.parseInt(params[4]));
            conn = (Connection) dataSource.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(DataStorageConnection.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Fail: " + ex.getLocalizedMessage());
        }

    }

    //MÉTODO PARA OBTENER LA INSTANCIA  
    public static DataStorageConnection getInstance() {
        if (instancia == null) //SI ES NULL  
        {
            parametros = readConfigFile();
            instancia = new DataStorageConnection(parametros);   
        }
        return instancia;
    }

    /**
     * Lee archivos de configuracion para la conexion a la BD
     * @return parametros usados para la conexion
     */
    public static String[] readConfigFile() {
        String[] params = new String[5];

        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;

        try {
            archivo = new File("datastorage.config");
            fr = new FileReader(archivo);
            br = new BufferedReader(fr);

            // Lectura del fichero
            String linea;
            int cont = 0;
            while ((linea = br.readLine()) != null) {
                params[cont] = linea;
                cont++;
            }
        } catch (Exception ex) {
            System.err.println("Error al leer archivo datastorage.config"+ex.getLocalizedMessage());
        } finally {
            try {
                if (null != fr) {
                    fr.close();
                }
            } catch (Exception e2) {
                e2.getLocalizedMessage();
            }
            return params;
        }
    }
}
