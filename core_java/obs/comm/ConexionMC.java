package obs.comm;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import java.io.File;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.PrintWriter;

import obs.spaces.data.DatoPerfilDoc;
import obs.grouping.Grouping;

import obs.affinities.Affinity;

import java.sql.*;
import java.sql.SQLException;
import com.mysql.jdbc.Driver;

//generador de una coleccion de docs basada en perfiles
//lee el xml original y almacena los textos originales asociados a un docid
public class ConexionMC{
	
	private Connection conexion=null;
	
	private PreparedStatement select_grouping_log;
	
	private PreparedStatement select_fullNames;
	private PreparedStatement select_twitter;
	private PreparedStatement select_addresses;
	private PreparedStatement select_networks;
	private PreparedStatement select_emails;
	private PreparedStatement select_phones;
	private PreparedStatement select_organizations;
	private PreparedStatement select_educations;
	private PreparedStatement select_demographics;
	
	private PreparedStatement select_fullNames_processing;
	private PreparedStatement select_twitter_processing;
	private PreparedStatement select_addresses_processing;
	private PreparedStatement select_networks_processing;
	private PreparedStatement select_emails_processing;
	private PreparedStatement select_phones_processing;
	private PreparedStatement select_organizations_processing;
	private PreparedStatement select_educations_processing;
	private PreparedStatement select_demographics_processing;
	
	private PreparedStatement select_fullNames_new;
	private PreparedStatement select_twitter_new;
	private PreparedStatement select_addresses_new;
	private PreparedStatement select_networks_new;
	private PreparedStatement select_emails_new;
	private PreparedStatement select_phones_new;
	private PreparedStatement select_organizations_new;
	private PreparedStatement select_educations_new;
	private PreparedStatement select_demographics_new;
	
	private PreparedStatement select_fullNames_group;
	private PreparedStatement select_twitter_group;
	private PreparedStatement select_addresses_group;
	private PreparedStatement select_networks_group;
	private PreparedStatement select_emails_group;
	private PreparedStatement select_phones_group;
	private PreparedStatement select_organizations_group;
	private PreparedStatement select_educations_group;
	private PreparedStatement select_demographics_group;
	
	private PreparedStatement insert_voc;
	private PreparedStatement delete_voc;
	private PreparedStatement select_vocs;
	private PreparedStatement select_vocs_grupo;
	private PreparedStatement buscar_voc_global;
	private PreparedStatement update_voc_global;
	private PreparedStatement insert_voc_global;
	private PreparedStatement buscar_voc_at;
	private PreparedStatement insert_voc_at;
	private PreparedStatement update_voc_at;
	private PreparedStatement delete_voc_at;
	private PreparedStatement select_vocs_at;
	private PreparedStatement delete_voc_at_lazy;
	
	private PreparedStatement buscar_as_group;
	private PreparedStatement insert_as_group;
	private PreparedStatement update_as_group;
	private PreparedStatement update_as_group_simple;
	private PreparedStatement update_exact_groups;
	
	private PreparedStatement select_usuario_contacto;
	
	private PreparedStatement select_contact_nombre;
	private PreparedStatement select_contact_mail;
	
	private PreparedStatement select_group_contact;
	
	private PreparedStatement select_nombres_grupo;
	private PreparedStatement select_mails_grupo;
	private PreparedStatement select_phones_grupo;
	private PreparedStatement select_orgs_grupo;
			
	private PreparedStatement select_affinities_index;
	private PreparedStatement select_affinities;
	private PreparedStatement insert_affinities_index;
	private PreparedStatement insert_affinities;
	private PreparedStatement insert_affinities_groups;
	private PreparedStatement select_affinities_groups;
	private PreparedStatement update_affinities_groups;
	
	private PreparedStatement update_reprocess_user;
	
	private PreparedStatement insert_log;
	
	public Connection getConexion(){
		return conexion;
	}
	
	public ConexionMC(String servidor, String bd, String usuario, String clave){
		
		System.out.println("ConexionMC - inicio (cargando desde "+servidor+"/"+bd+")");
		
		if(!conectar(servidor, bd, usuario, clave)){
			System.out.println("ConexionMC - Terminado por problemas al conectar");
			return;
		}
		prepararConsultas();
		
		System.out.println("ConexionMC - fin");
		
	}
	
	public int getMaxGroupId(){
		int max_group_id=-1;
		try{
			Statement s=conexion.createStatement();
			ResultSet r=s.executeQuery("select max(groupId) from as_groups");
			if(r.next()){
				max_group_id=r.getInt(1);
			}
			s.close();
		}
		catch(SQLException e){
			System.out.println("ConexionMC.getMaxGroupId - Error al consultar ("+e+")");
			e.printStackTrace();
		}
		
		return max_group_id;
		
	}
	
	public int getMaxAffinityId(){
		int max_id=-1;
		try{
			Statement s=conexion.createStatement();
			ResultSet r=s.executeQuery("select max(affinity_id) from data_affinities_index");
			if(r.next()){
				max_id=r.getInt(1);
			}
			s.close();
		}
		catch(SQLException e){
			System.out.println("ConexionMC.getMaxAffinityId - Error al consultar ("+e+")");
			e.printStackTrace();
		}
		
		return max_id;
		
	}
	
	public int getTotalPerfiles(){
		System.out.println("ConexionMC.getTotalPerfiles - inicio");
		int total_perfiles=-1;
		try{
			Statement s = conexion.createStatement();
			ResultSet r = s.executeQuery("select count(1) from contacts");
			if(r.next()){
				total_perfiles=r.getInt(1);
			}
			s.close();
		}
		catch(SQLException e){
			System.out.println("ConexionMC.getTotalPerfiles - Error al consultar ("+e+")");
			e.printStackTrace();
		}
		System.out.println("ConexionMC.getTotalPerfiles - total_perfiles: "+total_perfiles+"");
		return total_perfiles;
	}
	
	public int getTotalPerfilesProcessing(){
		int total_perfiles=-1;
		try{
			Statement s=conexion.createStatement();
			ResultSet r=s.executeQuery("select count(1) from contacts where status=\"processing\"");
			if(r.next()){
				total_perfiles=r.getInt(1);
			}
			s.close();
		}
		catch(SQLException e){
			System.out.println("ConexionMC.getTotalPerfilesProcessing - Error al consultar ("+e+")");
			e.printStackTrace();
		}
		return total_perfiles;
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
	
	public boolean stopAffinitiesGenerator(){
		return stop("stopAffinitiesGenerator");
	}
	
	public boolean stopProcessingCell(){
		return stop("ProcessingCell");
	}
	
	public boolean stopCollectionGenerator(){
		return stop("CollectionGenerator");
	}
	
	public boolean stopAffinitiesClassificator(){
		return stop("AffinitiesClassificator");
	}
	
	public boolean stop(String module){
		String sql;
		int res = 1;
		try{
			Statement s = conexion.createStatement();
			sql = "select stop from data_control where module = \""+module+"\"";
			ResultSet r = s.executeQuery(sql);
			if(r.next()){
				res = r.getInt(1);
			}
			s.close();
		}
		catch(SQLException e){
			System.out.println("ConexionMC.stop - Error al consultar ("+e+")");
			e.printStackTrace();
		}
		
		return (res==1);
		
	}
	
	public void guardarTotalDocs(int total_docs){
		String sql;
		try{
			Statement s = conexion.createStatement();
			sql = "update data_statistics set value = "+total_docs+" where name = \"total_docs\"";
			s.executeUpdate(sql);
			s.close();
		}
		catch(SQLException e){
			System.out.println("ConexionMC.guardarTotalDocs - Error al consultar ("+e+")");
			e.printStackTrace();
		}
	}
	
	public int getTotalDocs(){
		String sql;
		int total_docs = 1;
		try{
			Statement s = conexion.createStatement();
			sql = "select value from data_statistics where name = \"total_docs\"";
			ResultSet r = s.executeQuery(sql);
			if(r.next()){
				total_docs = r.getInt(1);
			}
			s.close();
		}
		catch(SQLException e){
			System.out.println("ConexionMC.getTotalDocs - Error al consultar ("+e+")");
			e.printStackTrace();
		}
		
		return total_docs;
		
	}
	
	public List<Integer> getUsuarios(){
		List<Integer> lista = new LinkedList<Integer>();
		try{
			Statement s = conexion.createStatement();
			ResultSet r = s.executeQuery("select distinct(user_id) from contacts");
			while(r.next()){
				lista.add(r.getInt(1));
			}
			s.close();
		}
		catch(SQLException e){
			System.out.println("ConexionMC.getUsuarios - Error al consultar ("+e+")");
			e.printStackTrace();
			lista.clear();
			return null;
		}
		System.out.println("ConexionMC.getUsuarios - fin ("+lista.size()+" usuarios)");
		return lista;
	}
	
	public List<Integer> getUsuariosProcessing(){
		List<Integer> lista=new LinkedList<Integer>();
		try{
			Statement s=conexion.createStatement();
			ResultSet r=s.executeQuery("select distinct(user_id) from contacts where status=\"processing\"");
			while(r.next()){
				lista.add(r.getInt(1));
			}
			s.close();
		}
		catch(SQLException e){
			System.out.println("ConexionMC.getUsuariosProcessing - Error al consultar ("+e+")");
			e.printStackTrace();
			lista.clear();
			return null;
		}
		System.out.println("ConexionMC.getUsuariosProcessing - fin ("+lista.size()+" usuarios)");
		return lista;
	}
	
	public List<Integer> getUsuariosNew(){
		List<Integer> lista=new LinkedList<Integer>();
		try{
			Statement s=conexion.createStatement();
			ResultSet r=s.executeQuery("select distinct(user_id) from contacts where status=\"new\" or status=\"updated\"");
			while(r.next()){
				lista.add(r.getInt(1));
			}
			s.close();
		}
		catch(SQLException e){
			System.out.println("ConexionMC.getUsuariosNew - Error al consultar ("+e+")");
			e.printStackTrace();
			lista.clear();
			return null;
		}
		System.out.println("ConexionMC.getUsuariosNew - fin ("+lista.size()+" usuarios)");
		return lista;
	}
	
	public List<Integer> getUsuariosNew(int max){
		List<Integer> lista=new LinkedList<Integer>();
		try{
			Statement s=conexion.createStatement();
			ResultSet r=s.executeQuery("select distinct(user_id) from contacts where status=\"new\" or status=\"updated\" order by rand()");
//			ResultSet r=s.executeQuery("select count(contactid), user_id from contacts where status=\"new\" or status=\"updated\" group by user_id order by count(contactid) desc limit "+max+"");
			while(r.next()){
//				lista.add(r.getInt(2));
				lista.add(r.getInt(1));
			}
			s.close();
		}
		catch(SQLException e){
			System.out.println("ConexionMC.getUsuariosNew - Error al consultar ("+e+")");
			e.printStackTrace();
			lista.clear();
			return null;
		}
		System.out.println("ConexionMC.getUsuariosNew - fin ("+lista.size()+" usuarios)");
		return lista;
	}
	
	public List<Integer> getUsuariosGenerar(int max){
		System.out.println("ConexionMC.getUsuariosGenerar - inicio");
		Set<Integer> lista_set=new TreeSet<Integer>();
		try{
			Statement s=conexion.createStatement();
			ResultSet r=s.executeQuery("select count(contactid), user_id from contacts where status=\"new\" or status=\"updated\" group by user_id order by count(contactid) desc limit "+max+"");
			while(r.next()){
				lista_set.add(r.getInt(2));
			}
			s.close();
			
			s=conexion.createStatement();
			r=s.executeQuery("select distinct(user_id) from userCake_Users where reprocess=\"true\" limit "+(max-lista_set.size())+"");
			while(r.next()){
				lista_set.add(r.getInt(2));
				update_reprocess_user.setInt(1, r.getInt(2));
				update_reprocess_user.executeUpdate();
			}
			s.close();
			
		}
		catch(SQLException e){
			System.out.println("ConexionMC.getUsuariosGenerar - Error al consultar ("+e+")");
			e.printStackTrace();
			lista_set.clear();
			return null;
		}
		List<Integer> lista=new LinkedList<Integer>();
		lista.addAll(lista_set);
		System.out.println("ConexionMC.getUsuariosGenerar - fin ("+lista.size()+" usuarios)");
		return lista;
	}
	
	public List<Integer> getContactos(){
		List<Integer> lista=new LinkedList<Integer>();
		try{
			Statement s=conexion.createStatement();
			ResultSet r=s.executeQuery("select contactId from contacts");
			while(r.next()){
				lista.add(r.getInt(1));
			}
			s.close();
		}
		catch(SQLException e){
			System.out.println("ConexionMC.getContactos - Error al consultar ("+e+")");
			e.printStackTrace();
		}
		return lista;
	}
	
	public List<Integer> getContactosNuevos(){
		List<Integer> lista=new LinkedList<Integer>();
		try{
			Statement s=conexion.createStatement();
			ResultSet r=s.executeQuery("select contactId from contacts where status = \"new\"");
			while(r.next()){
				lista.add(r.getInt(1));
			}
			s.close();
		}
		catch(SQLException e){
			System.out.println("ConexionMC.getContactosNuevos - Error al consultar ("+e+")");
			e.printStackTrace();
		}
		return lista;
	}
	
	//retorna los contact_id de todos los contactos que no esten en as_groups
	public List<Integer> getContactosFaltantes(){
		System.out.println("ConexionMC.getContactosFaltantes - inicio");
		List<Integer> lista=new LinkedList<Integer>();
		try{
			Statement s=conexion.createStatement();
			ResultSet r=s.executeQuery("select contactId from contacts where status!=\"black\" and contactId not in (select contactId from as_groups)");
			while(r.next()){
				lista.add(r.getInt(1));
			}
			s.close();
		}
		catch(SQLException e){
			System.out.println("ConexionMC.getContactosFaltantes - Error al consultar ("+e+")");
			e.printStackTrace();
		}
		System.out.println("ConexionMC.getContactosFaltantes - fin ("+lista.size()+" contactos adicionales)");
		return lista;
	}
	
	public List<Integer> getContactosModificados(){
		List<Integer> lista=new LinkedList<Integer>();
		try{
			Statement s=conexion.createStatement();
			ResultSet r=s.executeQuery("select contactId from contacts where status = \"new\" or status = \"updated\"");
			while(r.next()){
				lista.add(r.getInt(1));
			}
			s.close();
		}
		catch(SQLException e){
			System.out.println("ConexionMC.getContactosModificados - Error al consultar ("+e+")");
			e.printStackTrace();
		}
		return lista;
	}
	
	public List<Integer> getContactosProcessing(){
		System.out.println("ConexionMC.getContactosProcessing - inicio");
		List<Integer> lista=new LinkedList<Integer>();
		try{
			Statement s=conexion.createStatement();
			String sql="select contactId from contacts where status = \"processing\"";
			System.out.println("ConexionMC.getContactosProcessing - sql: ["+sql+"]");
			ResultSet r=s.executeQuery(sql);
			while(r.next()){
				lista.add(r.getInt(1));
			}
			s.close();
		}
		catch(SQLException e){
			System.out.println("ConexionMC.getContactosModificados - Error al consultar ("+e+")");
			e.printStackTrace();
		}
		System.out.println("ConexionMC.getContactosProcessing - fin (total: "+lista.size()+")");
		return lista;
	}
	
	public void cambiarEstadoContactos(List<Integer> contactos, String estado){
		if(contactos.size()==0){
			return;
		}
		try{
			String sQuery="update contacts set status = \""+estado+"\" where contactId = ?";
			PreparedStatement s = (PreparedStatement) conexion.prepareStatement(sQuery);

			Iterator<Integer> it_contactos=contactos.iterator();
			while(it_contactos.hasNext()){
				s.setInt(1, it_contactos.next());
				s.executeUpdate();
			}
			
			s.close();
		}
		catch(SQLException e){
			System.out.println("ConexionMC.getContactosModificados - Error al consultar ("+e+")");
			e.printStackTrace();
		}
	}
	
	public Map<Integer, List<Integer>> getGrupos(){
		System.out.println("ConexionMC.getGrupos - inicio");
		Map<Integer, List<Integer>> mapa=new TreeMap<Integer, List<Integer>>();
		try{
			Statement s=conexion.createStatement();
			String sql="select contactId, groupId from as_groups where groupId != -1";
			ResultSet r=s.executeQuery(sql);
//			System.out.println("ConexionMC.getGrupos - sql: ["+sql+"]");
			int contact_id, group_id;
			while(r.next()){
				contact_id=r.getInt(1);
				group_id=r.getInt(2);
				if(mapa.containsKey(group_id)){
					mapa.get(group_id).add(contact_id);
				}
				else{
					List<Integer> lista=new LinkedList<Integer>();
					lista.add(contact_id);
					mapa.put(group_id, lista);
				}
			}
			s.close();
		}
		catch(SQLException e){
			System.out.println("ConexionMC.getGrupos - Error al consultar ("+e+")");
			e.printStackTrace();
		}
		
		System.out.println("ConexionMC.getGrupos - fin (total: "+mapa.size()+")");
		return mapa;
		
	}
	
	public Map<Integer, List<Integer>> getGruposUsuario(int user_id){
		System.out.println("ConexionMC.getGruposUsuario - inicio");
		Map<Integer, List<Integer>> mapa=new TreeMap<Integer, List<Integer>>();
		try{
			Statement s=conexion.createStatement();
			String sql="select contactId, groupId from as_groups where user_id = "+user_id+" and groupId != -1";
			ResultSet r=s.executeQuery(sql);
//			System.out.println("ConexionMC.getGrupos - sql: ["+sql+"]");
			int contact_id, group_id;
			while(r.next()){
				contact_id=r.getInt(1);
				group_id=r.getInt(2);
				if(mapa.containsKey(group_id)){
					mapa.get(group_id).add(contact_id);
				}
				else{
					List<Integer> lista=new LinkedList<Integer>();
					lista.add(contact_id);
					mapa.put(group_id, lista);
				}
			}
			s.close();
		}
		catch(SQLException e){
			System.out.println("ConexionMC.getGruposUsuario - Error al consultar ("+e+")");
			e.printStackTrace();
		}
		
		System.out.println("ConexionMC.getGruposUsuario - fin (total: "+mapa.size()+")");
		return mapa;
		
	}
	
	public Map<Integer, List<Integer>> getGruposProcessing(){
		System.out.println("ConexionMC.getGruposProcessing - inicio");
		Map<Integer, List<Integer>> mapa=new TreeMap<Integer, List<Integer>>();
		try{
			Statement s=conexion.createStatement();
			String sql="select as_groups.contactId, as_groups.groupId from as_groups, contacts where as_groups.groupId != -1 and as_groups.contactId=contacts.contactId and contacts.status=\"processing\" order by as_groups.groupId";
			ResultSet r=s.executeQuery(sql);
//			System.out.println("ConexionMC.getGruposProcessing - sql: ["+sql+"]");
			int contact_id, group_id;
			while(r.next()){
				contact_id=r.getInt(1);
				group_id=r.getInt(2);
				if(mapa.containsKey(group_id)){
					mapa.get(group_id).add(contact_id);
				}
				else{
					List<Integer> lista=new LinkedList<Integer>();
					lista.add(contact_id);
					mapa.put(group_id, lista);
				}
			}
			s.close();
		}
		catch(SQLException e){
			System.out.println("ConexionMC.getGruposProcessing - Error al consultar ("+e+")");
			e.printStackTrace();
		}
		
		System.out.println("ConexionMC.getGruposProcessing - fin (total: "+mapa.size()+")");
		return mapa;
		
	}
	
	public Map<Integer, List<Integer>> getGruposModificados(){
		Map<Integer, List<Integer>> mapa=new TreeMap<Integer, List<Integer>>();
		try{
			Statement s=conexion.createStatement();
			ResultSet r=s.executeQuery("select as_groups.contactId, as_groups.groupId from as_groups, contacts where as_groups.groupId != -1 and as_groups.contactId=contacts.contactId and ( contacts.status=\"new\" or contacts.status=\"updated\" ) order by as_groups.groupId");
			int contact_id, group_id;
			while(r.next()){
				contact_id=r.getInt(1);
				group_id=r.getInt(2);
				if(mapa.containsKey(group_id)){
					mapa.get(group_id).add(contact_id);
				}
				else{
					List<Integer> lista=new LinkedList<Integer>();
					lista.add(contact_id);
					mapa.put(group_id, lista);
				}
			}
			s.close();
		}
		catch(SQLException e){
			System.out.println("ConexionMC.getGruposModificados - Error al consultar ("+e+")");
			e.printStackTrace();
		}
		
		return mapa;
		
	}
	
	public List<DatoPerfilDoc> getPerfilesUsuarioNuevos(int user_id){
		
		System.out.println("ConexionMC.getPerfilesUsuarioNuevos - inicio (user: "+user_id+")");
		
		Set<Integer> perfiles_creados = new TreeSet<Integer>();
		Map<Integer, DatoPerfilDoc> mapa_perfiles = new TreeMap<Integer, DatoPerfilDoc>();
		List<DatoPerfilDoc> lista_retorno = new LinkedList<DatoPerfilDoc>();
		
		DatoPerfilDoc perfil;
		int contact_id;
		String full_name, address, network;
		String email, phone, organization;
		String orgName, orgTitle, jobStatus;
		String hometown, location;
		String twitter;
		int jobStatus_type=0;
		ResultSet r;
		
		try{
			
			//names
			select_fullNames_new.setInt(1, user_id);
//			select_fullNames_new.setInt(2, user_id);
			r = select_fullNames_new.executeQuery();
			while(r.next()){
				contact_id = r.getInt(1);
				full_name = r.getString(2);
				if(full_name != null && full_name.length()>1){
					full_name = reemplazarCaracteres(full_name);
					if(perfiles_creados.contains(contact_id)){
						perfil = mapa_perfiles.get(contact_id);
						//se dejara el mas largo
						if(perfil.getFullName()==null
							|| full_name.length() > perfil.getFullName().length()){
							perfil.setFullName(full_name);
						}
					}//if... perfil existe (agregar)
					else{
						perfil = new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.setFullName(full_name);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... nombre valido
			}//while... cada nombre
			
			//twitter
			select_twitter_new.setInt(1, user_id);
			r = select_twitter_new.executeQuery();
			while(r.next()){
				contact_id = r.getInt(1);
				twitter = r.getString(2);
				if(twitter != null && twitter.length()>1){
					if(perfiles_creados.contains(contact_id)){
						perfil = mapa_perfiles.get(contact_id);
						perfil.setTwitter(twitter);
					}//if... perfil existe (agregar)
					else{
						perfil = new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.setTwitter(twitter);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... nombre valido
			}//while... cada nombre
			
			//addresses
			select_addresses_new.setInt(1, user_id);
			r=select_addresses_new.executeQuery();
			while(r.next()){
				contact_id=r.getInt(1);
				address=r.getString(2);
				if(address!=null && address.length()>1){
					address=reemplazarCaracteres(address);
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						//se dejara el mas largo
						if(perfil.getAddress()==null
							|| address.length() > perfil.getAddress().length()){
							perfil.setAddress(address);
						}
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.setAddress(address);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... address valido
			}//while... cada address
			
			//demographics
			select_demographics_new.setInt(1, user_id);
			r=select_demographics_new.executeQuery();
			while(r.next()){
				contact_id=r.getInt(1);
				hometown=r.getString(2);
				location=r.getString(3);
//				hometown=hometown+" "+location;

				if(hometown!=null && hometown.length()>1){
					hometown=reemplazarCaracteres(hometown);
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						//agrego a address
						perfil.setHometown(hometown);
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.setHometown(hometown);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... address valido
				
				if(location!=null && location.length()>1){
					location=reemplazarCaracteres(location);
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						//agrego a address
						perfil.setLives(location);
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.setLives(location);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... address valido
				
				
			}//while... cada address
			
			//networks
			select_networks_new.setInt(1, user_id);
			r=select_networks_new.executeQuery();
			while(r.next()){
				contact_id=r.getInt(1);
				network=r.getString(2);
				if(network!=null && network.length()>1){
					network=reemplazarCaracteres(network);
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						//se dejara el mas largo
						if(perfil.getNetwork()==null
							|| network.length() > perfil.getNetwork().length()){
							perfil.setNetwork(network);
						}
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.setNetwork(network);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... network valido
			}//while... cada network
			
			//emails
			select_emails_new.setInt(1, user_id);
//			select_emails_new.setInt(2, user_id);
			r=select_emails_new.executeQuery();
			while(r.next()){
				contact_id=r.getInt(1);
				email=r.getString(2);
				if(email!=null && email.length()>1){
					email=reemplazarCaracteres(email);
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						perfil.addEmail(email);
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.addEmail(email);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... email valido
			}//while... cada email
			
			//phones
			select_phones_new.setInt(1, user_id);
//			select_phones_new.setInt(2, user_id);
			r=select_phones_new.executeQuery();
			while(r.next()){
				contact_id=r.getInt(1);
				phone=r.getString(2);
				if(phone!=null && phone.length()>1){
					phone=reemplazarCaracteres(phone);
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						perfil.addPhone(phone);
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.addPhone(phone);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... phones valido
			}//while... cada phones
			
			//organizations
			select_organizations_new.setInt(1, user_id);
//			select_organizations_new.setInt(2, user_id);
			r=select_organizations_new.executeQuery();
			while(r.next()){
				contact_id=r.getInt(1);
				orgName=r.getString(2);
				orgTitle=r.getString(3);
				orgTitle=null;
				jobStatus=r.getString(4);
				if(jobStatus==null || jobStatus.length()==0 || jobStatus.toLowerCase().equals("null")){
					jobStatus_type=0;
				}
				else if(jobStatus.toLowerCase().equals("current")){
					jobStatus_type=1;
				}
				else if(jobStatus.toLowerCase().equals("past")){
					jobStatus_type=2;
				}
				else{
					jobStatus_type=0;
				}
				//preparar string organization
				if(orgName!=null && orgTitle!=null){
					organization=orgName+" "+orgTitle;
				}
				else if(orgName!=null){
					organization=orgName;
				}
				else if(orgTitle!=null){
					organization=orgTitle;
				}
				else{
					organization=null;
				}
				if(organization!=null && organization.length()>1){
					organization=reemplazarCaracteres(organization);
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						perfil.addOrganization(organization, jobStatus_type);
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.addOrganization(organization, jobStatus_type);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... organization valido
			}//while... cada organization
			
			//education
			//por ahora, agregare de "education" a content del perfil (de forma similar a orgs)
			select_educations_new.setInt(1, user_id);
			r=select_educations_new.executeQuery();
			while(r.next()){
				//schoolName degree fieldOfStudy
				contact_id=r.getInt(1);
				String schoolName=r.getString(2);
				String fieldOfStudy=r.getString(3);
				//preparar string education
				String education=null;
				if(schoolName!=null && fieldOfStudy!=null){
					education=schoolName+" "+fieldOfStudy;
				}
				else if(schoolName!=null){
					education=schoolName;
				}
				else if(fieldOfStudy!=null){
					education=fieldOfStudy;
				}
				else{
					education=null;
				}
				if(education!=null && education.length()>1){
					education=reemplazarCaracteres(education);
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						perfil.addContent(education);
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.addContent(education);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... education valido
			}//while... cada education
			
		}
		catch(Exception e){
			System.out.println("ConexionMC.getPerfilesUsuarioNuevos - Error al cargar perfiles ("+e+")");
			e.printStackTrace();
		}
		
		Iterator<Map.Entry<Integer, DatoPerfilDoc>> it_perfiles;
		Map.Entry<Integer, DatoPerfilDoc> par_perfiles;
		it_perfiles=mapa_perfiles.entrySet().iterator();
		while(it_perfiles.hasNext()){
			par_perfiles=it_perfiles.next();
			perfil=par_perfiles.getValue();
//			System.out.println("ConexionMC.getPerfilesUsuarioNuevos - add: "+perfil+"");
			lista_retorno.add(perfil);
		}
		
		System.out.println("ConexionMC.getPerfilesUsuarioNuevos - fin ("+lista_retorno.size()+" perfiles cargados)");
		return lista_retorno;
		
	}
	
	public List<DatoPerfilDoc> getPerfilesUsuarioProcessing(int user_id){
		
//		System.out.println("ConexionMC.getPerfilesUsuarioProcessing - inicio (user: "+user_id+")");
		
		Set<Integer> perfiles_creados=new TreeSet<Integer>();
		Map<Integer, DatoPerfilDoc> mapa_perfiles=new TreeMap<Integer, DatoPerfilDoc>();
		List<DatoPerfilDoc> lista_retorno=new LinkedList<DatoPerfilDoc>();
		
		DatoPerfilDoc perfil;
		int contact_id;
		String full_name, address, network;
		String email, phone, organization;
		String orgName, orgTitle, jobStatus;
		String hometown, location;
		String twitter;
		int jobStatus_type=0;
		ResultSet r;
		
		try{
			
			//names
			select_fullNames_processing.setInt(1, user_id);
//			select_fullNames_processing.setInt(2, user_id);
			r=select_fullNames_processing.executeQuery();
			while(r.next()){
				contact_id=r.getInt(1);
				full_name=r.getString(2);
				if(full_name!=null && full_name.length()>1){
					full_name=reemplazarCaracteres(full_name);
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						//se dejara el mas largo
						if(perfil.getFullName()==null
							|| full_name.length() > perfil.getFullName().length()){
							perfil.setFullName(full_name);
						}
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.setFullName(full_name);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... nombre valido
			}//while... cada nombre
			
			//twitter
			select_twitter_processing.setInt(1, user_id);
			r=select_twitter_processing.executeQuery();
			while(r.next()){
				contact_id=r.getInt(1);
				twitter=r.getString(2);
				if(twitter!=null && twitter.length()>1){
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						perfil.setTwitter(twitter);
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.setTwitter(twitter);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... nombre valido
			}//while... cada nombre
			
			//addresses
			select_addresses_processing.setInt(1, user_id);
			r=select_addresses_processing.executeQuery();
			while(r.next()){
				contact_id=r.getInt(1);
				address=r.getString(2);
				if(address!=null && address.length()>1){
					address=reemplazarCaracteres(address);
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						//se dejara el mas largo
						if(perfil.getAddress()==null
							|| address.length() > perfil.getAddress().length()){
							perfil.setAddress(address);
						}
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.setAddress(address);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... address valido
			}//while... cada address
			
			//demographics
			select_demographics_processing.setInt(1, user_id);
			r=select_demographics_processing.executeQuery();
			while(r.next()){
				contact_id=r.getInt(1);
				hometown=r.getString(2);
				location=r.getString(3);
//				hometown=hometown+" "+location;
				
				if(hometown!=null && hometown.length()>1){
					hometown=reemplazarCaracteres(hometown);
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						//agrego a address
						perfil.setHometown(hometown);
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.setHometown(hometown);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... address valido
				
				if(location!=null && location.length()>1){
					location=reemplazarCaracteres(location);
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						//agrego a address
						perfil.setLives(location);
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.setLives(location);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... address valido
				
				
			}//while... cada address
			
			//networks
			select_networks_processing.setInt(1, user_id);
			r=select_networks_processing.executeQuery();
			while(r.next()){
				contact_id=r.getInt(1);
				network=r.getString(2);
				if(network!=null && network.length()>1){
					network=reemplazarCaracteres(network);
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						//se dejara el mas largo
						if(perfil.getNetwork()==null
							|| network.length() > perfil.getNetwork().length()){
							perfil.setNetwork(network);
						}
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.setNetwork(network);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... network valido
			}//while... cada network
			
			//emails
			select_emails_processing.setInt(1, user_id);
//			select_emails_processing.setInt(2, user_id);
			r=select_emails_processing.executeQuery();
			while(r.next()){
				contact_id=r.getInt(1);
				email=r.getString(2);
				if(email!=null && email.length()>1){
					email=reemplazarCaracteres(email);
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						perfil.addEmail(email);
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.addEmail(email);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... email valido
			}//while... cada email
			
			//phones
			select_phones_processing.setInt(1, user_id);
//			select_phones_processing.setInt(2, user_id);
			r=select_phones_processing.executeQuery();
			while(r.next()){
				contact_id=r.getInt(1);
				phone=r.getString(2);
				if(phone!=null && phone.length()>1){
					phone=reemplazarCaracteres(phone);
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						perfil.addPhone(phone);
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.addPhone(phone);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... phones valido
			}//while... cada phones
			
			//organizations
			select_organizations_processing.setInt(1, user_id);
//			select_organizations_processing.setInt(2, user_id);
			r=select_organizations_processing.executeQuery();
			while(r.next()){
				contact_id=r.getInt(1);
				orgName=r.getString(2);
				orgTitle=r.getString(3);
				orgTitle=null;
				jobStatus=r.getString(4);
				if(jobStatus==null || jobStatus.length()==0 || jobStatus.toLowerCase().equals("null")){
					jobStatus_type=0;
				}
				else if(jobStatus.toLowerCase().equals("current")){
					jobStatus_type=1;
				}
				else if(jobStatus.toLowerCase().equals("past")){
					jobStatus_type=2;
				}
				else{
					jobStatus_type=0;
				}
				//preparar string organization
				if(orgName!=null && orgTitle!=null){
					organization=orgName+" "+orgTitle;
				}
				else if(orgName!=null){
					organization=orgName;
				}
				else if(orgTitle!=null){
					organization=orgTitle;
				}
				else{
					organization=null;
				}
				if(organization!=null && organization.length()>1){
					organization=reemplazarCaracteres(organization);
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						perfil.addOrganization(organization, jobStatus_type);
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.addOrganization(organization, jobStatus_type);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... organization valido
			}//while... cada organization
			
			//education
			//por ahora, agregare de "education" a content del perfil (de forma similar a orgs)
			select_educations_processing.setInt(1, user_id);
			r=select_educations_processing.executeQuery();
			while(r.next()){
				//schoolName degree fieldOfStudy
				contact_id=r.getInt(1);
				String schoolName=r.getString(2);
				String fieldOfStudy=r.getString(3);
				//preparar string education
				String education=null;
				if(schoolName!=null && fieldOfStudy!=null){
					education=schoolName+" "+fieldOfStudy;
				}
				else if(schoolName!=null){
					education=schoolName;
				}
				else if(fieldOfStudy!=null){
					education=fieldOfStudy;
				}
				else{
					education=null;
				}
				if(education!=null && education.length()>1){
					education=reemplazarCaracteres(education);
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						perfil.addContent(education);
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.addContent(education);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... education valido
			}//while... cada education
			
			
		}
		catch(Exception e){
			System.out.println("ConexionMC.getPerfilesUsuarioProcessing - Error al cargar perfiles ("+e+")");
			e.printStackTrace();
		}
		
		Iterator<Map.Entry<Integer, DatoPerfilDoc>> it_perfiles;
		Map.Entry<Integer, DatoPerfilDoc> par_perfiles;
		it_perfiles=mapa_perfiles.entrySet().iterator();
		while(it_perfiles.hasNext()){
			par_perfiles=it_perfiles.next();
			perfil=par_perfiles.getValue();
//			System.out.println("ConexionMC.getPerfilesUsuarioProcessing - add: "+perfil+"");
			lista_retorno.add(perfil);
		}
		
		
//		System.out.println("ConexionMC.getPerfilesUsuarioProcessing - fin ("+lista_retorno.size()+" perfiles cargados)");
		return lista_retorno;
		
	}
	
	public List<DatoPerfilDoc> getPerfilesGrupo(int group_id){
		
//		System.out.println("ConexionMC.getPerfilesGrupo - inicio (group_id: "+group_id+")");
		
		Set<Integer> perfiles_creados=new TreeSet<Integer>();
		Map<Integer, DatoPerfilDoc> mapa_perfiles=new TreeMap<Integer, DatoPerfilDoc>();
		List<DatoPerfilDoc> lista_retorno=new LinkedList<DatoPerfilDoc>();
		
		//por eficiencia pido los atributos de todos los contactos del usuario
		//es decir, todos los nombres, todos los mails, etc.
		//todas retornan: (contact_id, datos) para asociarlos rapidamente
		
		//estos los omito
		//hay que unificar el nombre
//		private String updated;
//		private String givenName;
//		private String familyName;
//		private String content;
		
		DatoPerfilDoc perfil;
		int contact_id;
		String full_name, address, network;
		String email, phone, organization;
		String orgName, orgTitle, jobStatus;
		String hometown, location;
		String twitter;
		int jobStatus_type=0;
		ResultSet r;
		
//		private List<String> emails;
//		private List<String> phones;
//		private List<String> organizations;
		
		try{
			
			//names
			select_fullNames_group.setInt(1, group_id);
			r=select_fullNames_group.executeQuery();
			while(r.next()){
				contact_id=r.getInt(1);
				full_name=r.getString(2);
				if(full_name!=null && full_name.length()>1){
					full_name=reemplazarCaracteres(full_name);
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						//se dejara el mas largo
						if(perfil.getFullName()==null
							|| full_name.length() > perfil.getFullName().length()){
							perfil.setFullName(full_name);
						}
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.setFullName(full_name);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... nombre valido
			}//while... cada nombre
			
			//twitter
			select_twitter_group.setInt(1, group_id);
			r=select_twitter_group.executeQuery();
			while(r.next()){
				contact_id=r.getInt(1);
				twitter=r.getString(2);
				if(twitter!=null && twitter.length()>1){
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						perfil.setTwitter(twitter);
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.setTwitter(twitter);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... nombre valido
			}//while... cada nombre
			
			//addresses
			select_addresses_group.setInt(1, group_id);
			r=select_addresses_group.executeQuery();
			while(r.next()){
				contact_id=r.getInt(1);
				address=r.getString(2);
				if(address!=null && address.length()>1){
					address=reemplazarCaracteres(address);
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						//se dejara el mas largo
						if(perfil.getAddress()==null
							|| address.length() > perfil.getAddress().length()){
							perfil.setAddress(address);
						}
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.setAddress(address);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... address valido
			}//while... cada address
			
			//demographics
			select_demographics_group.setInt(1, group_id);
			r=select_demographics_group.executeQuery();
			while(r.next()){
				contact_id=r.getInt(1);
				hometown=r.getString(2);
				location=r.getString(3);
//				hometown=hometown+" "+location;
				
				if(hometown!=null && hometown.length()>1){
					hometown=reemplazarCaracteres(hometown);
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						//agrego a address
						perfil.setHometown(hometown);
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.setHometown(hometown);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... address valido

				if(location!=null && location.length()>1){
					location=reemplazarCaracteres(location);
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						//agrego a address
						perfil.setLives(location);
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.setLives(location);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... address valido
				
			}//while... cada address
			
			//networks
			select_networks_group.setInt(1, group_id);
			r=select_networks_group.executeQuery();
			while(r.next()){
				contact_id=r.getInt(1);
				network=r.getString(2);
				if(network!=null && network.length()>1){
					network=reemplazarCaracteres(network);
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						//se dejara el mas largo
						if(perfil.getNetwork()==null
							|| network.length() > perfil.getNetwork().length()){
							perfil.setNetwork(network);
						}
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.setNetwork(network);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... network valido
			}//while... cada network
			
			//emails
			select_emails_group.setInt(1, group_id);
			r=select_emails_group.executeQuery();
			while(r.next()){
				contact_id=r.getInt(1);
				email=r.getString(2);
				if(email!=null && email.length()>1){
					email=reemplazarCaracteres(email);
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						perfil.addEmail(email);
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.addEmail(email);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... email valido
			}//while... cada email
			
			//phones
			select_phones_group.setInt(1, group_id);
			r=select_phones_group.executeQuery();
			while(r.next()){
				contact_id=r.getInt(1);
				phone=r.getString(2);
				if(phone!=null && phone.length()>1){
					phone=reemplazarCaracteres(phone);
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						perfil.addPhone(phone);
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.addPhone(phone);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... phones valido
			}//while... cada phones
			
			//organizations
			select_organizations_group.setInt(1, group_id);
			r=select_organizations_group.executeQuery();
			while(r.next()){
				contact_id=r.getInt(1);
				orgName=r.getString(2);
				orgTitle=r.getString(3);
				orgTitle=null;
				jobStatus=r.getString(4);
				if(jobStatus==null || jobStatus.length()==0 || jobStatus.toLowerCase().equals("null")){
					jobStatus_type=0;
				}
				else if(jobStatus.toLowerCase().equals("current")){
					jobStatus_type=1;
				}
				else if(jobStatus.toLowerCase().equals("past")){
					jobStatus_type=2;
				}
				else{
					jobStatus_type=0;
				}
				//preparar string organization
				if(orgName!=null && orgTitle!=null){
					organization=orgName+" "+orgTitle;
				}
				else if(orgName!=null){
					organization=orgName;
				}
				else if(orgTitle!=null){
					organization=orgTitle;
				}
				else{
					organization=null;
				}
				if(organization!=null && organization.length()>1){
					organization=reemplazarCaracteres(organization);
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						perfil.addOrganization(organization, jobStatus_type);
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.addOrganization(organization, jobStatus_type);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... organization valido
			}//while... cada organization
			
			//education
			//por ahora, agregare de "education" a content del perfil (de forma similar a orgs)
			select_educations_group.setInt(1, group_id);
			r=select_educations_group.executeQuery();
			while(r.next()){
				//schoolName degree fieldOfStudy
				contact_id=r.getInt(1);
				String schoolName=r.getString(2);
				String fieldOfStudy=r.getString(3);
				//preparar string education
				String education=null;
				if(schoolName!=null && fieldOfStudy!=null){
					education=schoolName+" "+fieldOfStudy;
				}
				else if(schoolName!=null){
					education=schoolName;
				}
				else if(fieldOfStudy!=null){
					education=fieldOfStudy;
				}
				else{
					education=null;
				}
				if(education!=null && education.length()>1){
					education=reemplazarCaracteres(education);
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						perfil.addContent(education);
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.addContent(education);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... education valido
			}//while... cada education
			
			
		}
		catch(Exception e){
			System.out.println("ConexionMC.getPerfilesGrupo - Error al cargar perfiles ("+e+")");
			e.printStackTrace();
		}
		
		Iterator<Map.Entry<Integer, DatoPerfilDoc>> it_perfiles;
		Map.Entry<Integer, DatoPerfilDoc> par_perfiles;
		it_perfiles=mapa_perfiles.entrySet().iterator();
		while(it_perfiles.hasNext()){
			par_perfiles=it_perfiles.next();
			perfil=par_perfiles.getValue();
//			System.out.println("ConexionMC.getPerfilesUsuario - add: "+perfil+"");
			lista_retorno.add(perfil);
		}
		
		
//		System.out.println("ConexionMC.getPerfilesUsuario - fin ("+lista_retorno.size()+" perfiles cargados)");
		return lista_retorno;
		
	}
	
	public List<DatoPerfilDoc> getPerfilesUsuario(int user_id){
		
//		System.out.println("ConexionMC.getPerfilesUsuario - inicio (user: "+user_id+")");
		
		Set<Integer> perfiles_creados=new TreeSet<Integer>();
		Map<Integer, DatoPerfilDoc> mapa_perfiles=new TreeMap<Integer, DatoPerfilDoc>();
		List<DatoPerfilDoc> lista_retorno=new LinkedList<DatoPerfilDoc>();
		
		//por eficiencia pido los atributos de todos los contactos del usuario
		//es decir, todos los nombres, todos los mails, etc.
		//todas retornan: (contact_id, datos) para asociarlos rapidamente
		
		//estos los omito
		//hay que unificar el nombre
//		private String updated;
//		private String givenName;
//		private String familyName;
//		private String content;
		
		DatoPerfilDoc perfil;
		int contact_id;
		String full_name, address, network;
		String email, phone, organization;
		String orgName, orgTitle, jobStatus;
		String hometown, location;
		String twitter;
		int jobStatus_type=0;
		ResultSet r;
		
//		private List<String> emails;
//		private List<String> phones;
//		private List<String> organizations;
		
		try{
			
			//names
			select_fullNames.setInt(1, user_id);
			r=select_fullNames.executeQuery();
			while(r.next()){
				contact_id=r.getInt(1);
				full_name=r.getString(2);
				if(full_name!=null && full_name.length()>1){
					full_name=reemplazarCaracteres(full_name);
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						//se dejara el mas largo
						if(perfil.getFullName()==null
							|| full_name.length() > perfil.getFullName().length()){
							perfil.setFullName(full_name);
						}
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.setFullName(full_name);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... nombre valido
			}//while... cada nombre
			
			//twitter
			select_twitter.setInt(1, user_id);
			r=select_twitter.executeQuery();
			while(r.next()){
				contact_id=r.getInt(1);
				twitter=r.getString(2);
				if(twitter!=null && twitter.length()>1){
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						perfil.setTwitter(twitter);
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.setTwitter(twitter);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... nombre valido
			}//while... cada nombre
			
			//addresses
			select_addresses.setInt(1, user_id);
			r=select_addresses.executeQuery();
			while(r.next()){
				contact_id=r.getInt(1);
				address=r.getString(2);
				if(address!=null && address.length()>1){
					address=reemplazarCaracteres(address);
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						//se dejara el mas largo
						if(perfil.getAddress()==null
							|| address.length() > perfil.getAddress().length()){
							perfil.setAddress(address);
						}
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.setAddress(address);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... address valido
			}//while... cada address
			
			//demographics
			select_demographics.setInt(1, user_id);
			r=select_demographics.executeQuery();
			while(r.next()){
				contact_id=r.getInt(1);
				hometown=r.getString(2);
				location=r.getString(3);
//				hometown=hometown+" "+location;

				if(hometown!=null && hometown.length()>1){
					hometown=reemplazarCaracteres(hometown);
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						//agrego a address
						perfil.setHometown(hometown);
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.setHometown(hometown);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... address valido

				if(location!=null && location.length()>1){
					location=reemplazarCaracteres(location);
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						//agrego a address
						perfil.setLives(location);
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.setLives(location);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... address valido
				
				
			}//while... cada address
			
			//networks
			select_networks.setInt(1, user_id);
			r=select_networks.executeQuery();
			while(r.next()){
				contact_id=r.getInt(1);
				network=r.getString(2);
				if(network!=null && network.length()>1){
					network=reemplazarCaracteres(network);
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						//se dejara el mas largo
						if(perfil.getNetwork()==null
							|| network.length() > perfil.getNetwork().length()){
							perfil.setNetwork(network);
						}
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.setNetwork(network);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... network valido
			}//while... cada network
			
			//emails
			select_emails.setInt(1, user_id);
			r=select_emails.executeQuery();
			while(r.next()){
				contact_id=r.getInt(1);
				email=r.getString(2);
				if(email!=null && email.length()>1){
					email=reemplazarCaracteres(email);
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						perfil.addEmail(email);
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.addEmail(email);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... email valido
			}//while... cada email
			
			//phones
			select_phones.setInt(1, user_id);
			r=select_phones.executeQuery();
			while(r.next()){
				contact_id=r.getInt(1);
				phone=r.getString(2);
				if(phone!=null && phone.length()>1){
					phone=reemplazarCaracteres(phone);
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						perfil.addPhone(phone);
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.addPhone(phone);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... phones valido
			}//while... cada phones
			
			//organizations
			select_organizations.setInt(1, user_id);
			r=select_organizations.executeQuery();
			while(r.next()){
				contact_id=r.getInt(1);
				orgName=r.getString(2);
				orgTitle=r.getString(3);
				orgTitle=null;
				jobStatus=r.getString(4);
				if(jobStatus==null || jobStatus.length()==0 || jobStatus.toLowerCase().equals("null")){
					jobStatus_type=0;
				}
				else if(jobStatus.toLowerCase().equals("current")){
					jobStatus_type=1;
				}
				else if(jobStatus.toLowerCase().equals("past")){
					jobStatus_type=2;
				}
				else{
					jobStatus_type=0;
				}
				//preparar string organization
				if(orgName!=null && orgTitle!=null){
					organization=orgName+" "+orgTitle;
				}
				else if(orgName!=null){
					organization=orgName;
				}
				else if(orgTitle!=null){
					organization=orgTitle;
				}
				else{
					organization=null;
				}
				if(organization!=null && organization.length()>1){
					organization=reemplazarCaracteres(organization);
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						perfil.addOrganization(organization, jobStatus_type);
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.addOrganization(organization, jobStatus_type);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... organization valido
			}//while... cada organization
			
			//education
			//por ahora, agregare de "education" a content del perfil (de forma similar a orgs)
			select_educations.setInt(1, user_id);
			r=select_educations.executeQuery();
			while(r.next()){
				//schoolName degree fieldOfStudy
				contact_id=r.getInt(1);
				String schoolName=r.getString(2);
				String fieldOfStudy=r.getString(3);
				//preparar string education
				String education=null;
				if(schoolName!=null && fieldOfStudy!=null){
					education=schoolName+" "+fieldOfStudy;
				}
				else if(schoolName!=null){
					education=schoolName;
				}
				else if(fieldOfStudy!=null){
					education=fieldOfStudy;
				}
				else{
					education=null;
				}
				if(education!=null && education.length()>1){
					education=reemplazarCaracteres(education);
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						perfil.addContent(education);
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.addContent(education);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... education valido
			}//while... cada education
			
			
		}
		catch(Exception e){
			System.out.println("ConexionMC.getPerfilesUsuario - Error al cargar perfiles ("+e+")");
			e.printStackTrace();
		}
		
		Iterator<Map.Entry<Integer, DatoPerfilDoc>> it_perfiles;
		Map.Entry<Integer, DatoPerfilDoc> par_perfiles;
		it_perfiles=mapa_perfiles.entrySet().iterator();
		while(it_perfiles.hasNext()){
			par_perfiles=it_perfiles.next();
			perfil=par_perfiles.getValue();
//			System.out.println("ConexionMC.getPerfilesUsuario - add: "+perfil+"");
			lista_retorno.add(perfil);
		}
		
		
//		System.out.println("ConexionMC.getPerfilesUsuario - fin ("+lista_retorno.size()+" perfiles cargados)");
		return lista_retorno;
		
	}
	
	private String reemplazarCaracteres(String texto){
		texto=texto.replace('á', 'a');
		texto=texto.replace('é', 'e');
		texto=texto.replace('í', 'i');
		texto=texto.replace('ó', 'o');
		texto=texto.replace('ú', 'u');
		texto=texto.replace('ñ', 'n');
		return texto.toLowerCase();
	}
	
	
	public List<Affinity> getAffinities(){
		
		System.out.println("ConexionMC.getAffinities - inicio");
		
		Map<Integer, Map<String, Double>> map_affinities=new TreeMap<Integer, Map<String, Double>>();
		int affinity_id;
		Map<String, Double> voc=null;
		String term;
		double valor;
		
		ResultSet r;
		
		try{
			//select (affinity_id, term, value) from data_affinities
			Statement s=conexion.createStatement();
			r=s.executeQuery("select affinity_id, term, value from data_affinities");
			while(r.next()){
				affinity_id=r.getInt(1);
				term=r.getString(2);
				valor=r.getFloat(3);
				if(map_affinities.containsKey(affinity_id)){
					voc=map_affinities.get(affinity_id);
					voc.put(term, valor);
				}
				else{
					voc=new TreeMap<String, Double>();
					voc.put(term, valor);
					map_affinities.put(affinity_id, voc);
				}
			}
			s.close();
		}
		catch(Exception e){
			System.out.println("ConexionMC.getAffinities - Error al cargar ("+e+")");
			e.printStackTrace();
		}
		
		List<Affinity> affinities=new LinkedList<Affinity>();
		
		for(Map.Entry<Integer, Map<String, Double>> par : map_affinities.entrySet()){
			affinity_id=par.getKey();
			voc=par.getValue();
			Affinity aff=new Affinity(voc);
			aff.setId(affinity_id);
			affinities.add(aff);
		}
		
		
		System.out.println("ConexionMC.getAffinities - fin");
		
		return affinities;
		
	}
	
	public void guardarAffinityGroup(int affinity_id, int group_id, double value){
		//insert into data_affinities_groups (affinity_id, group_id, value) values (affinity_id, group_id, value)
		
		ResultSet r;
		
		try{
			
			select_affinities_groups.setInt(1, affinity_id);
			select_affinities_groups.setInt(2, group_id);
			r=select_affinities_groups.executeQuery();
			if(r.next()){
				float old_value=r.getFloat(1);
				if(old_value!=value){
					update_affinities_groups.setFloat(1, (float)value);
					update_affinities_groups.setInt(2, affinity_id);
					update_affinities_groups.setInt(3, group_id);
					update_affinities_groups.executeUpdate();
				}
			}
			else{
				insert_affinities_groups.setInt(1, affinity_id);
				insert_affinities_groups.setInt(2, group_id);
				insert_affinities_groups.setFloat(3, (float)value);
				insert_affinities_groups.executeUpdate();
			}
		}
		catch(Exception e){
			System.out.println("ConexionMC.guardarAffinityGroup - Error ("+e+")");
			e.printStackTrace();
		}
		
	}
	
	public void guardarAffinities(List<Affinity> affinities){
		System.out.println("ConexionMC.guardarAffinities - inicio");
		
		Iterator<Affinity> it_affinities;
		Affinity affinity;
		Iterator<Map.Entry<String, Double>> it_voc;
		Map.Entry<String, Double> par_voc;
		
		String term=null;
		double valor=0.0;
		
		int new_id, affinity_id;
		boolean agregar;
		
		Map<String, Double> voc;
		Affinity aff1, aff2;
		
		ResultSet r1, r2;
		
		try{
			
			//por cada afinidad
			//buscarlo en el indice
			//por cada match encontrado
			//pedir el exacto y comprar
			
			it_affinities=affinities.iterator();
			while(it_affinities.hasNext()){
				aff1=it_affinities.next();
				
				agregar=true;
				
				String terms="";
				it_voc=aff1.iterator();
				while(it_voc.hasNext()){
					par_voc=it_voc.next();
					term=par_voc.getKey();
					valor=par_voc.getValue();
					terms+=term;
				}
				if(terms.length()>120){
					terms=terms.substring(0, 120);
				}
				
				//select affinity_id from data_affinities_index where n_terms=? and terms=?
				
				select_affinities_index.setInt(1, aff1.size());
				select_affinities_index.setString(2, terms);
				
				r1=select_affinities_index.executeQuery();
				while(r1.next()){
					affinity_id=r1.getInt(1);
					
					voc=new TreeMap<String, Double>();
					
					//select term, value from data_affinities where affinity_id=?
					
					select_affinities.setInt(1, affinity_id);
					r2=select_affinities.executeQuery();
					while(r2.next()){
						term=r2.getString(1);
						valor=r2.getFloat(2);
						voc.put(term, valor);
					}
					
					aff2=new Affinity(voc);
					
					//comparar ambas afinidades
					if(aff1.similar(aff2)){
						agregar=false;
						break;
					}
					
				}//while... cada candidato a revisar
				
				if(agregar){
					//pedir nuevo id
					new_id=getMaxAffinityId();
					new_id++;
					
					//insert into data_affinities_index (affinity_id, n_terms, terms) values (?, ?, ?)
					
					insert_affinities_index.setInt(1, new_id);
					insert_affinities_index.setInt(2, aff1.size());
					insert_affinities_index.setString(3, terms);
					insert_affinities_index.executeUpdate();
					
					it_voc=aff1.iterator();
					while(it_voc.hasNext()){
						par_voc=it_voc.next();
						term=par_voc.getKey();
						valor=par_voc.getValue();
						
						//insert into data_affinities (affinity_id, term, value) values (?, ?, ?)
						insert_affinities.setInt(1, new_id);
						insert_affinities.setString(2, term);
						insert_affinities.setFloat(3, (float)valor);
						insert_affinities.executeUpdate();
					}
					
				}
				
			}//while... cada afinidad
			
		}
		catch(Exception e){
			System.out.println("ConexionMC.guardarAffinities - Error al guardar vocabulario ("+e+")");
			e.printStackTrace();
		}
		
		System.out.println("ConexionMC.guardarAffinities - fin");
		
	}
	
//	public void guardarAffinities(List<Map<String, Double>> grupos){
//		System.out.println("ConexionMC.guardarAffinities - inicio");
//		
//		Iterator<Map<String, Double>> it_grupos;
//		Map<String, Double> grupo;
//		Iterator<Map.Entry<String, Double>> it_grupo;
//		Map.Entry<String, Double> par_grupo;
//		
//		String term=null;
//		double valor=0.0;
//		
//		int new_id, affinity_id;
//		boolean agregar;
//		
//		Map<String, Double> voc;
//		Affinity aff1, aff2;
//		
//		ResultSet r1, r2;
//		
//		try{
//			
//			//por cada grupo
//			//buscarlo en el indice
//			//por cada encontrado
//			//pedir el exacto y comprar
//			
//			it_grupos=grupos.iterator();
//			while(it_grupos.hasNext()){
//				grupo=it_grupos.next();
//				aff1=new Affinity(grupo);
//				
//				agregar=true;
//				
//				String terms="";
//				it_grupo=grupo.entrySet().iterator();
//				while(it_grupo.hasNext()){
//					par_grupo=it_grupo.next();
//					term=par_grupo.getKey();
//					valor=par_grupo.getValue();
//					terms+=term;
//				}
//				if(terms.length()>120){
//					terms=terms.substring(0, 120);
//				}
//				
//				//select affinity_id from data_affinities_index where n_terms=? and terms=?
//				
//				select_affinities_index.setInt(1, grupo.size());
//				select_affinities_index.setString(2, terms);
//				
//				r1=select_affinities_index.executeQuery();
//				while(r1.next()){
//					affinity_id=r1.getInt(1);
//					
//					voc=new TreeMap<String, Double>();
//					
//					//select term, value from data_affinities where affinity_id=?
//					
//					select_affinities.setInt(1, affinity_id);
//					r2=select_affinities.executeQuery();
//					while(r2.next()){
//						term=r2.getString(1);
//						valor=r2.getFloat(2);
//						voc.put(term, valor);
//					}
//					
//					aff2=new Affinity(voc);
//					
//					//comparar ambas grupos
//					if(aff1.similar(aff2)){
//						agregar=false;
//						break;
//					}
//					
//				}//while... cada candidato a revisar
//				
//				if(agregar){
//					//pedir nuevo id
//					new_id=getMaxAffinityId();
//					new_id++;
//					
//					//insert into data_affinities_index (affinity_id, n_terms, terms) values (?, ?, ?)
//					
//					insert_affinities_index.setInt(1, new_id);
//					insert_affinities_index.setInt(2, grupo.size());
//					insert_affinities_index.setString(3, terms);
//					insert_affinities_index.executeUpdate();
//					
//					it_grupo=grupo.entrySet().iterator();
//					while(it_grupo.hasNext()){
//						par_grupo=it_grupo.next();
//						term=par_grupo.getKey();
//						valor=par_grupo.getValue();
//						
//						//insert into data_affinities (affinity_id, term, value) values (?, ?, ?)
//						insert_affinities.setInt(1, new_id);
//						insert_affinities.setString(2, term);
//						insert_affinities.setFloat(3, (float)valor);
//						insert_affinities.executeUpdate();
//					}
//					
//				}
//				
//			}//while... cada grupo
//			
//		}
//		catch(Exception e){
//			System.out.println("ConexionMC.guardarAffinities - Error al guardar vocabulario ("+e+")");
//			e.printStackTrace();
//		}
//		
//		System.out.println("ConexionMC.guardarAffinities - fin");
//		
//		
//	}
	
	public void guardarVocabulario(int user_id, int contact_id, Map<String, Double> voc){
//		System.out.println("ConexionMC.guardarVocabulario - inicio (user: "+user_id+", contact: "+contact_id+")");
		
		Iterator<Map.Entry<String, Double>> it_voc;
		Map.Entry<String, Double> par_voc;
		
		String term=null;
		double valor=0.0;
		
		try{
			//borrar el vocabulario
			delete_voc.setInt(1, contact_id);
			delete_voc.executeUpdate();
			
			//guardar cada termino del voc
			insert_voc.setInt(1, user_id);
			insert_voc.setInt(2, contact_id);
			it_voc=voc.entrySet().iterator();
			while(it_voc.hasNext()){
				par_voc=it_voc.next();
				term=par_voc.getKey();
				valor=par_voc.getValue();
				
				insert_voc.setString(3, term);
				//insert_voc.setString(3, new String(term.getBytes(), "UTF-8"));
				insert_voc.setFloat(4, (float)valor);
				insert_voc.executeUpdate();
				
			}//while... cada termino
			
		}
		catch(Exception e){
			System.out.println("ConexionMC.guardarVocabulario - Error al guardar vocabulario (term: "+term+", valor: "+valor+") ("+e+")");
			e.printStackTrace();
		}
		
//		System.out.println("ConexionMC.guardarVocabulario - fin");
	}
	
	public Map<Integer, Map<String, Map<String, Double>>> getVocabulariosGrupo(int group_id){
//		System.out.println("ConexionMC.getVocabulariosGrupo - inicio (user "+user_id+")");
		
		Map<Integer, Map<String, Map<String, Double>>> vocabularios=new TreeMap<Integer, Map<String, Map<String, Double>>>();
		Map<String, Map<String, Double>> mapa_atributos;
		
		ResultSet r=null;
		int contact_id=0;
		String atributo=null;
		String term=null;
		double valor=0;
		
		try{
			select_vocs_grupo.setInt(1, group_id);
			r=select_vocs_grupo.executeQuery();
			while(r.next()){
				contact_id=r.getInt(1);
				atributo=r.getString(2);
				term=r.getString(3);
				valor=r.getFloat(4);
				if(atributo.compareTo("name")!=0){
					if(vocabularios.containsKey(contact_id)){
						mapa_atributos=vocabularios.get(contact_id);
						if(mapa_atributos.containsKey(atributo)){
							mapa_atributos.get(atributo).put(term, valor);
						}
						else{
							Map<String, Double> vocabulario=new TreeMap<String, Double>();
							vocabulario.put(term, valor);
							mapa_atributos.put(atributo, vocabulario);
						}
					}
					else{
						mapa_atributos=new TreeMap<String, Map<String, Double>>();
						Map<String, Double> vocabulario=new TreeMap<String, Double>();
						vocabulario.put(term, valor);
						mapa_atributos.put(atributo, vocabulario);
						vocabularios.put(contact_id, mapa_atributos);
					}
				}//if... atributo != nombre
			}
		}
		catch(Exception e){
			System.out.println("ConexionMC.getVocabulariosGrupo - Error al cargar vocabulario (contact_id: "+contact_id+", term: "+term+", valor: "+valor+") ("+e+")");
			e.printStackTrace();
		}
		
//		System.out.println("ConexionMC.getVocabulariosGrupo - fin");
		return vocabularios;
	}
	
	public Map<Integer, Map<String, Double>> cargarVocabularios(int user_id){
//		System.out.println("ConexionMC.cargarVocabularios - inicio (user "+user_id+")");
		
		Map<Integer, Map<String, Double>> vocabularios=new TreeMap<Integer, Map<String, Double>>();
		
		ResultSet r=null;
		int contact_id=0;
		String term=null;
		double valor=0;
		
		try{
			select_vocs.setInt(1, user_id);
			r=select_vocs.executeQuery();
			while(r.next()){
				contact_id=r.getInt(1);
				term=(r.getString(2)).toLowerCase();
				valor=r.getFloat(3);
				if(vocabularios.containsKey(contact_id)){
					vocabularios.get(contact_id).put(term, valor);
				}
				else{
					Map<String, Double> vocabulario=new TreeMap<String, Double>();
					vocabulario.put(term, valor);
					vocabularios.put(contact_id, vocabulario);
				}
			}
		}
		catch(Exception e){
			System.out.println("ConexionMC.cargarVocabularios - Error al cargar vocabulario (contact_id: "+contact_id+", term: "+term+", valor: "+valor+") ("+e+")");
			e.printStackTrace();
		}
		
//		System.out.println("ConexionMC.cargarVocabularios - fin");
		return vocabularios;
	}
	
	public void guardarVocabulario(int user_id, String atributo, int contact_id, Map<String, Double> voc){
//		System.out.println("ConexionMC.guardarVocabulario - inicio (user: "+user_id+", contact: "+contact_id+")");
		
		Iterator<Map.Entry<String, Double>> it_voc;
		Map.Entry<String, Double> par_voc;
		
		ResultSet r=null;
		
		String term=null;
		double valor=0.0;
		
		try{
			//borrar (lazy) el vocabulario (por performance)
			
			delete_voc_at_lazy.setInt(1, user_id);
			delete_voc_at_lazy.setInt(2, contact_id);
			delete_voc_at_lazy.setString(3, atributo);
			delete_voc_at_lazy.executeUpdate();
			
			//Lo siguiente podria hacerse con "insert ... on duplicate key update"
			//No lo implemento asi por el momento por dudas de la key
			
			//buscar cada termino del voc
			buscar_voc_at.setInt(1, user_id);
			buscar_voc_at.setInt(2, contact_id);
			buscar_voc_at.setString(3, atributo);
			
			//update cada termino del voc
			update_voc_at.setInt(2, user_id);
			update_voc_at.setInt(3, contact_id);
			update_voc_at.setString(4, atributo);
			
			//guardar cada termino del voc
			insert_voc_at.setInt(1, user_id);
			insert_voc_at.setInt(2, contact_id);
			insert_voc_at.setString(3, atributo);
			
			it_voc=voc.entrySet().iterator();
			while(it_voc.hasNext()){
				par_voc=it_voc.next();
				term=par_voc.getKey();
				valor=par_voc.getValue();
				
				buscar_voc_at.setString(4, term);
				r=buscar_voc_at.executeQuery();
				if(r.next()){
					//update
					update_voc_at.setFloat(1, (float)valor);
					update_voc_at.setString(5, term);
					update_voc_at.executeUpdate();
				}
				else{
					//insert
					insert_voc_at.setString(4, term);
					insert_voc_at.setFloat(5, (float)valor);
					insert_voc_at.executeUpdate();
				}
				
			}//while... cada termino
			
			//borrar los terminos de bd que no esten en el mapa
			
		}
		catch(Exception e){
			System.out.println("ConexionMC.guardarVocabulario - Error al guardar vocabulario (term: "+term+", valor: "+valor+") ("+e+")");
			e.printStackTrace();
		}
		
//		System.out.println("ConexionMC.guardarVocabulario - fin");
	}
	
	public Map<Integer, Map<String, Double>> cargarVocabularios(int user_id, String atributo){
//		System.out.println("ConexionMC.cargarVocabularios - inicio (user "+user_id+")");
		
		Map<Integer, Map<String, Double>> vocabularios=new TreeMap<Integer, Map<String, Double>>();
		
		ResultSet r=null;
		int contact_id=0;
		String term=null;
		double valor=0;
		
		try{
			
			select_vocs_at.setInt(1, user_id);
			select_vocs_at.setString(2, atributo);
			r=select_vocs_at.executeQuery();
			while(r.next()){
				contact_id=r.getInt(1);
				term=(r.getString(2)).toLowerCase();
				valor=r.getFloat(3);
				if(vocabularios.containsKey(contact_id)){
					vocabularios.get(contact_id).put(term, valor);
				}
				else{
					Map<String, Double> vocabulario=new TreeMap<String, Double>();
					vocabulario.put(term, valor);
					vocabularios.put(contact_id, vocabulario);
				}
			}
		}
		catch(Exception e){
			System.out.println("ConexionMC.cargarVocabularios - Error al cargar vocabulario (contact_id: "+contact_id+", term: "+term+", valor: "+valor+") ("+e+")");
			e.printStackTrace();
		}
		
//		System.out.println("ConexionMC.cargarVocabularios - fin");
		return vocabularios;
	}
	
	public Map<String, Integer> cargarVocabularioGlobalConcurrente(){
		System.out.println("ConexionMC.cargarVocabularioGlobal - inicio");
		
		Map<String, Integer> vocabulario = new ConcurrentHashMap<String, Integer>();
		
		ResultSet r=null;
		String term=null;
		int count=0;
		
		try{
			Statement s=conexion.createStatement();
			r=s.executeQuery("select term, count from data_globalVocabulary where count >= 5");
			while(r.next()){
				term=(r.getString(1)).toLowerCase();
				count=r.getInt(2);
				vocabulario.put(term, count);
			}
		}
		catch(Exception e){
			System.out.println("ConexionMC.cargarVocabularioGlobal - Error al cargar vocabulario (r: "+r+", term: "+term+", count: "+count+") ("+e+")");
			e.printStackTrace();
		}
		
		System.out.println("ConexionMC.cargarVocabularioGlobal - fin ("+vocabulario.size()+" terminos)");
		return vocabulario;
	}
	
	public Map<String, Integer> cargarVocabularioGlobal(){
		System.out.println("ConexionMC.cargarVocabularioGlobal - inicio");
		
		Map<String, Integer> vocabulario = new TreeMap<String, Integer>();
		
		ResultSet r = null;
		String term = null;
		int count=0;
		
		try{
			Statement s=conexion.createStatement();
			r = s.executeQuery("select term, count from data_globalVocabulary where count >= 5");
			while(r.next()){
				term = (r.getString(1)).toLowerCase();
				count = r.getInt(2);
				vocabulario.put(term, count);
			}
		}
		catch(Exception e){
			System.out.println("ConexionMC.cargarVocabularioGlobal - Error al cargar vocabulario (r: "+r+", term: "+term+", count: "+count+") ("+e+")");
			e.printStackTrace();
		}
		
		System.out.println("ConexionMC.cargarVocabularioGlobal - fin ("+vocabulario.size()+" terminos)");
		return vocabulario;
	}
	
	public void guardarVocabularioGlobalModificado(Map<String, Integer> vocabulario){
		System.out.println("ConexionMC.guardarVocabularioGlobalModificado - inicio ("+vocabulario.size()+" terminos)");
		
		Map.Entry<String, Integer> par_voc;
		Iterator<Map.Entry<String, Integer>> it_voc;
		
		ResultSet r=null;
		String term=null;
		int count=0;
		
		try{
			
			it_voc=vocabulario.entrySet().iterator();
			while(it_voc.hasNext()){
				par_voc=it_voc.next();
				term=par_voc.getKey();
				count=par_voc.getValue();
				
				buscar_voc_global.setString(1, term);
				r=buscar_voc_global.executeQuery();
				if(r.next()){
					//existe, update
					update_voc_global.setInt(1, count);
					update_voc_global.setString(2, term);
					update_voc_global.executeUpdate();
				}
				else{
					//no existe, insert
					insert_voc_global.setString(1, term);
					insert_voc_global.setInt(2, count);
					insert_voc_global.executeUpdate();
				}
				
			}
		}
		catch(Exception e){
			System.out.println("ConexionMC.guardarVocabularioGlobalModificado - Error al guardar voc (term: "+term+", count: "+count+") ("+e+")");
			e.printStackTrace();
		}
		
		System.out.println("ConexionMC.guardarVocabularioGlobalModificado - fin");
	}
	
	public void guardarVocabularioGlobal(Map<String, Integer> vocabulario){
		System.out.println("ConexionMC.guardarVocabularioGlobal - inicio ("+vocabulario.size()+" terminos)");
		
		ResultSet r = null;
		String term = null;
		int count=0;
		
		try{
			System.out.println("ConexionMC.guardarVocabularioGlobal - vaciando voc global");
			
			Statement s=conexion.createStatement();
			s.executeUpdate("truncate table data_globalVocabulary");
			
			System.out.println("ConexionMC.guardarVocabularioGlobal - guardando terminos");
			
			int n_partes = 20;
			int parte = vocabulario.size()/n_partes;
			int completado = 1;
			int terms_guardados = 0;
			
			for(Map.Entry<String, Integer> par_voc : vocabulario.entrySet()){
				
				if( terms_guardados++ > parte*completado){
					System.out.println("ConexionMC.guardarVocabularioGlobal - "+completado*100/n_partes+"% completado");
					completado++;
				}
				
				term = par_voc.getKey();
				count = par_voc.getValue();
				
				if(term.length() > 100){
					term = term.substring(0, 100);
				}
				insert_voc_global.setString(1, term);
				insert_voc_global.setInt(2, count);
				try{
					insert_voc_global.executeUpdate();
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		catch(Exception e){
			System.out.println("ConexionMC.guardarVocabularioGlobal - Error al guardar vocabulario (term: "+term+", count: "+count+") ("+e+")");
			e.printStackTrace();
		}
		
		System.out.println("ConexionMC.guardarVocabularioGlobal - fin");
	}
	
	public List<DatoPerfilDoc> getPerfilesReducidosUsuario(int user_id){
		
//		System.out.println("ConexionMC.getPerfilesUsuario - inicio (user: "+user_id+")");
		
		Set<Integer> perfiles_creados=new TreeSet<Integer>();
		Map<Integer, DatoPerfilDoc> mapa_perfiles=new TreeMap<Integer, DatoPerfilDoc>();
		List<DatoPerfilDoc> lista_retorno=new LinkedList<DatoPerfilDoc>();
		
		//por eficiencia pido los atributos de todos los contactos del usuario
		//es decir, todos los nombres, todos los mails, etc.
		//todas retornan: (contact_id, datos) para asociarlos rapidamente
		
		//estos los omito
		//hay que unificar el nombre
//		private String updated;
//		private String givenName;
//		private String familyName;
//		private String content;
		
		DatoPerfilDoc perfil;
		int contact_id;
		String full_name, network;
		ResultSet r;
		
		try{
			
			//names
			select_fullNames.setInt(1, user_id);
			r=select_fullNames.executeQuery();
			while(r.next()){
				contact_id=r.getInt(1);
				full_name=r.getString(2);
				if(full_name!=null && full_name.length()>1){
					full_name=reemplazarCaracteres(full_name);
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						//se dejara el mas largo
						if(perfil.getFullName()==null
							|| full_name.length() > perfil.getFullName().length()){
							perfil.setFullName(full_name);
						}
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.setFullName(full_name);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... nombre valido
			}//while... cada nombre
			
			//networks
			select_networks.setInt(1, user_id);
			r=select_networks.executeQuery();
			while(r.next()){
				contact_id=r.getInt(1);
				network=r.getString(2);
				if(network!=null && network.length()>1){
					network=reemplazarCaracteres(network);
					if(perfiles_creados.contains(contact_id)){
						perfil=mapa_perfiles.get(contact_id);
						//se dejara el mas largo
						if(perfil.getNetwork()==null
							|| network.length() > perfil.getNetwork().length()){
							perfil.setNetwork(network);
						}
					}//if... perfil existe (agregar)
					else{
						perfil=new DatoPerfilDoc();
						perfil.setId(contact_id);
						perfil.setNetwork(network);
						mapa_perfiles.put(contact_id, perfil);
						perfiles_creados.add(contact_id);
					}//else... nuevo perfil
				}//if... network valido
			}//while... cada network
			
		}
		catch(Exception e){
			System.out.println("ConexionMC.getPerfilesUsuario - Error al cargar perfiles ("+e+")");
			e.printStackTrace();
		}
		
		Iterator<Map.Entry<Integer, DatoPerfilDoc>> it_perfiles;
		Map.Entry<Integer, DatoPerfilDoc> par_perfiles;
		it_perfiles=mapa_perfiles.entrySet().iterator();
		while(it_perfiles.hasNext()){
			par_perfiles=it_perfiles.next();
			perfil=par_perfiles.getValue();
//			System.out.println("ConexionMC.getPerfilesUsuario - add: "+perfil+"");
			lista_retorno.add(perfil);
		}
		
		
//		System.out.println("ConexionMC.getPerfilesUsuario - fin ("+lista_retorno.size()+" perfiles cargados)");
		return lista_retorno;
		
	}
	
	public void actualizarAsGroup(int group_id_old, int group_id_new){
		
		//este metodo tambien debe actualizar otras referencias de group_id (como exact_groups)
		
		ResultSet r;
		
		try{
			//existe, update
			
			update_as_group_simple.setInt(1, group_id_new);
			update_as_group_simple.setInt(2, group_id_old);
			update_as_group_simple.executeUpdate();
			
			update_exact_groups.setInt(1, group_id_new);
			update_exact_groups.setInt(2, group_id_old);
			update_exact_groups.executeUpdate();
			
		}
		catch(Exception e){
			System.out.println("ConexionMC.actualizarAsGroup - Error al guardar ("+e+")");
			e.printStackTrace();
		}
		
//		System.out.println("ConexionMC.actualizarAsGroup - fin");
	}
	
	public void agregarAsGroup(int contact_id, int group_id, int user_id){
		
		ResultSet r;
		
		try{
			buscar_as_group.setInt(1, contact_id);
			r=buscar_as_group.executeQuery();
			if(r.next()){
				//existe, update
				update_as_group.setInt(1, group_id);
				update_as_group.setInt(2, user_id);
				update_as_group.setInt(3, contact_id);
				update_as_group.executeUpdate();
			}
			else{
				//no existe, insert
				insert_as_group.setInt(1, contact_id);
				insert_as_group.setInt(2, group_id);
				insert_as_group.setInt(3, user_id);
				insert_as_group.executeUpdate();
			}
			
		}
		catch(Exception e){
			System.out.println("ConexionMC.agregarAsGroup - Error al guardar ("+e+")");
			e.printStackTrace();
		}
		
//		System.out.println("ConexionMC.agregarAsGroup - fin");
	}
	
	public int getUsuarioContacto(int contact_id){
		
		ResultSet r;
		int user_id=-1;
		
		try{
			select_usuario_contacto.setInt(1, contact_id);
			r=select_usuario_contacto.executeQuery();
			if(r.next()){
				user_id=r.getInt(1);
			}
			else{
				System.out.println("ConexionMC.getUsuarioContacto - user_id de contacto "+contact_id+" no encontrado (-1)");
			}
		}
		catch(Exception e){
			System.out.println("ConexionMC.getUsuarioContacto - Error al guardar ("+e+")");
			e.printStackTrace();
		}
		return user_id;
	}
	
	public List<Integer> getPerfilesNombre(String palabra){
		
		List<Integer> lista=new LinkedList<Integer>();
		ResultSet r;
		
		try{
			select_contact_nombre.setString(1, palabra);
			r=select_contact_nombre.executeQuery();
			while(r.next()){
				lista.add(r.getInt(1));
			}
		}
		catch(SQLException e){
			System.out.println("ConexionMC.getPerfilesNombre - Error al consultar ("+e+")");
			e.printStackTrace();
		}
		//System.out.println("ConexionMC.getPerfilesNombre - fin ("+lista.size()+" perfiles)");
		return lista;
		
	}
	
	public List<Integer> getPerfilesMail(String palabra){
		
		List<Integer> lista=new LinkedList<Integer>();
		ResultSet r;
		
		try{
			select_contact_mail.setString(1, palabra);
			r=select_contact_mail.executeQuery();
			while(r.next()){
				lista.add(r.getInt(1));
			}
		}
		catch(SQLException e){
			System.out.println("ConexionMC.getPerfilesMail - Error al consultar ("+e+")");
			e.printStackTrace();
		}
		//System.out.println("ConexionMC.getPerfilesMail - fin ("+lista.size()+" perfiles)");
		return lista;
		
	}
	
	public int getGroupId(int contact_id){
		
		int group_id=-1;
		ResultSet r;
		
		try{
			select_group_contact.setInt(1, contact_id);
			r=select_group_contact.executeQuery();
			if(r.next()){
				group_id=r.getInt(1);
			}
		}
		catch(SQLException e){
			System.out.println("ConexionMC.getGroupId - Error al consultar ("+e+")");
			e.printStackTrace();
		}
		return group_id;
		
	}
	
	public Set<String> getNombresGrupo(int group_id){
		
		Set<String> textos=new TreeSet<String>();
		ResultSet r;
		
		try{
			select_nombres_grupo.setInt(1, group_id);
			r=select_nombres_grupo.executeQuery();
			while(r.next()){
				textos.add(r.getString(1));
			}
		}
		catch(SQLException e){
			System.out.println("ConexionMC.getNombresGrupo - Error al consultar ("+e+")");
			e.printStackTrace();
		}
		//System.out.println("ConexionMC.getNombresGrupo - fin ("+textos.size()+" palabras)");
		return textos;
		
	}
	
	public Set<String> getMailsGrupo(int group_id){
		
		Set<String> textos=new TreeSet<String>();
		ResultSet r;
		
		try{
			select_mails_grupo.setInt(1, group_id);
			r=select_mails_grupo.executeQuery();
			while(r.next()){
				textos.add(r.getString(1));
			}
		}
		catch(SQLException e){
			System.out.println("ConexionMC.getMailsGrupo - Error al consultar ("+e+")");
			e.printStackTrace();
		}
		//System.out.println("ConexionMC.getMailsGrupo - fin ("+textos.size()+" palabras)");
		return textos;
		
	}
	
	public Set<String> getPhonesGrupo(int group_id){
		
		Set<String> textos=new TreeSet<String>();
		ResultSet r;
		
		try{
			select_phones_grupo.setInt(1, group_id);
			r=select_phones_grupo.executeQuery();
			while(r.next()){
				textos.add(r.getString(1));
			}
		}
		catch(SQLException e){
			System.out.println("ConexionMC.getPhonesGrupo - Error al consultar ("+e+")");
			e.printStackTrace();
		}
		//System.out.println("ConexionMC.getPhonesGrupo - fin ("+textos.size()+" palabras)");
		return textos;
		
	}
	
	public Set<String> getOrgsGrupo(int group_id){
		
		Set<String> textos=new TreeSet<String>();
		ResultSet r;
		
		try{
			select_orgs_grupo.setInt(1, group_id);
			r=select_orgs_grupo.executeQuery();
			while(r.next()){
				textos.add(r.getString(1)+" "+r.getString(2));
			}
		}
		catch(SQLException e){
			System.out.println("ConexionMC.getOrgsGrupo - Error al consultar ("+e+")");
			e.printStackTrace();
		}
		//System.out.println("ConexionMC.getOrgsGrupo - fin ("+textos.size()+" palabras)");
		return textos;
		
	}
	
//	public Map<Integer, String> getLogGrupoFormat(int group_id){
	public Set<Grouping> getLogGrupoFormat(int group_id){
		
//		Map<Integer, String> logs = new TreeMap<Integer, String>();
		Set<Grouping> logs = new TreeSet<Grouping>();
		ResultSet r;
		
		int old_group_id, new_group_id, contact_id, module_id;
		String message, time;
		
		try{
			select_grouping_log.setInt(1, group_id);
			r = select_grouping_log.executeQuery();
			while(r.next()){
				old_group_id = r.getInt(1);
				new_group_id = r.getInt(2);
				contact_id = r.getInt(3);
				module_id = r.getInt(4);
				message = r.getString(5);
				time = r.getString(6);
				
				logs.add(new Grouping(old_group_id, new_group_id, contact_id, module_id, message, time));
				
//				String mensaje = "";
//				
//				if(module.equals("NamesGrouper")){
//					mensaje="Name ("+message+")";
//				}
//				else if(module.equals("ProcessingCell")){
//					mensaje=message;
//				}
//				else if(module.equals("groupingExactSocialNetwork")){
//					mensaje=message.substring(message.indexOf(' ')+1);
//					mensaje=mensaje.substring(0, message.indexOf(' '));
//					mensaje="Exact ("+mensaje+")";
//				}
//				else{
//					mensaje=module+" ("+message+")";
//				}
				
//				logs.put(old_group_id, mensaje);
			}
		}
		catch(SQLException e){
			System.out.println("ConexionMC.getLogGrupo - Error al consultar ("+e+")");
			e.printStackTrace();
		}
		
		return logs;
		
	}
	
	private void prepararConsultas(){
		//PreparedStatement para ser reutilizados en la clase
		String sql;
		
		try{
			
			sql="select fullName from names, as_groups where as_groups.groupId = ? and as_groups.contactId = names.contactId";
			select_nombres_grupo=conexion.prepareStatement(sql);
			
			sql="select address from email, as_groups where as_groups.groupId = ? and as_groups.contactId = email.contactId";
			select_mails_grupo=conexion.prepareStatement(sql);
			
			sql="select Text from phonenumber, as_groups where as_groups.groupId = ? and as_groups.contactId = phonenumber.contactId";
			select_phones_grupo=conexion.prepareStatement(sql);
			
			sql="select orgName, orgTitle from organization, as_groups where as_groups.groupId = ? and as_groups.contactId = organization.contactId";
			select_orgs_grupo=conexion.prepareStatement(sql);
			
			sql="select groupId from as_groups where contactId = ?";
			select_group_contact=conexion.prepareStatement(sql);
			
			sql="select contactId from names where fullName = ?";
			select_contact_nombre=conexion.prepareStatement(sql);
			
			sql="select contactId from email where address = ?";
			select_contact_mail=conexion.prepareStatement(sql);
			
			sql="select user_id from contacts where contactId = ?";
			select_usuario_contacto=conexion.prepareStatement(sql);
			
			sql="select groupId from as_groups where contactId = ?";
			buscar_as_group=conexion.prepareStatement(sql);
			
			sql="insert into as_groups (contactId, groupId, user_Id) values (?, ?, ?)";
			insert_as_group=conexion.prepareStatement(sql);
			
			sql="update as_groups set groupId = ?, user_Id = ? where contactId = ?";
			update_as_group=conexion.prepareStatement(sql);
			
			sql="update as_groups set groupId = ? where groupId = ?";
			update_as_group_simple=conexion.prepareStatement(sql);
			
			sql="update exact_groups set groupId = ? where groupId = ?";
			update_exact_groups=conexion.prepareStatement(sql);
			
			
			//select datos de contactos
			
			sql="select names.contactId, names.fullName from contacts, names where contacts.user_Id = ? and contacts.status!=\"black\" and contacts.contactId = names.contactId";
			select_fullNames=conexion.prepareStatement(sql);
			
			sql="select profiles.contactId, profiles.value from contacts, profiles where contacts.user_Id = ? and contacts.status!=\"black\" and contacts.contactId = profiles.contactId and profiles.source=\"twitter\"";
			select_twitter=conexion.prepareStatement(sql);
			
			sql="select postalAddress.contactId, postalAddress.Text from contacts, postalAddress where contacts.user_Id = ? and contacts.status!=\"black\" and contacts.contactId = postalAddress.contactId";
			select_addresses=conexion.prepareStatement(sql);
			
			sql="select demographics.contactId, demographics.hometown, demographics.location from contacts, demographics where contacts.user_Id = ? and contacts.status!=\"black\" and contacts.contactId = demographics.contactId";
			select_demographics=conexion.prepareStatement(sql);
			
			sql="select contactId, srcName from contacts where user_Id = ? and status!=\"black\"";
			select_networks=conexion.prepareStatement(sql);
			
			sql="select email.contactId, email.address from contacts, email where contacts.user_Id = ? and contacts.status!=\"black\" and contacts.contactId = email.contactId";
			select_emails=conexion.prepareStatement(sql);
			
			sql="select phonenumber.contactId, phonenumber.Text from contacts, phonenumber where contacts.user_Id = ? and contacts.status!=\"black\" and contacts.contactId = phonenumber.contactId";
			select_phones=conexion.prepareStatement(sql);
			
			sql="select organization.contactId, organization.orgName, organization.orgTitle, organization.jobStatus from contacts, organization where contacts.user_Id = ? and contacts.status!=\"black\" and contacts.contactId = organization.contactId";
			select_organizations=conexion.prepareStatement(sql);
			
			sql="select education.contactId, education.schoolName, education.fieldOfStudy from contacts, education where contacts.user_Id = ? and contacts.status!=\"black\" and contacts.contactId = education.contactId";
			select_educations=conexion.prepareStatement(sql);
			
			//Inicio nuevos
			
			sql="select names.contactId, names.fullName from contacts, names where contacts.user_Id = ? and ( contacts.status=\"new\" or contacts.status=\"updated\" ) and contacts.contactId = names.contactId";
			select_fullNames_new=conexion.prepareStatement(sql);
			
			sql="select profiles.contactId, profiles.value from contacts, profiles where contacts.user_Id = ? and ( contacts.status=\"new\" or contacts.status=\"updated\" ) and contacts.contactId = profiles.contactId and profiles.source=\"twitter\"";
			select_twitter_new=conexion.prepareStatement(sql);
			
			sql="select postalAddress.contactId, postalAddress.Text from contacts, postalAddress where contacts.user_Id = ? and ( contacts.status=\"new\" or contacts.status=\"updated\" ) and contacts.contactId = postalAddress.contactId";
			select_addresses_new=conexion.prepareStatement(sql);
			
			sql="select demographics.contactId, demographics.hometown, demographics.location from contacts, demographics where contacts.user_Id = ? and ( contacts.status=\"new\" or contacts.status=\"updated\" ) and contacts.contactId = demographics.contactId";
			select_demographics_new=conexion.prepareStatement(sql);
			
			sql="select contactId, srcName from contacts where user_Id = ? and ( contacts.status=\"new\" or contacts.status=\"updated\" )";
			select_networks_new=conexion.prepareStatement(sql);
			
			sql="select email.contactId, email.address from contacts, email where contacts.user_Id = ? and ( contacts.status=\"new\" or contacts.status=\"updated\" ) and contacts.contactId = email.contactId";
			select_emails_new=conexion.prepareStatement(sql);
			
			sql="select phonenumber.contactId, phonenumber.Text from contacts, phonenumber where contacts.user_Id = ? and ( contacts.status=\"new\" or contacts.status=\"updated\" ) and contacts.contactId = phonenumber.contactId";
			select_phones_new=conexion.prepareStatement(sql);
			
			sql="select organization.contactId, organization.orgName, organization.orgTitle, organization.jobStatus from contacts, organization where contacts.user_Id = ? and ( contacts.status=\"new\" or contacts.status=\"updated\" ) and contacts.contactId = organization.contactId";
			select_organizations_new=conexion.prepareStatement(sql);
			
			sql="select education.contactId, education.schoolName, education.fieldOfStudy from contacts, education where contacts.user_Id = ? and ( contacts.status=\"new\" or contacts.status=\"updated\" ) and contacts.contactId = education.contactId";
			select_educations_new=conexion.prepareStatement(sql);
			
			//Fin nuevos
			
			//Inicio processing
			
			sql="select names.contactId, names.fullName from contacts, names where contacts.user_Id = ? and contacts.status=\"processing\" and contacts.contactId = names.contactId";
			select_fullNames_processing=conexion.prepareStatement(sql);
			
			sql="select profiles.contactId, profiles.value from contacts, profiles where contacts.user_Id = ? and contacts.status=\"processing\" and contacts.contactId = profiles.contactId and profiles.source=\"twitter\"";
			select_twitter_processing=conexion.prepareStatement(sql);
			
			sql="select postalAddress.contactId, postalAddress.Text from contacts, postalAddress where contacts.user_Id = ? and contacts.status=\"processing\" and contacts.contactId = postalAddress.contactId";
			select_addresses_processing=conexion.prepareStatement(sql);
			
			sql="select demographics.contactId, demographics.hometown, demographics.location from contacts, demographics where contacts.user_Id = ? and contacts.status=\"processing\" and contacts.contactId = demographics.contactId";
			select_demographics_processing=conexion.prepareStatement(sql);
			
			sql="select contactId, srcName from contacts where user_Id = ? and status=\"processing\"";
			select_networks_processing=conexion.prepareStatement(sql);
			
			sql="select email.contactId, email.address from contacts, email where contacts.user_Id = ? and contacts.status=\"processing\" and contacts.contactId = email.contactId";
			select_emails_processing=conexion.prepareStatement(sql);
			
			sql="select phonenumber.contactId, phonenumber.Text from contacts, phonenumber where contacts.user_Id = ? and contacts.status=\"processing\" and contacts.contactId = phonenumber.contactId";
			select_phones_processing=conexion.prepareStatement(sql);
			
			sql="select organization.contactId, organization.orgName, organization.orgTitle, organization.jobStatus from contacts, organization where contacts.user_Id = ? and contacts.status=\"processing\" and contacts.contactId = organization.contactId";
			select_organizations_processing=conexion.prepareStatement(sql);
			
			sql="select education.contactId, education.schoolName, education.fieldOfStudy from contacts, education where contacts.user_Id = ? and contacts.status=\"processing\" and contacts.contactId = education.contactId";
			select_educations_processing=conexion.prepareStatement(sql);
			
			//Fin processing
			
			//Inicio Group
			
			sql="select names.contactId, names.fullName from as_groups, names where as_groups.groupId = ? and as_groups.contactId = names.contactId";
			select_fullNames_group=conexion.prepareStatement(sql);
			
			sql="select profiles.contactId, profiles.value from as_groups, profiles where as_groups.groupId = ? and as_groups.contactId = profiles.contactId and profiles.source=\"twitter\"";
			select_twitter_group=conexion.prepareStatement(sql);
			
			sql="select postalAddress.contactId, postalAddress.Text from as_groups, postalAddress where as_groups.groupId = ? and as_groups.contactId = postalAddress.contactId";
			select_addresses_group=conexion.prepareStatement(sql);
			
			sql="select demographics.contactId, demographics.hometown, demographics.location from as_groups, demographics where as_groups.groupId = ? and as_groups.contactId = demographics.contactId";
			select_demographics_group=conexion.prepareStatement(sql);
			
			sql="select contacts.contactId, contacts.srcName from as_groups, contacts where as_groups.groupId = ? and as_groups.contactId = contacts.contactId";
			select_networks_group=conexion.prepareStatement(sql);
			
			sql="select email.contactId, email.address from as_groups, email where as_groups.groupId = ? and as_groups.contactId = email.contactId";
			select_emails_group=conexion.prepareStatement(sql);
			
			sql="select phonenumber.contactId, phonenumber.Text from as_groups, phonenumber where as_groups.groupId = ? and as_groups.contactId = phonenumber.contactId";
			select_phones_group=conexion.prepareStatement(sql);
			
			sql="select organization.contactId, organization.orgName, organization.orgTitle, organization.jobStatus from as_groups, organization where as_groups.groupId = ? and as_groups.contactId = organization.contactId";
			select_organizations_group=conexion.prepareStatement(sql);
			
			sql="select education.contactId, education.schoolName, education.fieldOfStudy from as_groups, education where as_groups.groupId = ? and as_groups.contactId = education.contactId";
			select_educations_group=conexion.prepareStatement(sql);
			
			//Fin Group
			
			sql="insert into data_vocabularies (user_id, contact_id, term, value) values (?, ?, ?, ?)";
			insert_voc=conexion.prepareStatement(sql);
			
			sql="delete from data_vocabularies where contact_id = ?";
			delete_voc=conexion.prepareStatement(sql);
			
			sql="select contact_id, term, value from data_vocabularies where user_id = ?";
			select_vocs=conexion.prepareStatement(sql);
			
			sql="select count(1) from data_globalVocabulary where term = ?";
			buscar_voc_global=conexion.prepareStatement(sql);
			
			sql="update data_globalVocabulary set count = ? where term = ?";
			update_voc_global=conexion.prepareStatement(sql);
			
			sql="insert into data_globalVocabulary (term, count) values (?, ?)";
			insert_voc_global=conexion.prepareStatement(sql);
			
			//este no usa el campo lazy_delete pues esto se usa para el insert/update
			sql="select value from data_vocabularies_attributes where user_id = ? and contact_id = ? and attribute = ? and term = ?";
			buscar_voc_at=conexion.prepareStatement(sql);
			
			sql="insert into data_vocabularies_attributes (user_id, contact_id, attribute, term, value, lazy_delete) values (?, ?, ?, ?, ?, 0)";
			insert_voc_at=conexion.prepareStatement(sql);
			
			sql="update data_vocabularies_attributes set value = ?, lazy_delete = 0 where user_id = ? and contact_id = ? and attribute = ? and term = ?";
			update_voc_at=conexion.prepareStatement(sql);
			
			sql="update data_vocabularies_attributes set lazy_delete = 1 where user_id = ? and contact_id = ? and attribute = ?";
			delete_voc_at_lazy=conexion.prepareStatement(sql);
			
			sql="delete from data_vocabularies_attributes where user_id = ? and contact_id = ? and attribute = ?";
			delete_voc_at=conexion.prepareStatement(sql);
			
			sql="select contact_id, term, value from data_vocabularies_attributes where user_id = ? and attribute = ? and lazy_delete = 0";
			select_vocs_at=conexion.prepareStatement(sql);
			
			sql="select data_vocabularies_attributes.contact_id, attribute, term, value from as_groups, data_vocabularies_attributes where as_groups.groupId = ? and as_groups.contactId = data_vocabularies_attributes.contact_id and lazy_delete = 0";
			select_vocs_grupo=conexion.prepareStatement(sql);
			
			//Inicio Affinities
			
			sql="select affinity_id from data_affinities_index where n_terms=? and terms=?";
			select_affinities_index=conexion.prepareStatement(sql);
			
			sql="select term, value from data_affinities where affinity_id=?";
			select_affinities=conexion.prepareStatement(sql);
			
			sql="insert into data_affinities_index (affinity_id, n_terms, terms) values (?, ?, ?)";
			insert_affinities_index=conexion.prepareStatement(sql);
			
			sql="insert into data_affinities (affinity_id, term, value) values (?, ?, ?)";
			insert_affinities=conexion.prepareStatement(sql);
			
			sql="insert into data_affinities_groups (affinity_id, group_id, value) values (?, ?, ?)";
			insert_affinities_groups=conexion.prepareStatement(sql);
			
			sql="select value from data_affinities_groups where affinity_id = ? and group_id = ?";
			select_affinities_groups=conexion.prepareStatement(sql);
			
			sql="update data_affinities_groups set value = ? where affinity_id = ? and group_id = ?";
			update_affinities_groups=conexion.prepareStatement(sql);
			
			//Fin Affinities
			
			sql="select * from data_grouping_log where new_group_id = ?";
			select_grouping_log = conexion.prepareStatement(sql);
			
			sql="update userCake_Users set reprocess = \"false\" where user_id = ?";
			update_reprocess_user=conexion.prepareStatement(sql);
			
			sql = "insert into data_grouping_log (old_group_id, new_group_id, contact_id, module_id, message) values (?, ?, ?, ?, ?)";
			insert_log = conexion.prepareStatement(sql);
			
		}
		catch(SQLException e){
			System.out.println("ConexionMC.prepararConsultas - Error al preparar consultas ("+e+")");
			e.printStackTrace();
		}
		
	}
	
	private boolean conectar(String servidor, String bd, String usuario, String clave){
		
		try{
			Class.forName("com.mysql.jdbc.Driver");
			conexion=DriverManager.getConnection ("jdbc:mysql://"+servidor+"/"+bd, usuario, clave);
		}
		catch(Exception e){
			System.out.println("ConexionMC.conectar - Incapaz de Conectar :");
			System.out.println("Username: \""+usuario+"\", Password: \""+clave+"\"");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean close(){
		System.out.println("ConexionMC.close");
		try{
			conexion.close();
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public boolean isValid(){
		try{
			//timeout in seconds
			return conexion.isValid(1);
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	
}



