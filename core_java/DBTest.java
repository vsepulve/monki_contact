
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

import obs.comm.ConexionMC;

import java.util.Date;

public class DBTest{
	
	public static void main(String[]args){
		
		if(args.length != 1){
			System.out.println("");
			System.out.println("Modo de Uso");
			System.out.println("> java DBTest archivo_config");
			System.out.println("");
			return;
		}
		
		String archivo_config = args[0];
		
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
			System.err.println("CollectionGenerator - Error al leer configuracion ("+archivo_config+")");
			e.printStackTrace();
			return;
		}
		
		System.out.println("CollectionGenerator - Inicio ("+host+", "+bd+")");
		
		ConexionMC conexion = new ConexionMC(host, bd, usuario, clave);
		
		Map<String, Integer> voc_global=conexion.cargarVocabularioGlobal();
		
		List<Integer> usuarios = null;
		int segundos = 0;
		
		//while...
		//consultar
		//esperar 
		//probar conexion
		
		while(true){
			
			//verificar stop
			if(conexion.stopCollectionGenerator()){
				conexion.close();
				System.out.println("Marca stop, saliendo");
				return;
			}
			
			System.out.println("-----");
			usuarios = conexion.getUsuariosGenerar(10);
			System.out.println("usuarios new: "+usuarios.size()+"");
			segundos = 60;
			System.out.println("Durmiendo "+segundos+" segundos");
			
			try{
				Thread.sleep(segundos * 1000);
			}
			catch(Exception e){
				e.printStackTrace();
			}
			
			//despues de dormir, la conexion puede haber sido cerrada
			while(! conexion.isValid()){
				segundos = 10;
				System.out.println("Conexion Invalida, cerrando y esperando "+segundos+" segundos");
				//cerrar conexion
				conexion.close();
				conexion = null;
				//esperar 10 segundos
				try{
					Thread.sleep(segundos * 1000);
				}
				catch(Exception e){
					e.printStackTrace();
				}
				//reiniciar conexion
				conexion = new ConexionMC(host, bd, usuario, clave);
			}
			
			
		}
		
		
		
		
		
	}
	
}



