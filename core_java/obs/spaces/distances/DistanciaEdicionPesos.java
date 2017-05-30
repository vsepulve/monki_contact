package obs.spaces.distances;

import obs.spaces.data.Dato;
import obs.spaces.data.DatoTexto;

public class DistanciaEdicionPesos extends Distancia {

	int max_palabra;
	//la matriz es de (max+1)x(max+1) !
	int[][] m;

	public DistanciaEdicionPesos(){
		max_palabra = 255;
		m = new int[max_palabra + 1][max_palabra + 1];
	}

	public DistanciaEdicionPesos(int _max_palabra){
		max_palabra = _max_palabra;
		m = new int[max_palabra + 1][max_palabra + 1];
	}
	
	public double d(Dato o1, Dato o2){
		if (!(o1 instanceof DatoTexto) || !(o2 instanceof DatoTexto)){
			System.err.println("DistanciaEdicionPesos.d() - Los objetos deben ser de tipo DatoTexto");
			return Double.POSITIVE_INFINITY;
		}
		
		DatoTexto p1 = (DatoTexto) o1;
		DatoTexto p2 = (DatoTexto) o2;

		contador++;
		double r = 0.0;

		int n1 = p1.largo();
		int n2 = p2.largo();
		int n_max;
		if (n1 > n2){
			n_max = n1;
		}
		else{
			n_max = n2;
		}
		if (n_max > max_palabra){
			System.err.println("DistanciaEdicionPesos.d() - Palabras demasiado grandes (" + n_max + " > " + max_palabra + ")");
			return Double.POSITIVE_INFINITY;
		}
		for (int i = 0; i <= n1; i++){
			m[i][0] = i;
		}
		for (int j = 0; j <= n2; j++){
			m[0][j] = j;
		}
		//generar matriz de costo
		int costo;
		for (int j = 1; j <= n2; j++){
			for (int i = 1; i <= n1; i++){
				if (p1.getTexto().charAt(i - 1) == p2.getTexto().charAt(j - 1)){
					costo = 0;
				}
				else{
					costo = 3;
				}
				//Operaciones
				m[i][j] = min(m[i - 1][j] + 1, //eliminación
					m[i][j - 1] + 1, //inserción
					m[i - 1][j - 1] + costo); //sustitución
				
			}
		}
		r = m[n1][n2];
//System.out.println("DistanciaEdicionPesos.d("+contador+") - d("+p1.getTexto()+", "+p2.getTexto()+"): "+r+"");
		return r;
	}

	private int min(int i1, int i2, int i3){
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
}
