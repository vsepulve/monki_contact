package obs.comm;

import java.lang.Comparable;

public class Machine implements Comparable{
	
	String host;
	int port;
	
	public Machine(String _host, int _port){
		host=_host;
		port=_port;
	}
	
	public String getHost(){
		return host;
	}
	
	public int getPort(){
		return port;
	}
	
	public int compareTo(Object o){
		if(! (o instanceof Machine) ){
			return 1;
		}
		int c=host.compareTo(((Machine)o).getHost());
		if(c==0){
			if(port < ((Machine)o).getPort()){
				return -1;
			}
			else if(port > ((Machine)o).getPort()){
				return 1;
			}
			else{
				return 0;
			}
		}
		else{
			return c;
		}
	}
	
	public boolean equals(Object o){
		if(! (o instanceof Machine) ){
			return false;
		}
		if(host.equals(((Machine)o).getHost()) && port ==((Machine)o).getPort()){
			return true;
		}
		else{
			return false;
		}
	}
	
	public String toString(){
		String s=host+":"+port;
		return s;
	}
}
