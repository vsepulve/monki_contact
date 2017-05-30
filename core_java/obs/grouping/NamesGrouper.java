package obs.grouping;

import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Comparator;

import java.io.FileOutputStream;
import java.io.PrintWriter;

import java.text.DecimalFormat;

import java.lang.Comparable;

import obs.spaces.data.DatoPerfilDoc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.SQLException;
//import java.util.logging.Level;
//import java.util.logging.Logger;

import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;

import java.util.Date;
import java.text.SimpleDateFormat;

import obs.comm.ConexionMC;
//import obs.grouping.GroupingLogger;

public class NamesGrouper{
	
//    static String archivo_log="/root/logs/log_detallado.txt";
	static SimpleDateFormat formato_fecha = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
	static int module_id = 2;
	
	ConexionMC conexion;
//	GroupingLogger logger;
	
	public static void main(String[]args) throws SQLException {
		
		if(args.length!=2){
			System.out.println("");
			System.out.println("Modo de Uso");
			System.out.println("> java NamesGrouper archivo_config user_id");
			System.out.println("");
			return;
		}
		
		String archivo_config = args[0];
		int user_buscado = new Integer(args[1]);
		
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
			
		}
		catch(Exception e){
			System.err.println("NamesGrouper - Error al leer configuracion ("+archivo_config+")");
			e.printStackTrace();
			return;
		}
		
		ConexionMC conexion = new ConexionMC(host, bd, usuario, clave);
		
		NamesGrouper grouper = new NamesGrouper(conexion);
		
		grouper.groupByNames(user_buscado);
		
		System.out.println("NamesGrouper - Fin");
		
	}
	
	public NamesGrouper(ConexionMC _conexion){
		conexion = _conexion;
	}
	
	public void setConexion(ConexionMC _conexion){
		conexion = _conexion;
	}
	
	public void groupByNames(int user_id){
	
		System.out.println("groupByNames - Inicio ("+user_id+")");
		
		Date d_inicio=null;
		Date d_fin=null;
		
		//traer datos para llenar mapa
		//se necesita de la bd:
		//  (contact_id, red, nombre, (group_id?))
		List<DatoPerfilDoc> perfiles = conexion.getPerfilesReducidosUsuario(user_id);
		Iterator<DatoPerfilDoc> it_perfiles;
		DatoPerfilDoc perfil;
		
		//preparar mapa 
		
		//mapa de nombre -> list (contact/group ids)
		Map<String, List<Integer>> mapa_nombres = new TreeMap<String, List<Integer>>();
		Iterator<Map.Entry<String, List<Integer>>> it_nombres;
		Map.Entry<String, List<Integer>> par_nombres;
		List<Integer> lista_contactos;
		Iterator<Integer> it_contactos;
		String nombre;
		int contact_id;
		
		//mapa de contactid -> red social
		Map<Integer, String> mapa_redes=new TreeMap<Integer, String>();
		Iterator<Map.Entry<Integer, String>> it_redes;
		Map.Entry<Integer, String> par_redes;
		String red;
		
		it_perfiles=perfiles.iterator();
		while(it_perfiles.hasNext()){
			perfil = it_perfiles.next();
			contact_id = perfil.getId();
			nombre = perfil.getFullName();
			red = perfil.getNetwork();
			
			//Solo considero nombres razonablemente largos (8 o mas)
			//Notar que el indexOf lo pido mayor que cero (no sirve un espacio inicial)
			if(nombre!=null && red!=null && nombre.length()>=8 && nombre.indexOf(' ')>0){
				mapa_redes.put(contact_id, red);
				//Map<String, List<Integer>> mapa_nombres
				if(mapa_nombres.containsKey(nombre)){
					lista_contactos=mapa_nombres.get(nombre);
					lista_contactos.add(contact_id);
				}
				else{
					lista_contactos=new LinkedList<Integer>();
					lista_contactos.add(contact_id);
					mapa_nombres.put(nombre, lista_contactos);
				}
			}//if... red y nombre valido
			
		}//while... cada perfil
		
		//iterar por mapa vindo las listas
		// => guardar los contactid (o groupid) que deben ser agrupados
		
		//Si se desea el numero de contactos por red, basta con convertirlo en un Mapa
		Set<String> redes_distintas = new TreeSet<String>();
		
		SortedSet<Integer> grupos = new TreeSet<Integer>();
		Iterator<Integer> it_grupos;
		int group_id=0;
		it_nombres = mapa_nombres.entrySet().iterator();
		while(it_nombres.hasNext()){
			par_nombres = it_nombres.next();
			nombre = par_nombres.getKey();
			lista_contactos = par_nombres.getValue();
			
			if(lista_contactos.size()>1){
				//procesar lista
				
				it_contactos = lista_contactos.iterator();
				while(it_contactos.hasNext()){
					contact_id = it_contactos.next();
					red = mapa_redes.get(contact_id);
					redes_distintas.add(red);
				}//while... cada contacto
				
				if(redes_distintas.size()>=2){
					
					//en este caso tiene que haber solo 1 id en cada red
					//agrupar (id_facebook, id_linkedin)
					//esto tambien debe incluir, al menos, los contactos gmail con el mismo nombre
					//otra opcion es, en este caso, usar todos los ids
					//agruparContactos(lista_contactos);
					
					it_contactos=lista_contactos.iterator();
					while(it_contactos.hasNext()){
						contact_id = it_contactos.next();
						group_id = conexion.getGroupId(contact_id);
						if(group_id>0){
							grupos.add(group_id);
						}
					}//while... cada contacto
					
					if(grupos.size()>1){
						it_grupos = grupos.iterator();
						int min_group_id = it_grupos.next();
						while(it_grupos.hasNext()){
							group_id = it_grupos.next();
							System.out.println(""+group_id+" -> "+min_group_id+" ("+nombre+")");
							
							conexion.writeLog(group_id, min_group_id, 0, module_id, nombre+", user: "+user_id+"");
							
							conexion.actualizarAsGroup(group_id, min_group_id);
							
						}
					}
					grupos.clear();
					
				}//if... agrupar
				
				redes_distintas.clear();
				
			}//if... procesar lista
		}//while... cada nombre
		
		//procesar los grupos que deben ser mergeados
		//quizas se deban usar reglas para impedir el merge (N de mails distintos, etc)
		
		
		System.out.println("groupByNames - Fin");
		
	}
	
}




