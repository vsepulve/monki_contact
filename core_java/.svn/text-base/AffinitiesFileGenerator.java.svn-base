
import java.text.DecimalFormat;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
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
import java.io.EOFException;

import java.text.DecimalFormatSymbols;

import obs.affinities.Affinity;
import obs.comm.ConexionMC;

//import obs.spaces.data.DatoPerfilDoc;
//import obs.spaces.data.DatoVocabulario;
//import obs.spaces.distances.Distancia;
//import obs.spaces.distances.DistanciaCosVocabulario;

import java.util.Date;

public class AffinitiesFileGenerator{
	
	public static void main(String[]args){
		
		if(args.length!=2){
			System.out.println("");
			System.out.println("Modo de Uso");
			System.out.println("> java AffinitiesFileGenerator archivo_config archivo_salida");
			System.out.println("");
			return;
		}
		
		Date d_inicio, d_fin;
		
		d_inicio=new Date();
		
		String archivo_config=args[0];
		String archivo_salida=args[1];
		
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
			System.err.println("AffinitiesGenerator - Error al leer configuracion ("+archivo_config+")");
			e.printStackTrace();
			return;
		}
		
		System.out.println("AffinitiesGenerator - Inicio ("+host+", "+bd+")");
		
		ConexionMC conexion=new ConexionMC(host, bd, usuario, clave);
		
		List<Affinity> affinities=conexion.getAffinities();
		//Map<Integer, Map<String, Double>> 
		
		guardarGruposTerminos(affinities, archivo_salida);
		
		
	}

	private static void guardarGruposTerminos(List<Affinity> affinities, String archivo_salida){
		
		DecimalFormatSymbols simbolos=new DecimalFormatSymbols();
		simbolos.setDecimalSeparator('.');
		DecimalFormat formato=new DecimalFormat("##.##", simbolos);
		
		Iterator<Affinity> it_affinities;
		Affinity affinity;
		Iterator<Map.Entry<String, Double>> it_voc;
		Map.Entry<String, Double> par_voc;
		String term;
		double valor;
		
		PrintWriter salida=null;
		
		try{
			salida=new PrintWriter(new OutputStreamWriter(new FileOutputStream(archivo_salida), "UTF-8"));
			
			salida.println(affinities.size());
			
			it_affinities=affinities.iterator();
			while(it_affinities.hasNext()){
				affinity=it_affinities.next();
				salida.print(affinity.size()+" ");
				it_voc=affinity.iterator();
				while(it_voc.hasNext()){
					par_voc=it_voc.next();
					term=par_voc.getKey();
					valor=par_voc.getValue();
					salida.print(term+" "+formato.format(valor)+" ");
				}
				salida.println("");
			}
			
			salida.close();
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}

}

