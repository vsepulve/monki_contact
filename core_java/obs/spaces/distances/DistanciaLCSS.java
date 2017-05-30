package obs.spaces.distances;

import obs.spaces.data.Dato;
import obs.spaces.data.DatoTexto;

public class DistanciaLCSS extends Distancia {

	int max_palabra;
	//la matriz es de (max+1)x(max+1) !
	int[][] m;
	boolean similitud;

	public DistanciaLCSS(){
		max_palabra = 255;
		m = new int[max_palabra + 1][max_palabra + 1];
		similitud=false;
	}

	public DistanciaLCSS(int _max_palabra){
		max_palabra = _max_palabra;
		m = new int[max_palabra + 1][max_palabra + 1];
		similitud=false;
	}
	
	public void setSimilitud(boolean _similitud){
		similitud=_similitud;
	}
	
	public double d(Dato o1, Dato o2){
		if (!(o1 instanceof DatoTexto) || !(o2 instanceof DatoTexto)){
			System.err.println("DistanciaEdicion.d() - Los objetos deben ser de tipo DatoTexto");
			return Double.POSITIVE_INFINITY;
		}
		DatoTexto p1 = (DatoTexto) o1;
		DatoTexto p2 = (DatoTexto) o2;

		contador++;
		double r = 0.0;
		
		if(p1==null || p1.largo()==0 || 
			p2==null || p2.largo()==0){
			return 1.0;
		}

		int n1 = p1.largo();
		int n2 = p2.largo();
		int n_max = max(n1, n2);
		
		if (n_max > max_palabra){
			System.err.println("DistanciaEdicion.d() - Palabras demasiado grandes (" + n_max + " > " + max_palabra + ")");
			return Double.POSITIVE_INFINITY;
		}
		for (int i = 0; i <= n1; i++){
			m[i][0] = 0;
		}
		for (int j = 0; j <= n2; j++){
			m[0][j] = 0;
		}

		//generar matriz de costo
		int costo;
		for (int i = 1; i <= n1; i++){
			for (int j = 1; j <= n2; j++){
				if (p1.getTexto().charAt(i - 1) == p2.getTexto().charAt(j - 1)){
					m[i][j] = m[i - 1][j - 1] + 1; //p1[i] == p2[j]
				}
				else{
					m[i][j] = max(m[i][j - 1], m[i - 1][j]); //p1[i] != p2[j]
				}
				// 0 si i=0 o j=0
			}
		}
		//System.out.println("LCSS: "+m[n1][n2]+"");
		//hay que buscar una manera adecuada de convertir esto en distancia

		//Opción 1: n1+n2 - 2*lcss;
		//r=n1+n2-2*m[n1][n2];
		if(similitud){
			//como similitud, normalizo por el mayor (por substring muy corto, 1 letra)
			if (n1 < n2){
				r = ((double) (m[n1][n2])) / n2;
			}
			else{
				r = ((double) (m[n1][n2])) / n1;
			}
			return r;
		}

		//Opción 2: 1 - lcss/min(n1,n2);
		if (n1 < n2){
			r = 1.0 - (double) (m[n1][n2]) / n1;
		}
		else{
			r = 1.0 - (double) (m[n1][n2]) / n2;
		}

		return r;
	}

	private int max(int i1, int i2){
		if (i1 > i2){
			return i1;
		}
		return i2;
	}
}
