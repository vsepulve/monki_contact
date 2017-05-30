/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.tygabytes.deduping.controller;

import cl.tygabytes.deduping.db.ActiveStorageConnection;
import cl.tygabytes.deduping.db.DataStorageConnection;
import cl.tygabytes.deduping.model.Im;
import cl.tygabytes.deduping.model.Contact;
import cl.tygabytes.deduping.model.Demographics;
import cl.tygabytes.deduping.model.Education;
import cl.tygabytes.deduping.model.Email;
import cl.tygabytes.deduping.model.ExtendedProperty;
import cl.tygabytes.deduping.model.Names;
import cl.tygabytes.deduping.model.Organization;
import cl.tygabytes.deduping.model.PhoneNumber;
import cl.tygabytes.deduping.model.PostalAddress;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Claudio
 */
public class DedupingController {

    private static Connection DS_CONN;
    private static Connection AS_CONN;
    private static String STORED = "stored";
    public static String DEDUBING = "dedubing";
    public static String DEDUBED = "dedubed";
    public static String GROUPED = "grouped";
    public static String NEW = "new";
    public static String UPDATED = "updated";
    public static String LOCAL_DB = "LOCAL";
    public static String REMOTE_DB = "REMOTE";
    //TRUNCATE ACTIVE STORAGE
    public static String TRUNCATE_AS_CONTACTS = "TRUNCATE TABLE as_contacts";
    public static String TRUNCATE_AS_EMAIL = "TRUNCATE TABLE as_email";
    public static String TRUNCATE_AS_EXTENDED_PROPERTY = "TRUNCATE TABLE as_extendedProperty";
    public static String TRUNCATE_AS_IM = "TRUNCATE TABLE as_im";
    public static String TRUNCATE_AS_NAMES = "TRUNCATE TABLE as_names";
    public static String TRUNCATE_AS_ORGANIZATION = "TRUNCATE TABLE as_organization";
    public static String TRUNCATE_AS_PHONENUMBER = "TRUNCATE TABLE as_phonenumber";
    public static String TRUNCATE_AS_POSTALADDRESS = "TRUNCATE TABLE as_postalAddress";
    public static String TRUNCATE_AS_EDUCATION = "TRUNCATE TABLE as_education";
    public static String TRUNCATE_AS_DEMOGRAPHICS = "TRUNCATE TABLE as_demographics";
    //DELETE BY USER ID
    public static String DELETE_AS_CONTACTS_BY_USER = "DELETE FROM as_contacts WHERE user_Id=";
    public static String DELETE_AS_EMAIL_BY_USER = "DELETE FROM as_email WHERE user_Id=";
    public static String DELETE_AS_EXTENDED_PROPERTY_BY_USER = "DELETE FROM as_extendedProperty WHERE user_Id=";
    public static String DELETE_AS_IM_BY_USER = "DELETE FROM as_im WHERE user_Id=";
    public static String DELETE_AS_NAMES_BY_USER = "DELETE FROM as_names WHERE user_Id=";
    public static String DELETE_AS_ORGANIZATION_BY_USER = "DELETE FROM as_organization WHERE user_Id=";
    public static String DELETE_AS_PHONENUMBER_BY_USER = "DELETE FROM as_phonenumber WHERE user_Id=";
    public static String DELETE_AS_POSTALADDRESS_BY_USER = "DELETE FROM as_postalAddress WHERE user_Id=";
    public static String DELETE_AS_EDUCATION_BY_USER = "DELETE FROM as_education WHERE user_Id=";
    public static String DELETE_AS_DEMOGRAPHICS_BY_USER = "DELETE FROM as_demographics WHERE user_Id=";
    //DELETE BY GROUPID
    public static String DELETE_AS_CONTACTS_BY_GROUP = "DELETE FROM as_contacts WHERE groupId=";
    public static String DELETE_AS_EMAIL_BY_GROUP = "DELETE FROM as_email WHERE groupId=";
    public static String DELETE_AS_EXTENDED_PROPERTY_BY_GROUP = "DELETE FROM as_extendedProperty WHERE groupId=";
    public static String DELETE_AS_IM_BY_GROUP = "DELETE FROM as_im WHERE groupId=";
    public static String DELETE_AS_NAMES_BY_GROUP = "DELETE FROM as_names WHERE groupId=";
    public static String DELETE_AS_ORGANIZATION_BY_GROUP = "DELETE FROM as_organization WHERE groupId=";
    public static String DELETE_AS_PHONENUMBER_BY_GROUP = "DELETE FROM as_phonenumber WHERE groupId=";
    public static String DELETE_AS_POSTALADDRESS_BY_GROUP = "DELETE FROM as_postalAddress WHERE groupId=";
    public static String DELETE_AS_EDUCATION_BY_GROUP = "DELETE FROM as_education WHERE groupId=";
    public static String DELETE_AS_DEMOGRAPHICS_BY_GROUP = "DELETE FROM as_demographics WHERE groupId=";
    public static String LAST_DEDUPING_DB = LOCAL_DB;

    /**
     * Constructor de la clase deduplicadora
     * @param allUsers true: desduplicación para todos los usuarios, false: desduplicacion para un usuario
     */
    public DedupingController(boolean allUsers) {

        try {
            DS_CONN = DataStorageConnection.getInstance().conn;

            //SE GENERA LA CONEXION SEGÚN LA ÚLTIMA BD DONDE SE HIZO LA DESDUPLICACION
            LAST_DEDUPING_DB = this.lookupLastDb();

            if (allUsers) {
                if (LAST_DEDUPING_DB.equals(LOCAL_DB)) {
                    //SE ACTIVA LA CONEXION REMOTA
                    AS_CONN = ActiveStorageConnection.getInstance(false).conn;
                } else {
                    //SE ACTIVA LA CONEXION LOCAL
                    AS_CONN = ActiveStorageConnection.getInstance(true).conn;
                }
            } else { //SOLO UN USUARIO, DESDUPLICACION EN MISMA DB

                if (LAST_DEDUPING_DB.equals(LOCAL_DB)) {
                    //SE ACTIVA LA CONEXION LOCAL
                    AS_CONN = ActiveStorageConnection.getInstance(true).conn;
                } else {
                    //SE ACTIVA LA CONEXION REMOTA
                    AS_CONN = ActiveStorageConnection.getInstance(false).conn;
                }
            }
        } catch (Exception ex) {
            System.err.println("Error DedupingController: " + ex.getLocalizedMessage());
        }
    }

    /**
     * Método que busca la última BD donde se realizó la desduplicación
     * @return última BD donde se realizó la desduplicación
     * @throws SQLException 
     */
    private String lookupLastDb() throws SQLException {
        String sQuery = "select value from variables where variable='LASTDEDUPINGDB' limit 1";
        String value = LOCAL_DB;
        Statement stmt = (Statement) DS_CONN.createStatement();
        try {
            ResultSet rs = stmt.executeQuery(sQuery);
            while (rs.next()) {
                value = rs.getString("value");
            }
        } catch (SQLException ex) {
            System.err.println("lookupLastDb: " + ex.getLocalizedMessage());
        } finally {
            stmt.close();
            return value;
        }
    }

    public static String getDedubed() {
        return DEDUBED;
    }

    public static String getDedubing() {
        return DEDUBING;
    }

    /**
     * Métodoq que trunca las tablas de Active Storage
     * @param conn conexion de Active Storage
     * @throws SQLException 
     */
    public void truncateActiveStorage(Connection conn) throws SQLException {
        Statement stmt = (Statement) conn.createStatement();
        try {
            stmt.executeUpdate(TRUNCATE_AS_CONTACTS);
            stmt.executeUpdate(TRUNCATE_AS_EMAIL);
            stmt.executeUpdate(TRUNCATE_AS_EXTENDED_PROPERTY);
            stmt.executeUpdate(TRUNCATE_AS_IM);
            stmt.executeUpdate(TRUNCATE_AS_NAMES);
            stmt.executeUpdate(TRUNCATE_AS_ORGANIZATION);
            stmt.executeUpdate(TRUNCATE_AS_PHONENUMBER);
            stmt.executeUpdate(TRUNCATE_AS_POSTALADDRESS);
            stmt.executeUpdate(TRUNCATE_AS_EDUCATION);
            stmt.executeUpdate(TRUNCATE_AS_DEMOGRAPHICS);
        } catch (SQLException ex) {
            System.err.println("truncateActiveStorage: " + ex.getLocalizedMessage());
        } finally {
            stmt.close();
        }
    }

    public void truncateActiveStorage(Connection conn, Integer userId) throws SQLException {
        Statement stmt = (Statement) conn.createStatement();
        try {
            stmt.executeUpdate("delete from as_contacts where user_Id=" + userId);
            stmt.executeUpdate("delete from as_email where user_Id=" + userId);
            stmt.executeUpdate("delete from as_extendedProperty");
            stmt.executeUpdate("delete from as_im where user_Id=" + userId);
            stmt.executeUpdate("delete from as_names where user_Id=" + userId);
            stmt.executeUpdate("delete from as_organization where user_Id=" + userId);
            stmt.executeUpdate("delete from as_phonenumber where user_Id=" + userId);
            stmt.executeUpdate("delete from as_postalAddress where user_Id=" + userId);
            stmt.executeUpdate("delete from as_education where user_Id=" + userId);
            stmt.executeUpdate("delete from as_demographics where user_Id=" + userId);
        } catch (SQLException ex) {
            System.err.println("truncateActiveStorage2: " + ex.getLocalizedMessage());
        } finally {
            stmt.close();
        }
    }

    /**
     * Método que realiza la desduplicación para todos los usuarios
     */
    public void dedupingAllUsers() {
        try {


            // ANTES DE DESDUPLICAR TRUNCAMOS DESDUPLICACION ANTIGUA
            //this.truncateActiveStorage(AS_CONN);
            Date dedupingDate = new Date();

            List<Integer> users = this.lookupAllUsersFromAsGroups();
            for (Integer userId : users) {
                this.updateSocialNetworksStatus(userId, DEDUBING);
                this.dedupingByUserFromAsGroups(userId);
                this.updateSocialNetworksStatus(userId, DEDUBED);
            }

            this.updateLastDeduping(dedupingDate.getTime());
            this.updateLastDedupingDB(LAST_DEDUPING_DB);


        } catch (Exception ex) {
            System.err.println("dedupingAllUsers: " + ex.getLocalizedMessage());
        }
    }

    public void dedupingUser(Integer userId) {
        try {


            // ANTES DE CONSTRUIR TRUNCAMOS DESDUPLICACION ANTIGUA
            this.truncateActiveStorage(AS_CONN, userId);
            this.updateSocialNetworksStatus(userId, DEDUBING);
            this.dedupingByUserFromAsGroups(userId);
            this.updateSocialNetworksStatus(userId, DEDUBED);


        } catch (Exception ex) {
            System.err.println("dedupingAllUsers: " + ex.getLocalizedMessage());
        }
    }

    /**
     * Método que permite realizar la desduplicación para un conjunto seleccionado
     * por las reglas de desduplicacion
     * @param lastDeduping 
     */
    private void dedupingSelectedUsers(Integer lastDeduping) {
        try {
            //DESDUPLICACION CON SELECCION DE USUARIOS
            List<Integer> usersLessPriority = this.lookupUserReadyToDeduping(lastDeduping);
            List<Integer> usersMorePriority = this.lookupUsersHighPriority(usersLessPriority);
            usersLessPriority.removeAll(usersMorePriority);

            //DESDUPLICACION DE LOS USUARIOS SIN DESDUPLICACION ANTERIOR
            for (Integer userId : usersMorePriority) {
                this.dedupingByUserId(userId, lastDeduping);
            }

            //DESDUPLICACION USUARIO CON MENOR PRIORIDAD
            for (Integer userId : usersLessPriority) {
                this.dedupingByUserId(userId, lastDeduping);
            }
        } catch (Exception ex) {
            System.err.println("dedupingSelectedUsers: " + ex.getLocalizedMessage());
        }

    }

    private List<Integer> lookupAllUsers() throws SQLException {
        List<Integer> users = new ArrayList<Integer>();
        String sQuery = "select distinct(User_ID) from socialNetworks order by User_ID";
        Statement stmt = (Statement) DS_CONN.createStatement();
        try {
            ResultSet rs = stmt.executeQuery(sQuery);
            while (rs.next()) {
                users.add(rs.getInt("User_ID"));
            }
        } catch (SQLException ex) {
            System.err.println("lookupAllUsers: " + ex.getLocalizedMessage());
        } finally {
            stmt.close();
            return users;
        }

    }

    private List<Integer> lookupAllUsersFromAsGroups() throws SQLException {
        List<Integer> users = new ArrayList<Integer>();
        String sQuery = "select distinct(user_Id) from as_groups order by user_Id";
        Statement stmt = (Statement) AS_CONN.createStatement();
        try {
            ResultSet rs = stmt.executeQuery(sQuery);
            while (rs.next()) {
                users.add(rs.getInt("user_Id"));
            }
        } catch (SQLException ex) {
            System.err.println("lookupAllUsersFromAsGroups: " + ex.getLocalizedMessage());
        } finally {
            stmt.close();
            return users;
        }

    }

    /**
     * Método que desduplica para un usuario en específico
     * @param userId usuario que se quiere desduplicar 
     * @param lastDeduping fecha última desduplicacion realizada 
     */
    public void dedupingByUserId(Integer userId, Integer lastDeduping) {

        try {
//            if (this.userReadyForDeduping(userId, lastDeduping) && this.userHaveAllSourcesStored(userId)) {
            if (this.userHaveAllSourcesStored(userId)) {
                this.deleteAsByUserId(userId);
                this.updateSocialNetworksStatus(userId, DEDUBING);

                Date d = new Date();
                System.out.println("Comienzo desduplicación usuario=" + userId.toString() + " a las= " + d.getTime());
                this.dedupingByEmailAndUserId(userId);
                Set<Integer> contacts = this.dedupingByPhoneAndName(userId);
                this.dedupingContactWithoutEmail(userId, contacts);
                this.dedupingSingleContacts(userId);
                //this.executeUnificationBetweenGmail(userId, conn);
                this.updateSocialNetworksStatus(userId, DEDUBED);


                Date d2 = new Date();
                System.out.println("Fin desduplicación usuario=" + userId.toString() + " a las= " + d2.getTime());
                System.out.println("Tiempo: " + (d2.getTime() - d.getTime()));
            } else {
                System.out.println("User id:" + userId + " con fuentes sin necesidad de desduplicar");
            }
        } catch (SQLException ex) {
            System.err.println("dedupingByUserId: " + ex.getLocalizedMessage());
        }
    }

    /**
     * Método que desduplica para un usuario en específico
     * @param userId usuario que se quiere desduplicar 
     * @param lastDeduping fecha última desduplicacion realizada 
     */
    public void dedupingByUserIdWithoutRules(Integer userId) {

        try {
            this.deleteAsByUserId(userId);
            this.updateSocialNetworksStatus(userId, DEDUBING);
            Date d = new Date();
            System.out.println("Comienzo desduplicación usuario=" + userId.toString() + " a las= " + d.getTime());
            this.dedupingByEmailAndUserId(userId);
            Set<Integer> contacts = this.dedupingByPhoneAndName(userId);
            this.dedupingContactWithoutEmail(userId, contacts);
            this.dedupingSingleContacts(userId);
            //this.executeUnificationBetweenGmail(userId, conn);
            this.updateSocialNetworksStatus(userId, DEDUBED);


            Date d2 = new Date();
            System.out.println("Fin desduplicación usuario=" + userId.toString() + " a las= " + d2.getTime());
            System.out.println("Tiempo: " + (d2.getTime() - d.getTime()));

        } catch (SQLException ex) {
            System.err.println("dedupingByUserIdWithoutRules: " + ex.getLocalizedMessage());
        }
    }

    /**
     * Método que desduplica para un usuario en específico a partir de los
     * contactos indicados como agrupados
     * @param userId usuario que se quiere desduplicar 
     * @param lastDeduping fecha última desduplicacion realizada 
     */
    public void dedupingByUserFromAsGroups(Integer userId) {

        try {
            //this.deleteAsByUserId(userId);
            Date d = new Date();
            System.out.println("Comienzo construccion AS usuario: " + userId.toString() + " a las: " + d.getTime());

            //se obtienen los ids de grupos solo para los contactos con estado grouped (o deberia ser new o updated)
            Set<Integer> contactos = this.lookupGroupedContacts(userId);
            Set<Integer> groups = new TreeSet<Integer>();
            //Se obtienen los grupos de cada contacto agrupado
            groups = this.lookupGroupsByGroupedContacts(contactos);


            //se obtienen los grupos segun user
//            Set<Integer> groups = this.lookupGroupsByUserId(userId);


            //pada cada grupo del usuario
            for (Integer grupo : groups) {
                this.dedupingByUserAndGroup(userId, grupo);
            }

            //se actualiza el estado de los contactos agrupados
            this.updateGroupedContacts(contactos);

            Date d2 = new Date();
            System.out.println("Fin construccion AS usuario=" + userId.toString() + " a las= " + d2.getTime());
            System.out.println("Tiempo: " + (d2.getTime() - d.getTime()) + " (ms)");

        } catch (Exception ex) {
            System.err.println("dedupingByUserFromAsGroups: " + ex.getLocalizedMessage());
        }
    }

    private void dedupingByUserAndGroup(Integer userId, Integer grupo) {
        try {
            Set<Integer> contacts = this.lookupContactByUserAndGroup(userId, grupo);
            this.populateActiveStorageFromAsGroups(contacts, userId, grupo, AS_CONN);
        } catch (SQLException ex) {
            System.err.println("dedupingByUserAndGroup: " + ex.getLocalizedMessage());
        }
    }

    private Set<Integer> lookupContactByUserAndGroup(Integer userId, Integer grupo) throws SQLException {
        Set<Integer> contacts = new HashSet<Integer>();
        String sQuery = "select distinct(contactId) from as_groups where groupId=? and user_Id=?";
        PreparedStatement stmt = (PreparedStatement) AS_CONN.prepareStatement(sQuery);
        try {
            stmt.setInt(1, grupo);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                contacts.add(rs.getInt("contactId"));
            }
        } catch (SQLException ex) {
            System.err.println("lookupContactByUserAndGroup: " + ex.getLocalizedMessage());
        } finally {
            stmt.close();
            return contacts;
        }
    }

    private Set<Integer> lookupGroupsByUserId(Integer userId) throws SQLException {
        Set<Integer> groups = new HashSet<Integer>();
        String sQuery = "select distinct(groupId) from as_groups where user_Id=?";
        PreparedStatement stmt = (PreparedStatement) AS_CONN.prepareStatement(sQuery);
        try {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                groups.add(rs.getInt("groupId"));
            }

        } catch (SQLException ex) {
            System.err.println("lookupGroupsByUserId: " + ex.getLocalizedMessage());
        } finally {
            stmt.close();
            return groups;
        }
    }

    /**
     * Método que retorma los usuarios que tienen más prioridad para ser deduplicados
     * @param usersLessPriority usuarios encontrados que deben ser desduplicados
     * @return usuarios con mayor prioridad para ser desduplicados
     */
    private List<Integer> lookupUsersHighPriority(List<Integer> usersLessPriority) {
        List<Integer> uhp = new ArrayList<Integer>();
        try {
            for (Integer user : usersLessPriority) {
                if (!this.haveUserAsContacts(user)) {
                    uhp.add(user);
                }
            }

        } catch (Exception ex) {
            System.err.println("lookupUsersHighPriority: " + ex.getLocalizedMessage());
        } finally {
            return uhp;
        }
    }

    /**
     * Método que indica si un usuario ha sido desduplicado anterior mente
     * @param userId identificador del usuario
     * @return true: ha sido desduplicado antes, false: no ha sido desduplicado
     * @throws SQLException 
     */
    private boolean haveUserAsContacts(Integer userId) throws SQLException {
        boolean result = false;
        String sQuery = "select count(1) as cont from as_contacts where user_Id=?";
        PreparedStatement stmt = (PreparedStatement) AS_CONN.prepareStatement(sQuery);
        try {
            Integer cont = new Integer(0);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                cont = rs.getInt("cont");
            }
            if (cont > 0) {
                result = true;
            }

        } catch (SQLException ex) {
            System.err.println("haveUserAsContacts: " + ex.getLocalizedMessage());
        } finally {
            stmt.close();
            return result;
        }
    }

    /**
     * Método que elimina pasadas desduplicaciones de un usuario particular
     * @param userId: usuario al que se quiere eliminar la historia
     * @throws SQLException 
     */
    private void deleteAsByUserId(Integer userId) throws SQLException {
        Statement stmt = (Statement) AS_CONN.createStatement();
        try {
            stmt.executeUpdate(DELETE_AS_CONTACTS_BY_USER + userId.toString());
            stmt.executeUpdate(DELETE_AS_EMAIL_BY_USER + userId.toString());
            stmt.executeUpdate(DELETE_AS_IM_BY_USER + userId.toString());
            stmt.executeUpdate(DELETE_AS_NAMES_BY_USER + userId.toString());
            stmt.executeUpdate(DELETE_AS_ORGANIZATION_BY_USER + userId.toString());
            stmt.executeUpdate(DELETE_AS_PHONENUMBER_BY_USER + userId.toString());
            stmt.executeUpdate(DELETE_AS_POSTALADDRESS_BY_USER + userId.toString());
            stmt.executeUpdate(DELETE_AS_EXTENDED_PROPERTY_BY_USER + userId.toString());
            stmt.executeUpdate(DELETE_AS_EDUCATION_BY_USER + userId.toString());
            stmt.executeUpdate(DELETE_AS_DEMOGRAPHICS_BY_USER + userId.toString());
        } catch (SQLException ex) {
            System.err.println("deleteAsByUserId: " + ex.getLocalizedMessage());
        } finally {
            stmt.close();
        }
    }

    private void updateLastDedupingDB(String lastDedupingDb) throws SQLException {
        String sQuery = "update variables set value=? where variable='LASTDEDUPINGDB'";
        PreparedStatement stmt = (PreparedStatement) DS_CONN.prepareStatement(sQuery);
        try {
            if (lastDedupingDb.equals(LOCAL_DB)) {
                stmt.setString(1, REMOTE_DB);
            } else {
                stmt.setString(1, LOCAL_DB);
            }
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("updateLastDedupingDB:" + ex.getLocalizedMessage());
        } finally {
            stmt.close();
        }
    }

    public Integer lookupLastDeduping() throws SQLException {
        String value = null;
        Integer lastUpdated = 2000000000;
        String sQuery = "select value from variables where variable='LASTDEDUPING' limit 1";
        Statement stmt = (Statement) DS_CONN.createStatement();
        try {
            ResultSet rs = stmt.executeQuery(sQuery);
            while (rs.next()) {
                value = rs.getString("value");
            }

            lastUpdated = new Integer(value);
        } catch (SQLException ex) {
            System.err.println("lookupLastDeduping: " + ex.getLocalizedMessage());
        } finally {
            stmt.close();
            return lastUpdated;
        }
    }

    public boolean userReadyForDeduping(Integer userId, Integer lastDeduping) throws SQLException {
        boolean ready = false;
        Integer lu = 0;
        String sQuery = "select lastUpdated from socialNetworks where User_ID=? order by lastUpdated desc limit 1";
        PreparedStatement stmt = (PreparedStatement) DS_CONN.prepareStatement(sQuery);
        try {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lu = rs.getInt("lastUpdated");
            }
            // Si la última actualización del usuario fue después de la última desduplicación 
            if (lastDeduping < lu) {
                ready = true;
            }
        } catch (SQLException ex) {
            System.err.println("userReadyForDeduping: " + ex.getLocalizedMessage());
        } finally {
            stmt.close();
            return ready;
        }
    }

    public boolean userHaveAllSourcesStored(Integer userId) throws SQLException {
        boolean ready = true;
        String sQuery = "select distinct(status) from socialNetworks where user_Id=?";
        PreparedStatement stmt = (PreparedStatement) DS_CONN.prepareStatement(sQuery);
        try {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String s = rs.getString("status");
                if (!s.equals(STORED)) {
                    ready = false;
                    break;
                }
            }
        } catch (SQLException ex) {
            System.err.println("userHaveAllSourcesStored: " + ex.getLocalizedMessage());
        } finally {
            stmt.close();
            return ready;
        }
    }

    public void updateSocialNetworksStatus(Integer userId, String newStatus) throws SQLException {
        String sQuery = "update socialNetworks set status=? where user_Id=?";
        PreparedStatement stmt = (PreparedStatement) DS_CONN.prepareStatement(sQuery);
        try {
            stmt.setString(1, newStatus);
            stmt.setInt(2, userId);

            stmt.executeUpdate();

        } catch (SQLException ex) {
            System.err.println("updateSocialNetworksStatus:" + ex.getLocalizedMessage());
        } finally {
            stmt.close();
        }
    }

    private List<Integer> lookupUserReadyToDeduping(Integer lastUpdated) throws SQLException {
        List<Integer> userIds = new ArrayList<Integer>();
        String sQuery = "select distinct(User_ID) from socialNetworks where lastUpdated>? order by lastUpdated desc";
        PreparedStatement stmt = (PreparedStatement) DS_CONN.prepareStatement(sQuery);
        try {

            stmt.setInt(1, lastUpdated);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                userIds.add(rs.getInt("User_ID"));
            }
        } catch (SQLException ex) {
            System.err.println("lookupUserReadyToDeduping: " + ex.getLocalizedMessage());
        } finally {
            stmt.close();
            return userIds;
        }
    }

    public void updateLastDeduping(long newDeduping) throws SQLException {
        String sQuery = "update variables set value=? where variable='LASTDEDUPING'";
        PreparedStatement stmt = (PreparedStatement) DS_CONN.prepareStatement(sQuery);

        try {
            Long s = new Long(newDeduping);
            String aux = s.toString().substring(0, 10);


            stmt.setString(1, aux);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("updateLastDeduping:" + ex.getLocalizedMessage());
        } finally {
            stmt.close();
        }
    }

    public void dedupingByEmailAndUserId(Integer userId) {
        try {

            List<String> emails = this.lookupEmailsByUserId(userId, DS_CONN);
            for (String e : emails) {
                this.dedupingByEmail(e, userId);
            }
        } catch (Exception ex) {
            System.err.println("dedupingByEmailAndUserId: " + ex.getLocalizedMessage());
        }
    }

    private List<String> lookupEmailsByUserId(Integer userId, Connection conn) throws SQLException {
        List<String> emails = new ArrayList<String>();
        Statement stmt = (Statement) conn.createStatement();
        try {

            String sQuery = "select distinct(address) from email where user_Id=" + userId.toString() + " and address<>''";
            ResultSet rs = stmt.executeQuery(sQuery);
            while (rs.next()) {
                emails.add(rs.getString("address"));
            }
        } catch (SQLException ex) {
            System.err.println("lookupEmailsByUserId: " + ex.getLocalizedMessage());
        } catch (Exception ex) {
            System.err.println("lookupEmailsByUserId: " + ex.getLocalizedMessage());
        } finally {
            stmt.close();
            return emails;
        }

    }

    /** Metodo que pobla AS según los contactos que deben unificarse y el usuarios
     * 
     * @param contactsId: Contactos que deben ser desduplicados
     * @param userId: usuario al cual pertenecen los contactos
     * @param conn 
     */
    private void populateActiveStorageFromAsGroups(Set<Integer> contactsId, Integer userId, Integer groupId, Connection conn) {
        boolean is_single_contact = false;

        if (contactsId.size() == 1) {
            is_single_contact = true;
        }

        try {
            this.deleteOldGroup(groupId);


            //Insert into as_email
            this.createAsEmails(groupId, contactsId, userId);

            //Insert into as_names
            String titleName = this.createAsFullNames(groupId, contactsId, userId);

            //insert into as_phonenumber
            this.createAsPhoneNumbers(groupId, contactsId, userId);

            //insert as_postalAddress
            this.createAsPostalAddresses(groupId, contactsId, userId);


            //insert into as_organization
            this.createAsOrganization(groupId, contactsId, userId);

            //insert as_im
            this.createAsIm(groupId, contactsId, userId);

            //insert into as_extendedPorperty
            this.createAsExtendedProperty(groupId, contactsId, userId);


            //insert into as_education
            this.createAsEducation(groupId, contactsId, userId);

            //insert into as_demographics
            this.createAsDemographics(groupId, contactsId, userId);


            //Insert into as_contacts
            this.createAsContact(groupId, contactsId, userId, titleName, is_single_contact);

            //Clear as_phonenumber quita guiones y espacios
            this.cleanAsPhoneNumber(userId);

        } catch (Exception ex) {
            System.err.println("populateActiveStorageFromAsGroups: " + ex.getLocalizedMessage());
        }

    }

    /** Metodo que pobla AS según los contactos que deben unificarse y el usuarios
     * 
     * @param contactsId: Contactos que deben ser desduplicados
     * @param userId: usuario al cual pertenecen los contactos
     * @param conn 
     */
    private void populateActiveStorage(Set<Integer> contactsId, Integer userId, Connection conn) {
        boolean is_single_contact = false;

        if (contactsId.size() == 1) {
            is_single_contact = true;
        }

        try {
            //Insert into as_parent and lookup new child_id
            Integer childId = this.initCreationAsParent(contactsId, userId);

            if (!childId.equals(new Integer(-1))) {

                //Insert into as_email
                this.createAsEmails(childId, contactsId, userId);

                //Insert into as_names
                String titleName = this.createAsFullNames(childId, contactsId, userId);

                //insert into as_phonenumber
                this.createAsPhoneNumbers(childId, contactsId, userId);

                //insert as_postalAddress
                this.createAsPostalAddresses(childId, contactsId, userId);


                //insert into as_organization
                this.createAsOrganization(childId, contactsId, userId);

                //insert as_im
                this.createAsIm(childId, contactsId, userId);

                //insert into as_extendedPorperty
                this.createAsExtendedProperty(childId, contactsId, userId);

                //Insert into as_contacts
                this.createAsContact(childId, contactsId, userId, titleName, is_single_contact);

                //Clear as_phonenumber
                this.cleanAsPhoneNumber(userId);
            }
        } catch (Exception ex) {
            System.err.println(ex.getLocalizedMessage());
        }

    }

    public void dedupingByEmail(String mail, Integer userId) throws SQLException {
        // Call function deduping
        Set<String> emailsSet = new HashSet<String>();
        emailsSet.add(mail);

        //Retorna el conjunto de contactos que deben unirse en uno
        Set<Integer> contactsId = this.executeDeduping(emailsSet, mail, userId, DS_CONN);
        if (contactsId.size() >= 1) {
            this.populateActiveStorage(contactsId, userId, AS_CONN);
        } else {
            System.out.println("Email doesn't exist");
        }
    }

    private Integer initCreationAsParent(Set<Integer> contactsId, Integer userId) throws SQLException {
        // Se busca si existen en la tabla parents
        List<Integer> childIds = this.retrieveChildIds(contactsId, userId, AS_CONN);

        Integer childId;
        //Clear tables AS
        if (childIds.size() >= 1) {
            Integer oldChilId = childIds.get(0);
            this.clearOldChildIds(childIds, AS_CONN);
            childId = this.createAsParents(oldChilId, contactsId, userId, AS_CONN);
        } else {
            //Insert into table parents
            childId = this.createAsParents(contactsId, userId, AS_CONN);
        }

        return childId;
    }

    public Set<Integer> dedupingByPhoneAndName(Integer userId) {
        try {
            Set<Integer> dedupingContacts = dedupingContactsByPhoneAndName(userId);
            return dedupingContacts;
        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
            return null;
        }
    }

    private Set<Integer> dedupingContactsByPhoneAndName(Integer userId) throws SQLException {

        Set<Integer> contacts = this.lookupContactsWhithoutEmailByPhonenumber(userId, DS_CONN);
        Set<Integer> originalContacts = contacts;
        for (Integer c : contacts) {
            List<String> phoneNumbers = this.lookupPhoneNumber(userId, c, DS_CONN);
            String contactName = this.lookupContactName(userId, c, DS_CONN);
            for (String phone : phoneNumbers) {
                Set<Integer> duplicateContacts = this.lookupDuplicatedContactsByPhoneAndNumber(userId, phone, contactName, DS_CONN);
                if (duplicateContacts.size() >= 1) {
                    this.populateActiveStorage(duplicateContacts, userId, AS_CONN);
                }
            }
        }

        return originalContacts;
    }

    private Set<Integer> removeReadyContacts(Set<Integer> contacts, Set<Integer> duplicateContacts) {
        contacts.removeAll(duplicateContacts);
        return contacts;
    }

    private Set<Integer> lookupDuplicatedContactsByPhoneAndNumber(Integer userId, String phone, String contactName, Connection conn) throws SQLException {
        Set<Integer> d_contacts = new HashSet<Integer>();

        String query = "select contactId from contacts where user_Id=? and contactId in (select contactId from phonenumber where user_Id=? and substring(replace(replace(Text,' ',''),'-',''),-7)=?) and title=?";
        PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(query);

        try {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            pstmt.setString(3, phone);
            pstmt.setString(4, contactName);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                d_contacts.add(rs.getInt("contactId"));
            }

        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
        } finally {

            pstmt.close();
            return d_contacts;
        }

    }

    private List<String> lookupPhoneNumber(Integer userId, Integer c, Connection conn) throws SQLException {
        List<String> phones = new ArrayList<String>();
        Statement stmt = (Statement) conn.createStatement();
        try {
            String sQuery = "select substring(replace(replace(Text,'-',''),' ',''),-7) as Text from phonenumber "
                    + "where user_Id=" + userId.toString() + " and contactId=" + c.toString() + ";";

            ResultSet rs = stmt.executeQuery(sQuery);
            while (rs.next()) {
                phones.add(rs.getString("Text"));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
        } finally {
            stmt.close();
            return phones;
        }
    }

    private String lookupContactName(Integer userId, Integer c, Connection conn) throws SQLException {
        String name = null;
        Statement stmt = (Statement) conn.createStatement();
        try {
            String sQuery = "select title from contacts where user_Id=" + userId.toString() + " and contactId=" + c.toString();
            ResultSet rs = stmt.executeQuery(sQuery);
            while (rs.next()) {
                name = rs.getString("title");
            }
        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
        } finally {
            stmt.close();
            return name;
        }
    }

    public void dedupingContactWithoutEmail(Integer userId, Set<Integer> dedupingContacts) throws SQLException {

        Set<Integer> contacts = this.lookupContactsWhithoutEmail(userId, DS_CONN);
        contacts.removeAll(dedupingContacts);

        Set<Integer> contactsId = new HashSet<Integer>();

        for (Integer c : contacts) {
            contactsId.add(c);
            this.populateActiveStorage(contactsId, userId, AS_CONN);
            contactsId.remove(c);
        }
    }

    private Set<Integer> lookupContactsWhithoutEmail(Integer userId, Connection conn) throws SQLException {
        Set<Integer> contacts = new HashSet<Integer>();

        Statement stmt = (Statement) conn.createStatement();
        try {
            String sQuery = "SELECT distinct(contactId) FROM contacts WHERE contactId NOT IN "
                    + "(SELECT contactId FROM email where user_Id=" + userId.toString() + ") and user_Id=" + userId.toString() + ";";

            ResultSet rs = stmt.executeQuery(sQuery);

            while (rs.next()) {
                contacts.add(rs.getInt("contactId"));
            }


        } catch (Exception ex) {
            System.err.println(ex.getLocalizedMessage());
        } finally {
            stmt.close();
            return contacts;
        }

    }

    public Set<Integer> lookupContactsWhithoutEmailByPhonenumber(Integer userId, Connection conn) throws SQLException {
        Set<Integer> contacts = new HashSet<Integer>();

        Statement stmt = (Statement) conn.createStatement();
        try {
            String sQuery = "SELECT distinct(contactId) FROM phonenumber WHERE contactId NOT IN "
                    + "(SELECT contactId FROM email where user_Id=" + userId.toString() + ") and user_Id=" + userId.toString() + ";";

            ResultSet rs = stmt.executeQuery(sQuery);
            while (rs.next()) {
                contacts.add(rs.getInt("contactId"));
            }

            sQuery = "SELECT distinct(contactId) FROM phonenumber WHERE contactId IN "
                    + "(SELECT contactId FROM phonenumber where source in ('linkedin','facebook') AND user_Id= " + userId.toString() + ") AND user_Id=" + userId.toString() + ";";

            ResultSet rs2 = stmt.executeQuery(sQuery);
            while (rs2.next()) {
                contacts.add(rs2.getInt("contactId"));
            }

        } catch (Exception ex) {
            System.err.println(ex.getLocalizedMessage());
        } finally {
            stmt.close();
            return contacts;
        }
    }

    /**
     * Method that lookup contact ids which are grouped in a new contact
     * @param emailsSet
     * @param currentEmail
     * @param conn
     * @return
     */
    private Set<Integer> executeDeduping(Set<String> emailsSet, String currentEmail, Integer userId, Connection conn) {

        Set<Integer> parentsIdSet = new HashSet<Integer>();
        List<Email> emails_list = this.lookupContactsEmail(currentEmail, userId, conn);
        for (Email e : emails_list) {
            parentsIdSet.add(e.getContactId());
        }
        //¿Existen duplicados?
        if (emails_list.size() >= 1) {
            //Each contact lookup his emails
            for (Email e : emails_list) {
                //Lookup emails
                List<Email> emails = this.lookupEmailsByContactId(e.getContactId(), conn);
                for (Email e2 : emails) {
                    // new email?
                    if (emailsSet.add(e2.getAddress())) {
                        Set<Integer> newIds = this.executeDeduping(emailsSet, e2.getAddress(), userId, conn);
                        parentsIdSet.addAll(newIds);
                    }
                }
            }
        }

        return parentsIdSet;
    }

    /**
     * Given an email, look for the ids of the contacts
     * @param testMail
     * @param conn
     * @return
     */
    private List<Email> lookupContactsEmail(String testMail, Integer userId, Connection conn) {
        List<Email> contactEmails = null;
        //IEmail em = AbstractFactory.getAbstractFactory().getEmail();
        Email em = new Email();
        em.setAddress(testMail);
        try {
            //contactEmails = DAOFactory.getDAOFactory().getEmailDTO().retrieveByAddress(em,conn);
            contactEmails = this.retrieveContactsByEmail(em, userId, conn);
        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
        } finally {
            return contactEmails;
        }
    }

    /**
     * Retrieve contacts for an email
     * @param em
     * @param conn
     * @return
     * @throws SQLException
     */
    private List<Email> retrieveContactsByEmail(Email em, Integer userId, Connection conn) throws SQLException {
        List<Email> contacts = new ArrayList<Email>();
        Statement stmt = (Statement) conn.createStatement();
        try {

            ResultSet rs = stmt.executeQuery("select contactId,address from email where address='" + em.getAddress() + "' and user_Id=" + userId.toString());
            while (rs.next()) {
                Email e = new Email();
                e.setContactId(rs.getInt("contactId"));
                e.setAddress(rs.getString("address"));
                contacts.add(e);

                //System.out.println("Current contactId:" + rs.getInt("contactId"));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
        } catch (Exception ex) {
            System.err.println(ex.getLocalizedMessage());

        } finally {
            stmt.close();
            return contacts;
        }
    }

    /**
     * Lookup emails for a contact id
     * @param contactId
     * @param conn
     * @return
     */
    private List<Email> lookupEmailsByContactId(Integer contactId, Connection conn) {
        List<Email> emails = null;
        //IEmail em = AbstractFactory.getAbstractFactory().getEmail();
        Contact c = new Contact();
        c.setContactId(contactId);
        try {
            //contactEmails = DAOFactory.getDAOFactory().getEmailDTO().retrieveByAddress(em,conn);
            emails = this.retrieveEmailsByContactId(c, conn);
        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
        } finally {
            return emails;
        }
    }

    /**
     * Retrieve emails for a contact id
     * @param c
     * @param conn
     * @return
     * @throws SQLException
     */
    private List<Email> retrieveEmailsByContactId(Contact c, Connection conn) throws SQLException {

        List<Email> emails = new ArrayList<Email>();
        Statement stmt = (Statement) conn.createStatement();
        try {
            String sQuery = "select contactId,address from email where contactId=" + c.getContactId();
            ResultSet rs = stmt.executeQuery(sQuery);
            while (rs.next()) {
                Email e = new Email();
                e.setContactId(rs.getInt("contactId"));
                e.setAddress(rs.getString("address"));
                emails.add(e);

                //System.out.println("Current email:" + rs.getString("address"));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
        } catch (Exception ex) {
            System.err.println(ex.getLocalizedMessage());

        } finally {
            stmt.close();
            return emails;
        }
    }

    /**
     * Method that make inserts into table as_parents
     * @param contactsId
     * @param conn
     */
    private Integer createAsParents(Set<Integer> contactsId, Integer userId, Connection conn) {

        try {
            Integer childId = this.getMaxChildId(conn);
            childId++;

            for (Integer parentId : contactsId) {
                this.createParent(parentId, childId, userId, conn);
            }

            return childId;
        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
            return -1;
        }

    }

    /**
     * Method thad make inserts into table as_parents
     * @param contactsId
     * @param conn
     */
    private Integer createAsParents(Integer childId, Set<Integer> contactsId, Integer userId, Connection conn) {

        try {

            for (Integer parentId : contactsId) {
                this.createParent(parentId, childId, userId, conn);
            }

            return childId;
        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
            return -1;
        }

    }

    /**
     * Get max child id
     * @param conn
     * @return
     * @throws SQLException
     */
    private Integer getMaxChildId(Connection conn) throws SQLException {
        Integer max = null;
        Statement stmt = (Statement) conn.createStatement();
        try {
            String sQuery = "select max(child_id) as max from as_parents;";

            ResultSet rs = stmt.executeQuery(sQuery);


            while (rs.next()) {
                max = rs.getInt("max");
            }

        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
        } finally {
            stmt.close();
            return max;
        }
    }

    /**
     * Insert a new parent (parent,child, userId)
     * @param parentId
     * @param childId
     * @param conn
     * @throws SQLException
     */
    private void createParent(Integer parentId, Integer childId, Integer userId, Connection conn) throws SQLException {

        Statement stmt = (Statement) conn.createStatement();
        try {
            String sQuery = "insert into as_parents values(" + parentId + "," + childId + "," + userId + ");";
            stmt.executeUpdate(sQuery);
        } catch (SQLException ex) {
            //ex.printStackTrace();
        } finally {
            stmt.close();
        }
    }

    private void createAsContact(Integer groupId, Set<Integer> contactsId, Integer userId, String title, boolean is_single_contact) {
        try {
            this.createContact(groupId, contactsId, userId, title, is_single_contact, AS_CONN);


        } catch (SQLException ex) {
            Logger.getLogger(DedupingController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createContact(Integer groupId, Set<Integer> contactsId, Integer userId, String title, boolean is_single_contact, Connection conn) throws SQLException {

        String sQuery = "insert into as_contacts(groupId,user_Id,srcId,srcName,title,link,allText) values(?,?,?,?,?,?,?)";
        PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(sQuery);
        Integer c_id = new Integer(0);
        try {
            String srcName = null;

            //SI SOLO SE ESTA COPIANDO UN CONTACTO DE DS a AS
            //SE MANTIENE EL SOURCE DE ORIGEN
            if (is_single_contact) {
                for (Integer c : contactsId) {
                    c_id = c;
                }
                srcName = this.lookupSourceName(userId, c_id, DS_CONN);
            } else {//BUSQUEDA DE FUENTES SEPARADAS
                srcName = this.lookupSourcesNames(contactsId);
            }

            String srcId = groupId.toString() + "-" + userId.toString();
            String link = "";
            String allText = groupId.toString() + "-" + userId.toString();

            stmt.setInt(1, groupId);
            stmt.setInt(2, userId);
            stmt.setString(3, srcId);
            stmt.setString(4, srcName);
            stmt.setString(5, title);
            stmt.setString(6, link);
            stmt.setString(7, allText);

            stmt.executeUpdate();

        } catch (SQLException ex) {
            System.err.println("createContact: " + ex.getLocalizedMessage());
        } finally {
            stmt.close();
        }

    }

    private String lookupSourcesNames(Set<Integer> contactsId) throws SQLException {
        String s = "";
        String sQuery = "select distinct(srcName) from contacts where contactId in " + getContactIdLikeString(contactsId);
        PreparedStatement stmt = (PreparedStatement) DS_CONN.prepareStatement(sQuery);
        try {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                s += rs.getString("srcName") + ",";
            }
            s = s.substring(0, s.length() - 1);
        } catch (SQLException ex) {
            System.err.println("Error (lookupSourcesNames): " + ex.getLocalizedMessage());
        } finally {
            stmt.close();
            return s;
        }
    }

    private String lookupSourceName(Integer userId, Integer contactId, Connection conn) throws SQLException {
        String srcName = null;
        String sQuery = "select srcName from contacts where user_Id=? and contactId=?";
        PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(sQuery);
        try {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, contactId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                srcName = rs.getString("srcName");
            }
        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
        } finally {
            pstmt.close();
            return srcName;
        }
    }

    private String cleanName(String title) {

        if (title.indexOf("\"") > -1) {
            return title.replace("\"", "");
        }
        return title;
    }

    /**
     * Method that make inserts into table as_email
     * @param groupId
     * @param contactsId
     * @param conn
     */
    private void createAsEmails(Integer groupId, Set<Integer> contactsId, Integer userId) {
        try {
            List<String> emails = this.lookupDistinctEmails(contactsId, userId, DS_CONN);
            for (String mail : emails) {
                this.createEmail(groupId, mail, userId);
            }
        } catch (Exception ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }

    /**
     * Lookup distinct mails for a set of contacts id
     * @param contactsId
     * @param conn
     * @return
     * @throws SQLException
     */
    private List<String> lookupDistinctEmails(Set<Integer> contactsId, Integer userId, Connection conn) throws SQLException {
        List<String> emails = new ArrayList<String>();
        String sQuery1 = "select distinct(address) as address from email where user_Id=" + userId.toString() + " and address<>'' and contactId in ";
        String sQuery2 = this.getContactIdLikeString(contactsId);
        String sQuery = sQuery1 + sQuery2;

        Statement stmt = (Statement) conn.createStatement();

        try {
            ResultSet rs = stmt.executeQuery(sQuery);
            while (rs.next()) {
                emails.add(rs.getString("address"));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
            return null;
        } catch (Exception ex) {
            System.err.println(ex.getLocalizedMessage());
            return null;
        } finally {
            stmt.close();
            return emails;
        }
    }

    /**
     * Transform contacts ids to string used in queries
     * @param contactsId
     * @return
     */
    private String getContactIdLikeString(Set<Integer> contactsId) {
        String s = null;
        int length = contactsId.size();
        if (length >= 1) {
            s = "(";
            int cont = 1;
            for (Integer i : contactsId) {
                s = s + i.toString();
                if (cont != length) {
                    s = s + ',';
                    cont++;
                } else {
                    break;
                }
            }
            s = s + ")";
        }

        return s;
    }

    /**
     * Insert new email into as_email
     * @param groupId
     * @param mail
     * @param conn
     * @throws SQLException
     */
    private void createEmail(Integer groupId, String mail, Integer userId) throws SQLException {

        try {
            Email e = this.lookupEmailByEmailAndUserId(mail, userId, DS_CONN);

            if (e.getContactId() != null) {
                this.insertAsEmail(e, groupId, AS_CONN);
            }

        } catch (SQLException ex) {
            System.err.println("createEmail: " + ex.getLocalizedMessage());
        } finally {
            return;
        }
    }

    private Email lookupEmailByEmailAndUserId(String mail, Integer userId, Connection conn) throws SQLException {
        String sQuery = "select * from email where address=? and user_Id=? order by email.primary desc limit 1";
        PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(sQuery);
        Email e = new Email();
        try {
            stmt.setString(1, mail);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                e.setContactId(rs.getInt("contactId"));
                e.setUserId(userId);
                e.setAddress(rs.getString("address"));
                e.setLabel(rs.getString("label"));
                e.setType(rs.getString("type"));
                e.setPrimary(rs.getInt("primary"));
                e.setSource(rs.getString("source"));
                e.setInsertDate(rs.getInt("insertDate"));
                e.setEmailID(rs.getInt("emailID"));
                e.setAccountSrc(rs.getString("accountSrc"));
            }


        } catch (SQLException ex) {
            System.err.println("lookupEmailByEmailAndUserId: " + ex.getLocalizedMessage());
        } finally {
            stmt.close();
            return e;
        }
    }

    private void insertAsEmail(Email e, Integer groupId, Connection conn) throws SQLException {

        String sQuery = "insert into as_email(emailID,groupId,user_Id,address,label,`type`,`primary`,source,insertDate) values(?,?,?,?,?,?,?,?,?)";
        PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(sQuery);

        try {

            pstmt.setInt(1, e.getEmailID());
            pstmt.setInt(2, groupId);
            pstmt.setInt(3, e.getUserId());
            pstmt.setString(4, e.getAddress());
            pstmt.setString(5, e.getLabel());
            pstmt.setString(6, e.getType());
            pstmt.setInt(7, e.getPrimary());
            pstmt.setString(8, e.getSource());
            pstmt.setInt(9, e.getInsertDate());
            //pstmt.setString(10, e.getAccountSrc());

            pstmt.executeUpdate();


        } catch (SQLException ex) {
            //System.out.println("insertAsEmail:"+ex.getLocalizedMessage());
        } finally {
            pstmt.close();
        }
    }

    private String createAsFullNames(Integer groupId, Set<Integer> contactsId, Integer userId) {
        int length = 0;
        String titleName = "";
        try {
            List<String> fullNames = this.lookupDistinctFullNames(contactsId, userId);
            for (String fullName : fullNames) {
                //Selection full
                if (fullName.length() >= length) {
                    titleName = fullName;
                    length = fullName.length();
                }
                this.createFullName(groupId, fullName, contactsId, userId);
            }
        } catch (Exception ex) {
            System.err.println("createAsFullNames: " + ex.getLocalizedMessage());
            return null;
        }
        return titleName;
    }

    /**
     * Lookup disctinct fullnames for a set of contacts ids
     * @param contactsId
     * @param conn
     * @return
     * @throws SQLException
     */
    private List<String> lookupDistinctFullNames(Set<Integer> contactsId, Integer userId) throws SQLException {
        List<String> fullNames = new ArrayList<String>();
        String sQuery1 = "select distinct(fullname) as fullname from names where user_Id=" + userId.toString() + " and contactId in ";
        String sQuery2 = this.getContactIdLikeString(contactsId);
        String sQuery = sQuery1 + sQuery2;

        Statement stmt = (Statement) DS_CONN.createStatement();

        try {
            ResultSet rs = stmt.executeQuery(sQuery);
            while (rs.next()) {
                fullNames.add(rs.getString("fullname"));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
        } finally {
            stmt.close();
            return fullNames;
        }
    }

    /**
     * Method that insert new fullname into as_names
     * @param groupId
     * @param fullName
     * @param conn
     * @throws SQLException
     */
    private void createFullName(Integer groupId, String fullName, Set<Integer> contactsId, Integer userId) throws SQLException {

        String sContactIds = this.getContactIdLikeString(contactsId);
        try {

            Names name = this.lookupNameByFullNameAndUserId(fullName, userId, sContactIds, DS_CONN);

            if (name.getContactId() != null) {
                this.insertAsNames(name, groupId, AS_CONN);
            }

        } catch (SQLException ex) {
            System.err.print("createFullName: " + ex.getLocalizedMessage());
        } finally {
            return;
        }
    }

    private Names lookupNameByFullNameAndUserId(String fullName, Integer userId, String sContactIds, Connection conn) throws SQLException {
        String sQuery = "select * from names where fullName=? and user_Id=? and contactId in " + sContactIds + " limit 1";
        Names name = new Names();
        PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(sQuery);
        try {
            stmt.setString(1, fullName);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                name.setNameId(rs.getInt("nameId"));
                name.setContactId(rs.getInt("contactId"));
                name.setUserId(userId);
                name.setFullName(fullName);
                name.setFirstName(rs.getString("firstName"));
                name.setLastName(rs.getString("lastName"));
                name.setSource(rs.getString("source"));
                name.setInsertDate(rs.getInt("insertDate"));
                name.setAccountSrc(rs.getString("accountSrc"));
            }
        } catch (SQLException ex) {
            System.err.println("lookupNameByFullNameAndUserId: " + ex.getLocalizedMessage());
        } finally {
            stmt.close();
            return name;
        }
    }

    private void insertAsNames(Names name, Integer groupId, Connection conn) throws SQLException {
        Statement stmt = (Statement) conn.createStatement();
        String query = "insert into as_names(nameId,groupId,user_Id,fullName,firstName,lastName,source,insertDate) values(?,?,?,?,?,?,?,?)";
        //String query = "insert into as_names(nameId,groupId,user_Id,fullName,firstName,lastName,source,insertDate,accountSrc) values(?,?,?,?,?,?,?,?,?)";
        PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(query);


        try {
            pstmt.setInt(1, name.getNameId());
            pstmt.setInt(2, groupId);
            pstmt.setInt(3, name.getUserId());
            pstmt.setString(4, name.getFullName());
            pstmt.setString(5, name.getFirstName());
            pstmt.setString(6, name.getLastName());
            pstmt.setString(7, name.getSource());
            pstmt.setInt(8, name.getInsertDate());
            //pstmt.setString(9, name.getAccountSrc());

            pstmt.executeUpdate();

        } catch (SQLException ex) {
            //ex.printStackTrace();
        } finally {
            stmt.close();
            pstmt.close();
        }

    }

    /**
     * Create New Phone Numbers into as_phonenumber
     * @param groupId
     * @param contactsId
     * @param conn
     */
    private void createAsPhoneNumbers(Integer groupId, Set<Integer> contactsId, Integer userId) {
        try {
            List<String> phoneNumbers = this.lookupDistinctPhoneNumbers(contactsId, userId, DS_CONN);
            for (String phone : phoneNumbers) {
                this.createPhoneNumber(groupId, phone, contactsId, userId);
            }
        } catch (Exception ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }

    /**
     * Lookup distinct phone numbers for a set of contacts ids
     * @param contactsId
     * @param conn
     * @return
     * @throws SQLException
     */
    private List<String> lookupDistinctPhoneNumbers(Set<Integer> contactsId, Integer userId, Connection conn) throws SQLException {
        List<String> phones = new ArrayList<String>();
        String sQuery1 = "select distinct(Text) as Text from phonenumber where user_Id=" + userId.toString() + " and contactId in ";
        String sQuery2 = this.getContactIdLikeString(contactsId);
        String sQuery = sQuery1 + sQuery2;

        Statement stmt = (Statement) conn.createStatement();

        try {
            ResultSet rs = stmt.executeQuery(sQuery);
            while (rs.next()) {
                phones.add(rs.getString("Text"));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
        } finally {
            stmt.close();
            return phones;
        }
    }

    /**
     * Insert into as_phonenumber a new phone
     * @param groupId
     * @param phone
     * @param conn
     * @throws SQLException
     */
    private void createPhoneNumber(Integer groupId, String phone, Set<Integer> contactsId, Integer userId) throws SQLException {
        try {
            String sContactIds = this.getContactIdLikeString(contactsId);

            PhoneNumber p = this.lookupPhoneNumberByPhoneAndUserId(phone, userId, sContactIds, DS_CONN);
            if (p.getContactId() != null) {
                this.insertAsPhoneNumber(p, groupId, AS_CONN);
            }

        } catch (SQLException ex) {
            System.err.append("createPhoneNumber: " + ex.getLocalizedMessage());
        } finally {
            return;
        }
    }

    private PhoneNumber lookupPhoneNumberByPhoneAndUserId(String phone, Integer userId, String sContactIds, Connection conn) throws SQLException {
        PhoneNumber p = new PhoneNumber();
        String sQuery = "select * from phonenumber where Text=? and user_Id=? and contactId in " + sContactIds + " limit 1";
        PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(sQuery);
        try {

            stmt.setString(1, phone);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                p.setPhoneID(rs.getInt("phoneID"));
                p.setContactId(rs.getInt("contactId"));
                p.setUserId(userId);
                p.setLabel(rs.getString("label"));
                p.setType(rs.getString("type"));
                p.setPrimary(rs.getInt("primary"));
                p.setText(rs.getString("Text"));
                p.setSource(rs.getString("source"));
                p.setInsertDate(rs.getInt("insertDate"));
                p.setAccountSrc(rs.getString("accountSrc"));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
        } finally {
            stmt.close();
            return p;
        }
    }

    private void insertAsPhoneNumber(PhoneNumber p, Integer groupId, Connection conn) throws SQLException {
        Statement stmt = (Statement) conn.createStatement();
        String query = "insert into as_phonenumber(phoneID,groupId,user_Id,label,`type`,`primary`,Text,source,insertDate) values(?,?,?,?,?,?,?,?,?)";
        //String query = "insert into as_phonenumber(phoneID,groupId,user_Id,label,`type`,`primary`,Text,source,insertDate,accountSrc) values(?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(query);
        String sQuery = "";
        try {

            pstmt.setInt(1, p.getPhoneID());
            pstmt.setInt(2, groupId);
            pstmt.setInt(3, p.getUserId());
            pstmt.setString(4, p.getLabel());
            pstmt.setString(5, p.getType());
            pstmt.setInt(6, p.getPrimary());
            pstmt.setString(7, p.getText());
            pstmt.setString(8, p.getSource());
            pstmt.setInt(9, p.getInsertDate());
            //pstmt.setString(10, p.getAccountSrc());

            pstmt.executeUpdate();

        } catch (SQLException ex) {
            //ex.getLocalizedMessage();
            //System.out.println("Query fail:" + sQuery);
        } finally {
            stmt.close();
            pstmt.close();
        }
    }

    /**
     * Create new postal addresses into AS
     * @param groupId
     * @param contactsId
     * @param conn
     */
    private void createAsPostalAddresses(Integer groupId, Set<Integer> contactsId, Integer userId) {
        try {
            List<String> postalAdresses = this.lookupDistinctPostalAddresses(contactsId, userId, DS_CONN);
            for (String postalAddress : postalAdresses) {
                this.createPostalAddress(groupId, postalAddress, contactsId, userId);
            }
        } catch (Exception ex) {
            System.err.println("createAsPostalAddresses: " + ex.getLocalizedMessage());
        }
    }

    /**
     * Lookup distinct postal address for a set of contacts ids
     * @param contactsId
     * @param conn
     * @return
     * @throws SQLException
     */
    private List<String> lookupDistinctPostalAddresses(Set<Integer> contactsId, Integer userId, Connection conn) throws SQLException {
        List<String> addresses = new ArrayList<String>();
        String sQuery1 = "select distinct(Text) as Text from postalAddress where user_Id=" + userId.toString() + " and contactId in ";
        String sQuery2 = this.getContactIdLikeString(contactsId);
        String sQuery = sQuery1 + sQuery2;

        Statement stmt = (Statement) conn.createStatement();

        try {
            ResultSet rs = stmt.executeQuery(sQuery);
            while (rs.next()) {
                addresses.add(rs.getString("Text"));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
        } finally {
            stmt.close();
            return addresses;
        }
    }

    /**
     * Insert a new postal address into as_postalAddress
     * @param groupId
     * @param postalAddress
     * @param conn
     * @throws SQLException
     */
    private void createPostalAddress(Integer groupId, String postalAddress, Set<Integer> contactsId, Integer userId) throws SQLException {
        try {
            String sContactIds = this.getContactIdLikeString(contactsId);

            PostalAddress pa = this.lookupPostalAddress(postalAddress, userId, sContactIds, DS_CONN);
            if (pa.getContactId() != null) {
                this.insertAsPostalAddress(pa, groupId, sContactIds, AS_CONN);
            }

        } catch (SQLException ex) {
            System.out.println("createPostalAddress: " + ex.getLocalizedMessage());
        } finally {
            return;
        }
    }

    private PostalAddress lookupPostalAddress(String postalAddress, Integer userId, String sContactIds, Connection conn) throws SQLException {
        PostalAddress pa = new PostalAddress();
        String sQuery = "select * from postalAddress where Text=? and user_Id=? and contactId in " + sContactIds + " limit 1";

        PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(sQuery);

        try {
            stmt.setString(1, postalAddress);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                pa.setPaID(rs.getInt("paID"));
                pa.setContactId(rs.getInt("contactId"));
                pa.setUserId(userId);
                pa.setLabel(rs.getString("label"));
                pa.setType(rs.getString("type"));
                pa.setPrimary(rs.getInt("primary"));
                pa.setText(rs.getString("Text"));
                pa.setSource(rs.getString("source"));
                pa.setInsertDate(rs.getInt("insertDate"));
                pa.setAccountSrc(rs.getString("accountSrc"));
            }


        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
        } finally {
            stmt.close();
            return pa;
        }

    }

    private void insertAsPostalAddress(PostalAddress pa, Integer groupId, String sContactIds, Connection conn) throws SQLException {
        String sQuery = "insert into as_postalAddress(paID,groupId,user_Id,label,`type`,`primary`,Text,source,insertDate) values (?,?,?,?,?,?,?,?,?)";
        //String sQuery = "insert into as_postalAddress(paID,groupId,user_Id,label,`type`,`primary`,Text,source,insertDate,accountSrc) values (?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(sQuery);

        try {
            stmt.setInt(1, pa.getPaID());
            stmt.setInt(2, groupId);
            stmt.setInt(3, pa.getUserId());
            stmt.setString(4, pa.getLabel());
            stmt.setString(5, pa.getType());
            stmt.setInt(6, pa.getPrimary());
            stmt.setString(7, pa.getText());
            stmt.setString(8, pa.getSource());
            stmt.setInt(9, pa.getInsertDate());
            //stmt.setString(10, pa.getAccountSrc());

            stmt.executeUpdate();


        } catch (SQLException ex) {
            ex.getLocalizedMessage();
            System.err.println("insertAsPostalAddress: " + ex.getLocalizedMessage());
        } finally {
            stmt.close();
        }
    }

    /**
     * Retrieve child id for a set of contacts ids
     * @param contactsId
     * @param conn
     * @return
     * @throws SQLException
     */
    private List<Integer> retrieveChildIds(Set<Integer> contactsId, Integer userId, Connection conn) throws SQLException {
        List<Integer> childIds = new ArrayList<Integer>();
        Statement stmt = (Statement) conn.createStatement();
        String sQuery1 = "select distinct(child_id) from as_parents where user_Id=" + userId + " and parent_id in ";
        String sQuery2 = this.getContactIdLikeString(contactsId);
        String sQuery = sQuery1 + sQuery2;

        try {
            ResultSet rs = stmt.executeQuery(sQuery);

            while (rs.next()) {
                childIds.add(rs.getInt("child_id"));
            }

            return childIds;
        } catch (SQLException ex) {
            //      ex.getLocalizedMessage();
            System.out.println("Query fail:" + sQuery);
            return null;
        }
    }

    /**
     * Clean AS for a child-id
     * @param childIds
     * @param conn
     */
    private void clearOldChildIds(List<Integer> childIds, Connection conn) {
        try {
            for (Integer c : childIds) {
                this.deleteAsContacts(c, conn);
                this.deleteAsParent(c, conn);
                this.deleteAsEmail(c, conn);
                this.deleteAsNames(c, conn);
                this.deleteAsPhoneNumber(c, conn);
                this.deleteAsPostalAddress(c, conn);

            }
        } catch (Exception ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }

    private void deleteAsContacts(Integer c, Connection conn) throws SQLException {
        Statement stmt = (Statement) conn.createStatement();

        try {
            String sQuery = "delete from as_contacts where contactId=" + c.toString() + ";";
            stmt.executeUpdate(sQuery);
        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
        } finally {
            stmt.close();
        }
    }

    /**
     * Delete as_parents for child id
     * @param c
     * @param conn
     * @throws SQLException
     */
    private void deleteAsParent(Integer c, Connection conn) throws SQLException {
        Statement stmt = (Statement) conn.createStatement();

        try {
            String sQuery = "delete from as_parents where child_id=" + c.toString() + ";";
            stmt.executeUpdate(sQuery);
        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
        } finally {
            stmt.close();
        }
    }

    /**
     * Delete as_email for child id
     * @param c
     * @param conn
     * @throws SQLException
     */
    private void deleteAsEmail(Integer c, Connection conn) throws SQLException {
        Statement stmt = (Statement) conn.createStatement();

        try {
            String sQuery = "delete from as_email where contactId=" + c.toString() + ";";
            stmt.executeUpdate(sQuery);
        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
        } finally {
            stmt.close();
        }
    }

    /**
     * Delete as_names for child id
     * @param c
     * @param conn
     * @throws SQLException
     */
    private void deleteAsNames(Integer c, Connection conn) throws SQLException {
        Statement stmt = (Statement) conn.createStatement();

        try {
            String sQuery = "delete from as_names where contactId=" + c.toString() + ";";
            stmt.executeUpdate(sQuery);
        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
        } finally {
            stmt.close();
        }
    }

    /**
     * Delete as_phonenumber for child id
     * @param c
     * @param conn
     * @throws SQLException
     */
    private void deleteAsPhoneNumber(Integer c, Connection conn) throws SQLException {
        Statement stmt = (Statement) conn.createStatement();

        try {
            String sQuery = "delete from as_phonenumber where contactId=" + c.toString() + ";";
            stmt.executeUpdate(sQuery);
        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
        } finally {
            stmt.close();
        }
    }

    /**
     * Delete as_postalAddress for child id
     * @param c
     * @param conn
     * @throws SQLException
     */
    private void deleteAsPostalAddress(Integer c, Connection conn) throws SQLException {
        Statement stmt = (Statement) conn.createStatement();

        try {
            String sQuery = "delete from as_postalAddress where contactId=" + c.toString() + ";";
            stmt.executeUpdate(sQuery);
        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
        } finally {
            stmt.close();
        }
    }

    private Integer lookupUserIdByContactId(Integer id, Connection conn) throws SQLException {
        Integer userId = null;
        Statement stmt = (Statement) conn.createStatement();

        try {
            String sQuery = "select user_Id from contacts where contactId=" + id;
            ResultSet rs = stmt.executeQuery(sQuery);
            while (rs.next()) {
                userId = rs.getInt("user_Id");
            }
        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
            return null;
        } finally {
            stmt.close();
            return userId;
        }
    }

    private void cleanAsPhoneNumber(Integer userId) {
        try {
            this.cleanPhoneNumber(userId, AS_CONN);
        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }

    private void cleanPhoneNumber(Integer userId, Connection conn) throws SQLException {
        Statement stmt = (Statement) conn.createStatement();
        try {
            String sQuery = "update as_phonenumber set Text=replace(replace(Text,' ',''),'-','') where user_Id=" + userId.toString();
            stmt.executeUpdate(sQuery);
        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }

    /**
     * Contact unification post de-duping by phone and name between gmail contacts
     * @param userId
     * @param conn 
     */
    public void executeUnificationBetweenGmail(Integer userId, Connection conn) {
        Map<Integer, Set<Integer>> contacts = new TreeMap<Integer, Set<Integer>>();
        try {
            contacts = this.lookupContactsEqualPhoneNumber(userId, conn);
            // para conjunto
            for (Map.Entry<Integer, Set<Integer>> entry : contacts.entrySet()) {
                this.unificateContactsBetweenGmail(entry.getKey(), entry.getValue(), conn);


            }

        } catch (SQLException ex) {
            Logger.getLogger(DedupingController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Map<Integer, Set<Integer>> lookupContactsEqualPhoneNumber(Integer userId, Connection conn) throws SQLException {
        Map<Integer, Set<Integer>> contacts = new TreeMap<Integer, Set<Integer>>();
        Statement stmt = (Statement) conn.createStatement();

        try {
            String sQuery = "select "
                    + "p1.contactId as ct1, "
                    + "p2.contactId as ct2 "
                    + "from phonenumber p1 "
                    + "inner join phonenumber p2 "
                    + "on (p1.user_Id=p2.user_Id "
                    + "and p1.contactId<>p2.contactId "
                    + "and substring(replace(replace(p1.Text,' ',''),'-',''),-7)=substring(replace(replace(p2.Text,' ',''),'-',''),-7) "
                    + "and p1.phoneID<>p2.phoneID "
                    + "and p1.source=p2.source) "
                    + "where p1.user_Id= " + userId.toString() + " "
                    + "and p1.source='gmail' "
                    + "order by p1.contactId;";

            ResultSet rs = stmt.executeQuery(sQuery);
            Integer key_temp = -1;
            while (rs.next()) {
                Integer key = rs.getInt("ct1");
                Integer cid = rs.getInt("ct2");

                // No existen Set para esta key
                if (!key_temp.equals(key)) {
                    Set<Integer> lcid = new HashSet<Integer>();
                    lcid.add(cid);
                    contacts.put(key, lcid);
                    key_temp = key;
                } // ya existe set para esa key
                else {
                    Set<Integer> lcid = contacts.get(key);
                    lcid.add(cid);
                    contacts.put(key, lcid);
                    key_temp = key;
                }
            }
        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
        } finally {
            stmt.close();
            return contacts;
        }

    }

    /**
     * unifica contactos según criterio de phonenumber completo y nombre con match exacto
     * @param ct1
     * @param lcid
     * @param conn
     * @throws SQLException 
     */
    private void unificateContactsBetweenGmail(Integer ct1, Set<Integer> lcid, Connection conn) throws SQLException {

        String name_ct1 = this.lookupTrimFullNameByContactId(ct1, conn);
        for (Integer ct2 : lcid) {
            String name_ct2 = lookupTrimFullNameByContactId(ct2, conn);
            if (name_ct1 != null && name_ct2 != null) {
                if (name_ct1.equals(name_ct2)) {
                    Integer child_ct1 = this.lookupChildIdByParentId(ct1, conn);
                    Integer child_ct2 = this.lookupChildIdByParentId(ct2, conn);

                    if (child_ct1 != null && child_ct2 != null) {
                        if (!child_ct1.equals(child_ct2)) {
                            int menor_child_id = Math.min(child_ct1.intValue(), child_ct2.intValue());
                            int mayor_chil_id = Math.max(child_ct2.intValue(), child_ct2.intValue());
                            this.unificationUpdateAsParent(new Integer(menor_child_id), new Integer(mayor_chil_id), conn);
                        }
                    }
                }
            }
        }
    }

    private String lookupTrimFullNameByContactId(Integer c1, Connection conn) throws SQLException {
        Statement stmt = (Statement) conn.createStatement();
        String fullname = null;
        try {
            String sQuery = "select trim(fullName) as fullname from names where contactId=" + c1.toString();
            ResultSet rs = stmt.executeQuery(sQuery);
            while (rs.next()) {
                fullname = rs.getString("fullname");
            }
        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
        } finally {
            stmt.close();
            return fullname;
        }

    }

    private Integer lookupChildIdByParentId(Integer parenttId, Connection conn) throws SQLException {
        Statement stmt = (Statement) conn.createStatement();
        Integer child_id = null;
        try {
            String sQuery = "select child_id from as_parents where parent_id=" + parenttId.toString();
            ResultSet rs = stmt.executeQuery(sQuery);
            while (rs.next()) {
                child_id = rs.getInt("child_id");
            }
        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
        } finally {
            stmt.close();
            return child_id;
        }
    }

    private void unificationUpdateAsParent(Integer min, Integer max, Connection conn) throws SQLException {
        Statement stmt = (Statement) conn.createStatement();

        try {
            //UNIFICAMOS
            String sQuery = "update as_parents set child_id=" + min.toString() + " where child_id=" + max.toString();
            stmt.executeUpdate(sQuery);
            //BORRAMOS EL CONTACTO ANTIGUO DE AS
            String sQuery2 = "delete from as_contacts where contactId=" + max.toString();
            stmt.executeUpdate(sQuery2);

        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
        } finally {
            stmt.close();
            return;
        }
    }

    public void dedupingSingleContacts(Integer userId) throws SQLException {
        // Se obtienen los contactos no desduplicados 
        Set<Integer> singleContacts = this.lookupSingleContacts(userId, DS_CONN);

        Set<Integer> contactsId = new HashSet<Integer>();
        for (Integer c : singleContacts) {
            contactsId.add(c);
            this.populateActiveStorage(contactsId, userId, AS_CONN);
            contactsId.remove(c);
        }
    }

    private Set<Integer> lookupSingleContacts(Integer userId, Connection conn) throws SQLException {
        Set<Integer> singleContacts = new HashSet<Integer>();

        Statement stmt = (Statement) conn.createStatement();
        try {
            String sQuery = "SELECT distinct(contactId) FROM contacts WHERE contactId NOT IN (SELECT distinct(parent_id) FROM as_parents WHERE user_Id=" + userId.toString() + ") AND user_Id=" + userId.toString() + ";";
            ResultSet rs = stmt.executeQuery(sQuery);

            while (rs.next()) {
                singleContacts.add(rs.getInt("contactId"));

            }

        } catch (SQLException ex) {
            //ex.printStackTrace();
        } finally {
            stmt.close();
            return singleContacts;
        }
    }

    private void createAsOrganization(Integer groupId, Set<Integer> contactsId, Integer userId) {
        try {
            List<String> organizations = this.lookupDistinctOrganization(contactsId, userId, DS_CONN);
            for (String o : organizations) {
                this.createOrganization(groupId, o, contactsId, userId);
            }
        } catch (Exception ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }

    private List<String> lookupDistinctOrganization(Set<Integer> contactsId, Integer userId, Connection conn) throws SQLException {
        List<String> organizations = new ArrayList<String>();
        String sQuery1 = "select distinct(orgName) as orgName from organization where user_Id=" + userId.toString() + " and contactId in ";
        String sQuery2 = this.getContactIdLikeString(contactsId);
        String sQuery = sQuery1 + sQuery2;

        Statement stmt = (Statement) conn.createStatement();

        try {
            ResultSet rs = stmt.executeQuery(sQuery);
            while (rs.next()) {
                organizations.add(rs.getString("orgName"));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
        } finally {
            stmt.close();
            return organizations;
        }
    }

    private void createOrganization(Integer groupId, String organization, Set<Integer> contactsId, Integer userId) throws SQLException {
        try {
            String sContactIds = this.getContactIdLikeString(contactsId);

            Organization o = this.lookupOrganization(organization, userId, sContactIds, DS_CONN);
            if (o.getContactId() != null) {
                this.insertAsOrganization(o, groupId, AS_CONN);
            }
        } catch (SQLException ex) {
            System.err.println("createOrganization: " + ex.getLocalizedMessage());
        } finally {
            return;
        }
    }

    private Organization lookupOrganization(String organization, Integer userId, String sContactIds, Connection conn) throws SQLException {
        Organization o = new Organization();
        o.setStartDate("0000-00-00");

        String sQuery = "select * from organization where orgName=? and user_Id=? and contactId in " + sContactIds + " limit 1";
        PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(sQuery);

        try {
            stmt.setString(1, organization);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                o.setOrgID(rs.getInt("orgID"));
                o.setUserId(rs.getInt("user_Id"));
                o.setContactId(rs.getInt("contactId"));
                o.setLabel(rs.getString("label"));
                o.setType(rs.getString("type"));
                o.setPrimary(rs.getInt("primary"));
                o.setOrgName(rs.getString("orgName"));
                o.setDepartment(rs.getString("department"));
                o.setOrgTitle(rs.getString("orgTitle"));
                o.setLocation(rs.getString("location"));
                o.setDescription(rs.getString("description"));
                o.setIndustry(rs.getString("industry"));
                o.setSource(rs.getString("source"));
                o.setInsertDate(rs.getInt("insertDate"));
                o.setStartDate(rs.getString("startDate"));
                o.setEndDate(rs.getString("endDate"));
                o.setJobStatus(rs.getString("jobStatus"));
                o.setAccountSrc(rs.getString("accountSrc"));
            }
        } catch (SQLException ex) {
            //ex.getLocalizedMessage();
            //System.out.println("Qury fail: "+sQuery);
        } finally {
            stmt.close();
            return o;
        }

    }

    private void insertAsOrganization(Organization o, Integer groupId, Connection conn) throws SQLException {
        String sQuery = "insert into as_organization(orgID,user_Id,groupId,label,`type`,`primary`,orgName,department,orgTitle,startDate,location,`description`,industry,source,insertDate,endDate,jobStatus)"
                + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        //String sQuery = "insert into as_organization(orgID,user_Id,groupId,label,`type`,`primary`,orgName,department,orgTitle,startDate,location,`description`,industry,source,insertDate,accountSrc)"
        //        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(sQuery);

        try {

            stmt.setInt(1, o.getOrgID());
            stmt.setInt(2, o.getUserId());
            stmt.setInt(3, groupId);
            stmt.setString(4, o.getLabel());
            stmt.setString(5, o.getType());
            stmt.setInt(6, o.getPrimary());
            stmt.setString(7, o.getOrgName());
            stmt.setString(8, o.getDepartment());
            stmt.setString(9, o.getOrgTitle());
            stmt.setString(10, o.getStartDate());
            stmt.setString(11, o.getLocation());
            stmt.setString(12, o.getDescription());
            stmt.setString(13, o.getIndustry());
            stmt.setString(14, o.getSource());
            stmt.setInt(15, o.getInsertDate());
            stmt.setString(16, o.getEndDate());
            stmt.setString(17, o.getJobStatus());

            //stmt.setString(16,o.getAccountSrc());

            stmt.executeUpdate();

        } catch (SQLException ex) {
            //System.err.println("insertAsOrganization:" + ex.getLocalizedMessage());
        } finally {
            stmt.close();
        }
    }

    private void createAsIm(Integer groupId, Set<Integer> contactsId, Integer userId) {
        try {
            List<String> ims = this.lookupDistinctIm(contactsId, userId, DS_CONN);
            for (String i : ims) {
                this.createIm(groupId, i, contactsId, userId);
            }
        } catch (Exception ex) {
            System.err.println("createAsIm: " + ex.getLocalizedMessage());
        }
    }

    private List<String> lookupDistinctIm(Set<Integer> contactsId, Integer userId, Connection conn) throws SQLException {
        List<String> ims = new ArrayList<String>();
        String sQuery1 = "select distinct(address) as address from im where user_Id=" + userId.toString() + " and contactId in ";
        String sQuery2 = this.getContactIdLikeString(contactsId);
        String sQuery = sQuery1 + sQuery2;

        Statement stmt = (Statement) conn.createStatement();

        try {
            ResultSet rs = stmt.executeQuery(sQuery);
            while (rs.next()) {
                ims.add(rs.getString("address"));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
        } finally {
            stmt.close();
            return ims;
        }
    }

    private void createIm(Integer groupId, String im_address, Set<Integer> contactsId, Integer userId) throws SQLException {
        try {
            String sContactIds = this.getContactIdLikeString(contactsId);

            Im im = this.lookupIm(im_address, userId, sContactIds, DS_CONN);
            if (im.getContactId() != null) {
                this.insertAsIm(im, groupId, AS_CONN);
            }
        } catch (SQLException ex) {
            System.err.println("createIm: " + ex.getLocalizedMessage());
        } finally {
            return;
        }
    }

    private Im lookupIm(String im_addres, Integer userId, String sContactIds, Connection conn) throws SQLException {
        Im im = new Im();
        String sQuery = "select * from im where address=? and user_Id=? and contactId in " + sContactIds + " limit 1";
        PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(sQuery);


        try {
            stmt.setString(1, im_addres);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                im.setImID(rs.getInt("imID"));
                im.setUserId(userId);
                im.setContactId(rs.getInt("contactId"));
                im.setAddress(rs.getString("address"));
                im.setLabel(rs.getString("label"));
                im.setType(rs.getString("type"));
                im.setProtocol(rs.getString("protocol"));
                im.setPrimary(rs.getInt("primary"));
                im.setSource(rs.getString("source"));
                im.setInsertDate(rs.getInt("insertDate"));
                im.setAccountSrc(rs.getString("accountSrc"));
            }
        } catch (SQLException ex) {
            //      ex.getLocalizedMessage();
        } finally {
            stmt.close();
            return im;
        }

    }

    private void insertAsIm(Im im, Integer groupId, Connection conn) throws SQLException {
        String sQuery = "insert into as_im (imID,groupId,user_Id,address,label,`type`,protocol,`primary`,source,insertDate) values(?,?,?,?,?,?,?,?,?,?)";
        //String sQuery = "insert into as_im (imID,groupId,user_Id,address,label,`type`,protocol,`primary`,source,insertDate,accountSrc) values(?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(sQuery);

        try {
            pstmt.setInt(1, im.getImID());
            pstmt.setInt(2, groupId);
            pstmt.setInt(3, im.getUserId());
            pstmt.setString(4, im.getAddress());
            pstmt.setString(5, im.getLabel());
            pstmt.setString(6, im.getType());
            pstmt.setString(7, im.getProtocol());
            pstmt.setInt(8, im.getPrimary());
            pstmt.setString(9, im.getSource());
            pstmt.setInt(10, im.getInsertDate());
            //pstmt.setString(11, im.getAccountSrc());

            pstmt.executeUpdate();

        } catch (SQLException ex) {
            System.out.println("insertAsIm: " + ex.getLocalizedMessage());
        } finally {
            pstmt.close();
        }
    }

    private void createAsExtendedProperty(Integer groupId, Set<Integer> contactsId, Integer userId) {
        try {
            List<String> exps = this.lookupDistinctExtendedProperty(contactsId, userId, DS_CONN);
            for (String e : exps) {
                this.createExtendedProperty(groupId, e, contactsId, userId);
            }
        } catch (Exception ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }

    private List<String> lookupDistinctExtendedProperty(Set<Integer> contactsId, Integer userId, Connection conn) throws SQLException {
        List<String> exps = new ArrayList<String>();
        String sQuery1 = "select distinct(name) as name from extendedProperty where user_Id=" + userId.toString() + " and contactId in ";
        String sQuery2 = this.getContactIdLikeString(contactsId);
        String sQuery = sQuery1 + sQuery2;

        Statement stmt = (Statement) conn.createStatement();

        try {
            ResultSet rs = stmt.executeQuery(sQuery);
            while (rs.next()) {
                exps.add(rs.getString("name"));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
        } finally {
            stmt.close();
            return exps;
        }
    }

    private void createExtendedProperty(Integer groupId, String name, Set<Integer> contactsId, Integer userId) throws SQLException {
        try {
            String sContactIds = this.getContactIdLikeString(contactsId);

            ExtendedProperty ep = this.lookupExtendedProperty(name, userId, sContactIds, DS_CONN);
            if (ep.getContactId() != null) {
                this.insertAsExtendedProperty(ep, groupId, AS_CONN);
            }
        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
        } finally {
        }
    }

    private ExtendedProperty lookupExtendedProperty(String name, Integer userId, String sContactIds, Connection conn) throws SQLException {
        ExtendedProperty ep = new ExtendedProperty();
        String sQuery = "select * from extendedProperty where name=? and user_Id=? and contactId in " + sContactIds + " limit 1";
        PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(sQuery);


        try {

            stmt.setString(1, name);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ep.setEpID(rs.getInt("epID"));
                ep.setUserId(userId);
                ep.setContactId(rs.getInt("contactId"));
                ep.setName(rs.getString("name"));
                ep.setValue(rs.getString("value"));
                ep.setSource(rs.getString("source"));
                ep.setInsertDate(rs.getInt("insertDate"));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
        } finally {
            stmt.close();
            return ep;
        }

    }

    private void insertAsExtendedProperty(ExtendedProperty ep, Integer groupId, Connection conn) throws SQLException {
        String query = "insert into as_extendedProperty(epID,groupId,user_Id,name,value,source,insertDate) values (?,?,?,?,?,?,?)";
        PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(query);

        try {

            pstmt.setInt(1, ep.getEpID());
            pstmt.setInt(2, groupId);
            pstmt.setInt(3, ep.getUserId());
            pstmt.setString(4, ep.getName());
            pstmt.setString(5, ep.getValue());
            pstmt.setString(6, ep.getSource());
            pstmt.setInt(7, ep.getInsertDate());

            pstmt.executeUpdate();


        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
        } finally {
            pstmt.close();
        }
    }

    private void createAsEducation(Integer groupId, Set<Integer> contactsId, Integer userId) {
        try {
            List<String> education = this.lookupDistinctEducation(contactsId, userId, DS_CONN);
            for (String e : education) {
                this.createEducation(groupId, e, contactsId, userId);
            }
        } catch (Exception ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }

    private List<String> lookupDistinctEducation(Set<Integer> contactsId, Integer userId, Connection conn) throws SQLException {
        List<String> educations = new ArrayList<String>();
        String sQuery1 = "select distinct(schoolName) as schoolName from education where user_Id=" + userId.toString() + " and contactId in ";
        String sQuery2 = this.getContactIdLikeString(contactsId);
        String sQuery = sQuery1 + sQuery2;

        Statement stmt = (Statement) conn.createStatement();

        try {
            ResultSet rs = stmt.executeQuery(sQuery);
            while (rs.next()) {
                educations.add(rs.getString("schoolName"));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
        } finally {
            stmt.close();
            return educations;
        }
    }

    private void createEducation(Integer groupId, String education, Set<Integer> contactsId, Integer userId) {
        try {
            String sContactIds = this.getContactIdLikeString(contactsId);

            Education e = this.lookupEducation(education, userId, sContactIds, DS_CONN);
            if (e.getContactId() != null) {
                this.insertAsEducation(e, groupId, AS_CONN);
            }
        } catch (SQLException ex) {
            System.err.println("createEducation: " + ex.getLocalizedMessage());
        } finally {
            return;
        }
    }

    private Education lookupEducation(String education, Integer userId, String sContactIds, Connection conn) throws SQLException {
        Education e = new Education();


        String sQuery = "select * from education where schoolName=? and user_Id=? and contactId in " + sContactIds + " limit 1";
        PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(sQuery);

        try {
            stmt.setString(1, education);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                e.setEduId(rs.getInt("eduId"));
                e.setUserId(rs.getInt("user_Id"));
                e.setAccountSrc(rs.getString("accountSrc"));
                e.setContactId(rs.getInt("contactId"));
                e.setSchoolName(rs.getString("schoolName"));
                e.setDegree(rs.getString("degree"));
                e.setFieldOfStudy(rs.getString("fieldOfStudy"));
                e.setStartYear(rs.getInt("startYear"));
                e.setEndYear(rs.getInt("endYear"));
                e.setSrcId(rs.getInt("srcId"));
                e.setSource(rs.getString("source"));

            }
        } catch (SQLException ex) {
            //ex.getLocalizedMessage();
            //System.out.println("Qury fail: "+sQuery);
        } finally {
            stmt.close();
            return e;
        }
    }

    private void insertAsEducation(Education e, Integer groupId, Connection conn) throws SQLException {
        String sQuery = "insert into as_education(eduId,user_Id,groupId,schoolName,degree,fieldOfStudy,startYear,endYear,srcId,source)"
                + " values (?,?,?,?,?,?,?,?,?,?)";
        //String sQuery = "insert into as_education(eduId,user_Id,groupId,schoolName,degree,fieldOfStudy,startYear,endYear,srcId,source,accountSrc)"
        //        + " values (?,?,?,?,?,?,?,?,?,?,?)";

        PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(sQuery);

        try {

            stmt.setInt(1, e.getEduId());
            stmt.setInt(2, e.getUserId());
            stmt.setInt(3, groupId);
            stmt.setString(4, e.getSchoolName());
            stmt.setString(5, e.getDegree());
            stmt.setString(6, e.getFieldOfStudy());
            stmt.setInt(7, e.getStartYear());
            stmt.setInt(8, e.getEndYear());
            stmt.setInt(9, e.getSrcId());
            stmt.setString(10, e.getSource());
            //stmt.setString(11, e.getAccountSrc());

            stmt.executeUpdate();

        } catch (SQLException ex) {
            //System.err.println("insertAsEducation:" + ex.getLocalizedMessage());
        } finally {
            stmt.close();
        }
    }

    private void createAsDemographics(Integer groupId, Set<Integer> contactsId, Integer userId) {
        try {
            List<String> demographics = this.lookupDistinctDemographics(contactsId, userId, DS_CONN);
            for (String d : demographics) {
                this.createDemographics(groupId, d, contactsId, userId);
            }
        } catch (Exception ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }

    private List<String> lookupDistinctDemographics(Set<Integer> contactsId, Integer userId, Connection conn) throws SQLException {
        List<String> educations = new ArrayList<String>();
        String sQuery1 = "select distinct(hometown) as hometown from demographics where user_Id=" + userId.toString() + " and contactId in ";
        String sQuery2 = this.getContactIdLikeString(contactsId);
        String sQuery = sQuery1 + sQuery2;

        Statement stmt = (Statement) conn.createStatement();

        try {
            ResultSet rs = stmt.executeQuery(sQuery);
            while (rs.next()) {
                educations.add(rs.getString("hometown"));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
        } finally {
            stmt.close();
            return educations;
        }
    }

    private void createDemographics(Integer groupId, String demographic, Set<Integer> contactsId, Integer userId) throws SQLException {
        try {
            String sContactIds = this.getContactIdLikeString(contactsId);

            Demographics d = this.lookupDemographic(demographic, userId, sContactIds, DS_CONN);
            if (d.getContactId() != null) {
                this.insertAsDemographic(d, groupId, AS_CONN);
            }
        } catch (SQLException ex) {
            //System.err.println("createDemographics: " + ex.getLocalizedMessage());
        } finally {
            return;
        }
    }

    private Demographics lookupDemographic(String demographic, Integer userId, String sContactIds, Connection conn) throws SQLException {
        Demographics d = new Demographics();

        String sQuery = "select * from demographics where hometown=? and user_Id=? and contactId in " + sContactIds + " limit 1";
        PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(sQuery);

        try {
            stmt.setString(1, demographic);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                d.setDemoId(rs.getInt("demoId"));
                d.setUserId(rs.getInt("user_Id"));
                d.setAccountSrc(rs.getString("accountSrc"));
                d.setContactId(rs.getInt("contactId"));
                d.setHometown(rs.getString("hometown"));
                d.setLocation(rs.getString("location"));
                d.setGender(rs.getString("gender"));
                d.setWebsite(rs.getString("website"));
                d.setSource(rs.getString("source"));

            }
        } catch (SQLException ex) {
            //ex.getLocalizedMessage();
            //System.out.println("Qury fail: "+sQuery);
        } finally {
            stmt.close();
            return d;
        }
    }

    private void insertAsDemographic(Demographics d, Integer groupId, Connection conn) throws SQLException {
        String sQuery = "insert into as_demographics(demoId,user_Id,groupId,hometown,location,gender,website,source)"
                + " values (?,?,?,?,?,?,?,?)";

        PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(sQuery);

        try {

            stmt.setInt(1, d.getDemoId());
            stmt.setInt(2, d.getUserId());
            stmt.setInt(3, groupId);
            stmt.setString(4, d.getHometown());
            stmt.setString(5, d.getLocation());
            stmt.setString(6, d.getGender());
            stmt.setString(7, d.getWebsite());
            stmt.setString(8, d.getSource());


            stmt.executeUpdate();

        } catch (SQLException ex) {
            //System.err.println("insertAsDemographic:" + ex.getLocalizedMessage());
        } finally {
            stmt.close();
        }
    }

    private Set<Integer> lookupGroupedContacts(Integer userId) {
        Set<Integer> new_contacts = new TreeSet<Integer>();
        //String sQuery = "select contactId from contacts where user_Id=? and status=?";
        String sQuery = "select contactId from contacts where user_Id=? and (status=? or status=?)";
        try {
            PreparedStatement pstmt = (PreparedStatement) DS_CONN.prepareStatement(sQuery);
            pstmt.setInt(1, userId);
            //pstmt.setString(2, GROUPED);
            pstmt.setString(2, NEW);
            pstmt.setString(3, UPDATED);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                new_contacts.add(rs.getInt("contactId"));
            }
            pstmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            return new_contacts;
        }
    }

    private Set<Integer> lookupGroupsByGroupedContacts(Set<Integer> contactos) {
        Set<Integer> grupos = new TreeSet<Integer>();
        String sQuery = "select groupId from as_groups where contactId=?";
        try {
            PreparedStatement pstmt = (PreparedStatement) AS_CONN.prepareStatement(sQuery);

            for (Integer c : contactos) {
                pstmt.setInt(1, c);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    grupos.add(rs.getInt("groupId"));
                }
            }

            pstmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            return grupos;
        }
    }

    private void deleteOldGroup(Integer groupId) {

        try {
            Statement stmt = (Statement) AS_CONN.createStatement();
            stmt.executeUpdate(DELETE_AS_CONTACTS_BY_GROUP + groupId.toString());
            stmt.executeUpdate(DELETE_AS_EMAIL_BY_GROUP + groupId.toString());
            stmt.executeUpdate(DELETE_AS_IM_BY_GROUP + groupId.toString());
            stmt.executeUpdate(DELETE_AS_NAMES_BY_GROUP + groupId.toString());
            stmt.executeUpdate(DELETE_AS_ORGANIZATION_BY_GROUP + groupId.toString());
            stmt.executeUpdate(DELETE_AS_PHONENUMBER_BY_GROUP + groupId.toString());
            stmt.executeUpdate(DELETE_AS_POSTALADDRESS_BY_GROUP + groupId.toString());
            stmt.executeUpdate(DELETE_AS_EXTENDED_PROPERTY_BY_GROUP + groupId.toString());
            stmt.executeUpdate(DELETE_AS_EDUCATION_BY_GROUP + groupId.toString());
            stmt.executeUpdate(DELETE_AS_DEMOGRAPHICS_BY_GROUP + groupId.toString());
            stmt.close();
        } catch (SQLException ex) {
            System.err.println("deleteOldGroup: " + ex.getLocalizedMessage());
        } finally {
        }
    }

    private void updateGroupedContacts(Set<Integer> contactos) {

        String sQuery = "update contacts set status='"+DEDUBED+"' where contactId=?";
        
        try {
            PreparedStatement pstmt = (PreparedStatement) DS_CONN.prepareStatement(sQuery);
            for (Integer c : contactos) {
                pstmt.setInt(1, c);
                pstmt.executeUpdate();
            }

            pstmt.close();
        } catch (SQLException ex) {
            System.out.println("updateNewContacts: " + ex.getLocalizedMessage());
        }
    }
}
