package obs.spaces.distances;

import obs.spaces.data.Dato;
import obs.spaces.data.DatoVector;

public class DistanciaL extends Distancia {

	int k;

	public DistanciaL(){
		k = 1;
	}

	public DistanciaL(int _k){
		k = _k;
	}
	
	public double d(Dato o1, Dato o2){
		if (!(o1 instanceof DatoVector) || !(o2 instanceof DatoVector)){
			System.err.println("DistanciaL.d() - Los objetos deben ser de tipo DatoVector");
			return Double.POSITIVE_INFINITY;
		}
		DatoVector v1 = (DatoVector) o1;
		DatoVector v2 = (DatoVector) o2;
		if (v1.dimension() != v2.dimension()){
			System.err.println("DistanciaL.d() - Ambos objetos deben tener la misma Dimension");
			return Double.POSITIVE_INFINITY;
		}
		contador++;
		double r = 0.0;
		if (k == 1){
			for (int i = 0; i < v1.dimension(); i++)
			{
				r += diferencia(v1.getValor(i), v2.getValor(i));
			}
		}
		else if (k == -1){
			double dif;
			for (int i = 0; i < v1.dimension(); i++){
				dif = diferencia(v1.getValor(i), v2.getValor(i));
				if (dif > r){
					r = dif;
				}
			}
		}
		else{
			for (int i = 0; i < v1.dimension(); i++){
				r += Math.pow(diferencia(v1.getValor(i), v2.getValor(i)), k);
			}
			r = Math.pow(r, (1.0 / k));
		}
		return r;
	}

	private double diferencia(double d1, double d2){
		if (d1 < d2){
			return d2 - d1;
		}
		else{
			return d1 - d2;
		}
	}
}



