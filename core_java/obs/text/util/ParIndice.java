package obs.text.util;

public class ParIndice{
	private int doc;
	private double val;
	public ParIndice(int _doc, double _val){
		val=_val;
		doc=_doc;
	}
	public int getDocId(){
		return doc;
	}
	public double getValor(){
		return val;
	}
}
