
import java.text.DecimalFormat;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import java.text.SimpleDateFormat;

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
import java.io.OutputStreamWriter;

import obs.spaces.data.DatoPerfilDoc;

import obs.spaces.util.GeneradorVocabularioPerfiles;

import obs.text.indexing.IndiceInvertidoMultiusuario;
import obs.text.indexing.IndiceInvertidoMultiusuarioAtributos;
import obs.text.indexing.ListaInvertida;

import obs.comm.ConexionMC;
import obs.comm.ServiceConnection;

import obs.grouping.NamesGrouper;

import java.util.Date;

public class CollectionGeneratorMultithread{
	
	static String archivo_log = "/root/logs/log_collection.txt";
	static SimpleDateFormat formato_fecha=new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
	
	static boolean debug=false;
	static Set<String> atributos;
	
	private ConexionMC conexion;
		
	private Date d_inicio;
	private Date d_fin;
	
	public static void main(String[]args){
		
		//fijo los atributos
		atributos=new TreeSet<String>();
		atributos.add("name");
		atributos.add("org");
		atributos.add("location");
		atributos.add("edu");
		atributos.add("hometown");
		atributos.add("lives");
		//fin de atributos
		
		if(args.length!=4){
			System.out.println("");
			System.out.println("Modo de Uso");
			System.out.println("> java CollectionGeneratorMultithread archivo_config base_archivo_indice base_archivo_grupos n_threads");
			System.out.println("Los arcvhivos se formaran como \"[base]_id.txt\"");
			System.out.println("");
			return;
		}
		
		String archivo_config = args[0];
		String base_archivo_indice = args[1];
		String base_archivo_grupos = args[2];
		int n_threads = new Integer(args[3]);
		
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
			System.err.println("CollectionGeneratorMultithread - Error al leer configuracion ("+archivo_config+")");
			e.printStackTrace();
			return;
		}
		
		System.out.println("CollectionGeneratorMultithread - Inicio ("+host+", "+bd+")");
		
		ConexionMC conexion = new ConexionMC(host, bd, usuario, clave);
//		NamesGrouper names_grouper = new NamesGrouper(conexion);
		
		//cargar vocabulario global
		
		Date d_inicio, d_fin;
		System.out.println("-----");
		System.out.println("Cargando Vocabulario global...");
		d_inicio=new Date();
		//Map<String, Integer> voc_global=conexion.cargarVocabularioGlobalConcurrente();
		Map<String, Integer> voc_global = conexion.cargarVocabularioGlobal();
		d_fin=new Date();
		System.out.println("Carga terminada ("+(long)(d_fin.getTime()-d_inicio.getTime())/1000+" s)");
		System.out.println("-----");
		
		List<Integer> lista_usuarios=null;
		ArrayList<List<Integer>> listas=new ArrayList<List<Integer>>();
		for(int i=0; i<n_threads; i++){
			listas.add(i, new LinkedList<Integer>());
		}
		
		List<Thread> threads=new LinkedList<Thread>();
		
		d_inicio=new Date();
		long segundos=0;
		
		//while true
		while(true){
			
			//verificar stop
			if(conexion.stopCollectionGenerator()){
				conexion.close();
				System.out.println("Marca stop, saliendo");
				return;
			}
			
			//cargar lista de usuarios
			lista_usuarios = conexion.getUsuariosGenerar(10*n_threads);
			if(lista_usuarios==null){
				System.out.println("Error al tomar lista de usuarios, reseteando conexion");
				conexion.close();
				
				System.out.println("Saliendo...");
				return ;
				
			}
			
			//escribir log
			try{
				PrintWriter salida=new PrintWriter(new FileOutputStream(archivo_log, true));
				salida.println("["+formato_fecha.format(new Date())+"] lista con "+lista_usuarios.size()+" usuarios");
				salida.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
			
			/*
			//desactivo la recarga por el momento
			d_fin=new Date();
			segundos=(long)(d_fin.getTime()-d_inicio.getTime())/1000;
			if( segundos>1*60*60 ){
				//actualizar voc global
				voc_global.clear();
				System.out.println("voc_global.size(): "+voc_global.size()+"");
				voc_global=null;
				System.gc();
				
				System.out.println("-----");
				System.out.println("(re)Cargando Vocabulario global...");
				d_inicio=new Date();
				voc_global=conexion.cargarVocabularioGlobal();
				d_fin=new Date();
				System.out.println("Carga terminada ("+(long)(d_fin.getTime()-d_inicio.getTime())/1000+" s)");
				System.out.println("-----");
				
				d_inicio=new Date();
			}
			*/
			
			//Por dormir o por actividad de los threads (con conexion propia), aqui cierro la conexion
			//Se reabre luego de que los threads
			
			conexion.close();
			conexion = null;
			
			//dormir si la lista esta vacia
			if(lista_usuarios.size()==0){
				//dormir por un cierto tiempo
				segundos = 60;
				System.out.println("-----");
				System.out.println("Sin contactos nuevos, durmiendo "+segundos+" segundos");
				System.out.println("-----");
				
//				conexion.close();
//				conexion = null;
				
				try{
					Thread.sleep(segundos*1000);
				}
				catch(Exception e){
					e.printStackTrace();
				}
//				conexion = new ConexionMC(host, bd, usuario, clave);
			}
			
			//separa lista de usuario en listas por thread
			for(int i=0; i<n_threads; i++){
				listas.get(i).clear();
			}
			int pos=0;
			for(Integer user_id : lista_usuarios){
				listas.get(pos).add(user_id);
				pos = (pos+1)%n_threads;
			}
			
			List<ConexionMC> lista_conexiones = new LinkedList<ConexionMC>();
			
			//lanzar los threads
			for(int i=0; i<n_threads; i++){
				ConexionMC conexion_thread = new ConexionMC(host, bd, usuario, clave);
				lista_conexiones.add(conexion_thread);
				NamesGrouper grouper_thread = new NamesGrouper(conexion_thread);
				threads.add(
					new Thread(
						new CollectionGeneratorThread(
							i, 
							conexion_thread, 
							listas.get(i), 
							base_archivo_indice, 
							base_archivo_grupos, 
							voc_global, 
							grouper_thread
						)
					)
				);
			}
			for(Thread t: threads){
				t.start();
			}
			
			//esperar los threads
			for(Thread t: threads){
				try{
					t.join();
				}
				catch (InterruptedException e){
					e.printStackTrace();
				}
			}
			threads.clear();
			
			for(ConexionMC conexion_thread : lista_conexiones){
				conexion_thread.close();
			}
			lista_conexiones.clear();
			lista_conexiones = null;
			
			//Threads terminados, reabro conexion
			conexion = new ConexionMC(host, bd, usuario, clave);
			
		}
		
//		System.out.println("CollectionGeneratorMultithread - Fin");
		
	}
	
	static class CollectionGeneratorThread implements Runnable{
	
	int id;
	Date d_inicio, d_fin;
	ConexionMC conexion;
	List<Integer> lista_usuarios;
	String base_indice;
	String base_grupos;
	Map<String, Integer> voc_global;
	NamesGrouper names_grouper;
	
	public CollectionGeneratorThread(){
		id = -1;
		conexion = null;
		lista_usuarios = null;
		base_indice = null;
		base_grupos = null;
		voc_global = null;
		names_grouper = null;
	}
	
	public CollectionGeneratorThread(int _id, ConexionMC _conexion, List<Integer> _lista_usuarios, String _base_indice, String _base_grupos, Map<String, Integer> _voc_global, NamesGrouper _names_grouper){
		id = _id;
		conexion = _conexion;
		lista_usuarios = _lista_usuarios;
		base_indice = _base_indice;
		base_grupos = _base_grupos;
		voc_global = _voc_global;
		names_grouper = _names_grouper;
	}
	
	public void run() {
		
		if(lista_usuarios==null || lista_usuarios.size()==0){
			return;
		}
		
		System.out.println("["+id+"] CollectionGeneratorThread.run - inicio ("+lista_usuarios.size()+" usuarios)");
		
		//Datos y estructuras que sera reutilizadas
		
		ServiceConnection conexion_servicio = new ServiceConnection();
		String ruta_indice = null;
		String ruta_grupos = null;
		
		//tomar relacion user -> servicios de la base de datos
//		Map<Integer, Map<String, List<Integer>>> servicios_usuarios = conexion.getUserServices();
		
		//Inicio Generar datos ficticios para todos los usuarios
		Map<Integer, Map<String, List<Integer>>> servicios_usuarios = new TreeMap<Integer, Map<String, List<Integer>>>();
		Map<Integer, List<String>> maquinas_usuarios = new TreeMap<Integer, List<String>>();
		Map<String, List<Integer>> mapa_maquinas=null;
		List<Integer> lista_puertos = null;
		String host;
		for(int user_id : lista_usuarios){
			mapa_maquinas = new TreeMap<String, List<Integer>>();
			lista_puertos = new LinkedList<Integer>();
			//lista_puertos.add(30001);
			//lista_puertos.add(31001);
			lista_puertos.add(32001);
			
			//Mapa de maquinas (para servicios)
			mapa_maquinas.put("localhost", lista_puertos);
//			mapa_maquinas.put("173.204.95.19", lista_puertos);
			servicios_usuarios.put(user_id, mapa_maquinas);
			
			//No tiene sentido incluir localhost aqui, solo las demas maquinas
			List<String> maquinas = new LinkedList<String>();
//			maquinas.add("localhost");
//			maquinas.add("173.204.95.19");
			maquinas_usuarios.put(user_id, maquinas);
		}
		//Fin Generar datos ficticios para todos los usuarios
		
		for(int user_id : lista_usuarios){
			System.out.println("["+id+"] ----- User "+user_id+" -----");
			
			names_grouper.groupByNames(user_id);
			
			//procesar vocabularios (solo recrear los new/update)
			generateVocabulary(user_id);
			
			//invertir y guardar
			ruta_indice = base_indice+"_"+user_id+".ii.txt";
			ruta_grupos = base_grupos+"_"+user_id+".txt";
			//ruta_grupos=base_grupos;
			generateIndex(user_id, ruta_indice);
			generateGroups(user_id, ruta_grupos);
			
			//copiar el indice a la maquina correcta (recordar que pueden ser varias)
			for(String h : maquinas_usuarios.get(user_id)){
				//cp indice... (asumir la misma ruta local)
				//cp grupos...
				copyFile(ruta_indice, h);
				copyFile(ruta_grupos, h);
			}
			
			//actualizar servicio de la maquina correcta  (recordar que pueden ser varias)
			for(Map.Entry<String, List<Integer>> par_maquinas : servicios_usuarios.get(user_id).entrySet()){
				host = par_maquinas.getKey();
				lista_puertos = par_maquinas.getValue();
				for(int port : lista_puertos){
					System.out.println("["+id+"] Actualizando "+host+", "+port+", "+user_id+"");
					
					if(conexion_servicio.connect(host, port)){
						//la instruccion deberia ser la misma para todos los servicios
						//OJO CON EL SERVICIO DE VOCABULARIO QUE NO USA GRUPOS, PERO DEBE RECIBIRLO
						conexion_servicio.write("us "+ruta_indice+" "+ruta_grupos+" 0 "+user_id);
						conexion_servicio.close();
					}
					else{
						System.out.println("Problemas al conectar a "+host+", "+port+", "+user_id+"");
					}
					
				}
			}
			
		}//while... cada usuario
		
		//Actualizar datos de usuarios del broker
		String maquina_broker = "localhost";
//		int puerto_broker = 32001;
		String ruta_usuarios = "archivo_usuarios.txt";
		
		generateUsersFile(servicios_usuarios, ruta_usuarios);
		//Map<Integer, Map<String, List<Integer>>> servicios_usuarios
		//cp usuarios... (asumir la misma ruta local)
		
//		conexion_servicio.connect(maquina_broker, puerto_broker);
//		conexion_servicio.write("u "+ruta_usuarios);
//		conexion_servicio.close();
		
	}//Fin generateCollection
	
	//Recibe la ruta ABSOLUTA del archivo
	//Asume que la ruta puede usarse en ambas maquinas
	public void copyFile(String file, String host){
		try {
			Process p = Runtime.getRuntime().exec("scp "+file+" "+host+":"+file+" ");
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line=reader.readLine();

			while (line != null) {	
				System.out.println(line);
				line = reader.readLine();
			}

		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void generateUsersFile(Map<Integer, Map<String, List<Integer>>> servicios_usuarios, String ruta_usuarios){
		
		System.out.println("["+id+"] generateUsersFile - inicio");
		
		Map<String, List<Integer>> mapa_maquinas = null;
		List<Integer> lista_puertos = null;
		int user_id;
		String host;
		int n_lineas = 0;
		
		PrintWriter salida = null;
		
		try{
			salida = new PrintWriter(new OutputStreamWriter(new FileOutputStream(ruta_usuarios), "UTF-8"));
			
			for(Map.Entry<Integer, Map<String, List<Integer>>> par_usuarios : servicios_usuarios.entrySet()){
				user_id = par_usuarios.getKey();
				mapa_maquinas = par_usuarios.getValue();
				
				for(Map.Entry<String, List<Integer>> par_maquinas : mapa_maquinas.entrySet()){
					host = par_maquinas.getKey();
					lista_puertos = par_maquinas.getValue();
					n_lineas += lista_puertos.size();
				}//for... cada maquina
			}//for... cada usuario
			
			//numero lineas
			salida.println(""+n_lineas);
			
			for(Map.Entry<Integer, Map<String, List<Integer>>> par_usuarios : servicios_usuarios.entrySet()){
				user_id = par_usuarios.getKey();
				mapa_maquinas = par_usuarios.getValue();
				for(Map.Entry<String, List<Integer>> par_maquinas : mapa_maquinas.entrySet()){
					host = par_maquinas.getKey();
					lista_puertos = par_maquinas.getValue();
					for(int puerto : lista_puertos){
						salida.println(""+user_id+" "+host+" "+puerto);
					}//for... cada puerto
				}//for... cada maquina
			}//for... cada usuario
			
			salida.close();
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.println("["+id+"] generateUsersFile - fin");
		
	}//Fin generateUsersFile
	
	private void generateGroups(int user_id, String ruta_grupos){
//		System.out.println("["+id+"] generateGroups - inicio ("+user_id+")");
		
		Map<Integer, List<Integer>> grupos = conexion.getGruposUsuario(user_id);
		List<Integer> grupo;
		int group_id = 0;
		int n_contacts = 0;
		
		PrintWriter salida = null;
		
		try{
			salida = new PrintWriter(new OutputStreamWriter(new FileOutputStream(ruta_grupos), "UTF-8"));
			
			for(Map.Entry<Integer, List<Integer>> par_grupos : grupos.entrySet()){
				group_id = par_grupos.getKey();
				grupo = par_grupos.getValue();
				n_contacts += grupo.size();
			}
			
			//numero contactos
			salida.println(""+n_contacts);
			
			for(Map.Entry<Integer, List<Integer>> par_grupos : grupos.entrySet()){
				group_id = par_grupos.getKey();
				grupo = par_grupos.getValue();
				for(int contact_id : grupo){
					salida.println(""+contact_id+" "+group_id);
				}
			}
			
			salida.close();
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
//		System.out.println("["+id+"] generateGroups - fin");
	}//Fin generateGroups
	
	private void generateVocabulary(int user_id){
		
		System.out.println("["+id+"] generateVocabulary - inicio ("+user_id+")");
		
		GeneradorVocabularioPerfiles generador_voc = new GeneradorVocabularioPerfiles();
		
//		System.out.println("Modificando Vocabulario Global");
//		d_inicio = new Date();
		
		//tomar todos sus perfiles
		//este paso es lento
		List<DatoPerfilDoc> lista_perfiles = conexion.getPerfilesUsuarioNuevos(user_id);
		
		//mapa para el vocabulario global
		//solo cuenta el numero de docs en los que aparece cada termino
		int total_docs = conexion.getTotalDocs();
		System.out.println("["+id+"] generateVocabulary - total_docs: "+total_docs+"");
		
		/*
		Map<String, Integer> voc_global_mod = new TreeMap<String, Integer>();
		
		//por cada perfil
		for(DatoPerfilDoc perfil : lista_perfiles){
			//extraer su vocabulario 
			Map<String, Double> voc = generador_voc.generarVocabulario(perfil);
			String term;
			//agregar el vocabulario al global 
			for(Map.Entry<String, Double> par_voc : voc.entrySet()){
				term = par_voc.getKey();
				if(voc_global.containsKey(term)){
					voc_global.put(term, 1 + voc_global.get(term));
				}
				else{
					voc_global.put(term, 1);
				}
				voc_global_mod.put(term, voc_global.get(term));
			}//for... cada term del perfil
		}//for... cada perfil del usuario
		
		conexion.guardarVocabularioGlobalModificado(voc_global_mod);
		voc_global_mod.clear();
		voc_global_mod = null;
		*/
		
//		d_fin = new Date();
//		System.out.println("["+id+"] Vocabulario global terminado ("+(long)(d_fin.getTime()-d_inicio.getTime())/1000+" s)");
//		System.out.println("["+id+"] -----");
		
		//generar vocabularios verdaderos (coleccion con valores)
		System.out.println("["+id+"] generateVocabulary - Generando coleccion con valores");
		d_inicio = new Date();
		
		//por cada perfil
		for(DatoPerfilDoc perfil : lista_perfiles){
			//vocabularios de cada atributo
//			for(String atributo : atributos){
//				Map<String, Double> voc = generador_voc.generarVocabularioAtributo(perfil, atributo);
			
			Map<String, Map<String, Double>> mapa_atributos = generador_voc.generarVocabularios(perfil);
			
			for(Map.Entry<String, Map<String, Double>> par_atributos : mapa_atributos.entrySet()){
				
				String atributo = par_atributos.getKey();
				Map<String, Double> voc = par_atributos.getValue();
				
				String term;
				double frec;
				double suma_frecs;
				
				//sumar las frecuencias (equivalente al largo del doc)
				suma_frecs = 0.0;
				for(Map.Entry<String, Double> par_voc : voc.entrySet()){
					term = par_voc.getKey();
					frec = par_voc.getValue();
					suma_frecs += frec;
				}//for... cada term del perfil
				
				if(suma_frecs < 1.0){
					suma_frecs = 1.0;
				}
				
				//calcular valor real
				Map<String, Double> voc_modificado = new TreeMap<String, Double>();
				double valor, tf, idf;
				for(Map.Entry<String, Double> par_voc : voc.entrySet()){
					term = par_voc.getKey();
					frec = par_voc.getValue();
					
					tf = frec/suma_frecs;
					//idf = total_docs/docs_con_term
					//por seguridad, se verifica docs_con_term
					if( !voc_global.containsKey(term) || voc_global.get(term) < 1){
						idf = Math.log( (double)(total_docs) );
					}
					else{
						idf = Math.log( (double)(total_docs)/(double)(voc_global.get(term)) );
					}
					if(idf < 0.01){
						idf = 0.01;
					}
					//tfidf modificado para darle mas peso a la frecuencia
					//valor = (frec-1) + tf*idf;
					valor = tf*idf;
					
					voc_modificado.put(term, valor);
					
				}//for... cada term del perfil
				
				conexion.guardarVocabulario(user_id, atributo, perfil.getId(), voc_modificado);
				
			}//for... cada atributo
		}//for... cada perfil del usuario
		
		generador_voc = null;
		
		//cambiar estado a processing
		
		List<Integer> contactos = new LinkedList<Integer>();
		for(DatoPerfilDoc perfil : lista_perfiles){
			contactos.add(perfil.getId());
		}
		conexion.cambiarEstadoContactos(contactos, "processing");
		
		d_fin = new Date();
		System.out.println("["+id+"] generateVocabulary - Coleccion terminado ("+(long)(d_fin.getTime()-d_inicio.getTime())/1000+" s)");
		System.out.println("["+id+"] -----");
		
	}//Fin generateVocabulary
	
	private void generateIndex(int user_id, String ruta_indice){
		
//		System.out.println("generateIndex - inicio ("+user_id+")");
		
		//generar indice invertido
		System.out.println("["+id+"] Generando indice");
		d_inicio = new Date();
		
		IndiceInvertidoMultiusuarioAtributos indice = new IndiceInvertidoMultiusuarioAtributos();
		
		//La carga de vocabularios deberia ser por usuario, atributo
		//Entoces, se trata cada perfil/atributo como doc individual
		
		for(String atributo : atributos){
			
			//Lo que sigue puede ser reemplazado por conservar un mapa con los vocs del usuario de la fase anterior
			Map<Integer, Map<String, Double>> vocabularios = conexion.cargarVocabularios(user_id, atributo);
			Map<String, Double> vocabulario;
			int contact_id;
			String term;
			double valor;
			
//			//estructura para guardar el indice del usuario en forma temporal
			Map<String, Map<Integer, Double>> indice_temporal = new TreeMap<String, Map<Integer, Double>>();
			for(Map.Entry<Integer, Map<String, Double>> par_vocabularios : vocabularios.entrySet()){
				contact_id = par_vocabularios.getKey();
				vocabulario = par_vocabularios.getValue();
				for(Map.Entry<String, Double> par_vocabulario : vocabulario.entrySet()){
					term = par_vocabulario.getKey();
					valor = par_vocabulario.getValue();
					if(indice_temporal.containsKey(term)){
						indice_temporal.get(term).put(contact_id, valor);
					}
					else{
						Map<Integer, Double> mapa_lista = new TreeMap<Integer, Double>();
						mapa_lista.put(contact_id, valor);
						indice_temporal.put(term, mapa_lista);
					}
				}//for... cada term del vocabulario
			}//for... cada vocabulario del usuario
			
			//guardar el indice temporal en el indice real
			Map<Integer, Double> mapa_lista;
			for(Map.Entry<String, Map<Integer, Double>> par_indice : indice_temporal.entrySet()){
				term = par_indice.getKey();
				mapa_lista = par_indice.getValue();
				indice.addLista(user_id, atributo, term, new ListaInvertida(mapa_lista));
				mapa_lista.clear();
			}
			indice_temporal.clear();
			
		}//for... cada atributo
		
		//guardar indice en texto
		indice.guardarTexto(ruta_indice, user_id);
		
		d_fin = new Date();
		System.out.println("["+id+"] Indice invertido terminado ("+(long)(d_fin.getTime()-d_inicio.getTime())/1000+" s)");
		System.out.println("["+id+"] -----");
		
	}//Fin generateIndex
	
	}//clase Runnable
	
}



