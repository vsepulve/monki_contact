package obs.spaces.indexing;

import obs.spaces.data.Dato;
import obs.spaces.distances.Distancia;

import obs.spaces.util.ParDatoDistancia;

import java.util.List;

public class Indice{
	
	private Distancia dist;
	
	private int n_datos;
	//estructura adecuada para los datos
	
	public Indice(){
		dist=null;
		n_datos=0;
	}
	
	public Indice(List<Dato> lista, Distancia _dist){
		dist=_dist;
		n_datos=lista.size();
	}
	
	public int size(){
		return n_datos;
	}
	
	public int rango(Dato q, double r, List<ParDatoDistancia> res){
		return 0;
	}
	
}
