package obs.text.indexing;

import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;

//Almacena y entrega listas
//No realiza rankings o busquedas
public class IndiceInvertidoMultiusuario{
	
//	private Map<String, ListaInvertida> mapa_terms;
//	private Map<Integer, IndiceInvertido> mapa_indices;
	private Map<Integer, Map<String, ListaInvertida>> mapa_indices;
	
	public IndiceInvertidoMultiusuario(){
		mapa_indices=new TreeMap<Integer, Map<String, ListaInvertida>>();
	}
	
//	public int size(){
//		return mapa_terms.size();
//	}
	
	public int numUsuarios(){
		return mapa_indices.size();
	}
	
	public Map<Integer, Map<String, ListaInvertida>> getMapa(){
		return mapa_indices;
	}
	
	public void guardar(String archivo){
		
		System.out.println("IndiceInvertidoMultiusuario.guardar - inicio (guardando en "+archivo+")");
		
		DataOutputStream salida=null;
		
		Iterator<Map.Entry<Integer, Map<String, ListaInvertida>>> it_mapa;
		Map.Entry<Integer, Map<String, ListaInvertida>> par_mapa;
		Map<String, ListaInvertida> mapa_terms;
		
		Iterator<Map.Entry<String, ListaInvertida>> it;
		Map.Entry<String, ListaInvertida> par;
		
		int user_id;
		String palabra;
		ListaInvertida lista;
		
		try{
			salida=new DataOutputStream(new FileOutputStream(archivo));
			
			//numero users
			salida.writeInt(mapa_indices.size());
			
			it_mapa=mapa_indices.entrySet().iterator();
			while(it_mapa.hasNext()){
				par_mapa=it_mapa.next();
				user_id=par_mapa.getKey();
				mapa_terms=par_mapa.getValue();
				
				//user id
				salida.writeInt(user_id);
			
				//numero terms
				salida.writeInt(mapa_terms.size());
				
				it=mapa_terms.entrySet().iterator();
				while(it.hasNext()){
					par=it.next();
					palabra=par.getKey();
					lista=par.getValue();
					//palabra
					salida.writeUTF(palabra);
					//lista
					lista.guardar(salida);
				}
				
			}
			salida.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.println("IndiceInvertidoMultiusuario.guardar - fin");
		
	}
	
	public void guardarTexto(String archivo){
		
		System.out.println("IndiceInvertidoMultiusuario.guardarTexto - inicio (guardando en "+archivo+")");
		
		PrintWriter salida=null;
		
		Iterator<Map.Entry<Integer, Map<String, ListaInvertida>>> it_mapa;
		Map.Entry<Integer, Map<String, ListaInvertida>> par_mapa;
		Map<String, ListaInvertida> mapa_terms;
		
		Iterator<Map.Entry<String, ListaInvertida>> it;
		Map.Entry<String, ListaInvertida> par;
		
		int user_id;
		String palabra;
		ListaInvertida lista;
		
		try{
//			salida=new PrintWriter(new FileOutputStream(archivo));
			salida=new PrintWriter(new OutputStreamWriter(new FileOutputStream(archivo), "UTF-8"));
			
			//numero users
			salida.println(mapa_indices.size());
			
			it_mapa=mapa_indices.entrySet().iterator();
			while(it_mapa.hasNext()){
				par_mapa=it_mapa.next();
				user_id=par_mapa.getKey();
				mapa_terms=par_mapa.getValue();
				
				//user id
				salida.println(user_id);
			
				//numero terms
				salida.println(mapa_terms.size());
				
				it=mapa_terms.entrySet().iterator();
				while(it.hasNext()){
					par=it.next();
					palabra=par.getKey();
					lista=par.getValue();
					//palabra
					salida.print(palabra+" ");
					//lista
					lista.guardarTexto(salida);
					salida.println();
				}
				
			}
			salida.close();
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.println("IndiceInvertidoMultiusuario.guardarTexto - fin");
		
	}
	
	public boolean cargar(String archivo){
		
		System.out.println("IndiceInvertidoMultiusuario.cargar - inicio (cargando desde "+archivo+")");
		
		mapa_indices.clear();
		
		Map<String, ListaInvertida> mapa_terms;
		
		DataInputStream entrada=null;
		
		int user_id;
		int n_users, n_terms;
		String palabra;
		ListaInvertida lista;
		
		try{
			entrada=new DataInputStream(new FileInputStream(archivo));
			
			//numero users
			n_users=entrada.readInt();
			
			for(int i=0; i<n_users; i++){
			
				//user id
				user_id=entrada.readInt();
				
				//numero terms
				n_terms=entrada.readInt();
				
				mapa_terms=new TreeMap<String, ListaInvertida>();
				
				for(int j=0; j<n_terms; j++){
					//palabra
					palabra=entrada.readUTF();
					//lista
					lista=new ListaInvertida();
					lista.cargar(entrada);
					mapa_terms.put(palabra, lista);
				}
				
				mapa_indices.put(user_id, mapa_terms);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
		System.out.println("IndiceInvertidoMultiusuario.cargar - fin");
		
		return true;
	}
	
	public void addLista(int user_id, String termino, ListaInvertida lista){
		if(mapa_indices.containsKey(user_id)){
			mapa_indices.get(user_id).put(termino, lista);
		}
		else{
			Map<String, ListaInvertida> mapa=new TreeMap<String, ListaInvertida>();
			mapa.put(termino, lista);
			mapa_indices.put(user_id, mapa);
		}
	}
	
	public ListaInvertida getLista(int user_id, String termino){
		if(mapa_indices.containsKey(user_id)){
			Map<String, ListaInvertida> mapa_terms=mapa_indices.get(user_id);
			if(mapa_terms.containsKey(termino)){
				return mapa_terms.get(termino);
			}
			else{
				return null;
			}
		}
		else{
			return null;
		}
	}
	
}



