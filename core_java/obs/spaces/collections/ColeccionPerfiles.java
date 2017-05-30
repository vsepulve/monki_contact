package obs.spaces.collections;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;

import java.io.File;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;

import java.io.FileNotFoundException;

import obs.spaces.data.DatoPerfilDoc;

import java.sql.*;
import java.sql.SQLException;
import com.mysql.jdbc.Driver;

/*
ESTA CLASE ESTA DESACTUALIZADA Y NO ESTA EN USO DE MOMENTO
LA CONSERVO EN EL SVN POR EL MOMENTO
PERO ES NECESARIO REVISAR SI PUEDE TENER ALGUNA UTILIDAD
*/


//generador de una coleccion de docs basada en perfiles
//lee el xml original y almacena los textos originales asociados a un docid
public class ColeccionPerfiles{
	
	//coleccion de ( docid -> docPerfil )
	//en esta version docid es el id en la base de datos
	//por ahora se continua usando el mismo DatoPerfilDoc
	
	private Map<Integer, Map<Integer, DatoPerfilDoc>> coleccion;
	
	//mapa global id_contacto -> id_grupo
	private Map<Integer, Integer> mapa_grupos;
	
	//este mapa es solo para guardar los ids de los contactos nuevos
	//se mantienen asociados al user_id por eficiencia
	//solo se usa una vez ejecutado "cargarPerfilesNuevos"
	private Map<Integer, List<Integer>> contactos_nuevos;
	
	private PreparedStatement select_fullName;
	private PreparedStatement select_firstName;
	private PreparedStatement select_lastName;
	private PreparedStatement select_emails;
	private PreparedStatement select_phones;
	private PreparedStatement select_organizations;
	private PreparedStatement select_address;
	private PreparedStatement select_network;
	private PreparedStatement select_grupos;
	
	private static Connection conexion=null;
	static String as_prefijo="as_";
	
	private static String prefijo="";
	
	class ParInt{
		public int user_id;
		public int contact_id;
		public ParInt(int i1, int i2){
			user_id=i1;
			contact_id=i2;
		}
	}
	
	//Se necesita la conexion para preparar los statements (y que la construccion funcione)
	//Por ahora, deshabilito el constructor vacio
	public ColeccionPerfiles(){
		System.out.println("ColeccionPerfiles - inicio (vacio)");
//		prepararConsultas();
		
		coleccion=new TreeMap<Integer, Map<Integer, DatoPerfilDoc>>();
		
		mapa_grupos=new TreeMap<Integer, Integer>();
		
		System.out.println("ColeccionPerfiles - fin");
	}
	
	public ColeccionPerfiles(String servidor, String bd, String usuario, String clave){
		
		System.out.println("ColeccionPerfiles - inicio (cargando desde "+servidor+"/"+bd+")");
		
		coleccion=new TreeMap<Integer, Map<Integer, DatoPerfilDoc>>();
		
		mapa_grupos=new TreeMap<Integer, Integer>();
		
		if(!conectar(servidor, bd, usuario, clave)){
			System.out.println("ColeccionPerfiles - Terminado por problemas al conectar");
			return;
		}
		
		prepararConsultas();
		
		int perfiles_cargados=cargarPerfiles();
		
		cargarGrupos();
		
		System.out.println("ColeccionPerfiles - fin ("+coleccion.size()+" usuarios, "+perfiles_cargados+" perfiles)");
		
	}
	
	public ColeccionPerfiles(String servidor, String bd, String usuario, String clave, String _prefijo){
		
		prefijo=_prefijo;
		
		System.out.println("ColeccionPerfiles - inicio (cargando desde "+servidor+"/"+bd+")");
		
		coleccion=new TreeMap<Integer, Map<Integer, DatoPerfilDoc>>();
		
		mapa_grupos=new TreeMap<Integer, Integer>();
		
		if(!conectar(servidor, bd, usuario, clave)){
			System.out.println("ColeccionPerfiles - Terminado por problemas al conectar");
			return;
		}
		
		prepararConsultas();
		
		int perfiles_cargados=cargarPerfiles();
		
		cargarGrupos();
		
		System.out.println("ColeccionPerfiles - fin ("+coleccion.size()+" usuarios, "+perfiles_cargados+" perfiles)");
		
	}
	
	public ColeccionPerfiles(String servidor, String bd, String usuario, String clave, String _prefijo, int user_id){
		
		prefijo=_prefijo;
		
		System.out.println("ColeccionPerfiles - inicio (cargando desde "+servidor+"/"+bd+"), user_id: "+user_id);
		
		coleccion=new TreeMap<Integer, Map<Integer, DatoPerfilDoc>>();
		
		mapa_grupos=new TreeMap<Integer, Integer>();
		
		if(!conectar(servidor, bd, usuario, clave)){
			System.out.println("ColeccionPerfiles - Terminado por problemas al conectar");
			return;
		}
		
		prepararConsultas();
		
		int perfiles_cargados=cargarPerfiles(user_id);
		
		cargarGrupos();
		
		System.out.println("ColeccionPerfiles - fin ("+coleccion.size()+" usuarios, "+perfiles_cargados+" perfiles)");
		
	}
	
	public ColeccionPerfiles(String servidor, String bd, String usuario, String clave, String _prefijo, boolean cargar){
		
		prefijo=_prefijo;
		
		System.out.println("ColeccionPerfiles - inicio (cargando desde "+servidor+"/"+bd+")");
		
		coleccion=new TreeMap<Integer, Map<Integer, DatoPerfilDoc>>();
		
		mapa_grupos=new TreeMap<Integer, Integer>();
		
		if(!conectar(servidor, bd, usuario, clave)){
			System.out.println("ColeccionPerfiles - Terminado por problemas al conectar");
			return;
		}
		
		prepararConsultas();
			
		if(cargar){
			int perfiles_cargados=cargarPerfiles();
			cargarGrupos();
			System.out.println("ColeccionPerfiles - fin ("+coleccion.size()+" usuarios, "+perfiles_cargados+" perfiles)");
		}
		else{
			System.out.println("ColeccionPerfiles - fin (sin datos cargados)");
		}
		
	}
	
	public void cargarNuevos(String servidor, String bd, String usuario, String clave, String _prefijo){
		
		prefijo=_prefijo;
		
		System.out.println("ColeccionPerfiles.cargarNuevos - inicio (cargando desde "+servidor+"/"+bd+")");
//		
//		coleccion=new TreeMap<Integer, Map<Integer, DatoPerfilDoc>>();
//		
//		mapa_grupos=new TreeMap<Integer, Integer>();

		contactos_nuevos=new TreeMap<Integer, List<Integer>>();
		
		if(!conectar(servidor, bd, usuario, clave)){
			System.out.println("ColeccionPerfiles.cargarNuevos - Terminado por problemas al conectar");
			return;
		}
		
		prepararConsultas();
		
		int perfiles_cargados=cargarPerfilesNuevos();
		
		cargarGrupos();
		
		System.out.println("ColeccionPerfiles.cargarNuevos - fin ("+coleccion.size()+" usuarios, "+perfiles_cargados+" perfiles nuevos)");
		
	}
	
	public int cargarPerfilesNuevos(){
		
		//por ahora, la coleccion es para un usuario especifico
		//Debe definirse una interfaz adecuada para definir al usuario actual 
		
		List<ParInt> contact_ids=cargarContactIdsNuevos();
		
		Iterator<ParInt> it_ids=contact_ids.iterator();
		ParInt par;
		int contact_id;
		int user_id;
		DatoPerfilDoc perfil;
		int perfiles_cargados=0;
		while(it_ids.hasNext()){
			par=it_ids.next();
			user_id=par.user_id;
			contact_id=par.contact_id;
			perfil=cargarPerfil(contact_id, user_id);
			
			if(coleccion.containsKey(user_id)){
				coleccion.get(user_id).put(contact_id, perfil);
			}
			else{
				Map<Integer, DatoPerfilDoc> mapa=new TreeMap<Integer, DatoPerfilDoc>();
				mapa.put(contact_id, perfil);
				coleccion.put(user_id, mapa);
			}
			perfiles_cargados++;
					
			//debbug			
//			if(perfiles_cargados>=10){
//				break;
//			}
			
		}
		
		return perfiles_cargados;
		
	}
	
	public int cargarPerfiles(){
		
		//por ahora, la coleccion es para un usuario especifico
		//Debe definirse una interfaz adecuada para definir al usuario actual 
		
		List<ParInt> contact_ids=cargarContactIds();
		
		Iterator<ParInt> it_ids=contact_ids.iterator();
		ParInt par;
		int contact_id;
		int user_id;
		DatoPerfilDoc perfil;
		int perfiles_cargados=0;
		while(it_ids.hasNext()){
			par=it_ids.next();
			user_id=par.user_id;
			contact_id=par.contact_id;
			perfil=cargarPerfil(contact_id, user_id);
			
			if(coleccion.containsKey(user_id)){
				coleccion.get(user_id).put(contact_id, perfil);
			}
			else{
				Map<Integer, DatoPerfilDoc> mapa=new TreeMap<Integer, DatoPerfilDoc>();
				mapa.put(contact_id, perfil);
				coleccion.put(user_id, mapa);
			}
			perfiles_cargados++;
					
			//debbug			
//			if(perfiles_cargados>=10){
//				break;
//			}
			
		}
		
		return perfiles_cargados;
		
	}
	
	public int cargarPerfiles(int user_id_buscado){
		
		//por ahora, la coleccion es para un usuario especifico
		//Debe definirse una interfaz adecuada para definir al usuario actual 
		
		List<ParInt> contact_ids=cargarContactIds(user_id_buscado);
		
		Iterator<ParInt> it_ids=contact_ids.iterator();
		ParInt par;
		int contact_id;
		int user_id;
		DatoPerfilDoc perfil;
		int perfiles_cargados=0;
		while(it_ids.hasNext()){
			par=it_ids.next();
			user_id=par.user_id;
			contact_id=par.contact_id;
			perfil=cargarPerfil(contact_id, user_id);
			
			if(coleccion.containsKey(user_id)){
				coleccion.get(user_id).put(contact_id, perfil);
			}
			else{
				Map<Integer, DatoPerfilDoc> mapa=new TreeMap<Integer, DatoPerfilDoc>();
				mapa.put(contact_id, perfil);
				coleccion.put(user_id, mapa);
			}
			perfiles_cargados++;
					
			//debbug			
//			if(perfiles_cargados>=10){
//				break;
//			}
			
		}
		
		return perfiles_cargados;
		
	}
	
	public int cargarGrupos(){
		//Map<Integer, Integer> mapa_grupos;
		
		try{
			Statement s=conexion.createStatement();
//			ResultSet r=s.executeQuery("select contactId, groupId from "+prefijo+"groups");
			ResultSet r=s.executeQuery("select contactId, groupId from as_groups");
			while(r.next()){
				mapa_grupos.put(r.getInt(1),r.getInt(2));
			}
			s.close();
			System.out.println("ColeccionPerfiles.cargarGrupos - elementos agrupados: "+mapa_grupos.size());
		}
		catch(SQLException e){
			System.err.println("ColeccionPerfiles.cargarGrupos - Error al cargar ids");
			e.printStackTrace();
		}
		
		return mapa_grupos.size();
	}
	
	private void prepararConsultas(){
		//PreparedStatement para ser reutilizados en la clase
		String sql;
		
		try{
				sql="select fullName from "+prefijo+"names where contactId = ? and user_Id = ?";
				select_fullName=conexion.prepareStatement(sql);
		
				sql="select firstName from "+prefijo+"names where contactId = ? and user_Id = ?";
				select_firstName=conexion.prepareStatement(sql);
		
				sql="select lastName from "+prefijo+"names where contactId = ? and user_Id = ?";
				select_lastName=conexion.prepareStatement(sql);
		
				sql="select address from "+prefijo+"email where contactId = ? and user_Id = ?";
				select_emails=conexion.prepareStatement(sql);
		
				sql="select Text from "+prefijo+"phonenumber where contactId = ? and user_Id = ?";
				select_phones=conexion.prepareStatement(sql);
		
				sql="select orgName, orgTitle from "+prefijo+"organization where contactId = ?";
				select_organizations=conexion.prepareStatement(sql);
			
				sql="select Text from "+prefijo+"postalAddress where contactId = ? and user_Id = ?";
				select_address=conexion.prepareStatement(sql);
				
				//aqui no hay prefijo
				sql="select srcName from "+prefijo+"contacts where contactId = ? and user_Id = ?";
				select_network=conexion.prepareStatement(sql);
			
			
		}
		catch(SQLException e){
			System.err.println("ColeccionPerfiles.prepararConsultas - Error al preparar consultas");
			e.printStackTrace();
		}
		
	}
	
	private static boolean conectar(String servidor, String bd, String usuario, String clave){
		
		try{
			Class.forName("com.mysql.jdbc.Driver");
			conexion=DriverManager.getConnection ("jdbc:mysql://"+servidor+"/"+bd, usuario, clave);
		}
		catch(Exception e){
			System.err.println("Incapaz de Conectar :");
			System.err.println("Username: \""+usuario+"\", Password: \""+clave+"\"");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private List<ParInt> cargarContactIds(){
		List<ParInt> lista=new LinkedList<ParInt>();
		try{
			Statement s=conexion.createStatement();
			ResultSet r=s.executeQuery("select user_id, contactId from "+prefijo+"contacts");
			while(r.next()){
				lista.add(new ParInt(r.getInt(1),r.getInt(2) ));
			}
			s.close();
		}
		catch(SQLException e){
			System.err.println("ColeccionPerfiles.cargarContactIds - Error al cargar ids");
			e.printStackTrace();
		}
		
		return lista;
		
	}
	
	public Map<Integer, Map<Integer, DatoPerfilDoc>> getMapaNuevos(){	
	
		Map<Integer, Map<Integer, DatoPerfilDoc>> coleccion_nuevos=new TreeMap<Integer, Map<Integer, DatoPerfilDoc>>();
		Iterator<Map.Entry<Integer, List<Integer>>> it_nuevos;
		Map.Entry<Integer, List<Integer>> par_nuevos;
		List<Integer> lista;
		Iterator<Integer> it_lista;
		int user_id=0;
		int doc_id=0;
		DatoPerfilDoc contacto;
		
		it_nuevos=contactos_nuevos.entrySet().iterator();
		while(it_nuevos.hasNext()){
			par_nuevos=it_nuevos.next();
			user_id=par_nuevos.getKey();
			lista=par_nuevos.getValue();
			
			Map<Integer, DatoPerfilDoc> mapa_user=new TreeMap<Integer, DatoPerfilDoc>();
			it_lista=lista.iterator();
			while(it_lista.hasNext()){
				doc_id=it_lista.next();
				contacto=getPerfil(user_id, doc_id);
				if(contacto!=null){
					mapa_user.put(doc_id, contacto);
				}//if... hay un contacto doc_id
			}//while... cada contacto nuevo del usuario
			
			if(mapa_user.size() > 0){
				coleccion_nuevos.put(user_id, mapa_user);
			}//if... el mapa del usuario tiene algun nuevo contacto
			
		}//while... usuarios con nuevos contactos
		
		return coleccion_nuevos;
	}
	
	private List<ParInt> cargarContactIdsNuevos(){
		System.out.println("ColeccionPerfiles.cargarContactIdsNuevos - inicio");
		List<ParInt> lista=new LinkedList<ParInt>();
		int user_id=0;
		int doc_id=0;
		try{
			Statement s=conexion.createStatement();
			ResultSet r=s.executeQuery("select user_id, contactId from "+prefijo+"contacts where status=\"processing\"");
			while(r.next()){
				user_id=r.getInt(1);
				doc_id=r.getInt(2);
				lista.add(new ParInt(user_id, doc_id));
				
				if(contactos_nuevos.containsKey(user_id)){
					contactos_nuevos.get(user_id).add(doc_id);
				}
				else{
					List<Integer> contactos_user=new LinkedList<Integer>();
					contactos_user.add(doc_id);
					contactos_nuevos.put(user_id, contactos_user);
				}
				
			}
			s.close();
		}
		catch(SQLException e){
			System.err.println("ColeccionPerfiles.cargarContactIdsNuevos - Error al cargar ids");
			e.printStackTrace();
		}
		
		System.out.println("ColeccionPerfiles.cargarContactIdsNuevos - fin ("+contactos_nuevos.size()+" usuarios con datos nuevos)");
		
		return lista;
		
	}
	
	private List<ParInt> cargarContactIds(int user_id){
		List<ParInt> lista=new LinkedList<ParInt>();
		try{
			Statement s=conexion.createStatement();
			ResultSet r=s.executeQuery("select user_id, contactId from "+prefijo+"contacts where user_id="+user_id+"");
			while(r.next()){
				lista.add(new ParInt(r.getInt(1),r.getInt(2) ));
			}
			s.close();
		}
		catch(SQLException e){
			System.err.println("ColeccionPerfiles.cargarContactIds - Error al cargar ids");
			e.printStackTrace();
		}
		
		return lista;
		
	}
	
	public DatoPerfilDoc cargarPerfil(int id_contact, int user_id){
		
		ResultSet r;
		
		//crear perfil
		DatoPerfilDoc perfil=new DatoPerfilDoc();
		String texto;
		
		try{
			
			perfil.setId(id_contact);
			
			//fullName
			select_fullName.setInt(1, id_contact);
			select_fullName.setInt(2, user_id);
			r=select_fullName.executeQuery();
			if(r.next()){
//				perfil.setFullName(""+r.getString(1));
				try{
					if(r.getString(1)!=null){
						texto=new String(r.getString(1).getBytes(), "UTF-8");
						texto=reemplazarCaracteres(texto);
						perfil.setFullName(""+texto);
//						System.out.println("fullName: "+perfil.getFullName());
					}
				}
				catch(Exception e){
					e.printStackTrace();
					perfil.setFullName("");
				}
			}
			
			//preparar el nombre correctamente
			perfil.prepararNombre();
		
			//emails
			select_emails.setInt(1, id_contact);
			select_emails.setInt(2, user_id);
			r=select_emails.executeQuery();
			while(r.next()){
//				perfil.addEmail(""+r.getObject(1));
				try{
					if(r.getString(1)!=null){
						texto=new String(r.getString(1).getBytes(), "UTF-8");
						texto=reemplazarCaracteres(texto);
						perfil.addEmail(""+texto);
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
			
			//phones
			select_phones.setInt(1, id_contact);
			select_phones.setInt(2, user_id);
			r=select_phones.executeQuery();
			while(r.next()){
//				perfil.addPhone(""+r.getObject(1));
				try{
					if(r.getString(1)!=null){
						texto=new String(r.getString(1).getBytes(), "UTF-8");
						texto=reemplazarCaracteres(texto);
						perfil.addPhone(""+texto);
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		
			//organizations (Esta tabla DEBERIA tener user_id pero no tiene aun)
			select_organizations.setInt(1, id_contact);
//			select_organizations.setInt(2, user_id);
			r=select_organizations.executeQuery();
			while(r.next()){
//				perfil.addOrganization(""+r.getString(1)+" "+r.getString(2));
				try{
					if(r.getObject(1)!=null && r.getObject(2)!=null){
						texto=new String(r.getObject(1).toString().getBytes(), "UTF-8")
							+" "+new String(r.getObject(2).toString().getBytes(), "UTF-8");
						texto=reemplazarCaracteres(texto);
						perfil.addOrganization(""+texto);
					}
					else if(r.getObject(1)!=null){
						texto=new String(r.getObject(1).toString().getBytes(), "UTF-8");
						texto=reemplazarCaracteres(texto);
						perfil.addOrganization(""+texto);
					}
					else if(r.getObject(2)!=null){
						texto=new String(r.getObject(2).toString().getBytes(), "UTF-8");
						texto=reemplazarCaracteres(texto);
						perfil.addOrganization(""+texto);
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
			
			//postalAddress en address
			select_address.setInt(1, id_contact);
			select_address.setInt(2, user_id);
			r=select_address.executeQuery();
			if(r.next()){
//				perfil.setAddress(""+r.getObject(1));
				try{
					if(r.getString(1)!=null){
						texto=new String(r.getString(1).getBytes(), "UTF-8");
						texto=reemplazarCaracteres(texto);
						perfil.setAddress(""+texto);
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
			
			//Network
			select_network.setInt(1, id_contact);
			select_network.setInt(2, user_id);
			r=select_network.executeQuery();
			if(r.next()){
//				perfil.setNetwork(""+r.getObject(1));
				try{
					if(r.getString(1)!=null){
						texto=new String(r.getString(1).getBytes(), "UTF-8");
						texto=reemplazarCaracteres(texto);
						perfil.setNetwork(""+texto);
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
			
		}
		catch(SQLException e){
			System.err.println("ColeccionPerfiles.cargarPerfil - Error al cargar datos");
			e.printStackTrace();
		}
		
		return perfil;
		
	}
	
	private String reemplazarCaracteres(String texto){
		texto=texto.replace('á', 'a');
		texto=texto.replace('é', 'e');
		texto=texto.replace('í', 'i');
		texto=texto.replace('ó', 'o');
		texto=texto.replace('ú', 'u');
		texto=texto.replace('ñ', 'n');
		return texto;
	}
	
	public DatoPerfilDoc getPerfil(int user_id, int doc_id){
		if(coleccion.containsKey(user_id)){
			Map<Integer, DatoPerfilDoc> mapa_user=coleccion.get(user_id);
			if(mapa_user.containsKey(doc_id)){
				return mapa_user.get(doc_id);
			}
			else{
				return null;
			}
		}
		else{
			return null;
		}
		
	}
	
	public Map<Integer, Map<Integer, DatoPerfilDoc>> getMapa(){
		return coleccion;
	}
	
	public Map<Integer, DatoPerfilDoc> getMapa(int user_id){
		if(coleccion.containsKey(user_id)){
			return coleccion.get(user_id);
		}
		else{
			return null;
		}
	}
	
	public void guardar(String archivo){
		
		System.out.println("ColeccionPerfiles.guardar - inicio (guardando en "+archivo+")");
		
		DataOutputStream salida=null;
		Iterator<Map.Entry<Integer, Map<Integer, DatoPerfilDoc>>> it_col;
		Map.Entry<Integer, Map<Integer, DatoPerfilDoc>> par_col;
		Map<Integer, DatoPerfilDoc> mapa;
		Iterator<Map.Entry<Integer, DatoPerfilDoc>> it_user;
		Map.Entry<Integer, DatoPerfilDoc> par_user;
		Iterator<Map.Entry<Integer, Integer>> it_grupos;
		Map.Entry<Integer, Integer> par_grupos;
		
		int user_id=0;
		int doc_id=0;
		DatoPerfilDoc perfil=null;
		
		try{
			salida=new DataOutputStream(new FileOutputStream(archivo));
			
			//numero users
			salida.writeInt(coleccion.size());
			it_col=coleccion.entrySet().iterator();
			while(it_col.hasNext()){
				par_col=it_col.next();
				user_id=par_col.getKey();
				mapa=par_col.getValue();
				//user id
				salida.writeInt(user_id);
				//numero docs
				salida.writeInt(mapa.size());
				it_user=mapa.entrySet().iterator();
				while(it_user.hasNext()){
					par_user=it_user.next();
					doc_id=par_user.getKey();
					perfil=par_user.getValue();
					//doc_id
					salida.writeInt(doc_id);
					//perfil
					perfil.guardar(salida);
				}
			}
			
			//aqui habria que guardar grupos en binario
			salida.writeInt(mapa_grupos.size());
			it_grupos=mapa_grupos.entrySet().iterator();
			while(it_grupos.hasNext()){
				par_grupos=it_grupos.next();
				salida.writeInt(par_grupos.getKey());
				salida.writeInt(par_grupos.getValue());
			}
			
			salida.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.println("ColeccionPerfiles.guardar - fin");
		
	}
	
	public void cargar(String archivo){
		
		System.out.println("ColeccionPerfiles.cargar - inicio (cargar de "+archivo+")");
		
		DataInputStream entrada=null;
		
		int user_id=0;
		int doc_id=0;
		DatoPerfilDoc perfil=null;
		Map<Integer, DatoPerfilDoc> mapa;
		int n_docs=0;
		int n_users=0;
		int n_grupos=0;
		int group_id=0;
		
		try{
			entrada=new DataInputStream(new FileInputStream(archivo));
			
			//numero users
			n_users=entrada.readInt();
			for(int i=0; i<n_users; i++){
				//user id
				user_id=entrada.readInt();
				//numero docs
				n_docs=entrada.readInt();
				mapa=new TreeMap<Integer, DatoPerfilDoc>();
				for(int j=0; j<n_docs; j++){
					doc_id=entrada.readInt();
					//perfil
					perfil=new DatoPerfilDoc();
					perfil.cargar(entrada);
					mapa.put(doc_id, perfil);
				}
				coleccion.put(user_id, mapa);
			}
			
			//cargar grupos
			try{
				n_grupos=entrada.readInt();
				for(int j=0; j<n_grupos; j++){
					doc_id=entrada.readInt();
					group_id=entrada.readInt();
					mapa_grupos.put(doc_id, group_id);
				}
				
			}
			catch(Exception e){
				System.out.println("ColeccionPerfiles.cargar - problema al cargar grupos");
			}
			//fin cargar grupos
			
			entrada.close();
		}
		catch(FileNotFoundException e){
			System.out.println("ColeccionPerfiles.cargar - Archivo no encontrado, no se cargaran datos");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.println("ColeccionPerfiles.cargar - fin ("+coleccion.size()+" users cargados)");
		
	}
	
	public void guardarGruposTexto(String archivo){
		
		System.out.println("ColeccionPerfiles.guardarGruposTexto - inicio (guardando en "+archivo+")");
		
		PrintWriter salida=null;
		Iterator<Map.Entry<Integer, Integer>> it_grupos;
		Map.Entry<Integer, Integer> par_grupos;
		
		try{
			salida=new PrintWriter(new FileOutputStream(archivo));
			
			//guardar grupos
			salida.println(mapa_grupos.size());
			it_grupos=mapa_grupos.entrySet().iterator();
			while(it_grupos.hasNext()){
				par_grupos=it_grupos.next();
				salida.println(""+par_grupos.getKey()+" "+par_grupos.getValue());
			}
			salida.close();
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.println("ColeccionPerfiles.guardarGruposTexto - fin");
	}
	
	public void guardarTexto(String archivo){
		
		System.out.println("ColeccionPerfiles.guardarTexto - inicio (guardando en "+archivo+")");
		
		PrintWriter salida=null;
		Iterator<Map.Entry<Integer, Map<Integer, DatoPerfilDoc>>> it_col;
		Map.Entry<Integer, Map<Integer, DatoPerfilDoc>> par_col;
		Map<Integer, DatoPerfilDoc> mapa;
		Iterator<Map.Entry<Integer, DatoPerfilDoc>> it_user;
		Map.Entry<Integer, DatoPerfilDoc> par_user;
		Iterator<Map.Entry<Integer, Integer>> it_grupos;
		Map.Entry<Integer, Integer> par_grupos;
		
		int user_id=0;
		int doc_id=0;
		DatoPerfilDoc perfil=null;
		
		try{
//			salida=new PrintWriter(new FileOutputStream(archivo));
			salida=new PrintWriter(new OutputStreamWriter(new FileOutputStream(archivo), "UTF-8"));
			//numero users
			salida.println("n_users: "+coleccion.size());
			it_col=coleccion.entrySet().iterator();
			while(it_col.hasNext()){
				par_col=it_col.next();
				user_id=par_col.getKey();
				mapa=par_col.getValue();
				//user id
				salida.println("user_id: "+user_id);
				//numero docs
				salida.println("n_docs: "+mapa.size());
				it_user=mapa.entrySet().iterator();
				while(it_user.hasNext()){
					par_user=it_user.next();
					doc_id=par_user.getKey();
					perfil=par_user.getValue();
					//doc_id
					salida.println("doc_id: "+doc_id);
					//perfil
					perfil.guardarTexto(salida);
				}
			}
			
			//guardar grupos
			
			salida.println("n_grupos: "+mapa_grupos.size());
			it_grupos=mapa_grupos.entrySet().iterator();
			while(it_grupos.hasNext()){
				par_grupos=it_grupos.next();
				salida.println(""+par_grupos.getKey()+" "+par_grupos.getValue());
			}
			
			salida.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.println("ColeccionPerfiles.guardarTexto - fin");
		
	}
	
	public static void main(String[]args){
		System.out.println("Prueba");
		
		ColeccionPerfiles coleccion=new ColeccionPerfiles("localhost", "tigabytes", "tigabytes", "clave123");
		coleccion.guardar("docs_archivo_mini.bin");
		coleccion=null;
		
		coleccion=new ColeccionPerfiles("localhost", "tigabytes", "tigabytes", "clave123");
		coleccion.cargar("docs_archivo_mini.bin");
		
	}
	
	
	
}



