package obs.spaces.distances;

import java.util.Map;
import java.util.Iterator;

import obs.spaces.data.Dato;
import obs.spaces.data.DatoVocabulario;
import obs.spaces.distances.Distancia;

public class DistanciaCosVocabulario extends Distancia{
	
	boolean similitud;
	
	public DistanciaCosVocabulario(){
		similitud=false;
	}
	
	public double d(Dato o1, Dato o2){
		if (!(o1 instanceof DatoVocabulario) || !(o2 instanceof DatoVocabulario)){
			System.err.println("DistanciaCosVocabulario.d() - Los objetos deben ser de tipo DatoVocabulario");
			return Double.POSITIVE_INFINITY;
		}
		DatoVocabulario v1 = (DatoVocabulario) o1;
		DatoVocabulario v2 = (DatoVocabulario) o2;
		
		Map<String, Double> m1=v1.getMapa();
		Map<String, Double> m2=v2.getMapa();
		Map.Entry<String, Double> par;
		Iterator<Map.Entry<String, Double>> it;
		double x, y;
		
		double r=0.0;
		double cx=0.0;
		double cy=0.0;
		
		//A: 1 - sum(x*y) / (sum(x^2)*sum(y^2))^(1/2)
		
		it=m1.entrySet().iterator();
		while(it.hasNext()){
			par=it.next();
			x=par.getValue();
			cx+=Math.pow(x, 2);
			if(m2.containsKey(par.getKey())){
				y=m2.get(par.getKey());
				r+=x*y;
				cy+=Math.pow(y, 2);
			}
		}
		
		//sumar terminos de 2 != de interseccion
		it=m2.entrySet().iterator();
		while(it.hasNext()){
			par=it.next();
			y=par.getValue();
			if(! m1.containsKey(par.getKey())){
				cy+=Math.pow(y, 2);
			}
		}
		
		
		//sin interseccion => r=0.0
		if(r>0.0){
			r/=Math.pow(cx*cy, 0.5);
		}
		
//		System.out.println("dcos final: "+(1.0-r)+"");
		
		if(similitud){
			return r;
		}
		
		return 1.0-r;
	}
	
}
