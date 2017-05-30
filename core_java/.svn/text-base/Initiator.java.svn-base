import java.util.Date;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import java.io.BufferedReader;
import java.io.FileReader;

import java.io.PrintWriter;
import java.io.FileOutputStream;

import java.sql.SQLException;

import obs.grouping.ProcessingCell;

import obs.comm.Communication;
import obs.comm.Interface;

class Initiator{

	public static void main(String[]args) throws SQLException {

		if(args.length < 5){
			System.out.println("");
			System.out.println("Usage:");
			System.out.println("> java Initiator archivo_config contactos_x_celula porcentaje_antiguos num_celulas archivo_output");
			System.out.println("");
			return;
		}

		String file_config = args[0];
		int range = Integer.parseInt(args[1]);
		int num_older = Integer.parseInt(args[2]);
		int num_cells = Integer.parseInt(args[3]);
		String file_output = args[4];

		String host, bd, user, pass;
		BufferedReader reader;
		try{
			reader = new BufferedReader(new FileReader(file_config));
			host = reader.readLine();
			bd = reader.readLine();
			user = reader.readLine();
			pass = reader.readLine();
			reader.close();
		}
		catch(Exception e){
			System.err.println("Initiator - Error al leer configuracion ("+file_config+")");
			e.printStackTrace();
			return;
		}
		Date d_init = null;
		Date d_end = null;
		
		Communication comm = new Communication(host, bd, user, pass);
		
		/*********************  LANZAR CELULAS *********************************/

		int iteracion = 1;
		List<Integer> tomados_ing = new LinkedList<Integer>();
		List<Integer> tomados_ed = new LinkedList<Integer>();
		while(true){
			d_init = new Date();
			if(comm.stop()){
				System.out.println("Initiator - stop encontrado");
				break;
			}
			System.out.println("\nInitiator - Iniciando iteracion " + iteracion);

			//los que necesito por celula, ej si rango = 5000
			int num_olds = range * num_older/100;   //rango x 20 por ciento = 1000
			int num_news = range - num_olds;		//rango - el 20 porciento = 4000
			
			//tomo los processing que necesito
			Set<Integer> processing_contacts = comm.getProcessingContacts(num_news*num_cells);
			Set<Integer> processed_contacts = comm.getProcessedContacts(num_olds*num_cells);
			
			int size_ed = processed_contacts.size();
			int size_ing = processing_contacts.size();

			int diff = -1*(size_ed - (num_olds*num_cells)); //los que hay - los que tome
			if(diff>0){									 //si hay menos grouped de los que necesito, tomo mas processing
				processing_contacts.addAll(comm.getProcessingContacts(diff));
			}

			int diff2 = -1*(size_ing - (num_news*num_cells));
			if(diff2>0){
				//si hay menos processing de los que necesito, tomo mas grouped
				processed_contacts.addAll(comm.getProcessedContacts(diff2));
			}
			
			//desde aqui no se usa comm hasta que las celulas terminen.
			//Entonces, podria cerrarse y reabrirse despues
			comm.close();
			
			//los disponibles realmente
			size_ed = processed_contacts.size(); 
			size_ing = processing_contacts.size(); 
			
			int totales = size_ed + size_ing;
			System.out.println("Initiator - Contactos totales a procesar en iteracion "+iteracion+": "+ totales);
			
			Collection<ProcessingCell> cells = new ArrayList<ProcessingCell>();
			Collection<Thread> cellThreads = new ArrayList<Thread>();
			
			for(int h=1; h<=num_cells; ++h){
				Set<Integer> processing = new TreeSet<Integer>();
				
				System.out.println("Initiator - Celula " + (h+num_cells*(iteracion-1)) + ": ("+ size_ing/num_cells +" nuevos, "+ size_ed/num_cells +" antiguos)");
				
				//Llenar set para pasarle a la celula
				//saco de los procesados
				for(int j=0; j<(size_ed/num_cells);++j){
					int ed = ((TreeSet<Integer>)processed_contacts).first();
					processing.add(ed);
					tomados_ed.add(ed);
					processed_contacts.remove(ed);
				}
				
				//saco de los processing
				for(int j=0; j<(size_ing/num_cells); ++j){
					int ing = ((TreeSet<Integer>)processing_contacts).first();
					processing.add(ing);
					tomados_ing.add(ing);
					processing_contacts.remove(ing);
				}
				
				//creo la celula
				ProcessingCell cell = new ProcessingCell(host, bd, user, pass, processing);
				cells.add(cell);
			}

			for(ProcessingCell cell: cells){
				cellThreads.add(new Thread(cell));
			}
			for(Thread t: cellThreads){
				t.start();
			}
			for(Thread t: cellThreads){
				try{
					t.join();
				} catch (InterruptedException e){
					e.printStackTrace();
				}
			}

			d_end = new Date();
			String tpo_it = (long)(d_end.getTime()-d_init.getTime())/1000 + "";
			
			/***************************** ESCRIBIR LOG *********************************/
			
			System.out.println("Initiator - Iteracion "+iteracion+" terminada ("+tomados_ing.size()+" nuevos, "+tomados_ed.size()+" antiguos, en "+tpo_it+" s)");
			
			if(! comm.open() ){
				System.out.println("Initiator - Problemas reabriendo la conexion");
				break;
			}
			
			int num_grupos = comm.getNumGroups();
			int num_proc = comm.getNumProcessing();
			
			PrintWriter salida = null;
			try{
				salida = new PrintWriter(new FileOutputStream(file_output, true));
				salida.println(iteracion+" "+num_grupos+" "+tpo_it+" "+num_proc);
				salida.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}

			tomados_ing.clear();
			tomados_ed.clear();
			++iteracion;
			
//			if(iteracion > 1){
//				break;
//			}

		}//while true

		System.out.println("Initiator - end");
	}

}
