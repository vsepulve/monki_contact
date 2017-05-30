package obs.spaces.data;

//por ahora, el id sera autoincremental entre TODAS las instancias (sin importar el tipo de Dato)
//Dato puede ser considerada clases abstractas, luego puede especificarse.
//Dato puede ser considerada interface, luego puede especificarse.
public class Dato {

    protected int id;
    static int new_id = 0;

    public Dato(){
        id = ++new_id;
    	//System.out.println("Nuevo Dato ("+id+")");
    }
	
	public int getId(){
		return id;
	}
	
	//para los casos en los que el Id tiene semantica para el dato
	public void setId(int _id){
		id=_id;
	}
	
    @Override
    public String toString()
    {
        String s = "";
        s += "Dato[" + id + "]";
        return s;
    }
}
