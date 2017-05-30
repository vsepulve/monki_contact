package obs.comm;

import java.sql.*;
import java.sql.SQLException;
import com.mysql.jdbc.Driver;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.Collection;

public class Communication{

	private Connection conexion = null;

	private PreparedStatement select_fullNames_group;
	private PreparedStatement select_emails_group;
	private PreparedStatement select_phones_group;
	private PreparedStatement select_orgs_group;
	private PreparedStatement select_users_group;

	private PreparedStatement select_group_contact;
	private PreparedStatement select_user_groups;
	private PreparedStatement update_as_group;
	private PreparedStatement update_exact_groups;

	private PreparedStatement select_contact_name;
	private PreparedStatement select_contact_mail;
	
	private PreparedStatement update_status_group;
	
	private PreparedStatement select_src_ids;
	private PreparedStatement select_contacts_group;
	private PreparedStatement insert_bad_groups_index;
	private PreparedStatement insert_bad_groups;
	private PreparedStatement select_bad_groups_index;
	private PreparedStatement select_bad_groups;
	
	private PreparedStatement insert_log;
	
	private String server;
	private String bd;
	private String user;
	private String pass;
	
	public Communication(String _server, String _bd, String _user, String _pass){
		
		server = _server;
		bd = _bd;
		user = _user;
		pass = _pass;
		
		//System.out.println("Communication - inicio (cargando desde "+server+"/"+bd+")");
		
		if(!connect(server, bd, user, pass)){
			System.out.println("Communication - Terminado por problemas al conectar");
			return;
		}
		setQueries();
	
	}
	
	public void close(){
		try{
			conexion.close();
		}
		catch(Exception e){
			System.out.println("Communication.close - Error al cerrar");
			e.printStackTrace();
			return;
		}
	}
	
	public boolean open(){
		if(!connect(server, bd, user, pass)){
			System.out.println("Communication.open - Error al conectar");
			return false;
		}
		setQueries();
		return true;
	}
	
	private boolean connect(String server, String bd, String user, String pass){
		
		try{
			Class.forName("com.mysql.jdbc.Driver");
			conexion = DriverManager.getConnection ("jdbc:mysql://"+server+"/"+bd, user, pass);
		}
		catch(Exception e){
			System.out.println("Communication.connect - Incapaz de Conectar (user: \""+user+"\", pass: \""+pass+"\")");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean stop(){
		String sql;
		int res = 0;
		try{
			Statement s = conexion.createStatement();
			sql = "select stop from data_control where module=\"ProcessingCell\"";
			ResultSet r = s.executeQuery(sql);
			if(r.next()){
				res = r.getInt(1);
			}
		}
		catch(SQLException e){
			System.out.println("Communication.stop - Error al consultar ("+e+")");
			e.printStackTrace();
		}
		
		return (res==1);
		
	}

    public void writeLog(int old_group_id, int new_group_id, int contact_id, int module_id, String message){
    	//write db
		try{
			insert_log.setInt(1, old_group_id);
			insert_log.setInt(2, new_group_id);
			insert_log.setInt(3, contact_id);
			insert_log.setInt(4, module_id);
			if(message.length()>100){
				message = message.substring(0, 100);
			}
			insert_log.setString(5, message);
			insert_log.executeUpdate();
		}
		catch(Exception e){
			System.out.println("GroupingLogger.writeLog - Error in insert log");
			e.printStackTrace();
		}
    }
    
	public Set<Integer> getProcessingContacts(int num){
		Set<Integer> processing_contacts = new TreeSet<Integer>();
		String sql;

		try{
			Statement s = conexion.createStatement();
//			sql = "select contacts.contactId from contacts, as_groups where status=\"processing\" and contacts.contactid = as_groups.contactid and as_groups.groupid != -1 order by rand() limit "+num;
			sql = "select contactId from contacts where status=\"processing\" order by rand() limit "+num;
			ResultSet r = s.executeQuery(sql);
			while(r.next()){
				processing_contacts.add(r.getInt(1));
			}

		}
		catch(SQLException e){
			System.out.println("Communication.getProcessingContacts - Error al consultar ("+e+")");
			e.printStackTrace();
		}
		
		return processing_contacts;	

	}

	public Set<Integer> getProcessedContacts(int limit){
		Set<Integer> processed = new TreeSet<Integer>();
		try{
			Statement s = conexion.createStatement();
//			ResultSet r = s.executeQuery("select contacts.contactId from contacts, as_groups where status=\"grouped\" and contacts.contactid = as_groups.contactid and as_groups.groupid != -1 order by rand() limit "+limit);
			ResultSet r = s.executeQuery("select contactId from contacts where status=\"grouped\" order by rand() limit "+limit);

			while(r.next()){
				processed.add(r.getInt(1));
			}
			s.close();
		}
		catch(SQLException e){
			System.out.println("Communication.getProcessedContacts - Error al consultar ("+e+")");
			e.printStackTrace();
		}
		return processed;
	}

	public int getNumGroups(){
		int num_groups = -1;
		String sql;
		try{
			Statement s = conexion.createStatement();
			sql = "select count(distinct groupId) from as_groups";
			ResultSet r = s.executeQuery(sql);
			if(r.next()){
				num_groups = r.getInt(1);
			}

		}
		catch(SQLException e){
			System.out.println("Communication.getNumGroups - Error al consultar ("+e+")");
			e.printStackTrace();
		}
		return num_groups;	
	}

	public int getNumProcessing(){
		int num_processing = -1;
		String sql;
		try{
			Statement s = conexion.createStatement();
			sql = "select count(1) from contacts where status= \"processing\"";
			ResultSet r = s.executeQuery(sql);
			if(r.next()){
				num_processing = r.getInt(1);
			}

		}
		catch(SQLException e){
			System.out.println("Communication.getNumProcessing - Error al consultar ("+e+")");
			e.printStackTrace();
		}
		
		return num_processing;	

	}

	public int getGroupId(int contact_id){
		
		int group_id = -1;
		ResultSet r;
		
		try{
			select_group_contact.setInt(1, contact_id);
			r = select_group_contact.executeQuery();
			if(r.next()){
				group_id = r.getInt(1);
			}

		}
		catch(SQLException e){
			System.out.println("Communication.getGroupId - Error al consultar ("+e+")");
			e.printStackTrace();
		}

		return group_id;
	}

	public Collection<String> getNamesGroup(int group_id){
		Map<Integer, String> contacts_map = new TreeMap<Integer, String>();
		Collection<String> names = new TreeSet<String>();
		String prev = null;
		int contact_id;
		String full_name;
		ResultSet r;
		
		try{
			//names
			select_fullNames_group.setInt(1, group_id);
			r = select_fullNames_group.executeQuery();
			while(r.next()){
				contact_id = r.getInt(1);
				full_name = r.getString(2);
				if(full_name!= null && full_name.length()>1){
					full_name = replaceCharacters(full_name);
					if(contacts_map.containsKey(contact_id)){
						prev = contacts_map.get(contact_id);
						if(prev == null || full_name.length() > prev.length()){
							contacts_map.put(contact_id,full_name);
						}
					}
					else{
						contacts_map.put(contact_id, full_name);
					}
				}
			}
		for(Map.Entry<Integer, String> par : contacts_map.entrySet()){
			full_name = par.getValue();
			if(! names.contains(full_name)){
				names.add(full_name);
			}
		}

		}catch(Exception e){
			System.out.println("Communication.getNamesGroup - Error al cargar nombres ("+e+")");
			e.printStackTrace();
		}

		return names;
	}

	public Collection<String> getMailsGroup(int group_id){

		Collection<String> emails = new TreeSet<String>();
		String email;
		ResultSet r;

		try{
			select_emails_group.setInt(1, group_id);
			r = select_emails_group.executeQuery();
			while(r.next()){
				email = r.getString(1);
				if(email!=null && email.length()>1){
					email = replaceCharacters(email);
					if(! emails.contains(email)){
						emails.add(email);
					}
				}
			}

		}catch(Exception e){
			System.out.println("Communication.getNamesGroup - Error al cargar mails ("+e+")");
			e.printStackTrace();
		}	  
		return emails;
	}

	public Map<String, Set<String>> getSrcIdsGroup(int group_id){
		
		Map<String, Set<String>> ids = new TreeMap<String, Set<String>>();
		Set<String> set_ids;
		String src_id, src_name;
		ResultSet r;
		
		try{
			select_src_ids.setInt(1, group_id);
			r = select_src_ids.executeQuery();
			while(r.next()){
				src_name = r.getString(1);
				src_id = r.getString(2);
				if(src_name!=null && src_id!=null){
					if(ids.containsKey(src_name)){
						set_ids=ids.get(src_name);
						if(! set_ids.contains(src_id)){
							set_ids.add(src_id);
						}
					}
					else{
						set_ids=new TreeSet<String>();
						set_ids.add(src_id);
						ids.put(src_name, set_ids);
					}
				}
			}
			
		}
		catch(Exception e){
			System.out.println("Communication.getNamesGroup - Error al cargar mails ("+e+")");
			e.printStackTrace();
		}	  
		return ids;
	}
	
	public Collection<String> getPhonesGroup(int group_id){

		Collection<String> phones = new TreeSet<String>();
		String phone;
		ResultSet r;

		try{
			select_phones_group.setInt(1, group_id);
			r = select_phones_group.executeQuery();
			while(r.next()){
				phone = r.getString(1);
				if(phone!=null && phone.length()>1){
					phone = replaceCharacters(phone);
					if(! phones.contains(phone)){
						phones.add(phone);
					}
				}
			}

		}catch(Exception e){
			System.out.println("Communication.getNamesGroup - Error al cargar telefonos ("+e+")");
			e.printStackTrace();
		}	  

		return phones;
	}

	public Collection<String> getOrgsGroup(int group_id){

		Collection<String> orgs = new TreeSet<String>();
		String org;
		ResultSet r;

		try{
			select_orgs_group.setInt(1, group_id);
			r = select_orgs_group.executeQuery();
			while(r.next()){
				org = r.getString(1);
				if(org!=null && org.length()>1){
					org = replaceCharacters(org);
					org = org.replaceAll("[ .,-]", "");
					if(org.length()>10){
						org = org.substring(0, 10);
					}
					if(org.length()>2 && (! orgs.contains(org)) ){
						orgs.add(org);
					}
				}
			}

		}
		catch(Exception e){
			System.out.println("Communication.getOrgsGroup - Error al cargar orgs ("+e+")");
			e.printStackTrace();
		}
		
		return orgs;
	}
	
	public Collection<Integer> getUsersGroup(int group_id){

		Collection<Integer> users = new TreeSet<Integer>();
		int user_id;
		ResultSet r;

		try{
			select_users_group.setInt(1, group_id);
			r = select_users_group.executeQuery();
			while(r.next()){
				user_id = r.getInt(1);
				users.add(user_id);
			}

		}
		catch(Exception e){
			System.out.println("Communication.getUsersGroup - Error al cargar user ("+e+")");
			e.printStackTrace();
		}
		
		return users;
	}

	public List<Integer> getContactsName(String name){
		
		List<Integer> contacts = new LinkedList<Integer>();
		ResultSet r;
		
		try{
			select_contact_name.setString(1, name);
			r = select_contact_name.executeQuery();
			while(r.next()){
				contacts.add(r.getInt(1));
			}

		}
		catch(SQLException e){
			System.out.println("Communication.getContactsName - Error al consultar ("+e+")");
			e.printStackTrace();
		}

		return contacts;
	}
	
	public List<Integer> getContactsMail(String mail){
		
		List<Integer> contacts = new LinkedList<Integer>();
		ResultSet r;
		
		try{
			select_contact_mail.setString(1, mail);
			r = select_contact_mail.executeQuery();
			while(r.next()){
				contacts.add(r.getInt(1));
			}

		}
		catch(SQLException e){
			System.out.println("Communication.getContactsMail - Error al consultar ("+e+")");
			e.printStackTrace();
		}

		return contacts;
	}
	
	public Map<Integer, Integer> getUserGroups(int user_id){
		
		Map<Integer, Integer> groups=new TreeMap<Integer, Integer>();
		ResultSet r;
		
		try{
			select_user_groups.setInt(1, user_id);
			r = select_user_groups.executeQuery();
			while(r.next()){
				groups.put(r.getInt(1), r.getInt(2));
			}
		}
		catch(SQLException e){
			System.out.println("Communication.getUserGroups - Error al consultar ("+e+")");
			e.printStackTrace();
		}
		
		return groups;
	}
	
	public void updateAsGroup(int group_id_old, int group_id_new){
//		System.out.println("Communication.updateAsGroup - updateando "+group_id_old+" por "+group_id_new);
		ResultSet r;

		try{
			update_as_group.setInt(1, group_id_new);
			update_as_group.setInt(2, group_id_old);
			update_as_group.executeUpdate();
		}
		catch(Exception e){
			System.out.println("Communication.updateAsGroup - Error al guardar ("+e+")");
			e.printStackTrace();
		}
	}

	public void updateExactGroups(int group_id_old, int group_id_new){
//		System.out.println("Communication.updateExactGroups - updateando "+group_id_old+" por "+group_id_new);
		ResultSet r;
		try{
			update_exact_groups.setInt(1, group_id_new);
			update_exact_groups.setInt(2, group_id_old);
			update_exact_groups.executeUpdate();
		}
		catch(Exception e){
			System.out.println("Communication.updateExactGroups - Error al guardar ("+e+")");
			e.printStackTrace();
		}
	}

	public void changeStatusContacts(Set<Integer> contacts, String status){
		if(contacts.size()==0){
			return;
		}
		try{
			String sQuery="update contacts set status = \""+status+"\" where contactId = ?";
			PreparedStatement s = (PreparedStatement) conexion.prepareStatement(sQuery);

			Iterator<Integer> it_contacts = contacts.iterator();
			while(it_contacts.hasNext()){
				s.setInt(1, it_contacts.next());
				s.executeUpdate();
			}
			
			s.close();
		}
		catch(SQLException e){
			System.out.println("Communication.changeStatusContacts - Error al consultar ("+e+")");
			e.printStackTrace();
		}
	}

	public void changeStatusGroup(int group_id, String status){
		try{
			update_status_group.setString(1, status);
			update_status_group.setInt(2, group_id);
			update_status_group.executeUpdate();
		}
		catch(SQLException e){
			System.out.println("Communication.changeStatusGroup - Error al consultar ("+e+")");
			e.printStackTrace();
		}
	}

	public void insertBadGroupsIndex(int hash, int group_id){
		try{
			insert_bad_groups_index.setInt(1, hash);
			insert_bad_groups_index.setInt(2, group_id);
			insert_bad_groups_index.executeUpdate();
		}
		catch(SQLException e){
			System.out.println("Communication.insertBadGroupsIndex - Error al consultar ("+e+")");
			e.printStackTrace();
		}
	}

	public void insertBadGroups(int group_id, int contact_id){
		try{
			insert_bad_groups.setInt(1, group_id);
			insert_bad_groups.setInt(2, contact_id);
			insert_bad_groups.executeUpdate();
		}
		catch(SQLException e){
			System.out.println("Communication.insertBadGroups - Error al consultar ("+e+")");
			e.printStackTrace();
		}
	}
	
	public Set<Integer> getContactsGroup(int group_id){
		
		Set<Integer> contacts=new TreeSet<Integer>();
		ResultSet r;
		
		try{
			select_contacts_group.setInt(1, group_id);
			r = select_contacts_group.executeQuery();
			while(r.next()){
				contacts.add(r.getInt(1));
			}
		}
		catch(SQLException e){
			System.out.println("Communication.getContactsGroup - Error al consultar ("+e+")");
			e.printStackTrace();
		}
		
		return contacts;
	}
	
	public Map<Integer, Set<Integer>> getBadGroupsCandidates(int hash){
		
		Map<Integer, Set<Integer>> bad_groups=new TreeMap<Integer, Set<Integer>>();
		ResultSet r, r2;
		int group_id, contact_id;
		Set<Integer> group;
		
		try{
			select_bad_groups_index.setInt(1, hash);
			r=select_bad_groups_index.executeQuery();
			while(r.next()){
				group_id=r.getInt(1);
				group=new TreeSet<Integer>();
				select_bad_groups.setInt(1, group_id);
				r2=select_bad_groups.executeQuery();
				while(r2.next()){
					contact_id=r2.getInt(1);
					group.add(contact_id);
				}
				bad_groups.put(group_id, group);
			}
		}
		catch(SQLException e){
			System.out.println("Communication.getBadGroupsCandidates - Error al consultar ("+e+")");
			e.printStackTrace();
		}
		
		return bad_groups;
	}

	private void setQueries(){
		String sql;
		
		try{
			sql = "select names.contactId, names.fullName from as_groups, names where as_groups.groupId = ? and as_groups.contactId = names.contactId";
			select_fullNames_group = conexion.prepareStatement(sql);

			sql = "select distinct(address) from email, as_groups where as_groups.groupId = ? and as_groups.contactId = email.contactId";
			select_emails_group = conexion.prepareStatement(sql);
			
			sql = "select distinct(Text) from phonenumber, as_groups where as_groups.groupId = ? and as_groups.contactId = phonenumber.contactId";
			select_phones_group = conexion.prepareStatement(sql);
			
			sql = "select orgName, orgTitle from organization, as_groups where as_groups.groupId = ? and as_groups.contactId = organization.contactId";
			select_orgs_group = conexion.prepareStatement(sql); 
			
			sql = "select distinct(user_id) from as_groups where groupId = ?";
			select_users_group = conexion.prepareStatement(sql); 

			sql = "select distinct(contactId) from names where fullName = ?";
			select_contact_name = conexion.prepareStatement(sql);
			
			sql = "select distinct(contactId) from email where address = ?";
			select_contact_mail = conexion.prepareStatement(sql);

			sql = "select groupId from as_groups where contactId = ?";
			select_group_contact = conexion.prepareStatement(sql);

			sql = "select contactId, groupId from as_groups where user_id = ? and as_groups.groupId != -1";
			select_user_groups = conexion.prepareStatement(sql);

			sql = "update as_groups set groupId = ? where groupId = ?";
			update_as_group = conexion.prepareStatement(sql);

			sql = "update exact_groups set groupId = ? where groupId = ?";
			update_exact_groups = conexion.prepareStatement(sql);

			sql = "update contacts, as_groups set contacts.status = ? where as_groups.groupid = ? and as_groups.contactId = contacts.contactId";
			update_status_group = conexion.prepareStatement(sql);

			sql = "select contacts.srcName, contacts.srcId from as_groups, contacts where as_groups.groupid = ? and as_groups.contactId = contacts.contactId";
			select_src_ids = conexion.prepareStatement(sql);
			
			sql = "select contactid from as_groups where groupid = ?";
			select_contacts_group = conexion.prepareStatement(sql);
			
			sql = "insert into data_bad_groups_index (hash, group_id) values (?, ?)";
			insert_bad_groups_index = conexion.prepareStatement(sql);
			
			sql = "insert into data_bad_groups (group_id, contact_id) values (?, ?)";
			insert_bad_groups = conexion.prepareStatement(sql);
			
			sql = "select distinct(group_id) from data_bad_groups_index where hash = ?";
			select_bad_groups_index = conexion.prepareStatement(sql);
			
			sql = "select distinct(contact_id) from data_bad_groups where group_id = ?";
			select_bad_groups = conexion.prepareStatement(sql);
			
			sql = "insert into data_grouping_log (old_group_id, new_group_id, contact_id, module_id, message) values (?, ?, ?, ?, ?)";
			insert_log = conexion.prepareStatement(sql);
			
		}
		catch(SQLException e){
			System.out.println("Communication.setQueries - Error al preparar consultas ("+e+")");
			e.printStackTrace();
		}
	}

	private String replaceCharacters(String texto){

		texto = texto.replace('á', 'a');
		texto = texto.replace('é', 'e');
		texto = texto.replace('í', 'i');
		texto = texto.replace('ó', 'o');
		texto = texto.replace('ú', 'u');
		texto = texto.replace('ñ', 'n');

		return texto.toLowerCase();
	}
}
