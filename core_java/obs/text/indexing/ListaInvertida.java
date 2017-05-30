package obs.text.indexing;

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Comparator;
import java.util.Arrays;
import java.util.Collections;

import java.lang.Comparable;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.PrintWriter;

import obs.text.indexing.ParIndice;

public class ListaInvertida{

	class ComparadorDoc implements Comparator<ParIndice>{
		public ComparadorDoc(){
		}
		//comparador por doc creciente
		public int compare(ParIndice o1, ParIndice o2){
			int d1, d2;
			d1=o1.getDocId();
			d2=o2.getDocId();
			if(d1 < d2){
				return -1;
			}
			else if(d1 > d2){
				return 1;
			}
			else{
				return 0;
			}
	
		}
		public boolean equals(Object obj){
			if(obj instanceof ComparadorDoc){
				return true;
			}
			else{
				return false;
			}
		} 
	}
	ComparadorDoc comp;
	
	private List<ParIndice> lista;
	private Iterator<ParIndice> iterador;
	
	public ListaInvertida(){
		lista=new LinkedList<ParIndice>();
		comp=new ComparadorDoc();
	}
	
	public ListaInvertida(Map<Integer, Double> mapa_entrada){
		lista=new LinkedList<ParIndice>();
		comp=new ComparadorDoc();
		
		Iterator<Map.Entry<Integer, Double>> it;
		Map.Entry<Integer, Double> par;
		it=mapa_entrada.entrySet().iterator();
		while(it.hasNext()){
			par=it.next();
			lista.add(new ParIndice(par.getKey(), par.getValue()));
		}
		
		Collections.sort(lista, comp);
		
	}
	
	public void reset(){
		iterador=lista.iterator();
	}
	
	public ParIndice next(){
		if(iterador.hasNext()){
			return iterador.next();
		}
		else{
			return null;
		}
	}
	
	public boolean hasNext(){
		return iterador.hasNext();
	}
	
	//deben agregarse con docid creciente
	boolean addPar(int doc, double val){
		if(doc < lista.get(lista.size()-1).getDocId()){
			System.err.println("ListaInvertida.addPar - doc_id menor al ultimo");
			return false;
		}
		lista.add(new ParIndice(doc, val));
		return true;
	}
	
	void guardar(DataOutputStream salida){
		//List<ParIndice> lista;
		
		Iterator<ParIndice> it;
		ParIndice par;
		
		try{
			salida.writeInt(lista.size());
		
			it=lista.iterator();
			while(it.hasNext()){
				par=it.next();
				salida.writeInt(par.getDocId());
				salida.writeDouble(par.getValor());
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	void guardarTexto(PrintWriter salida){
		//List<ParIndice> lista;
		
		Iterator<ParIndice> it;
		ParIndice par;
		
		try{
			salida.print(lista.size()+" ");
			
			it=lista.iterator();
			while(it.hasNext()){
				par=it.next();
				salida.print(par.getDocId()+" ");
				salida.print(par.getValor()+" ");
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	boolean cargar(DataInputStream entrada){
		
		int n_docs;
		int doc_id;
		double valor;
		
		try{
			n_docs=entrada.readInt();
			for(int i=0; i<n_docs; i++){
				doc_id=entrada.readInt();
				valor=entrada.readDouble();
				lista.add(new ParIndice(doc_id, valor));
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return true;
	}
	
	
}



