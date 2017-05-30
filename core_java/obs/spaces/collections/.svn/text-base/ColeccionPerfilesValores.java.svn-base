package obs.spaces.collections;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;

import java.io.File;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.BufferedOutputStream;

import java.io.FileNotFoundException;

/*
ESTA CLASE ESTA DESACTUALIZADA Y NO ESTA EN USO DE MOMENTO
LA CONSERVO EN EL SVN POR EL MOMENTO
PERO ES NECESARIO REVISAR SI PUEDE TENER ALGUNA UTILIDAD
*/

//genera la coleccion de perfiles con valores basados en frecuencia.
//carga una ColeccionPerfiles de disco para iniciar el proceso.
public class ColeccionPerfilesValores{
	
	//coleccion con id, perfil convertido en mapa de terminos
//	Map<Integer, Map<String, Double>> coleccion;
	
	//coleccion con id_user -> mapa (id_doc, perfil) convertido en mapa de terminos
//	Map<Integer, Map<String, Double>> coleccion;
	Map<Integer, Map<Integer, Map<String, Double>>> coleccion;
	
	public ColeccionPerfilesValores(){
//		coleccion=new TreeMap<Integer, Map<String, Double>>();
		coleccion=new TreeMap<Integer, Map<Integer, Map<String, Double>>>();
		
	}
	
	public Map<Integer, Map<Integer, Map<String, Double>>> getMapa(){
		return coleccion;
	}
	
	public Map<Integer, Map<String, Double>> getMapa(int user_id){
		if(coleccion.containsKey(user_id)){
			return coleccion.get(user_id);
		}
		else{
			return null;
		}
	}
	
	public void addPerfilVoc(int user_id, int doc_id, Map<String, Double> voc_perfil){
//		coleccion.put(doc_id, voc_perfil);
		if(coleccion.containsKey(user_id)){
			coleccion.get(user_id).put(doc_id, voc_perfil);
		}
		else{
			Map<Integer, Map<String, Double>> coleccion_user=new TreeMap<Integer, Map<String, Double>>();
			coleccion_user.put(doc_id, voc_perfil);
			coleccion.put(user_id, coleccion_user);
		}
	}
	
	public Map<String, Double> getPerfilVoc(int user_id, int doc_id){
		if(coleccion.containsKey(user_id)){
			Map<Integer, Map<String, Double>> coleccion_user=coleccion.get(user_id);
			if(coleccion_user.containsKey(doc_id)){
				return coleccion_user.get(doc_id);
			}
			else{
				return null;
			}
		}
		else{
			return null;
		}
	}
	
	public void guardar(String archivo){
		
		System.out.println("ColeccionPerfilesValores.guardar - inicio (guardando en "+archivo+")");
		
		DataOutputStream salida=null;
		
		Iterator<Map.Entry<Integer, Map<Integer, Map<String, Double>>>> it_coleccion;
		Map.Entry<Integer, Map<Integer, Map<String, Double>>> par_coleccion;
		
		Map<Integer, Map<String, Double>> coleccion_user;
		
		Iterator<Map.Entry<Integer, Map<String, Double>>> it_user;
		Map.Entry<Integer, Map<String, Double>> par_user;
		
		int user_id, doc_id;
		Map<String, Double> voc_local;
		Iterator<Map.Entry<String, Double>> it_voc;
		Map.Entry<String, Double> par_voc;
		
		try{
			//salida=new DataOutputStream(new FileOutputStream(archivo));
			salida=new DataOutputStream(new BufferedOutputStream(new FileOutputStream(archivo)));
			
			//numero users
			salida.writeInt(coleccion.size());
			it_coleccion=coleccion.entrySet().iterator();
			while(it_coleccion.hasNext()){
				par_coleccion=it_coleccion.next();
				user_id=par_coleccion.getKey();
				coleccion_user=par_coleccion.getValue();
				
				//user id
				salida.writeInt(user_id);
				
				//numero docs
				salida.writeInt(coleccion_user.size());
				it_user=coleccion_user.entrySet().iterator();
				
				while(it_user.hasNext()){
					par_user=it_user.next();
					doc_id=par_user.getKey();
					voc_local=par_user.getValue();
					
					//doc_id
					salida.writeInt(doc_id);
					//n_terms
					salida.writeInt(voc_local.size());
					//vocabulario
					it_voc=voc_local.entrySet().iterator();
					while(it_voc.hasNext()){
						par_voc=it_voc.next();
						salida.writeUTF(par_voc.getKey());
						salida.writeDouble(par_voc.getValue());
					}
					
				}
				
			}
			salida.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.println("ColeccionPerfilesValores.guardar - fin");
		
	}
	
	public void cargar(String archivo){
		
		System.out.println("ColeccionPerfilesValores.cargar - inicio (cargando desde "+archivo+")");
		
		coleccion.clear();
		
		DataInputStream entrada=null;
		
		int n_users, n_docs, n_terms, user_id, doc_id;
		String palabra;
		Double valor;
		Map<String, Double> voc_local;
		
		Map<Integer, Map<String, Double>> coleccion_user;
		
		try{
			entrada=new DataInputStream(new FileInputStream(archivo));
			
			//numero users
			n_users=entrada.readInt();
			
			for(int i=0; i<n_users; i++){
				//user_id
				user_id=entrada.readInt();
				
				//numero docs
				n_docs=entrada.readInt();
				
				coleccion_user=new TreeMap<Integer, Map<String, Double>>();
				
				for(int j=0; j<n_docs; j++){
					//doc_id
					doc_id=entrada.readInt();
					//n_terms
					n_terms=entrada.readInt();
					//vocabulario
					voc_local=new TreeMap<String, Double>();
					for(int k=0; k<n_terms; k++){
						palabra=entrada.readUTF();
						valor=entrada.readDouble();
						voc_local.put(palabra, valor);
					}
					coleccion_user.put(doc_id, voc_local);
				}
				
				coleccion.put(user_id, coleccion_user);
			}
			entrada.close();
		}
		catch(FileNotFoundException e){
			System.out.println("ColeccionPerfilesValores.cargar - Archivo no encontrado, no se cargaran datos");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.println("ColeccionPerfilesValores.cargar - fin");
		
	}
	
}



