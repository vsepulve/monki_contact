
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

public class AffinitiesGenerator{
	
    static String archivo_log="/root/logs/log_affinities.txt";
    //static String archivo_log="./log_affinities.txt";
	
	static private class ParTerminoValor implements Comparable{
		String term;
		double valor;
		ParTerminoValor(String _term, double _valor){
			term=_term;
			valor=_valor;
		}
		public String getTerm(){
			return term;
		}
		public double getValor(){
			return valor;
		}
		public int compareTo(Object o){
			if(! (o instanceof ParTerminoValor) ){
				System.err.println("ParTerminoValor.compareTo - Objeto de tipo incorrecto");
				return 0;
			}
//			return valor.compareTo(((ParTerminoValor)o).getValor());
			if(valor > ((ParTerminoValor)o).getValor()){
				return -1;
			}
			else if(valor < ((ParTerminoValor)o).getValor()){
				return 1;
			}
			else{
				return 0;
			}
		}
	}
	
	private ConexionMC conexion;
	private Set<String> set_stopwords;
	
	public static void main(String[]args){
		
		if(args.length!=4){
			System.out.println("");
			System.out.println("Modo de Uso");
			System.out.println("> java AffinitiesGenerator archivo_config archivo_stopwords radio archivo_salida");
			System.out.println("");
			return;
		}
		
		Date d_inicio, d_fin;
		
		d_inicio=new Date();
		
		String archivo_config=args[0];
		String archivo_stopwords=args[1];
		double radio=new Double(args[2]);
		String archivo_salida=args[3];
		
		int min_numero_personas=5;
		double min_valor_terminos=2.0;
		int max_n_terms=10;
		
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
			System.err.println("AffinitiesGenerator - Error al leer configuracion ("+archivo_config+")");
			e.printStackTrace();
			return;
		}
		System.out.println("Stopwords: "+set_stopwords.size()+"");
		
		AffinitiesGenerator generator=new AffinitiesGenerator(conexion, set_stopwords);
		
		//tomar datos globales (que se reusaran)
		
		//iterar
		
		//verificar marca de terminacion
		
		//tomar personas a analizar
		//aqui pueden usarse varias logicas.
		//Por ahora, sera por usuario
		//(todas las personas que el usuario conozca, no solo su vista sino todos los datos)
		
		//generar para esas personas
		
		List<Integer> lista_usuarios;
		Iterator<Integer> it_usuarios;
		//lectura inicial de usuarios
		//lista_usuarios=conexion.getUsuarios();
		lista_usuarios=new LinkedList<Integer>();
		lista_usuarios.add(32);
		if(lista_usuarios.size()==0){
			System.out.println("Sin usuarios, saliendo");
			return;
		}
		it_usuarios=lista_usuarios.iterator();
		
		int user_id=0;
		List<DatoVocabulario> personas=null;
		while(true){
			
			if(conexion.stopAffinitiesGenerator()){
				System.out.println("Marca stop, saliendo");
				return;
			}
			
			if(! it_usuarios.hasNext()){
				//recargar lista usuarios
				lista_usuarios=conexion.getUsuarios();
				if(lista_usuarios.size()==0){
					System.out.println("Sin usuarios, saliendo");
					return;
				}
				it_usuarios=lista_usuarios.iterator();
			}
			user_id=it_usuarios.next();
			
			System.out.println("Procesando usuario "+user_id+"");
			
			personas=generator.cargarPersonasUsuario(conexion, set_stopwords, user_id);
			generator.generateAffinities(personas, radio, archivo_salida, min_numero_personas, min_valor_terminos, max_n_terms);
			
			//para la prueba, solo lo corro una vez
			break;
		}
		
	}
	
	public AffinitiesGenerator(ConexionMC _conexion, Set<String> _set_stopwords){
		conexion=_conexion;
		set_stopwords=_set_stopwords;
	}
	
	private void generateAffinities(List<DatoVocabulario> personas, double radio, String archivo_salida, int min_numero_personas, double min_valor_terminos, int max_n_terms){
		
		System.out.println("generateAffinities - inicio ("+personas.size()+" personas)");
		
		Date d_inicio, d_fin;
		
		Iterator<DatoVocabulario> it_personas;
		DatoVocabulario voc_persona;
		
		Distancia dist=new DistanciaCosVocabulario();
		
		int n_personas=personas.size();
		DatoVocabulario[] arr_personas=new DatoVocabulario[n_personas];
		int pos=0;
		it_personas=personas.iterator();
		while(it_personas.hasNext()){
			arr_personas[pos++]=it_personas.next();
		}
		
		System.out.println("Procesando...");
		d_inicio=new Date();
		
		double d;
		
		Map<Integer, List<DatoVocabulario>> mapa_clusters=new TreeMap<Integer, List<DatoVocabulario>>();
		Iterator<Map.Entry<Integer, List<DatoVocabulario>>> it_clusters;
		Map.Entry<Integer, List<DatoVocabulario>> par_clusters;
		List<DatoVocabulario> cluster;
		Iterator<DatoVocabulario> it_cluster;
		
		DatoVocabulario voc_i=null;
		DatoVocabulario voc_j=null;
		
		int total_agrupados=0;
		
		for(int i=0; i<n_personas; i++){
			voc_i=arr_personas[i];
			
			for(int j=i+1; j<n_personas; j++){
				voc_j=arr_personas[j];
				
				d=dist.d(voc_i, voc_j);
				//System.out.println("d: "+d+"");
				
				if(d<=radio){
					//por ahora la direccion es i -> j
					//en la version final deberia ser j (candidato) -> i
					
					if(mapa_clusters.containsKey(voc_j.getId())){
						(mapa_clusters.get(voc_j.getId())).add(voc_i);
						total_agrupados++;
					}
					else{
						cluster=new LinkedList<DatoVocabulario>();
						cluster.add(voc_i);
						cluster.add(voc_j);
						total_agrupados+=2;
						mapa_clusters.put(voc_j.getId(), cluster);
					}
				}//if... d<radio
				
			}
		}
		
		Iterator<Map.Entry<String, Double>> it_voc;
		Map.Entry<String, Double> par_voc;
		String term;
		double valor;
		
		d_fin=new Date();
		System.out.println("Procesamiento terminado en "+(long)(d_fin.getTime()-d_inicio.getTime())/1000+" s");
		
		System.out.println("Total de clusters: "+mapa_clusters.size()+" ("+((double)total_agrupados)/mapa_clusters.size()+" promedio)");
		
//		List<Map<String, Double>> grupos_terminos=new LinkedList<Map<String, Double>>();
//		Iterator<Map<String, Double>> it_grupos;
		
		Map<String, Double> grupo_terminos;
		
		List<Affinity> affinities=new LinkedList<Affinity>();
		Iterator<Affinity> it_aff;
		Affinity affinity, new_affinity;
		
		//Iterator<Map.Entry<Integer, List<DatoVocabulario>>> it_clusters;
		it_clusters=mapa_clusters.entrySet().iterator();
		while(it_clusters.hasNext()){
			par_clusters=it_clusters.next();
			cluster=par_clusters.getValue();
			
			//criterio minimo personas por afinidad
			if(cluster.size()>=min_numero_personas){
			
				System.out.println("-----");
				//System.out.println("Cluster "+par_clusters.getKey()+" de "+cluster.size()+" personas");
				System.out.println("("+cluster.size()+" personas)");
				
				Map<String, Double> voc_comun=new TreeMap<String, Double>();
				
				it_cluster=cluster.iterator();
				while(it_cluster.hasNext()){
					voc_persona=it_cluster.next();
					it_voc=voc_persona.getMapa().entrySet().iterator();
					while(it_voc.hasNext()){
						par_voc=it_voc.next();
						term=par_voc.getKey();
						valor=par_voc.getValue();
						if(voc_comun.containsKey(term)){
							voc_comun.put(term, valor+voc_comun.get(term));
						}
						else{
							voc_comun.put(term, valor);
						}
					}
				}
				
				SortedSet<ParTerminoValor> set_voc=new TreeSet<ParTerminoValor>();
				Iterator<ParTerminoValor> it_set;
				ParTerminoValor par;
				
				it_voc=voc_comun.entrySet().iterator();
				while(it_voc.hasNext()){
					par_voc=it_voc.next();
					set_voc.add(new ParTerminoValor(par_voc.getKey(), par_voc.getValue()));
				}
				
				grupo_terminos=new TreeMap<String, Double>();
				
				int contador=0;
				it_set=set_voc.iterator();
				while(it_set.hasNext()){
					par=it_set.next();
					term=par.getTerm();
					valor=par.getValor()/cluster.size();
					//Criterio de seleccion de terminos
					if(valor>=min_valor_terminos){
						System.out.println(" - "+term+"\t"+valor+"");
						grupo_terminos.put(term, valor);
					}
					if(contador++==max_n_terms){
						break;
					}
				}
				
				if(grupo_terminos.size()>0){
				
					new_affinity=new Affinity(grupo_terminos);
					//verificar si el grupo debe ser unido a uno ya existente
				
					boolean descartar=false;
				
					it_aff=affinities.iterator();
					while(it_aff.hasNext()){
						affinity=it_aff.next();
						if(affinity.similar(new_affinity)){
							descartar=true;
							break;
						}
					}
				
					if(!descartar){
						//grupos_terminos.add(grupo_terminos);
						affinities.add(new_affinity);
						
						//log
		                try{
							PrintWriter salida=new PrintWriter(new FileOutputStream(archivo_log, true));
							salida.println("["+new_affinity.getVocabulary()+"]");
							
							it_cluster=cluster.iterator();
							while(it_cluster.hasNext()){
								voc_persona=it_cluster.next();
								salida.println("("+voc_persona.getId()+"): "+voc_persona+"");
							}
							salida.println("-----");
							
							salida.close();
		                }
		                catch(Exception e){
		                	e.printStackTrace();
		                }
						
						
					}
				}
				set_voc.clear();
				voc_comun.clear();
				
			}//if... numero minimo de personas
		}//while... cada cluster (affinity)
		
		guardarGruposTerminos(affinities, archivo_salida);
		
		conexion.guardarAffinities(affinities);
		
	}
	
	private boolean gruposEquivalentes(Map<String, Double> g1, Map<String, Double> g2, Distancia distancia){
		
		if(g1.size() != g2.size()){
			return false;
		}
		
		Map.Entry<String, Double> par;
		Iterator<Map.Entry<String, Double>> it;
		
		it=g1.entrySet().iterator();
		while(it.hasNext()){
			par=it.next();
			if(! g2.containsKey(par.getKey())){
				return false;
			}
		}
		
		if(distancia.d(new DatoVocabulario(g1), new DatoVocabulario(g2)) > 0.1){
			return false;
		}
		
		return true;
		
	}
	
	private void guardarGruposTerminos(List<Affinity> affinities, String archivo_salida){
		
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
	
	private List<DatoVocabulario> cargarPersonasUsuario(ConexionMC conexion, Set<String> stopwords, int user_id){
		List<DatoVocabulario> personas=new LinkedList<DatoVocabulario>();
		DatoVocabulario voc_persona;
		
		Map<Integer, Map<String, Map<String, Double>>> vocs=null;
		Map<String, Double> vocabulario=null;
		
		Map<Integer, List<Integer>> grupos=conexion.getGruposUsuario(user_id);
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
	
//	private static List<DatoVocabulario> cargarPersonas(ConexionMC conexion, Set<String> stopwords){
//		List<DatoVocabulario> personas=new LinkedList<DatoVocabulario>();
//		DatoVocabulario voc_persona;
//		
//		Map<Integer, Map<String, Map<String, Double>>> vocs=null;
//		Map<String, Double> vocabulario=null;
//		
//		Map<Integer, List<Integer>> grupos=conexion.getGruposProcessing();
//		Iterator<Map.Entry<Integer, List<Integer>>> it_grupos;
//		Map.Entry<Integer, List<Integer>> par_grupos;
//		
//		int group_id;
//		
//		it_grupos=grupos.entrySet().iterator();
//		while(it_grupos.hasNext()){
//			par_grupos=it_grupos.next();
//			group_id=par_grupos.getKey();
//			
//			vocs=conexion.getVocabulariosGrupo(group_id);
//			vocabulario=combinarVocabularios(vocs, stopwords);
//			voc_persona=new DatoVocabulario(vocabulario);
//			voc_persona.setId(group_id);
//			personas.add(voc_persona);
//			
//		}
//		
//		return personas;
//	}
	
	public Map<String, Double> combinarVocabularios(Map<Integer, Map<String, Map<String, Double>>> vocs, Set<String> stopwords){
		
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










