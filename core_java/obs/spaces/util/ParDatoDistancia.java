package obs.spaces.util;

import obs.spaces.data.Dato;
import java.lang.Comparable;

public class ParDatoDistancia implements Comparable{
	public Dato dato;
	public double d;
	public ParDatoDistancia(){
		dato=null;
		d=0.0;
	}
	public ParDatoDistancia(Dato _dato, double _d){
		dato=_dato;
		d=_d;
	}
	public Dato getDato(){
		return dato;
	}
	public void setDato(Dato _dato){
		dato=_dato;
	}
	public double getDistancia(){
		return d;
	}
	public void setDistancia(double _d){
		d=_d;
	}
	
	//comparador DECRECIENTE por distancia (inverso a puntaje)
	//en igualdad, es CRECIENTE por id (=> knn unico)
	public int compareTo(Object o){
		if(!(o instanceof ParDatoDistancia)){
			System.err.println("compareTo - Ambos elementos deben ser de tipo ParDatoDistancia");
			return 0;
		}
		if(((ParDatoDistancia)o).getDistancia() < d){
			return 1;
		}
		else if(((ParDatoDistancia)o).getDistancia() > d){
			return -1;
		}
		else{
			if(((ParDatoDistancia)o).getDato().getId() < dato.getId()){
				return -1;
			}
			else if(((ParDatoDistancia)o).getDato().getId() > dato.getId()){
				return 1;
			}
			else{
				return 0;
			}//else... id igual
		}//else... distancia igual
		
	}
	
}
