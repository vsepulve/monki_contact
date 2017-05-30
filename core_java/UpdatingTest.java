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

import obs.grouping.GroupingLogger;

public class UpdatingTest{
	
	private Connection conexion=null;
	
	private PreparedStatement select_users_src;
	private PreparedStatement update_status;
	
	private Set<String> exact_sources;
	
	GroupingLogger logger;
	
	public static void main(String[]args){
		
		if(args.length!=2){
			System.out.println("");
			System.out.println("Modo de Uso");
			System.out.println("> java UpdatingTest archivo_config sleep_seconds");
			System.out.println("");
			return;
		}
		
		String archivo_config=args[0];
		int sleep_seconds=new Integer(args[1]);
		
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
			System.err.println("UpdatingTest - Error al leer configuracion ("+archivo_config+")");
			e.printStackTrace();
			return;
		}
		
		UpdatingTest updater=new UpdatingTest(host, bd, usuario, clave);
		updater.updateContacts(sleep_seconds);
		
	}
	
	public UpdatingTest(String host, String db, String user, String pass){
		System.out.println("UpdatingTest - inicio (cargando desde "+host+"/"+db+")");
		
		if(!connect(host, db, user, pass)){
			System.out.println("UpdatingTest - Terminado por problemas al conectar");
			return;
		}
		prepararConsultas();
		
		System.out.println("UpdatingTest - fin");
	}
	
	private boolean connect(String host, String db, String user, String pass){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			conexion=DriverManager.getConnection ("jdbc:mysql://"+host+"/"+db, user, pass);
		}
		catch(Exception e){
			System.out.println("UpdatingTest.conectar - Incapaz de Conectar :");
			System.out.println("Username: \""+user+"\", Password: \""+pass+"\"");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private void prepararConsultas(){
		String sql;
		
		try{
			sql="select distinct(user_id) from socialNetworks where source = ?";
			select_users_src=conexion.prepareStatement(sql);
			
			sql="update contacts set status = ? where user_id = ? and srcName = ?";
			update_status=conexion.prepareStatement(sql);
	
		}
		catch(Exception e){
			System.out.println("UpdatingTest.prepararConsultas - Error al preparar consultas");
			e.printStackTrace();
		}
	}
	
	public void updateContacts(int sleep_seconds){
		System.out.println("UpdatingTest.updateContacts - inicio");
		
		//tomar lista de usuarios
		List<Integer> usuarios=getUsuariosRed("linkedin");
		System.out.println("UpdatingTest.updateContacts - "+usuarios.size()+" usuarios obtenidos");
		
		//por cada usuario
		for(Integer user_id : usuarios){
			//update status
			System.out.println("UpdatingTest.updateContacts - updateando contactos de linkedin de user "+user_id+"");
			changeStatus(user_id, "linkedin", "updated");
			//sleep
			try{
				System.out.println("UpdatingTest.updateContacts - durmiendo "+sleep_seconds+" segundos (se puede parar el programa ahora)...");
				Thread.sleep(1000*(sleep_seconds-1));
				System.out.println("UpdatingTest.updateContacts - despertando (No parar el programa)...");
				Thread.sleep(1000);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		
		System.out.println("UpdatingTest.updateContacts - fin");
	}
	
	private List<Integer> getUsuariosRed(String red){
		
		List<Integer> lista_usuarios=new LinkedList<Integer>();
		ResultSet r=null;
		
		try{
			select_users_src.setString(1, red);
			r=select_users_src.executeQuery();
			while(r.next()){
				lista_usuarios.add(r.getInt(1));
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return lista_usuarios;
		
	}
	
	private void changeStatus(int user_id, String src_name, String status){
		
		ResultSet r=null;
		
		try{
			update_status.setString(1, status);
			update_status.setInt(2, user_id);
			update_status.setString(3, src_name);
			update_status.executeUpdate();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	
}



