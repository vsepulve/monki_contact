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
public class ActiveStorageConnection {

    private static ActiveStorageConnection instancia;
    private static String[] parametros;
    public Connection conn = null;

    private ActiveStorageConnection(boolean local, String[] params) {

        //Get connection
        try {
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setUser(params[0]);
            dataSource.setPassword(params[1]);
            dataSource.setDatabaseName(params[2]);
            
            //INDICA HOST DE LA CONEXION
            if (local) {
                dataSource.setServerName("127.0.0.1");
            } else {
                dataSource.setServerName(params[3]);
            }

            dataSource.setPort(Integer.parseInt(params[4]));


            conn = (Connection) dataSource.getConnection();


        } catch (SQLException ex) {
            Logger.getLogger(DataStorageConnection.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Fail: " + ex.getLocalizedMessage());
        }

    }

    /**
     * Método para la obtencion de la instacia singleton
     * @param local true: usar conexion local, false: usar conexion remota
     * @return retorna la instancia de la conexion
     */
    public static ActiveStorageConnection getInstance(boolean local) {
        if (instancia == null) //SI ES NULL  
        {
            parametros = readConfigFile();
            instancia = new ActiveStorageConnection(local, parametros); //LO INSTANCIAMOS  
        }
        return instancia;
    }

    /**
     * Metodo que retorna la instancia de conexion
     * @return instancia de conexion
     */
    public static ActiveStorageConnection getInstance() {
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
            archivo = new File("activestorage.config");
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
            System.err.println("Error al leer archivo activestorage.config"+ex.getLocalizedMessage());
        } finally {
            try {
                if (null != fr) {
                    fr.close();
                }
            } catch (Exception e2) {
                System.err.println("Imposible cerrar archivo: "+e2.getLocalizedMessage());
            }
            return params;
        }
    }
}
