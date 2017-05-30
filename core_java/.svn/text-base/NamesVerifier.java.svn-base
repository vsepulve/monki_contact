
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
import java.util.StringTokenizer;

import java.io.FileOutputStream;
import java.io.PrintWriter;

import java.text.DecimalFormat;

import java.lang.Comparable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;

import java.util.Date;
import java.text.SimpleDateFormat;

import obs.spaces.data.DatoPerfilDoc;

import obs.comm.ConexionMC;

public class NamesVerifier{
	
    //static String archivo_log="/root/logs/log_detallado.txt";
	static SimpleDateFormat formato_fecha=new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
	
	ConexionMC conexion;
	
	public static void main(String[]args) throws SQLException {
		
		if(args.length!=2){
			System.out.println("");
			System.out.println("Modo de Uso");
			System.out.println("> java NamesVerifier archivo_config user_id");
			System.out.println("Si user_id=0, permanecera revisando todos los usuarios.");
			System.out.println("");
			return;
		}
		
		String archivo_config=args[0];
		int user_buscado=new Integer(args[1]);
		
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
			System.err.println("NamesVerifier - Error al leer configuracion ("+archivo_config+")");
			e.printStackTrace();
			return;
		}
		
		ConexionMC conexion=new ConexionMC(host, bd, usuario, clave);
		
		NamesVerifier verifier=new NamesVerifier(conexion);
		
		List<Integer> lista_usuarios=null;
		Iterator<Integer> it_usuarios;
		int user_id;
		
		int iteraciones=0;
		long segundos=0;
		
		if(user_buscado>0){
			user_id=user_buscado;
			verifier.searchNames(user_id);
		}
		else{
			lista_usuarios=conexion.getUsuarios();
			it_usuarios=lista_usuarios.iterator();
			while(it_usuarios.hasNext()){
				user_id=it_usuarios.next();
				verifier.searchNames(user_id);
			}
		}
		
	}
	
	public NamesVerifier(ConexionMC _conexion){
		conexion=_conexion;
	}
	
	public void searchNames(int user_id){
	
		System.out.println("searchNames - Inicio ("+user_id+")");
		
		Date d_inicio=null;
		Date d_fin=null;
		
		//traer datos para llenar mapa
		//se necesita de la bd:
		//  (contact_id, red, nombre, (group_id?))
		List<DatoPerfilDoc> perfiles=conexion.getPerfilesReducidosUsuario(user_id);
		Iterator<DatoPerfilDoc> it_perfiles;
		DatoPerfilDoc perfil;
		
		//preparar mapa 
		
		//mapa de nombre -> list (contact/group ids)
		Map<String, List<Integer>> mapa_nombres=new TreeMap<String, List<Integer>>();
		Iterator<Map.Entry<String, List<Integer>>> it_nombres;
		Map.Entry<String, List<Integer>> par_nombres;
		List<Integer> lista_contactos;
		Iterator<Integer> it_contactos;
		String nombre;
		int contact_id;
		
		//
		Map<Integer, String> nombres_originales=new TreeMap<Integer, String>();
		
		it_perfiles=perfiles.iterator();
		while(it_perfiles.hasNext()){
			perfil=it_perfiles.next();
			contact_id=perfil.getId();
			nombre=perfil.getFullName();
			
			if(nombre==null || nombre.length()<3){
				continue;
			}
			
			nombres_originales.put(contact_id, nombre);
			
			//tokenizar el nombra
			//guardar en mapa_nombres
			StringTokenizer toks=new StringTokenizer(nombre);
			while(toks.hasMoreTokens()){
				String tok=toks.nextToken();
				if(tok.length()>2){
					if(mapa_nombres.containsKey(tok)){
						mapa_nombres.get(tok).add(contact_id);;
					}
					else{
						lista_contactos=new LinkedList<Integer>();
						lista_contactos.add(contact_id);
						mapa_nombres.put(tok, lista_contactos);
					}
				}
			}
			
		}//while... cada perfil
		
		int group_id=0;
		String nombre_original;
		it_nombres=mapa_nombres.entrySet().iterator();
		while(it_nombres.hasNext()){
			par_nombres=it_nombres.next();
			nombre=par_nombres.getKey();
			lista_contactos=par_nombres.getValue();
			
			Map<Integer, Set<String>> nombres_grupos=new TreeMap<Integer, Set<String>>();
			it_contactos=lista_contactos.iterator();
			while(it_contactos.hasNext()){
				contact_id=it_contactos.next();
				group_id=conexion.getGroupId(contact_id);
				nombre_original=nombres_originales.get(contact_id);
				if(nombres_grupos.containsKey(group_id)){
					nombres_grupos.get(group_id).add(nombre_original);
				}
				else{
					Set<String> nombres=new TreeSet<String>();
					nombres.add(nombre_original);
					nombres_grupos.put(group_id, nombres);
				}
			}
			
			
			if(nombres_grupos.size()>1){
				//procesar lista
				
				System.out.println("Nombre \""+nombre+"\"");
				
				for(Map.Entry<Integer, Set<String>> par_nombres_grupos : nombres_grupos.entrySet()){
					group_id=par_nombres_grupos.getKey();
					Set<String> nombres=par_nombres_grupos.getValue();
					
					System.out.print("Group "+group_id+" ");
					for(String s : nombres){
						System.out.print("\""+s+"\" ");
					}
					System.out.println("");
					
				}
				
				System.out.println("-----");
				
			}//if... procesar lista
			
		}//while... cada nombre
		
		System.out.println("searchNames - Fin");
		
	}
	
}




