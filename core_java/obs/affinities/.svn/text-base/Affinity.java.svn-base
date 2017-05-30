package obs.affinities;

import java.util.Map;
import java.util.Iterator;

import obs.spaces.data.DatoVocabulario;
import obs.spaces.distances.Distancia;
import obs.spaces.distances.DistanciaCosVocabulario;

public class Affinity{
	
	//En teoria podria ser otra distancia
	//Por ahora esta fija como DistanciaCosVocabulario
	static Distancia dist=new DistanciaCosVocabulario();
	
	protected DatoVocabulario voc;
	
	public Affinity(){
		voc=null;
	}
	
	public Affinity(DatoVocabulario _voc){
		voc=_voc;
	}
	
	public Affinity(Map<String, Double> mapa_voc){
		voc=new DatoVocabulario(mapa_voc);
	}
	
	public DatoVocabulario getVocabulary(){
		return voc;
	}
	
	public int size(){
		return voc.getMapa().size();
	}
	
	public Iterator<Map.Entry<String, Double>> iterator(){
		return voc.getMapa().entrySet().iterator();
	}
	
	public boolean containsKey(String term){
		return voc.getMapa().containsKey(term);
	}
	
	public double get(String term){
		return voc.getMapa().get(term);
	}
	
	public void setId(int id){
		voc.setId(id);
	}
	
	public boolean similar(Affinity aff){
		
		if(this.size() != aff.size()){
			return false;
		}
		
		Iterator<Map.Entry<String, Double>> it;
		Map.Entry<String, Double> par;
		
		it=aff.iterator();
		while(it.hasNext()){
			par=it.next();
			if(! this.containsKey(par.getKey())){
				return false;
			}
		}
		
		if(dist.d(this.getVocabulary(), aff.getVocabulary()) > 0.1){
			return false;
		}
		
		return true;
	}
	

}







