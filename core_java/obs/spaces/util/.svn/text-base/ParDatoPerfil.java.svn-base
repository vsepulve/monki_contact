package obs.spaces.util;

import obs.spaces.data.DatoPerfilDoc;
import java.lang.Comparable;

//Por ahora usare la opcion Comparable, pues el comparador parece estar perdiendo datos en los swaps.
public class ParDatoPerfil implements Comparable{
	public DatoPerfilDoc doc;
	public double val;
	public ParDatoPerfil(){
		doc=null;
		val=0.0;
	}
	public ParDatoPerfil(DatoPerfilDoc _doc, double _val){
		doc=_doc;
		val=_val;
	}
	public DatoPerfilDoc getPerfil(){
		return doc;
	}
	public void setPerfil(DatoPerfilDoc _doc){
		doc=_doc;
	}
	public double getValor(){
		return val;
	}
	public void setValor(double _val){
		val=_val;
	}
	
	//comparador DECRECIENTE (normal) por distancia (inverso a puntaje)
	public int compareTo(Object o){
		if(!(o instanceof ParDatoPerfil)){
			System.err.println("compareTo - Ambos elementos deben ser de tipo ParDatoPerfil");
			return 0;
		}
		if(((ParDatoPerfil)o).getValor() < val){
			return 1;
		}
		else if(((ParDatoPerfil)o).getValor() > val){
			return -1;
		}
		else{
			return 0;
		}
		
	}
	
}
