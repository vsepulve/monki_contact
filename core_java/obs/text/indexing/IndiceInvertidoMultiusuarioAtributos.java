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
public class IndiceInvertidoMultiusuarioAtributos{
	
//	private Map<String, ListaInvertida> mapa_terms;
//	private Map<Integer, IndiceInvertido> mapa_indices;
	// mapa de ( user_id -> mapa de ( atributo -> mapa de ( termino -> lista_invertida ) ) )
	private Map<Integer, Map<String, Map<String, ListaInvertida>>> mapa_indices;
	
	public IndiceInvertidoMultiusuarioAtributos(){
		mapa_indices=new TreeMap<Integer, Map<String, Map<String, ListaInvertida>>>();
	}
	
//	public int size(){
//		return mapa_terms.size();
//	}
	
	public int numUsuarios(){
		return mapa_indices.size();
	}
	
	public Map<Integer, Map<String, Map<String, ListaInvertida>>> getMapa(){
		return mapa_indices;
	}
	
	//metodo no adaptado para atributos
//	public void guardar(String archivo){
//		
//		System.out.println("IndiceInvertidoMultiusuarioAtributos.guardar - inicio (guardando en "+archivo+")");
//		
//		DataOutputStream salida=null;
//		
//		Iterator<Map.Entry<Integer, Map<String, ListaInvertida>>> it_mapa;
//		Map.Entry<Integer, Map<String, ListaInvertida>> par_mapa;
//		Map<String, ListaInvertida> mapa_terms;
//		
//		Iterator<Map.Entry<String, ListaInvertida>> it;
//		Map.Entry<String, ListaInvertida> par;
//		
//		int user_id;
//		String palabra;
//		ListaInvertida lista;
//		
//		try{
//			salida=new DataOutputStream(new FileOutputStream(archivo));
//			
//			//numero users
//			salida.writeInt(mapa_indices.size());
//			
//			it_mapa=mapa_indices.entrySet().iterator();
//			while(it_mapa.hasNext()){
//				par_mapa=it_mapa.next();
//				user_id=par_mapa.getKey();
//				mapa_terms=par_mapa.getValue();
//				
//				//user id
//				salida.writeInt(user_id);
//				
//				//numero terms
//				salida.writeInt(mapa_terms.size());
//				
//				it=mapa_terms.entrySet().iterator();
//				while(it.hasNext()){
//					par=it.next();
//					palabra=par.getKey();
//					lista=par.getValue();
//					//palabra
//					salida.writeUTF(palabra);
//					//lista
//					lista.guardar(salida);
//				}
//				
//			}
//			salida.close();
//		}
//		catch(Exception e){
//			e.printStackTrace();
//		}
//		
//		System.out.println("IndiceInvertidoMultiusuarioAtributos.guardar - fin");
//		
//	}
	
	public void guardarBinario(String archivo, int user_id){
		
		System.out.println("IndiceInvertidoMultiusuarioAtributos.guardarBinario - inicio (guardando en "+archivo+")");
		
		DataOutputStream salida = null;
		
		Iterator<Map.Entry<Integer, Map<String, Map<String, ListaInvertida>>>> it_mapa;
		Map.Entry<Integer,Map<String, Map<String, ListaInvertida>>> par_mapa;
		Map<String, Map<String, ListaInvertida>> mapa_atributos;
		Iterator<Map.Entry<String, Map<String, ListaInvertida>>> it_atributos;
		Map.Entry<String, Map<String, ListaInvertida>> par_atributos;
		
//		Iterator<Map.Entry<Integer, Map<String, ListaInvertida>>> it_mapa;
//		Map.Entry<Integer, Map<String, ListaInvertida>> par_mapa;

		Map<String, ListaInvertida> mapa_terms;
		Iterator<Map.Entry<String, ListaInvertida>> it;
		Map.Entry<String, ListaInvertida> par;
		
//		int user_id;
		String atributo;
		String palabra;
		ListaInvertida lista;
		
		try{

//			salida = new PrintWriter(new OutputStreamWriter(new FileOutputStream(archivo), "UTF-8"));
			salida = new DataOutputStream(new FileOutputStream(archivo));
			
			
			if(!mapa_indices.containsKey(user_id)){
				//numero users
//				salida.println("0");
				salida.writeInt(0);
				
			}
			else{
				//numero users
//				salida.println("1");
				salida.writeInt(1);
				
				mapa_atributos = mapa_indices.get(user_id);
				
				//user id
//				salida.println(user_id);
				salida.writeInt(user_id);
				
				//atributos...
				
				//numero de atributos
				
//				salida.println(mapa_atributos.size());
				salida.writeInt(mapa_atributos.size());
				
				it_atributos = mapa_atributos.entrySet().iterator();
				while(it_atributos.hasNext()){
					par_atributos = it_atributos.next();
					atributo = par_atributos.getKey();
					mapa_terms = par_atributos.getValue();
					
					//atributo
//					salida.println(atributo);
					salida.writeUTF(atributo);
					
					//numero terms
//					salida.println(mapa_terms.size());
					salida.writeInt(mapa_terms.size());
					
					it = mapa_terms.entrySet().iterator();
					while(it.hasNext()){
						par = it.next();
						palabra = par.getKey();
						lista = par.getValue();
						//palabra
//						salida.print(palabra+" ");
						salida.writeUTF(palabra);
						//lista
//						lista.guardarTexto(salida);
						lista.guardar(salida);
//						salida.println();
					}
					
				}//while... cada atributo
				
			}

			salida.close();
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.println("IndiceInvertidoMultiusuarioAtributos.guardarBinario - fin");
		
	}
	
	public void guardarTexto(String archivo, int user_id){
		
		System.out.println("IndiceInvertidoMultiusuarioAtributos.guardarTexto - inicio (guardando en "+archivo+")");
		
		PrintWriter salida = null;
		
		Iterator<Map.Entry<Integer, Map<String, Map<String, ListaInvertida>>>> it_mapa;
		Map.Entry<Integer,Map<String, Map<String, ListaInvertida>>> par_mapa;
		Map<String, Map<String, ListaInvertida>> mapa_atributos;
		Iterator<Map.Entry<String, Map<String, ListaInvertida>>> it_atributos;
		Map.Entry<String, Map<String, ListaInvertida>> par_atributos;
		
//		Iterator<Map.Entry<Integer, Map<String, ListaInvertida>>> it_mapa;
//		Map.Entry<Integer, Map<String, ListaInvertida>> par_mapa;
		
		Map<String, ListaInvertida> mapa_terms;
		Iterator<Map.Entry<String, ListaInvertida>> it;
		Map.Entry<String, ListaInvertida> par;
		
//		int user_id;
		String atributo;
		String palabra;
		ListaInvertida lista;
		
		try{
//			salida=new PrintWriter(new FileOutputStream(archivo));
			salida = new PrintWriter(new OutputStreamWriter(new FileOutputStream(archivo), "UTF-8"));
			
			
			if(!mapa_indices.containsKey(user_id)){
				//numero users
				salida.println("0");
			
			}
			else{
				//numero users
				salida.println("1");
				
				mapa_atributos = mapa_indices.get(user_id);
				
				//user id
				salida.println(user_id);
				
				//atributos...
				
				//numero de atributos
				
				salida.println(mapa_atributos.size());
				
				it_atributos = mapa_atributos.entrySet().iterator();
				while(it_atributos.hasNext()){
					par_atributos = it_atributos.next();
					atributo = par_atributos.getKey();
					mapa_terms = par_atributos.getValue();
					
					//atributo
					salida.println(atributo);
					
					//numero terms
					salida.println(mapa_terms.size());
					
					it = mapa_terms.entrySet().iterator();
					while(it.hasNext()){
						par = it.next();
						palabra = par.getKey();
						lista = par.getValue();
						//palabra
						salida.print(palabra+" ");
						//lista
						lista.guardarTexto(salida);
						salida.println();
					}
					
				}//while... cada atributo
				
			}

			salida.close();
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.println("IndiceInvertidoMultiusuarioAtributos.guardarTexto - fin");
		
	}
	
	public void guardarTexto(String archivo){
		
		System.out.println("IndiceInvertidoMultiusuarioAtributos.guardarTexto - inicio (guardando en "+archivo+")");
		
		PrintWriter salida=null;
		
		Iterator<Map.Entry<Integer,Map<String, Map<String, ListaInvertida>>>> it_mapa;
		Map.Entry<Integer,Map<String, Map<String, ListaInvertida>>> par_mapa;
		Map<String, Map<String, ListaInvertida>> mapa_atributos;
		Iterator<Map.Entry<String, Map<String, ListaInvertida>>> it_atributos;
		Map.Entry<String, Map<String, ListaInvertida>> par_atributos;
		
//		Iterator<Map.Entry<Integer, Map<String, ListaInvertida>>> it_mapa;
//		Map.Entry<Integer, Map<String, ListaInvertida>> par_mapa;

		Map<String, ListaInvertida> mapa_terms;
		Iterator<Map.Entry<String, ListaInvertida>> it;
		Map.Entry<String, ListaInvertida> par;
		
		int user_id;
		String atributo;
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
				//mapa_terms=par_mapa.getValue();
				mapa_atributos=par_mapa.getValue();
				
				//user id
				salida.println(user_id);
				
				
				
				//atributos...
				
				//numero de atributos
				
				salida.println(mapa_atributos.size());
				
				it_atributos=mapa_atributos.entrySet().iterator();
				while(it_atributos.hasNext()){
					par_atributos=it_atributos.next();
					atributo=par_atributos.getKey();
					mapa_terms=par_atributos.getValue();
					
					//atributo
					salida.println(atributo);
					
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
					
				}//while... cada atributo
				
			}
			salida.close();
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.println("IndiceInvertidoMultiusuarioAtributos.guardarTexto - fin");
		
	}
	
	//metodo no adaptado para atributos
//	public boolean cargar(String archivo){
//		
//		System.out.println("IndiceInvertidoMultiusuarioAtributos.cargar - inicio (cargando desde "+archivo+")");
//		
//		mapa_indices.clear();
//		
//		Map<String, ListaInvertida> mapa_terms;
//		Map<String, Map<String, ListaInvertida>> mapa_atributos;
//		
//		DataInputStream entrada=null;
//		
//		int user_id;
//		int n_users, n_terms;
//		String palabra;
//		String atributo;
//		ListaInvertida lista;
//		
//		try{
//			entrada=new DataInputStream(new FileInputStream(archivo));
//			
//			//numero users
//			n_users=entrada.readInt();
//			
//			for(int i=0; i<n_users; i++){
//			
//				//user id
//				user_id=entrada.readInt();
//				
//				//numero terms
//				n_terms=entrada.readInt();
//				
//				mapa_terms=new TreeMap<String, ListaInvertida>();
//				
//				for(int j=0; j<n_terms; j++){
//					//palabra
//					palabra=entrada.readUTF();
//					//lista
//					lista=new ListaInvertida();
//					lista.cargar(entrada);
//					mapa_terms.put(palabra, lista);
//				}
//				
//				mapa_indices.put(user_id, mapa_terms);
//			}
//		}
//		catch(Exception e){
//			e.printStackTrace();
//			return false;
//		}
//		
//		System.out.println("IndiceInvertidoMultiusuarioAtributos.cargar - fin");
//		
//		return true;
//	}
	
	public void addLista(int user_id, String atributo, String termino, ListaInvertida lista){
		//mapa_indices=new TreeMap<Integer, Map<String, Map<String, ListaInvertida>>>();
		if(mapa_indices.containsKey(user_id)){
			Map<String, Map<String, ListaInvertida>> mapa_usuario=mapa_indices.get(user_id);
			if(mapa_usuario.containsKey(atributo)){
				mapa_usuario.get(atributo).put(termino, lista);
			}
			else{
				Map<String, ListaInvertida> mapa=new TreeMap<String, ListaInvertida>();
				mapa.put(termino, lista);
				mapa_usuario.put(atributo, mapa);
			}
		}
		else{
			Map<String, Map<String, ListaInvertida>> mapa_usuario=new TreeMap<String, Map<String, ListaInvertida>>();
			Map<String, ListaInvertida> mapa=new TreeMap<String, ListaInvertida>();
			mapa.put(termino, lista);
			mapa_usuario.put(atributo, mapa);
			mapa_indices.put(user_id, mapa_usuario);
		}
	}
	
	public ListaInvertida getLista(int user_id, String atributo, String termino){
		if(mapa_indices.containsKey(user_id)){
			Map<String, Map<String, ListaInvertida>> mapa_usuario=mapa_indices.get(user_id);
			if(mapa_usuario.containsKey(atributo)){
				Map<String, ListaInvertida> mapa_terms=mapa_usuario.get(atributo);
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
		else{
			return null;
		}
	}
	
}



