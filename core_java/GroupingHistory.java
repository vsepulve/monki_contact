
import java.text.DecimalFormat;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.Iterator;

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

import obs.comm.ConexionMC;
import obs.grouping.Grouping;

public class GroupingHistory{

	static private Map<Integer, String> mapa_modulos;
	
	public static void main(String[]args){
		
		mapa_modulos = new TreeMap<Integer, String>();
		mapa_modulos.put(1, "ProcessingCell");
		mapa_modulos.put(2, "NamesGrouper");
		mapa_modulos.put(3, "InitialGrouped");
		mapa_modulos.put(4, "InitialIsolated");
		mapa_modulos.put(5, "GroupSeparator");
	
		if(args.length!=3){
			System.out.println("");
			System.out.println("Modo de Uso");
			System.out.println("> java GroupingHistory archivo_config group_id archivo_salida");
			System.out.println("");
			return;
		}
		
		String archivo_config=args[0];
		int group_id=new Integer(args[1]);
		String archivo_salida=args[2];
		
		System.out.println("");
		System.out.println("GroupingHistory - Inicio");
		
		String servidor="";
		String bd="";
		String usuario="";
		String clave="";
		BufferedReader lector=null;
		try{
			lector=new BufferedReader(new FileReader(archivo_config));
			servidor=lector.readLine();
			bd=lector.readLine();
			usuario=lector.readLine();
			clave=lector.readLine();
			
		}
		catch(Exception e){
			System.err.println("GroupingHistory - Error al leer configuracion ("+archivo_config+")");
			e.printStackTrace();
			return;
		}
		
		ConexionMC conexion=new ConexionMC(servidor, bd, usuario, clave);
		
		try{
			PrintWriter salida=new PrintWriter(new FileOutputStream(archivo_salida));
			salida.println("<?xml version='1.0' encoding='UTF-8'?>");
			writeLog(group_id, 0, salida, conexion);
			
			salida.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.println("");
		System.out.println("GroupingHistory - Fin");
		System.out.println("");
		
	}
	
	public static void writeLog(int new_group_id, int profundidad, PrintWriter salida, ConexionMC conexion){
		
		if(new_group_id==0) return;
		
		System.out.println("writeLog - history of "+new_group_id+"");
				
//		Map<Integer, String> log = conexion.getLogGrupoFormat(new_group_id);
		Set<Grouping> log = conexion.getLogGrupoFormat(new_group_id);
		
		int old_group_id, module_id;
		String mensaje;
		String linea = "";
		String modulo = "modulo";
		String tiempo;
		
		String tabs = "";
		for(int i=0; i<profundidad; i++){
			tabs += "\t";
		}
		
		salida.println(tabs+"<history group_id='"+new_group_id+"'>");
		
		if(log.size() > 0){
			salida.println("");
			for(Grouping g : log){
				//Cada uno de estos deberia ser un grouping del history de new_group_id
				old_group_id = g.getOldId();
				module_id = g.getRuleId();
				mensaje = g.getMessage();
				tiempo = g.getTime();
				
				if(mapa_modulos.containsKey(module_id)){
					modulo = mapa_modulos.get(module_id);
				}
				else{
					modulo = "Error-UnknownModule";
				}

				System.out.println("old: "+old_group_id+", module: "+modulo+"");
				
				linea = "<grouping module='"+modulo+"' old_group_id='"+old_group_id+"' time='"+tiempo+"'>";
				salida.println(tabs+"\t"+linea);
				
				linea = "<message>"+mensaje+"</message>";
				salida.println(tabs+"\t"+linea);
				
//				if(old_group_id>0){
					writeLog(old_group_id, profundidad+1, salida, conexion);
//				}
				
				salida.println(tabs+"\t</grouping>\n");
			}
		}
		
		salida.println(tabs+"</history>");
		
	}
	
	
	
	
}








