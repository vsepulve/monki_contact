
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

//import obs.spaces.data.DatoPerfilDoc;
import obs.spaces.data.DatoVocabulario;
import obs.spaces.distances.Distancia;
import obs.spaces.distances.DistanciaCosVocabulario;

import obs.affinities.Affinity;
import obs.comm.ConexionMC;

import java.util.Date;

public class AffinitiesClassificator{
	
	static private class ParVocSimilitud implements Comparable{
		DatoVocabulario voc;
		double sim;
		ParVocSimilitud(DatoVocabulario _voc, double _sim){
			voc=_voc;
			sim=_sim;
		}
		public DatoVocabulario getVoc(){
			return voc;
		}
		public double getSimilitud(){
			return sim;
		}
		public int compareTo(Object o){
			if(! (o instanceof ParVocSimilitud) ){
				System.err.println("ParVocSimilitud.compareTo - Objeto de tipo incorrecto");
				return 0;
			}
			if(sim < ((ParVocSimilitud)o).getSimilitud()){
				return 1;
			}
			else if(sim > ((ParVocSimilitud)o).getSimilitud()){
				return -1;
			}
			else{
				//para evitar eliminacion por igualdad, debe usarse un criterio completo
				//aunque esta parte no tiene semantica
				if(voc.getId() < ((ParVocSimilitud)o).getVoc().getId()){
					return 1;
				}
				if(voc.getId() > ((ParVocSimilitud)o).getVoc().getId()){
					return -1;
				}
				else{
					return 0;
				}
			}
		}
	}
	
	static private class ParVocDistancia implements Comparable{
		DatoVocabulario voc;
		double dist;
		ParVocDistancia(DatoVocabulario _voc, double _dist){
			voc=_voc;
			dist=_dist;
		}
		public DatoVocabulario getVoc(){
			return voc;
		}
		public double getDistancia(){
			return dist;
		}
		public int compareTo(Object o){
			if(! (o instanceof ParVocDistancia) ){
				System.err.println("ParVocDistancia.compareTo - Objeto de tipo incorrecto");
				return 0;
			}
			if(dist > ((ParVocDistancia)o).getDistancia()){
				return 1;
			}
			else if(dist < ((ParVocDistancia)o).getDistancia()){
				return -1;
			}
			else{
				//para evitar eliminacion por igualdad, debe usarse un criterio completo
				//aunque esta parte no tiene semantica
				if(voc.getId() > ((ParVocDistancia)o).getVoc().getId()){
					return 1;
				}
				if(voc.getId() < ((ParVocDistancia)o).getVoc().getId()){
					return -1;
				}
				else{
					return 0;
				}
			}
		}
	}
	
	public static void main(String[]args){
		
		if(args.length!=4){
			System.out.println("");
			System.out.println("Modo de Uso");
			System.out.println("> java AffinitiesClassificator archivo_config archivo_stopwords radio archivo_salida");
			System.out.println("");
			return;
		}
		
		Date d_inicio, d_fin;
		
		d_inicio=new Date();
		
		String archivo_config=args[0];
		String archivo_stopwords=args[1];
		double radio=new Double(args[2]);
		String archivo_salida=args[3];
		
		boolean modo_dist=true;
		int max_affinities=30;
		
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
			System.err.println("AffinitiesClassificator - Error al leer configuracion ("+archivo_config+")");
			e.printStackTrace();
			return;
		}
		
		System.out.println("AffinitiesClassificator - Inicio ("+host+", "+bd+")");
		
		ConexionMC conexion=new ConexionMC(host, bd, usuario, clave);
		
		Set<String> set_stopwords=new TreeSet<String>();
		String palabra;
		try{
			lector=new BufferedReader(new FileReader(archivo_stopwords));
			while((palabra=lector.readLine()) != null){
				set_stopwords.add(palabra);
			}
			
		}
		catch(EOFException e){
			System.out.println("Archivo de stopwords terminado");
		}
		catch(Exception e){
			System.err.println("AffinitiesClassificator - Error al leer configuracion ("+archivo_config+")");
			e.printStackTrace();
			return;
		}
		System.out.println("Stopwords: "+set_stopwords.size()+"");
		
		List<DatoVocabulario> personas=cargarPersonas(conexion, set_stopwords);
		Iterator<DatoVocabulario> it_personas;
		DatoVocabulario voc_persona;
		
		List<DatoVocabulario> affinities=cargarAffinities(conexion, set_stopwords);
		Iterator<DatoVocabulario> it_aff;
		DatoVocabulario voc_aff;
		
		Distancia dist=new DistanciaCosVocabulario();
		double d;
		
		Set<ParVocDistancia> ranking_d=null;
		Iterator<ParVocDistancia> it_ranking_d;
		ParVocDistancia par_ranking_d;
		
		Set<ParVocSimilitud> ranking_s=null;
		Iterator<ParVocSimilitud> it_ranking_s;
		ParVocSimilitud par_ranking_s;
		
		it_personas=personas.iterator();
		while(it_personas.hasNext()){
			voc_persona=it_personas.next();
			System.out.println("-----");
			System.out.println("Persona "+voc_persona.getId()+" ["+voc_persona+"]");
			
			if(modo_dist){
				ranking_d=new TreeSet<ParVocDistancia>();
			}
			else{
				ranking_s=new TreeSet<ParVocSimilitud>();
			}
			
			it_aff=affinities.iterator();
			while(it_aff.hasNext()){
				voc_aff=it_aff.next();
				if(modo_dist){
					d=dist.d(voc_persona, voc_aff);
					ranking_d.add(new ParVocDistancia(voc_aff, d));
				}
				else{
					d=similitud(voc_persona, voc_aff);
					ranking_s.add(new ParVocSimilitud(voc_aff, d));
				}
			}//while... cada affinity
			
			int contador=0;
			
			if(modo_dist){
				it_ranking_d=ranking_d.iterator();
				while(it_ranking_d.hasNext()){
					par_ranking_d=it_ranking_d.next();
					d=par_ranking_d.getDistancia();
					if(d==1.0 || contador++>=max_affinities){
						break;
					}
					voc_aff=par_ranking_d.getVoc();
					System.out.println("Aff "+voc_aff.getId()+" ("+d+"): "+voc_aff+"");
					conexion.guardarAffinityGroup(voc_aff.getId(), voc_persona.getId(), d);
				}
				
				ranking_d.clear();
				
			}
			else{
				it_ranking_s=ranking_s.iterator();
				while(it_ranking_s.hasNext()){
					par_ranking_s=it_ranking_s.next();
					d=par_ranking_s.getSimilitud();
					if(d==0.0 || contador++>=max_affinities){
						break;
					}
					voc_aff=par_ranking_s.getVoc();
					System.out.println("Aff "+voc_aff.getId()+" ("+d+"): "+voc_aff+"");
					//conexion.guardarAffinityGroup(voc_aff.getId(), voc_persona.getId(), d);
				}
				
				ranking_s.clear();
			}
			
		}//while... cada persona
		
	}
	
	private static double similitud(DatoVocabulario v1, DatoVocabulario v2){
		double d=0.0;
		
		Map<String, Double> m1=v1.getMapa();
		Map<String, Double> m2=v2.getMapa();
		Iterator<Map.Entry<String, Double>> it;
		Map.Entry<String, Double> par;
		String term;
		double valor;
		
		it=m1.entrySet().iterator();
		while(it.hasNext()){
			par=it.next();
			term=par.getKey();
			valor=par.getValue();
			if(m2.containsKey(term)){
				d+=(valor*m2.get(term));
			}
		}
		
		return d;
	}
			
	private static List<DatoVocabulario> cargarAffinities(ConexionMC conexion, Set<String> stopwords){
		List<DatoVocabulario> vocs=new LinkedList<DatoVocabulario>();
		DatoVocabulario voc_aff;
		
		//List<DatoPerfilDoc> getPerfilesGrupo(int group_id)
		//Map<Integer, List<Integer>> getGruposProcessing()
		//Map<Integer, Map<String, Map<String, Double>>> getVocabulariosGrupo(int group_id)
		
		List<Affinity> affinities=conexion.getAffinities();
		Iterator<Affinity> it_aff;
		Affinity aff;
		int affinity_id;
		
		it_aff=affinities.iterator();
		while(it_aff.hasNext()){
			aff=it_aff.next();
			vocs.add(aff.getVocabulary());
		}
		
		return vocs;
	}
	
	private static List<DatoVocabulario> cargarPersonas(ConexionMC conexion, Set<String> stopwords){
		List<DatoVocabulario> personas=new LinkedList<DatoVocabulario>();
		DatoVocabulario voc_persona;
		
		//List<DatoPerfilDoc> getPerfilesGrupo(int group_id)
		//Map<Integer, List<Integer>> getGruposProcessing()
		//Map<Integer, Map<String, Map<String, Double>>> getVocabulariosGrupo(int group_id)
		
		Map<Integer, Map<String, Map<String, Double>>> vocs=null;
		Map<String, Double> vocabulario=null;
		
		Map<Integer, List<Integer>> grupos=conexion.getGruposProcessing();
		Iterator<Map.Entry<Integer, List<Integer>>> it_grupos;
		Map.Entry<Integer, List<Integer>> par_grupos;
		
		int group_id;
		
		it_grupos=grupos.entrySet().iterator();
		while(it_grupos.hasNext()){
			par_grupos=it_grupos.next();
			group_id=par_grupos.getKey();
			
			vocs=conexion.getVocabulariosGrupo(group_id);
			vocabulario=combinarVocabularios(vocs, stopwords);
			voc_persona=new DatoVocabulario(vocabulario);
			voc_persona.setId(group_id);
			personas.add(voc_persona);
			
		}
		
		return personas;
	}
	
	public static Map<String, Double> combinarVocabularios(Map<Integer, Map<String, Map<String, Double>>> vocs, Set<String> stopwords){
		
		Map<String, Integer> frecuencias=new TreeMap<String, Integer>();
		Map<String, Double> vocabulario=new TreeMap<String, Double>();
		
		Iterator<Map.Entry<Integer, Map<String, Map<String, Double>>>> it_vocs;
		Map.Entry<Integer, Map<String, Map<String, Double>>> par_vocs;
		int contact_id;
		Map<String, Map<String, Double>> mapa_atributos;
		Iterator<Map.Entry<String, Map<String, Double>>> it_atributos;
		Map.Entry<String, Map<String, Double>> par_atributos;
		String atributo;
		Map<String, Double> voc;
		Iterator<Map.Entry<String, Double>> it_voc;
		Map.Entry<String, Double> par_voc;
		String term;
		double valor;
		
		it_vocs=vocs.entrySet().iterator();
		while(it_vocs.hasNext()){
			par_vocs=it_vocs.next();
			contact_id=par_vocs.getKey();
			mapa_atributos=par_vocs.getValue();
			it_atributos=mapa_atributos.entrySet().iterator();
			while(it_atributos.hasNext()){
				par_atributos=it_atributos.next();
				atributo=par_atributos.getKey();
				voc=par_atributos.getValue();
				it_voc=voc.entrySet().iterator();
				while(it_voc.hasNext()){
					par_voc=it_voc.next();
					term=par_voc.getKey();
					if(! stopwords.contains(term)){
						valor=par_voc.getValue();
						if(frecuencias.containsKey(term)){
							frecuencias.put(term, frecuencias.get(term)+1);
						}
						else{
							frecuencias.put(term, 1);
						}
						if(vocabulario.containsKey(term)){
							vocabulario.put(term, vocabulario.get(term)+valor);
						}
						else{
							vocabulario.put(term, valor);
						}
					}//if... no stopword
				}//while... cada termino
			}///while... cada atributo
		}//while... cada contacto
		
		Iterator<Map.Entry<String, Integer>> it_frec;
		Map.Entry<String, Integer> par_frec;
		int frec=0;
		
		it_frec=frecuencias.entrySet().iterator();
		while(it_frec.hasNext()){
			par_frec=it_frec.next();
			term=par_frec.getKey();
			frec=par_frec.getValue();
			vocabulario.put(term, (vocabulario.get(term))/frec);
		}
		
		return vocabulario;
		
	}
}










