package obs.text.ranking;

import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Comparator;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;

import obs.text.indexing.ParIndice;
import obs.text.indexing.ListaInvertida;
import obs.text.indexing.IndiceInvertidoMultiusuario;

//Almacena y entrega listas
//No realiza rankings o busquedas
public class RankeadorBasico{

	class ComparadorVal implements Comparator{
		public ComparadorVal(){
		}
		//comparador INVERSO por val (para ranking creciente)
		public int compare(Object o1, Object o2){
			if(!(o1 instanceof ParIndice) || !(o2 instanceof ParIndice)){
				System.err.println("ComparadorVal.compare - Ambos elementos deben ser de tipo ParIndice");
				return 0;
			}
			double d1, d2;
			d1=((ParIndice)o1).getValor();
			d2=((ParIndice)o2).getValor();
			if(d1 < d2){
				return 1;
			}
			else if(d1 > d2){
				return -1;
			}
			else{
				return 0;
			}
	
		}
		public boolean equals(Object obj){
			if(obj instanceof ComparadorVal){
				return true;
			}
			else{
				return false;
			}
		} 
	}
	
	IndiceInvertidoMultiusuario indice;
	ComparadorVal comp;
	
	public RankeadorBasico(IndiceInvertidoMultiusuario _indice){
		indice=_indice;
		comp=new ComparadorVal();
	}
	
	public List<ParIndice> consultar(int user_id, String[] terms){
	
		//System.out.println("RankeadorBasico.consultar - inicio ("+terms.length+" terminos)");
		
		List<ParIndice> list_res=new LinkedList<ParIndice>();
		
		Map<Integer, Double> resultados=new TreeMap<Integer, Double>();
		Iterator<Map.Entry<Integer, Double>> it_res;
		Map.Entry<Integer, Double> par_res;
		
		List<ListaInvertida> listas=new LinkedList<ListaInvertida>();
		Iterator<ListaInvertida> it_listas;
		ListaInvertida lista;
		
		ParIndice par;
		int doc_id;
		double valor;
		
		for(int i=0; i<terms.length; i++){
			lista=indice.getLista(user_id, terms[i]);
			if(lista==null){
				//System.out.println("Lista "+terms[i]+" no encontrada");
			}
			else{
				listas.add(lista);
			}
		}
		
		it_listas=listas.iterator();
		while(it_listas.hasNext()){
			lista=it_listas.next();
			lista.reset();
			while(lista.hasNext()){
				par=lista.next();
				doc_id=par.getDocId();
				valor=par.getValor();
				if(resultados.containsKey(doc_id)){
					resultados.put(doc_id, valor + resultados.get(doc_id));
				}
				else{
					resultados.put(doc_id, valor);
				}
			}
		}
		
		it_res=resultados.entrySet().iterator();
		while(it_res.hasNext()){
			par_res=it_res.next();
			list_res.add(new ParIndice(par_res.getKey(), par_res.getValue()));
		}
		Collections.sort(list_res, comp);
		
		//System.out.println("RankeadorBasico.consultar - fin ("+list_res.size()+" resultados)");
		
		return list_res;
		
	}
	
	public List<ParIndice> consultar(int user_id, String[] terms, Double[] pesos){
	
		//System.out.println("RankeadorBasico.consultar - inicio ("+terms.length+" terminos)");
		
		List<ParIndice> list_res=new LinkedList<ParIndice>();
		
		Map<Integer, Double> resultados=new TreeMap<Integer, Double>();
		Iterator<Map.Entry<Integer, Double>> it_res;
		Map.Entry<Integer, Double> par_res;
		
		List<ListaInvertida> listas=new LinkedList<ListaInvertida>();
		Iterator<ListaInvertida> it_listas;
		ListaInvertida lista;
		
		ParIndice par;
		int doc_id;
		double valor;
		
		for(int i=0; i<terms.length; i++){
			lista=indice.getLista(user_id, terms[i]);
			if(lista==null){
				//System.out.println("Lista "+terms[i]+" no encontrada");
			}
			else{
				listas.add(lista);
			}
		}
		
		it_listas=listas.iterator();
		int posicion=0;
		double peso;
		while(it_listas.hasNext()){
			peso=pesos[posicion];
			lista=it_listas.next();
			lista.reset();
			while(lista.hasNext()){
				par=lista.next();
				doc_id=par.getDocId();
				valor=peso*par.getValor();
				if(resultados.containsKey(doc_id)){
					//aqui se asegura de tener mas terminos de la consulta
//					resultados.put(doc_id, valor + resultados.get(doc_id));
					resultados.put(doc_id, 1.5*(valor + resultados.get(doc_id)));
				}
				else{
					resultados.put(doc_id, valor);
				}
			}
			posicion++;
		}
		
		it_res=resultados.entrySet().iterator();
		while(it_res.hasNext()){
			par_res=it_res.next();
			list_res.add(new ParIndice(par_res.getKey(), par_res.getValue()));
		}
		Collections.sort(list_res, comp);
		
		System.out.println("RankeadorBasico.consultar - fin ("+list_res.size()+" resultados)");
		
		return list_res;
		
	}
	
	public static void main(String[] agrs){
		System.out.println("Prueba");
	}
}



