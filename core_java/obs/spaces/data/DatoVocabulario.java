package obs.spaces.data;

import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;

import java.text.DecimalFormat;

import obs.spaces.data.Dato;

public class DatoVocabulario extends Dato{
	private static DecimalFormat formato = new DecimalFormat("##.###");
	private Map<String, Double> mapa;
	public DatoVocabulario(){
		mapa=new TreeMap<String, Double>();
	}
	public DatoVocabulario(Map<String, Double> _mapa){
		mapa=_mapa;
	}
	public Map<String, Double> getMapa(){
		return mapa;
	}
	public String toString(){
		String s="";
		Iterator<Map.Entry<String, Double>> it;
		Map.Entry<String, Double> par;
		it=mapa.entrySet().iterator();
		while(it.hasNext()){
			par=it.next();
			s+="("+par.getKey()+", "+formato.format(par.getValue())+") ";
		}
		return s;
	}
}



