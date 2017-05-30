package obs.spaces.indexing;

import obs.spaces.data.Dato;
import obs.spaces.distances.Distancia;

import obs.spaces.util.ParDatoDistancia;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

public class IndiceLineal extends Indice{
	
	private Distancia dist;
	
	private int n_datos;
	private List<Dato> datos;
	
	public IndiceLineal(){
		dist=null;
		n_datos=0;
		datos=new LinkedList<Dato>();
	}
	
	public IndiceLineal(List<Dato> lista, Distancia _dist){
		dist=_dist;
		n_datos=lista.size();
		datos=new LinkedList<Dato>();
		Iterator<Dato> it=lista.iterator();
		while(it.hasNext()){
			datos.add(it.next());
		}
	}
	
	public int size(){
		return n_datos;
	}
	
	public int rango(Dato q, double r, List<ParDatoDistancia> res){
		int agregados=0;
		
		Iterator<Dato> it=datos.iterator();
		Dato dato;
		double d;
		while(it.hasNext()){
			dato=it.next();
			d=dist.d(q, dato);
			if(d<=r){
				res.add(new ParDatoDistancia(dato, d));
				agregados++;
			}
		}
		
		return agregados;
	}
	
}





