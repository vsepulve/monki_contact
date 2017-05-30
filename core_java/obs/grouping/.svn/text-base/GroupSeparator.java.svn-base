package obs.grouping;

import java.util.Set;
import java.util.TreeSet;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.FileReader;

import java.sql.*;
import java.sql.SQLException;
import com.mysql.jdbc.Driver;

//import obs.grouping.GroupingLogger;

public class GroupSeparator{
	
	private Connection conexion = null;
	
	private PreparedStatement select_contacts;
	private PreparedStatement delete_as_groups;
	private PreparedStatement delete_exact_groups;
	private PreparedStatement insert_as_groups;
	private PreparedStatement select_group_id;
	private PreparedStatement insert_exact_groups;
	private PreparedStatement update_status_group;
	
	private Set<String> exact_sources;
	
//	GroupingLogger logger;
	
	public static void main(String[]args){
		
		if(args.length!=2){
			System.out.println("");
			System.out.println("Modo de Uso");
			System.out.println("> java GroupSeparator archivo_config group_id");
			System.out.println("");
			return;
		}
		
		String archivo_config=args[0];
		int group_id=new Integer(args[1]);
		
		String host="";
		String bd="";
		String usuario="";
		String clave="";
		String host_lectura="";
		BufferedReader lector=null;
		try{
			lector=new BufferedReader(new FileReader(archivo_config));
			host=lector.readLine();
			bd=lector.readLine();
			usuario=lector.readLine();
			clave=lector.readLine();
			host_lectura=lector.readLine();
			lector.close();
		}
		catch(Exception e){
			System.err.println("GroupSeparator - Error al leer configuracion ("+archivo_config+")");
			e.printStackTrace();
			return;
		}
		
		GroupSeparator separator=new GroupSeparator(host, bd, usuario, clave);
		separator.separateGroup(group_id);
		
	}
	
	public GroupSeparator(String host, String db, String user, String pass){
		System.out.println("GroupSeparator - inicio (cargando desde "+host+"/"+db+")");
		
		if(!connect(host, db, user, pass)){
			System.out.println("GroupSeparator - Terminado por problemas al conectar");
			return;
		}
		prepararConsultas();
		
//		logger=new GroupingLogger(host, db, user, pass, "log_separator.txt");
		
		exact_sources=new TreeSet<String>();
		exact_sources.add("facebook");
		exact_sources.add("linkedin");
		exact_sources.add("angellist");
		exact_sources.add("twitter");
		exact_sources.add("foursquare");
		exact_sources.add("meetup");
		
		System.out.println("GroupSeparator - fin");
	}
	
	private boolean connect(String host, String db, String user, String pass){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			conexion=DriverManager.getConnection ("jdbc:mysql://"+host+"/"+db, user, pass);
		}
		catch(Exception e){
			System.out.println("GroupSeparator.connect - Incapaz de Conectar :");
			System.out.println("Username: \""+user+"\", Password: \""+pass+"\"");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private void prepararConsultas(){
		String sql;
		
		try{
			sql="select contacts.contactId, as_groups.groupId, contacts.user_Id, contacts.srcId, contacts.srcName from as_groups, contacts where as_groups.groupId=? and as_groups.contactId=contacts.contactId";
			select_contacts=conexion.prepareStatement(sql);
			
			sql="delete from as_groups where groupId=?";
			delete_as_groups=conexion.prepareStatement(sql);
			
			sql="delete from exact_groups where groupId=?";
			delete_exact_groups=conexion.prepareStatement(sql);
			
			sql="insert into as_groups (contactId, user_Id) values (?, ?) ON DUPLICATE KEY UPDATE contactId=?";
			insert_as_groups=conexion.prepareStatement(sql);
			
			sql="select groupId from as_groups where contactId=?";
			select_group_id=conexion.prepareStatement(sql);
			
			sql="insert into exact_groups (source, sourceId, groupId) values (?, ?, ?) ON DUPLICATE KEY UPDATE groupId=?";
			insert_exact_groups=conexion.prepareStatement(sql);
			
			sql="update contacts, as_groups set contacts.status = ? where as_groups.groupid = ? and as_groups.contactId = contacts.contactId and contacts.status != \"black\"";
			update_status_group=conexion.prepareStatement(sql);
			
		}
		catch(Exception e){
			System.out.println("GroupSeparator.prepararConsultas - Error al preparar consultas");
			e.printStackTrace();
		}
	}
	
	private class ContactData{
		int contact_id;
		int group_id;
		int user_id;
		String src_id;
		String src_name;
		public ContactData(){
			contact_id=0;
			group_id=0;
			user_id=0;
			src_id="";
			src_name="";
		}
		
		public int getContactId(){
			return contact_id;
		}
		public int getGroupId(){
			return group_id;
		}
		public int getUserId(){
			return user_id;
		}
		public String getSrcId(){
			return src_id;
		}
		public String getSrcName(){
			return src_name;
		}
		
		public void setContactId(int _contact_id){
			contact_id=_contact_id;
		}
		public void setGroupId(int _group_id){
			group_id=_group_id;
		}
		public void setUserId(int _user_id){
			user_id=_user_id;
		}
		public void setSrcId(String _src_id){
			src_id=_src_id;
		}
		public void setSrcName(String _src_name){
			src_name=_src_name;
		}
	}
	
	public void separateGroup(int group_id){
	/*
		System.out.println("GroupSeparator.separateGroup - inicio");
		changeStatusGroup(group_id, "updated");
		List<ContactData> contacts = getContactsGroup(group_id);
		int new_group_id;
		deleteGroup(group_id);
		for(ContactData c : contacts){
			new_group_id = insertGroup(c.getContactId(), c.getUserId());
			//log: 0 -> new_group_id (contact_id)
			if(new_group_id == -1){
				logger.writeLog(0, new_group_id, "GroupSeparatorError", "contact_id = "+c.getContactId()+"");
			}
			else{
				logger.writeLog(0, new_group_id, "GroupSeparator", "contact_id = "+c.getContactId()+"");
				
				if(exactSource(c.getSrcName())){
					insertExactGroups(c.getSrcName(), c.getSrcId(), new_group_id);
				}
			}
		}
		System.out.println("GroupSeparator.separateGroup - fin");
		*/
	}

	public void changeStatusGroup(int group_id, String status){
		try{
			update_status_group.setString(1, status);
			update_status_group.setInt(2, group_id);
			update_status_group.executeUpdate();
		}
		catch(SQLException e){
			System.out.println("changeStatusGroup - Error al consultar ("+e+")");
			e.printStackTrace();
		}
	}
	
	private void insertExactGroups(String src_name, String src_id, int group_id){
		try{
			insert_exact_groups.setString(1, src_name);
			insert_exact_groups.setString(2, src_id);
			insert_exact_groups.setInt(3, group_id);
			insert_exact_groups.setInt(4, group_id);
			insert_exact_groups.executeUpdate();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private boolean exactSource(String src_name){
		return exact_sources.contains(src_name.toLowerCase());
	}
	
	private int insertGroup(int contact_id, int user_id){
		int group_id=-1;
		
		ResultSet r=null;
		
		try{
//			sql="insert into as_groups (contactId, user_Id) values (?, ?) ON DUPLICATE KEY UPDATE contactId=?";
//			insert_as_groups=conexion.prepareStatement(sql);
			insert_as_groups.setInt(1, contact_id);
			insert_as_groups.setInt(2, user_id);
			insert_as_groups.setInt(3, contact_id);
			insert_as_groups.executeUpdate();
			
//			sql="select groupId from as_groups where contactId=?";
//			select_group_id=conexion.prepareStatement(sql);
			select_group_id.setInt(1, contact_id);
			r=select_group_id.executeQuery();
			if(r.next()){
				group_id=r.getInt(1);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return group_id;
	}
	
	private List<ContactData> getContactsGroup(int group_id){
		List<ContactData> contacts=new LinkedList<ContactData>();
		
		ResultSet r=null;
		int contact_id, user_id;
		String src_id, src_name;
		ContactData c;
		
		try{
			select_contacts.setInt(1, group_id);
			r=select_contacts.executeQuery();
			while(r.next()){
				contact_id=r.getInt(1);
				//group_id=r.getInt(2);
				user_id=r.getInt(3);
				src_id=r.getString(4);
				src_name=r.getString(5);
				c=new ContactData();
				c.setContactId(contact_id);
				c.setGroupId(group_id);
				c.setUserId(user_id);
				c.setSrcId(src_id);
				c.setSrcName(src_name);
				contacts.add(c);
			}
		}
		catch(Exception e){
			System.out.println("GroupSeparator.getContactsGroup - Error al leer ("+e+")");
			e.printStackTrace();
		}
		return contacts;
	}
	
	//Borra el grupo de TODAS las tablas involucradas
	// - as_groups
	// - exactGroups
	private void deleteGroup(int group_id){
		
		try{
			delete_as_groups.setInt(1, group_id);
			delete_as_groups.executeUpdate();
			
			delete_exact_groups.setInt(1, group_id);
			delete_exact_groups.executeUpdate();
			
		}
		catch(SQLException e){
			System.out.println("GroupSeparator.deleteGroup - Error al borrar ("+e+")");
			e.printStackTrace();
		}
	}
	
}
