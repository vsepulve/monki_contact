package obs.comm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

import java.io.DataOutputStream;
import java.io.DataInputStream;

import java.net.Socket;
import java.net.InetAddress;

import java.net.*;
import java.io.*;

import obs.comm.Machine;

public class ServiceConnection{
	
	private String host;
	private int port;
	
//	private ObjectOutputStream salida;
//	private ObjectInputStream entrada;
	private DataOutputStream salida;
	private DataInputStream entrada;
	private Socket cliente;
	
	public ServiceConnection(String _host, int _port){
		host=_host;
		port=_port;
	}
	
	public ServiceConnection(){
		host=null;
		port=0;
	}
	
	public boolean connect(){
		try{
			cliente = new Socket( InetAddress.getByName( host ), port );
			//System.out.println("ServiceConnection.connect - "+cliente.getInetAddress().getHostName()+", "+port+"");
			
			//salida = new ObjectOutputStream( cliente.getOutputStream() );
			salida = new DataOutputStream( cliente.getOutputStream() );
			//salida.flush();
			
			//entrada = new ObjectInputStream( cliente.getInputStream() );
			entrada = new DataInputStream( cliente.getInputStream() );
			
			//System.out.println("ServiceConnection.connect - fin");
		}
		catch(IOException e){
			System.err.println("ServiceConnection.connect - Error al conectar");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean connect(String _host, int _port){
		host=_host;
		port=_port;
		return connect();
	}
	
	public boolean connect(Machine m){
		host=m.getHost();
		port=m.getPort();
		return connect();
	}
	
	public void close(){
		//System.out.println("ServiceConnection.close - inicio");
		try {
			salida.close();
			entrada.close();
			cliente.close();
		}
		catch(IOException e) {
			System.err.println("ServiceConnection.close - Error al terminar coneccion");
			e.printStackTrace();
		}
	}
	
	public void write(String message){
		try {
			//salida.writeObject(message);
			byte[] b=message.getBytes();
			salida.write(b, 0, b.length);
			salida.flush();
			//System.out.println("ServiceConnection.write - \""+message+"\" enviado");
		}
		catch (IOException e) {
			System.err.println("ServiceConnection.write - Error al enviar");
			e.printStackTrace();
		}
	}
	
	public String read(){
		String respuesta=null;
		try{
			//respuesta=(String)(entrada.readObject());
			byte[] b=new byte[1024];
			int n=entrada.read(b, 0, 1024);
			if(n>0){
				respuesta=new String(b, 0, n);
			}
		}
		catch(Exception e){
			System.err.println("ServiceConnection.read - Error al recibir");
			e.printStackTrace();
		}
		return respuesta;
	}
	
	public static void main(String[]args){
		
		if(args.length!=2){
			System.out.println("");
			System.out.println("Modo de Uso");
			System.out.println("> java ServiceConnection host port");
			System.out.println("");
			return;
		}
		
		String host=args[0];
		int port=new Integer(args[1]);
		
		ServiceConnection conexion=new ServiceConnection(host, port);
		
		BufferedReader lector=null;
		try{
			lector=new BufferedReader(new InputStreamReader(System.in));
		}
		catch(Exception e){
			e.printStackTrace();
		}
		String linea=null;
		
		while(true){
			
			System.out.print(">");
			try{
				linea=lector.readLine();
			}
			catch(IOException e){
				e.printStackTrace();
				linea="";
			}
			if(linea.compareTo("q")==0 || linea.compareTo("quit")==0){
				System.out.println("Saliendo...");
				break;
			}
			
			if(!conexion.connect()){
				System.out.println("Saliendo por problemas al conectar...");
				return;
			}
			
			System.out.println("Enviando: \""+linea+"\"");
			conexion.write(linea);
			linea=conexion.read();
			System.out.println("Respuesta: \""+linea+"\"");
			
			conexion.close();
		}
		
		
	}
	
}
