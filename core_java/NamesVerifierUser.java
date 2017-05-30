
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.ListIterator;
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

public class NamesVerifierUser{
	
    //static String archivo_log="/root/logs/log_detallado.txt";
	static SimpleDateFormat formato_fecha=new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
	
	ConexionMC conexion;
	
	public static void main(String[]args) throws SQLException {
		
		if(args.length!=2){
			System.out.println("");
			System.out.println("Modo de Uso");
			System.out.println("> java NamesVerifierUser archivo_config user_id");
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
			System.err.println("NamesVerifierUser - Error al leer configuracion ("+archivo_config+")");
			e.printStackTrace();
			return;
		}
		
		ConexionMC conexion=new ConexionMC(host, bd, usuario, clave);
		
		NamesVerifierUser verifier=new NamesVerifierUser(conexion);
		
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
	
	public NamesVerifierUser(ConexionMC _conexion){
		conexion=_conexion;
	}
	
	public void searchNames(int user_id){
	
		System.out.println("searchNames - Inicio ("+user_id+")");
		
		Date d_inicio=null;
		Date d_fin=null;
		
		//traer datos para llenar mapa
		//se necesita de la bd:
		//  (contact_id, red, nombre, (group_id?))
		List<DatoPerfilDoc> perfiles=conexion.getPerfilesUsuario(user_id);
		ListIterator<DatoPerfilDoc> it1, it2;
		DatoPerfilDoc p1, p2;
		boolean similares;
		String nombre1, nombre2;
		String[] palabras1, palabras2;
		Set<String> set_nombre=new TreeSet<String>();
		int g1, g2;
		
		it1=perfiles.listIterator();
		while(it1.hasNext()){
			p1=it1.next();
			nombre1=p1.getFullName();
			//condiciones de exclusion
			if(nombre1==null || nombre1.length()<3){
				continue;
			}
			palabras1=nombre1.split(" ");
			set_nombre.clear();
			for(int i=0; i<palabras1.length; i++){
				set_nombre.add(palabras1[i]);
			}
				
			it2=perfiles.listIterator(it1.nextIndex());
			while(it2.hasNext()){
				p2=it2.next();
				nombre2=p2.getFullName();
				//condiciones de exclusion
				if(nombre2==null || nombre2.length()<3){
					continue;
				}
				
				similares=false;
				
				if(! similares){
					//criterio 1: nombre igual
					if(nombre1.indexOf(' ')>0 && nombre2.indexOf(' ')>0
						&& nombre1.equals(nombre2)){
						similares=true;
					}
				}
				
				if(! similares){
					//criterio 2: un nombre contiene otro (en palabras)
					palabras2=nombre2.split(" ");
					boolean fallo=false;
					if(palabras1.length==1 || palabras2.length==1){
						fallo=true;
					}
					for(int i=0; !fallo && i<palabras2.length; i++){
						if(!set_nombre.contains(palabras2[i])){
							fallo=true;
							break;
						}
					}
					if(! fallo){
						similares=true;
					}
				}
				
				//si son similares, comprar grupos
				if(similares){
					g1=conexion.getGroupId(p1.getId());
					g2=conexion.getGroupId(p2.getId());
					if(g1 != g2){
						System.out.println("[grupo: "+g1+" - "+p1.getId()+"]: "+p1);
						System.out.println("[grupo: "+g2+" - "+p2.getId()+"]: "+p2);
						System.out.println("----- -----");
					}//if... grupos diferentes
				}//if... similares
				
			}//while... it2
		}//while... it1
		
		
		System.out.println("searchNames - Fin");
		
	}
	
}




