
import java.text.DecimalFormat;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
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
import java.io.OutputStreamWriter;

import java.util.Date;

import obs.comm.ConexionMC;
import obs.comm.ServiceConnection;

public class UpdateServices{
	
	static boolean debug=false;

	private ConexionMC conexion;
		
	private Date d_inicio;
	private Date d_fin;
	
	public static void main(String[]args){
		
		if(args.length != 5){
			System.out.println("");
			System.out.println("Modo de Uso");
			System.out.println("> java UpdateServices archivo_config base_archivo_indice base_archivo_grupos user_id host_update");
			System.out.println("Los arcvhivos se formaran como \"[base]_id.txt\"");
			System.out.println("user_id=0 => todos los usuarios");
			System.out.println("Se copiaran archivos y actualizaran los servicios SOLO en host_update.");
			System.out.println("");
			return;
		}
		
		String archivo_config = args[0];
		String base_archivo_indice = args[1];
		String base_archivo_grupos = args[2];
		int user_id_buscado = new Integer(args[3]);
		String host_update = args[4];
		
		String host = "";
		String bd = "";
		String usuario = "";
		String clave = "";
		String host_lectura = "";
		BufferedReader lector = null;
		try{
			lector = new BufferedReader(new FileReader(archivo_config));
			host = lector.readLine();
			bd = lector.readLine();
			usuario = lector.readLine();
			clave = lector.readLine();
			host_lectura = lector.readLine();
			
		}
		catch(Exception e){
			System.err.println("UpdateServices - Error al leer configuracion ("+archivo_config+")");
			e.printStackTrace();
			return;
		}
		
		System.out.println("UpdateServices - Inicio ("+host+", "+bd+")");
		
		ConexionMC conexion = new ConexionMC(host, bd, usuario, clave);
		
		UpdateServices generator = new UpdateServices(conexion);
		
		//por ahora le paso todos los usuarios
		//pero la idea es solo pasarle la fraccion de la que se encarga
		//esto podria recibirse de alguna forma como parametro
		if(user_id_buscado > 0){
			generator.updateForSingleUser(user_id_buscado, base_archivo_indice, base_archivo_grupos, host_update);
		}
		else{
			List<Integer> lista_usuarios = conexion.getUsuarios();
			generator.updateForUsers(lista_usuarios, base_archivo_indice, base_archivo_grupos, host_update);
		}
		System.out.println("UpdateServices - Fin");
		
	}
	
	public UpdateServices(){
		conexion = null;
	}
	
	public UpdateServices(ConexionMC _conexion){
		conexion = _conexion;
	}
	
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
	
	public void updateForSingleUser(int user_id, String base_indice, String base_grupos, String host_update){
		
		//Datos y estructuras que sera reutilizadas
		
		ServiceConnection conexion_servicio = new ServiceConnection();
		String ruta_indice = null;
		String ruta_grupos = null;
		
		//tomar relacion user -> servicios de la base de datos
		
		//Inicio Generar datos ficticios para todos los usuarios
		Map<Integer, Map<String, List<Integer>>> servicios_usuarios = new TreeMap<Integer, Map<String, List<Integer>>>();
		Map<Integer, List<String>> maquinas_usuarios = new TreeMap<Integer, List<String>>();
		Map<String, List<Integer>> mapa_maquinas = null;
		List<Integer> lista_puertos = null;
		String host;
//		int port;
		
		mapa_maquinas = new TreeMap<String, List<Integer>>();
		lista_puertos = new LinkedList<Integer>();
		//lista_puertos.add(30001);
		//lista_puertos.add(31001);
		lista_puertos.add(32001);
		
		//Mapa de maquinas (para servicios)
//		mapa_maquinas.put("localhost", lista_puertos);
//		mapa_maquinas.put("173.204.95.19", lista_puertos);
		mapa_maquinas.put(host_update, lista_puertos);
		servicios_usuarios.put(user_id, mapa_maquinas);
		
		//Lista de hosts (para copiar archivos)
		//No tiene sentido incluir localhost aqui, solo las demas maquinas
		List<String> maquinas = new LinkedList<String>();
//		maquinas.add("localhost");
//		maquinas.add("173.204.95.19");
		maquinas.add(host_update);
		maquinas_usuarios.put(user_id, maquinas);
		
		
		
		//Aqui solo se procesa un usuario
		
		System.out.println("----- User "+user_id+" -----");
		
		ruta_indice = base_indice+"_"+user_id+".ii.txt";
		ruta_grupos = base_grupos+"_"+user_id+".txt";
		//Solo genero el archivo de grupos aqui
		//generateIndex(user_id, ruta_indice);
		generateGroups(user_id, ruta_grupos);
		
		//copiar el indice a la maquina correcta (recordar que pueden ser varias)
		for(String h : maquinas_usuarios.get(user_id)){
			if(h.compareTo("localhost") != 0){
				copyFile(ruta_indice, h);
				copyFile(ruta_grupos, h);
			}
		}
		
		//actualizar servicio de la maquina correcta  (recordar que pueden ser varias)
		for(Map.Entry<String, List<Integer>> par_maquinas : servicios_usuarios.get(user_id).entrySet()){
			host = par_maquinas.getKey();
			lista_puertos = par_maquinas.getValue();
			for(int port : lista_puertos){
				
				System.out.println("Actualizando "+host+", "+port+", "+user_id+"");
				
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
		
	}
	
	public void updateForUsers(List<Integer> lista_usuarios, String base_indice, String base_grupos, String host_update){
		
		//Datos y estructuras que sera reutilizadas
		
		ServiceConnection conexion_servicio = new ServiceConnection();
		String ruta_indice = null;
		String ruta_grupos = null;
		
		//Inicio Generar datos ficticios para todos los usuarios
		Map<Integer, Map<String, List<Integer>>> servicios_usuarios = new TreeMap<Integer, Map<String, List<Integer>>>();
		Map<Integer, List<String>> maquinas_usuarios = new TreeMap<Integer, List<String>>();
		Map<String, List<Integer>> mapa_maquinas = null;
		List<Integer> lista_puertos = null;
		String host;
//		int port;

		for(int user_id : lista_usuarios){
			mapa_maquinas = new TreeMap<String, List<Integer>>();
			lista_puertos = new LinkedList<Integer>();
			//lista_puertos.add(30001);
			//lista_puertos.add(31001);
			lista_puertos.add(32001);
			
			//Mapa de maquinas (para servicios)
//			mapa_maquinas.put("localhost", lista_puertos);
//			mapa_maquinas.put("173.204.95.19", lista_puertos);
			mapa_maquinas.put(host_update, lista_puertos);
			servicios_usuarios.put(user_id, mapa_maquinas);
			
			//No tiene sentido incluir localhost aqui, solo las demas maquinas
			List<String> maquinas = new LinkedList<String>();
//			maquinas.add("localhost");
//			maquinas.add("173.204.95.19");
			maquinas.add(host_update);
			maquinas_usuarios.put(user_id, maquinas);
		}
		//Fin Generar datos ficticios para todos los usuarios
		
		for(int user_id : lista_usuarios){
		
			System.out.println("----- User "+user_id+" -----");
			
			ruta_indice = base_indice+"_"+user_id+".ii.txt";
			ruta_grupos = base_grupos+"_"+user_id+".txt";
			//Solo genero el archivo de grupos aqui
			//generateIndex(user_id, ruta_indice);
			generateGroups(user_id, ruta_grupos);
			
			//copiar el indice a la maquina correcta (recordar que pueden ser varias)
			for(String h : maquinas_usuarios.get(user_id)){
				if(h.compareTo("localhost") != 0){
					copyFile(ruta_indice, h);
					copyFile(ruta_grupos, h);
				}
			}
			
			//actualizar servicio de la maquina correcta  (recordar que pueden ser varias)
			for(Map.Entry<String, List<Integer>> par_maquinas : servicios_usuarios.get(user_id).entrySet()){
				host = par_maquinas.getKey();
				lista_puertos = par_maquinas.getValue();
				
				for(int port : lista_puertos){
				
					System.out.println("Actualizando "+host+", "+port+", "+user_id+"");
					
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
			//sleep por 1/10 de segundo para no copar la cola
			try{
				Thread.sleep(300);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}//while... cada usuario
		
	}
	
	private void generateGroups(int user_id, String ruta_grupos){
//		System.out.println("generateGroups - inicio ("+user_id+")");
		
		Map<Integer, List<Integer>> grupos = conexion.getGruposUsuario(user_id);
		
		List<Integer> grupo;
		int group_id = 0;
		int n_contacts = 0;
		
		PrintWriter salida = null;
		
		try{
			salida = new PrintWriter(new OutputStreamWriter(new FileOutputStream(ruta_grupos), "UTF-8"));
			
			for(Map.Entry<Integer, List<Integer>> par_grupos : grupos.entrySet()){
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
		
//		System.out.println("generateGroups - fin");
	}
	
}



