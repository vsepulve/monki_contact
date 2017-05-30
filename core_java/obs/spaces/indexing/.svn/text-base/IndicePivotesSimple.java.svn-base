package obs.spaces.indexing;

import obs.spaces.data.Dato;
import obs.spaces.distances.Distancia;

import obs.spaces.util.ParDatoDistancia;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class IndicePivotesSimple extends Indice{
	
	private Distancia dist;
	
	private int n_datos;
	private List<Dato> datos;
	private int n_pivotes;
	private List<Dato> pivotes;
	
	private double[][] matriz;
	
	private double[] matriz_q;
	
	public int evitadas;
	public int realizadas;
	
	public IndicePivotesSimple(){
		dist=null;
		n_datos=0;
		datos=new LinkedList<Dato>();
		n_pivotes=0;
		pivotes=new LinkedList<Dato>();
		matriz=null;
		matriz_q=null;
	}
	
	public IndicePivotesSimple(List<Dato> lista, Distancia _dist, int _n_pivotes){
		System.out.println("IndicePivotesSimple - inicio ("+lista.size()+" datos, "+_n_pivotes+" pivotes)");
		dist=_dist;
		datos=new LinkedList<Dato>();
		pivotes=new LinkedList<Dato>();
		n_datos=lista.size();
		n_pivotes=_n_pivotes;
		n_datos-=n_pivotes;
		
		Set<Integer> agrgados=new TreeSet<Integer>();
		int candidato;
		for(int i=0; i<n_pivotes; i++){
			candidato = (int)((double)n_datos * Math.random());
			while(agrgados.contains(candidato)){
				candidato = (int)((double)n_datos * Math.random());
			}
			agrgados.add(candidato);
		}
		System.out.println("IndicePivotesSimple - inicio ("+agrgados.size()+" candidatos a pivote)");
		
		Iterator<Dato> it, it_pivote;
		
		Dato dato, pivote;
		candidato=0;
		it=lista.iterator();
		while(it.hasNext()){
			dato=it.next();
			if(agrgados.contains(candidato)){
				pivotes.add(dato);
			}
			else{
				datos.add(dato);
			}
			candidato++;
		}
		
		System.out.println("IndicePivotesSimple - inicio ("+pivotes.size()+" pivotes ("+n_pivotes+"), "+datos.size()+" datos ("+n_datos+"))");
		
		matriz=new double[n_datos][n_pivotes];
		matriz_q=new double[n_pivotes];
		
		int i_dato, i_pivote;
		
		it=datos.iterator();
		i_dato=0;
		while(it.hasNext()){
			dato=it.next();
			it_pivote=pivotes.iterator();
			i_pivote=0;
			while(it_pivote.hasNext()){
				pivote=it_pivote.next();
				matriz[i_dato][i_pivote]=dist.d(dato, pivote);
				i_pivote++;
			}
			i_dato++;
		}
		
	}
	
	public int size(){
		return n_datos + n_pivotes;
	}
	
	public int rango(Dato q, double r, List<ParDatoDistancia> res){
		int agregados=0;
		evitadas=0;
		realizadas=0;
		
		Iterator<Dato> it, it_pivote;
		
		Dato dato, pivote;
		double d;
		boolean descartado;
		
		//pivotes
		it_pivote=pivotes.iterator();
		for(int i=0; i<n_pivotes; i++){
			pivote=it_pivote.next();
			matriz_q[i]=dist.d(q, pivote);
			realizadas++;
			if(matriz_q[i] <= r){
				res.add(new ParDatoDistancia(pivote, matriz_q[i]));
				agregados++;
			}
		}
		
		it=datos.iterator();
		for(int i=0; i<n_datos; i++){
			dato=it.next();
			it_pivote=pivotes.iterator();
			descartado=false;
			for(int j=0; j<n_pivotes; j++){
				d=matriz_q[j]-matriz[i][j];
				if(d<0)
					d=-d;
				if(d>r){
					evitadas++;
					descartado=true;
					break;
				}
			}
			if(!descartado){
				//si no lo puedo descartar, calculo su distancia
				d=dist.d(q, dato);
				realizadas++;
				if(d<=r){
					res.add(new ParDatoDistancia(dato, d));
					agregados++;
				}
			}
		}
		
		return agregados;
	}
	
}





