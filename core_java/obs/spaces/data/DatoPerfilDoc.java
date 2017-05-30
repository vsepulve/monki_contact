package obs.spaces.data;

import obs.spaces.data.Dato;

import java.util.List;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.Iterator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PrintWriter;

public class DatoPerfilDoc extends Dato {
	
	private String name;
	private String content;
	private String address;
	private String network;
	private String twitter;
	
	private List<String> emails;
	private List<String> phones;
	private List<String> organizations;
	private List<Integer> org_type;
	
	private String hometown;
	private String lives;
	
	//Para el modo configurable, puede haber algunos datos estructurados (como mails y orgs)
	//...pero el resto de los datos deberian ser textos genericos en un Map<Nombre, Texo>
	
	public DatoPerfilDoc(){
		
		name = null;
		content = null;
		address = null;
		network = null;
		
		emails = new LinkedList<String>();
		phones = new LinkedList<String>();
		organizations = new LinkedList<String>();
		org_type = new LinkedList<Integer>();
		
	}

//	public DatoPerfilDoc(String _name){
//		
//		name = _name;
//		content = null;
//		address = null;
//		network = null;
//		
//		emails = new LinkedList<String>();
//		phones = new LinkedList<String>();
//		organizations = new LinkedList<String>();
//		org_type = new LinkedList<Integer>();
//		
//	}
	
	public void clear(){
	
		name = null;
		content = null;
		address = null;
		network = null;
		
		emails.clear();
		phones.clear();
		organizations.clear();
		org_type.clear();
	}
	
	public void guardar(DataOutputStream salida)throws Exception{
		
		//System.out.println("DatoPerfilDoc.guardar - inicio (guardando en "+salida+")");
		
		salida.writeInt(id);
		
		if(name==null || name.length()==0){
			salida.writeBoolean(false);
		}
		else{
			salida.writeBoolean(true);
			salida.writeUTF(name);
		}
		if(content==null || content.length()==0){
			salida.writeBoolean(false);
		}
		else{
			salida.writeBoolean(true);
			salida.writeUTF(content);
		}
		if(address==null || address.length()==0){
			salida.writeBoolean(false);
		}
		else{
			salida.writeBoolean(true);
			salida.writeUTF(address);
		}
		if(network==null || network.length()==0){
			salida.writeBoolean(false);
		}
		else{
			salida.writeBoolean(true);
			salida.writeUTF(network);
		}
		
		String texto;
		
		salida.writeInt(emails.size());
		for(int i=0; i<emails.size(); i++){
			texto=emails.get(i);
			if(texto==null || texto.length()==0){
				salida.writeBoolean(false);
			}
			else{
				salida.writeBoolean(true);
				salida.writeUTF(texto);
			}
		}
		
		salida.writeInt(phones.size());
		for(int i=0; i<phones.size(); i++){
			texto=phones.get(i);
			if(texto==null || texto.length()==0){
				salida.writeBoolean(false);
			}
			else{
				salida.writeBoolean(true);
				salida.writeUTF(texto);
			}
		}
		
		salida.writeInt(organizations.size());
		for(int i=0; i<organizations.size(); i++){
			texto=organizations.get(i);
			if(texto==null || texto.length()==0){
				salida.writeBoolean(false);
			}
			else{
				salida.writeBoolean(true);
				salida.writeUTF(texto);
			}
		}
		
		//System.out.println("DatoPerfilDoc.guardar - fin");
		
	}
	
	public void guardarTextoSimple(PrintWriter salida)throws Exception{
		
		//System.out.println("DatoPerfilDoc.guardarTexto - inicio (guardando en "+salida+")");
		
		salida.print("Name:");
		if(name==null || name.length()<1)
			salida.println("null");
		else
			salida.println(name);
		
		salida.print("network:");
		if(network==null || network.length()<1)
			salida.println("null");
		else
			salida.println(network);
		
		String texto;
		
		salida.println("emails: "+emails.size());
		for(int i=0; i<emails.size(); i++){
			texto=emails.get(i);
			salida.println("  "+texto);
		}
		
		salida.println("phones: "+phones.size());
		for(int i=0; i<phones.size(); i++){
			texto=phones.get(i);
			salida.println("  "+texto);
		}
		
		salida.println("organizations: "+organizations.size());
		for(int i=0; i<organizations.size(); i++){
			texto=organizations.get(i);
			salida.println("  "+texto);
		}
		
		//System.out.println("DatoPerfilDoc.guardarTexto - fin");
		
	}
	
	public void guardarTexto(PrintWriter salida)throws Exception{
		
		//System.out.println("DatoPerfilDoc.guardarTexto - inicio (guardando en "+salida+")");
		
//		salida.println("doc_id: "+id);
		salida.println("id interno: "+id);
		
		salida.println("Name:");
		if(name==null || name.length()<1)
			salida.println("null");
		else
			salida.println(name);
		
		salida.println("content:");
		if(content==null || content.length()<1)
			salida.println("null");
		else
			salida.println(content);
		
		salida.println("address:");
		if(address==null || address.length()<1)
			salida.println("null");
		else
			salida.println(address);
		
		salida.println("network:");
		if(network==null || network.length()<1)
			salida.println("null");
		else
			salida.println(network);
		
		String texto;
		
		salida.println("emails: "+emails.size());
		for(int i=0; i<emails.size(); i++){
			texto=emails.get(i);
			salida.println(texto);
		}
		
		salida.println("phones: "+phones.size());
		for(int i=0; i<phones.size(); i++){
			texto=phones.get(i);
			salida.println(texto);
		}
		
		salida.println("organizations: "+organizations.size());
		for(int i=0; i<organizations.size(); i++){
			texto=organizations.get(i);
			salida.println(texto);
		}
		
		//System.out.println("DatoPerfilDoc.guardarTexto - fin");
		
	}
	
	public void cargar(DataInputStream entrada)throws Exception{
		
		id=entrada.readInt();
		
		boolean leer;
		
		leer=entrada.readBoolean();
		if(leer)
			name=entrada.readUTF();
		leer=entrada.readBoolean();
		if(leer)
			content=entrada.readUTF();
		leer=entrada.readBoolean();
		if(leer)
			address=entrada.readUTF();
		leer=entrada.readBoolean();
		if(leer)
			network=entrada.readUTF();
		
		int n;
		n=entrada.readInt();
		for(int i=0; i<n; i++){
			leer=entrada.readBoolean();
			if(leer)
				emails.add(entrada.readUTF());
		}
		
		n=entrada.readInt();
		for(int i=0; i<n; i++){
			leer=entrada.readBoolean();
			if(leer)
				phones.add(entrada.readUTF());
		}
		
		n=entrada.readInt();
		for(int i=0; i<n; i++){
			leer=entrada.readBoolean();
			if(leer)
				organizations.add(entrada.readUTF());
		}
		
	}
	
	public String getLives(){
		return lives;
	}
	
	public void setLives(String _lives){
		if(_lives != null && _lives.length() > 0){
			lives = _lives;
		}
	}
	
	public String getHometown(){
		return hometown;
	}
	
	public void setHometown(String _hometown){
		if(_hometown != null && _hometown.length() > 0){
			hometown = _hometown;
		}
	}
	
	public String getFullName(){
		return name;
	}

	public String getTwitter(){
		return twitter;
	}

	public void setTwitter(String _twitter){
		if(_twitter != null && _twitter.length() > 0){
			twitter = _twitter;
		}
	}

	public void setFullName(String _name){
		if(_name != null && _name.length() > 0){
			name = _name;
		}
	}

	public List<String> getEmails(){
		return emails;
	}

	public void addEmail(String email){
		if(! emails.contains(email)){
			emails.add(email);
		}
	}

	public List<String> getPhones(){
		return phones;
	}
	
	public void addPhone(String phone){
		if(! phones.contains(phone)){
			phones.add(phone);
		}
	}

	public List<String> getOrganizations(){
		return organizations;
	}

	public List<String> getOrganizations(int type){
		List<String> res = new LinkedList<String>();
		Iterator<String> it = organizations.iterator();
		Iterator<Integer> it_type = org_type.iterator();
		while(it.hasNext() && it_type.hasNext()){
			String o = it.next();
			int t = it_type.next();
			if(t == type){
				res.add(o);
			}
		}
		return res;
	}
	
	public void addOrganization(String organization){
		if(organization!=null && organization.length()>0 
			&& ! organizations.contains(organization)){
			organizations.add(organization);
			org_type.add(0);
		}
	}
	
	public void addOrganization(String organization, int type){
		//aca deberia considerarse tambien el tipo, esto es temporal
		if(organization!=null && organization.length()>0 
			&& ! organizations.contains(organization)){
			organizations.add(organization);
			org_type.add(type);
		}
	}
	
	public String getContent(){
		return content;
	}
	
	public void setContent(String _content){
		if(_content != null && _content.length() > 0){
			content = _content;
		}
	}
	
	public void addContent(String _content){
		if(_content != null && _content.length() > 0){
			content = content + " " + _content;
		}
	}
	
	public String getAddress(){
		return address;
	}
	
	public void setAddress(String _address){
		if(_address != null && _address.length() > 0){
			address = _address;
		}
	}
	
	public String getNetwork(){
		return network;
	}
	
	public void setNetwork(String _network){
		if(_network != null && _network.length() > 0){
			network = _network;
		}
	}
	
	public String toString(){
		String s = "";
		
		s+="DatoPerfil (";
		s+=name+", ";
		s+=content+", ";
		s+=address+", ";
		s+=network+", ";
		s+=twitter+", ";
		s+=getEmails()+", ";
		s+=getPhones()+", ";
		s+=getOrganizations()+" ";
		s+=")";
		return s;
	}
	
}















