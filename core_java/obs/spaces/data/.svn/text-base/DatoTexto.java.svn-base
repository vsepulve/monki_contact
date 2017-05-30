package obs.spaces.data;

public class DatoTexto extends Dato {

	private String texto;

	public DatoTexto(){
		texto = "";
	}

	public DatoTexto(String _texto){
		if(_texto==null || _texto.length()==0){
			texto="";
		}
		else{
			texto = _texto;
		}
	}
	
	public int largo(){
		return getTexto().length();
	}
	
	public String toString(){
		String s = "";
		int max_texto = 10;
		if (getTexto() == null){
			s += "DatoString[" + id + "](null)";
		}
		else if (getTexto().length() < max_texto){
			s += "DatoString[" + id + "](" + getTexto() + ")";
		}
		else{
			s += "DatoString[" + id + "](" + getTexto().substring(0, max_texto) + "...)";
		}
		return s;
	}
	
	public String getTexto(){
		return texto;
	}
	
	public void setTexto(String texto){
		this.texto = texto;
	}
	
}



