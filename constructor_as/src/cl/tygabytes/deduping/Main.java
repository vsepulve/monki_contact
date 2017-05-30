/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.tygabytes.deduping;

import cl.tygabytes.deduping.controller.DedupingController;
import cl.tygabytes.deduping.db.ActiveStorageConnection;
import cl.tygabytes.deduping.db.DataStorageConnection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Claudio
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        if(args.length>1){
            System.out.println("Modo de uso java -jar constructorAs");
            return;
        }
        try {
            DedupingController dpc = null;
            // DEDUPLICAR TODOS LOS USUARIOS
            // SE HACE EL SWITCH DE BD (LOCAL y REMOTA)
            dpc = new DedupingController(true);
            if (DataStorageConnection.getInstance().conn != null && ActiveStorageConnection.getInstance().conn != null) {
                if(args.length==1){
                    dpc.updateSocialNetworksStatus(new Integer(args[0]), DedupingController.DEDUBING);
                    dpc.dedupingByUserFromAsGroups(new Integer(args[0]));
                    dpc.updateSocialNetworksStatus(new Integer(args[0]), DedupingController.DEDUBED);

                }
                else{
                    dpc.dedupingAllUsers();
                }
            } else {
                System.out.println("Connection failed");
            }
            DataStorageConnection.getInstance().conn.close();
            ActiveStorageConnection.getInstance(true).conn.close();
            System.out.println("Deduping finished");
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Deduping failed: "+ex.getLocalizedMessage());
        }
    }
}
