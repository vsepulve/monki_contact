package obs.grouping;

public class Grouping implements Comparable<Grouping>{
	
	private int old_group_id;
	private int new_group_id;
	private int contact_id;
	private int rule_id;
	private String message;
	private String time;
	
	//Aqui podria guardarse informacion adicional para el log
	
	public Grouping(){
		old_group_id = -1;
		new_group_id = -1;
		contact_id = 0;
		rule_id = -1;
		message = "";
	}
	
	public Grouping(int _old_group_id, int _new_group_id, int _rule_id){
		old_group_id = _old_group_id;
		new_group_id = _new_group_id;
		contact_id = 0;
		rule_id = _rule_id;
		message = "";
		time = "";
	}
	
	public Grouping(int _old_group_id, int _new_group_id, int _rule_id, String _message){
		old_group_id = _old_group_id;
		new_group_id = _new_group_id;
		contact_id = 0;
		rule_id = _rule_id;
		message = _message;
		time = "";
	}
	
	public Grouping(int _old_group_id, int _new_group_id, int _contact_id, int _rule_id, String _message, String _time){
		old_group_id = _old_group_id;
		new_group_id = _new_group_id;
		contact_id = _contact_id;
		rule_id = _rule_id;
		message = _message;
		time = _time;
	}
	
	public int getOldId(){
		return old_group_id;
	}
	
	public int getNewId(){
		return old_group_id;
	}
	
	public int getContactId(){
		return contact_id;
	}
	
	public int getRuleId(){
		return rule_id;
	}
	
	public String getMessage(){
		return message;
	}
	
	public String getTime(){
		return time;
	}
	
	//Por definicion, no pueden haber dos agrupamientos diferentes para el mismo grupo
	//Por esto, considero que old_group_id es el identificador unico
	//Es decir, dos agrupamientos con el mismo old_group_id los defino iguales
	public int compareTo(Grouping grouping){
		if(old_group_id > grouping.getOldId())
			return 1;
		else if(old_group_id < grouping.getOldId())
			return -1;
		else
			return 0;
	}
}

