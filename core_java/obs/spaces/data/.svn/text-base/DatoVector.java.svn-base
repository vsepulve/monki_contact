package obs.spaces.data;

public class DatoVector extends Dato {

	private double[] vector;

	public DatoVector(){
		vector = null;
	}

	public DatoVector(double[] _vector){
		vector = new double[_vector.length];
		for (int i = 0; i < _vector.length; i++){
			vector[i] = _vector[i];
		}
	}

	public DatoVector(double[] _vector, int _dim){
		vector = new double[_dim];
		for (int i = 0; i < _dim; i++){
			vector[i] = _vector[i];
		}
	}

	public int dimension(){
		return vector.length;
	}
	
	public String toString(){
		String s = "";
		int max_vector = 10;
		if (vector == null){
			s += "DatoVector[" + id + "](null)";
		}
		else if (vector.length < max_vector){
			s += "DatoVector[" + id + "](";
			for (int i = 0; i < vector.length - 1; i++){
				s += "" + vector[i] + ", ";
			}
			s += "" + vector[vector.length - 1] + ")";
		}
		else{
			s += "DatoVector[" + id + "](";
			for (int i = 0; i < max_vector - 1; i++){
				s += "" + vector[i] + ", ";
			}
			s += "" + vector[max_vector - 1] + "...)";
		}
		return s;
	}
	
	public double[] getVector(){
		return vector;
	}
	
	public double getValor(int index){
		return vector[index];
	}
	
	public void setVector(double[] vector){
		this.vector = vector;
	}
	
}



