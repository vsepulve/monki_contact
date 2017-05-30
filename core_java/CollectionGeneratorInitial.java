
import java.text.DecimalFormat;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.EOFException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import obs.spaces.data.DatoPerfilDoc;

import obs.spaces.util.GeneradorVocabularioPerfiles;

import obs.text.indexing.IndiceInvertidoMultiusuario;
import obs.text.indexing.IndiceInvertidoMultiusuarioAtributos;
import obs.text.indexing.ListaInvertida;

import obs.comm.ConexionMC;

import obs.grouping.NamesGrouper;
import obs.grouping.GroupingLogger;

import java.util.Date;

//Desde ahora este programa se encarga solo de recrear el Vocabulario Global
//El proceso de regeneracion de los vocabularios (como parte de la agrupacion inicial)
//consta de usar este modulo primero, y luego dejar que el generador de la coleccion normal
//procese a todos los usuarios

public class CollectionGeneratorInitial{
	
	public static void main(String[]args){
		
		Date d_inicio = null;
		Date d_fin = null;
		
		if(args.length!=1){
			System.out.println("");
			System.out.println("Modo de Uso");
			System.out.println("> java GenerarColeccionNueva archivo_config");
			System.out.println("Genera el vocabulario global");
			System.out.println("");
			return;
		}
		
		String archivo_config = args[0];
		
		String host = "";
		String bd = "";
		String usuario = "";
		String clave = "";
		String host_lectura = "";
		BufferedReader lector = null;
		try{
			lector=new BufferedReader(new FileReader(archivo_config));
			host=lector.readLine();
			bd=lector.readLine();
			usuario=lector.readLine();
			clave=lector.readLine();
			host_lectura=lector.readLine();
		}
		catch(Exception e){
			System.err.println("CollectionGeneratorInitial - Error al leer configuracion ("+archivo_config+")");
			e.printStackTrace();
			return;
		}
		
		System.out.println("CollectionGeneratorInitial - Inicio ("+host+", "+bd+")");
		
		ConexionMC conexion = new ConexionMC(host, bd, usuario, clave);
		
		GeneradorVocabularioPerfiles generador_voc = new GeneradorVocabularioPerfiles();
		
		//mapa para el vocabulario global
		//solo cuenta el numero de docs en los que aparece cada termino
		Map<String, Integer> voc_global = new TreeMap<String, Integer>();
		int total_docs=0;
		
		//lista de usuarios
		List<Integer> users = conexion.getUsuarios();
		Iterator<Integer> it_users;
		
		//Generar vocabulario global
		//Este codigo puede reemplazarse por cargar el vocabulario global
		
		System.out.println("CollectionGeneratorInitial - Generando vocabulario global");
		d_inicio = new Date();
		
		//cargar el vocabulario de la base de datos
		//se generara la coleccion global desde 0
		total_docs = 0;
		int contador_user = 0;
		
		//por cada usuario
		for(int user_id : users){
			
			//tomar todos sus perfiles
			List<DatoPerfilDoc> lista_perfiles = conexion.getPerfilesUsuario(user_id);
			
			System.out.println("CollectionGeneratorInitial - user "+user_id+" ("+contador_user+"/"+users.size()+"), "+lista_perfiles.size()+" perfiles");
			contador_user++;
			
			for(DatoPerfilDoc perfil : lista_perfiles){
				
				Map<String, Map<String, Double>> mapa_atributos = generador_voc.generarVocabularios(perfil);
			
				for(Map.Entry<String, Map<String, Double>> par_atributos : mapa_atributos.entrySet()){
				
					String atributo = par_atributos.getKey();
					Map<String, Double> voc = par_atributos.getValue();
				
					//Esto es un doc (atributo de un perfil de un user)
					total_docs++;
					
					for(Map.Entry<String, Double> par_voc : voc.entrySet()){
						String term = par_voc.getKey();
						if(voc_global.containsKey(term)){
							voc_global.put(term, 1 + voc_global.get(term));
						}
						else{
							voc_global.put(term, 1);
						}
					}//for... cada term
					
				}//for... cada atributo (doc)
				
			}//while... cada perfil del usuario
			
//			if(contador_user == 3)
//				break;
			
		}//while... cada usuario
		
		d_fin = new Date();
		System.out.println("CollectionGeneratorInitial - Vocabulario global generado ("+(long)(d_fin.getTime()-d_inicio.getTime())/1000+" s, "+users.size()+" usuarios, "+total_docs+" docs)");
		
		d_inicio = new Date();
		System.out.println("CollectionGeneratorInitial - Guardando vocabulario");
		
		conexion.guardarVocabularioGlobal(voc_global);
		conexion.guardarTotalDocs(total_docs);
		
		d_fin = new Date();
		System.out.println("CollectionGeneratorInitial - Vocabulario global guardado ("+(long)(d_fin.getTime()-d_inicio.getTime())/1000+" s)");
		
		System.out.println("CollectionGeneratorInitial - Fin");
		
	}
}








