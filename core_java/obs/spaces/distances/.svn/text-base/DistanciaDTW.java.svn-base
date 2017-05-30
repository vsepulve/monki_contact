package obs.spaces.distances;

import obs.spaces.data.Dato;
import obs.spaces.data.DatoVector;

public class DistanciaDTW extends Distancia {

	int max_palabra;
	//la matriz es de (max+1)x(max+1) !
	double[][] m;
	
	public DistanciaDTW(){
		max_palabra = 255;
		m = new double[max_palabra + 1][max_palabra + 1];
	}

	public DistanciaDTW(int _max_palabra){
		max_palabra = _max_palabra;
		m = new double[max_palabra + 1][max_palabra + 1];
	}
	
	public double d(Dato o1, Dato o2){
		if (!(o1 instanceof DatoVector) || !(o2 instanceof DatoVector)){
			System.err.println("DistanciaDTW.d() - Los objetos deben ser de tipo DatoVector");
			return Double.POSITIVE_INFINITY;
		}
		
		DatoVector p1 = (DatoVector) o1;
		DatoVector p2 = (DatoVector) o2;

		contador++;
		double r = 0.0;

		int n1 = p1.dimension();
		int n2 = p2.dimension();
		int n_max;
		if (n1 > n2){
			n_max = n1;
		}
		else{
			n_max = n2;
		}
		if (n_max > max_palabra){
			System.err.println("DistanciaDTW.d() - Palabras demasiado grandes (" + n_max + " > " + max_palabra + ")");
			return Double.POSITIVE_INFINITY;
		}
		
		for(int i=1; i<=n1; i++){
			m[i][0]=Double.POSITIVE_INFINITY;
		}
		for(int j=1; j<=n2; j++){
			m[0][j]=Double.POSITIVE_INFINITY;
		}
		double costo;
		for(int i=1; i<=n1; i++){
			for(int j=1; j<=n2; j++){
				costo=diferencia(p1.getValor(i-1), p2.getValor(j-1));
				//cout<<"costo "<<i<<"x"<<j<<": "<<costo<<"\n";
				//insertar, borrar, reemplazar
				m[i][j]=costo + min(m[i-1][j], m[i][j-1], m[i-1][j-1]);
				//cout<<"=> "<<costo<<" + min("<<m[i-1][j]<<", "<<m[i][j-1]<<", "<<m[i-1][j-1]<<") = "<<m[i][j]<<"\n";
			}
		}
		r=m[n1][n2];
		
		//cout<<"dtw - "<<r<<"\n";
//		if(normalizar){
//			//factor=...
//			r/=factor;
//			//cout<<"dtw norm - "<<r<<"\n";
//		}

		return r;
		
	}

	private double min(double i1, double i2, double i3){
		if (i1 < i2){
			if (i1 < i3){
				return i1;
			}
			else{
				return i3;
			}
		}
		else{
			if (i2 < i3){
				return i2;
			}
			else{
				return i3;
			}
		}
	}
	
	private double diferencia(double d1, double d2){
		if(d1>d2)
			return d1-d2;
		return d2-d1;
	}
	
}




