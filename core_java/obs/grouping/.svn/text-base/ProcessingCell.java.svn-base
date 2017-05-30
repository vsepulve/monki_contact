package obs.grouping;

import java.util.Date;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.Collection;

import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import java.io.BufferedReader;
import java.io.FileReader;

import java.sql.SQLException;

import obs.grouping.Group;
import obs.grouping.Feature;
import obs.grouping.FeatureString;
import obs.grouping.GroupingRules;
import obs.grouping.Grouping;

import obs.comm.Interface;
import obs.comm.ServiceConnection;
import obs.comm.Machine;

public class ProcessingCell implements Runnable{
	
	static int module_id = 1;
	static int cells = 0;
	
	private Interface comm_interface;

	private Set<Integer> processing;
	private int id;
	
	//Hay que definir como entregar las rutas
	static String base_indice = "/root/datos/indices_usuarios/indice";
	static String base_grupos = "/root/datos/indices_usuarios/grupos";
	
	private String host;
	private String bd;
	private String user;
	private String pass;
	
	public ProcessingCell(String _host, String _bd, String _user, String _pass, 
							Set<Integer> _processing){
		
		host = _host;
		bd = _bd;
		user = _user;
		pass = _pass;
		
		++cells;
		id = cells;
		
		comm_interface = new Interface(host, bd, user, pass);
		processing = _processing;
		
	}

	public void run() {
		System.out.println("ProcessingCell.run "+id+" - start");
		
		Date d_ini = null;
		Date d_end = null;
		
		System.out.println("ProcessingCell.run "+id+" - tomando processing...");
		d_ini = new Date();
		
		//Tomar (e invertir) datos de los grupos processing
		Map<Integer, Group> news = comm_interface.getNewGroups(processing);
//		comm_interface.close();

		d_end = new Date();
		System.out.println("ProcessingCell.run "+id+" - datos tomados en "+(d_end.getTime()-d_ini.getTime())/1000+" s");
		
		Collection<Group> new_groups = new TreeSet<Group>();
		Group new_group;
		int group_id;
		Map<String, List<Integer>> group_index = new TreeMap<String, List<Integer>>();
		List<Integer> inverted_list;
		Collection<Feature> names;
		Collection<Feature> mails;
		String word;
		
		System.out.println("ProcessingCell.run "+id+" - invirtiendo datos");
		d_ini = new Date();
		
		for(Map.Entry<Integer,Group> pair_new_group : news.entrySet()){//for each processing group
			group_id = pair_new_group.getKey();
			new_group = pair_new_group.getValue();
			new_groups.add(new_group);
			names = new_group.getNames();
			mails = new_group.getMails();
			
			//inverted index
			for(Feature name : names){
				word = name.toString();
				if(group_index.containsKey(word)){
					group_index.get(word).add(group_id);
				}
				else{
					inverted_list = new LinkedList<Integer>();
					inverted_list.add(group_id);
					group_index.put(word, inverted_list);
				}
			}
			
			for(Feature mail : mails){
				word = mail.toString();
				if(group_index.containsKey(word)){
					group_index.get(word).add(group_id);
				}
				else{
					inverted_list = new LinkedList<Integer>();
					inverted_list.add(group_id);
					group_index.put(word, inverted_list);
				}
			}
			
		}
		
		d_end = new Date();
		System.out.println("ProcessingCell.run "+id+" - procesamiento listo en "+(d_end.getTime()-d_ini.getTime())/1000+" s");
		
		//Tomar (e invertir) datos de los grupos candidatos
		
		System.out.println("ProcessingCell.run "+id+" - tomando candidatos...");
		d_ini = new Date();
//		comm_interface.open();
		Map<Integer, Group> candidates = comm_interface.getCandidates(new_groups);
		comm_interface.close();
		d_end = new Date();
		System.out.println("ProcessingCell.run "+id+" - datos tomados en "+(d_end.getTime()-d_ini.getTime())/1000+" s");
		
		
		System.out.println("ProcessingCell.run "+id+" - invirtiendo datos");
		d_ini = new Date();
		
		Group candidate_group;
		for(Map.Entry<Integer,Group> pair_candidate_group : candidates.entrySet()){//for each candidate group
			group_id = pair_candidate_group.getKey();
			candidate_group = pair_candidate_group.getValue();

			names = candidate_group.getNames();
			mails = candidate_group.getMails();

			//inverted index
			for(Feature name : names){
				word = name.toString();
				if(group_index.containsKey(word)){
					group_index.get(word).add(group_id);
				}
				else{
					inverted_list = new LinkedList<Integer>();
					inverted_list.add(group_id);
					group_index.put(word, inverted_list);
				}
			}

			for(Feature mail : mails){
				word = mail.toString();
				if(group_index.containsKey(word)){
					group_index.get(word).add(group_id);
				}
				else{
					inverted_list = new LinkedList<Integer>();
					inverted_list.add(group_id);
					group_index.put(word, inverted_list);
				}
			}
			
		}
		
		d_end = new Date();
		System.out.println("ProcessingCell.run "+id+" - procesamiento listo en "+(d_end.getTime()-d_ini.getTime())/1000+" s");
		//Agrupar (GroupingRules)
		
		//Uno de los problemas es que el log y el cambio de grupos se hace en momentos diferentes
		//Deberian hacerse juntos, por lo que el resultado del GroupingRules deberias ser:
		// old_id -> new_id (Rule)
		
		Set<Grouping> changed_groups;
		GroupingRules grouping = new GroupingRules();
		changed_groups = grouping.execute(news, candidates, group_index);
		
		//Aqui deberia reabrirse la conexion
		if(! comm_interface.open() ){
			System.out.println("ProcessingCell.run "+id+" - Problema reabriendo conexion");
		}
		else{
			
			//Aqui se hace efectiva la agrupacion
			System.out.println("ProcessingCell.run "+id+" - Guardando cambios en BD");
			comm_interface.setNewGroups(changed_groups);
			comm_interface.changeStatusContacts(processing, "grouped");
			
			//Escribir el log (ahora hay informacion suficiente)
			
			for(Grouping g : changed_groups){
				int old_group_id = g.getOldId();
				int new_group_id = g.getNewId();
				int rule_id = g.getRuleId();
				if(rule_id < 1){
					//esto es un error
					comm_interface.writeLog(old_group_id, new_group_id, 0, module_id, "Rule X");
				}
				else{
					comm_interface.writeLog(old_group_id, new_group_id, 0, module_id, "Rule "+rule_id);
				}
			}
			
			//Enviar cambios a los servicios
			Map<Integer, Integer> modified_users = comm_interface.getModifiedUsers(changed_groups);
			sendChanges(modified_users);
			
			/*
			//Verificacion y separacion de grupos
			Set<Integer> new_group_ids=new TreeSet<Integer>();
			for(Map.Entry<Integer, Integer> pair : changed_groups.entrySet()){
				new_group_ids.add(pair.getValue());
			}
			for(Integer new_group_id : new_group_ids){
				//si new group rompe reglas
				//separar grupo
				if(comm_interface.groupBreakRules(new_group_id)){
					if(comm_interface.recurrentBadGroup(new_group_id)){
						//dar aviso
						System.out.println("\n[Recurrente] Celula "+id+" - grupo "+new_group_id+" rompe reglas");
						try{
							PrintWriter salida=new PrintWriter(new FileOutputStream("/root/logs/log_separacion_celulas.txt", true));
							salida.println(""+new_group_id+" [Recurrente]");
							salida.close();
						}
						catch(Exception e){
							e.printStackTrace();
						}
					}
					else{
						System.out.println("\n[Separando] Celula "+id+" - grupo "+new_group_id+" rompe reglas");
						try{
							PrintWriter salida=new PrintWriter(new FileOutputStream("/root/logs/log_separacion_celulas.txt", true));
							salida.println(""+new_group_id+" ");
							salida.close();
						}
						catch(Exception e){
							e.printStackTrace();
						}
						comm_interface.markBadGroup(new_group_id);
						separator.separateGroup(new_group_id);
					}
				}
			}
			*/
			
			
		}
		d_end = new Date();
		System.out.println("ProcessingCell.run "+id+" - end ("+changed_groups.size()+" mods)");
		
		comm_interface.close();
	}
	
	private void sendChanges(Map<Integer, Integer> modified_users){
		//por ahora itero por todos los usuarios, podria escogerse de acuerdo al numero de mods
		int user_id, n_mods;
		for(Map.Entry<Integer, Integer> user_pair : modified_users.entrySet()){
			user_id = user_pair.getKey();
			n_mods = user_pair.getValue();
			updateUsers(user_id);
			//sleep por 1/10 de segundo para no copar la cola
			try{
				Thread.sleep(300);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}//for... cada usuario
	}
	
	private void updateUsers(int user_id){
		String ruta_indice = base_indice+"_"+user_id+".ii.txt";
		String ruta_grupos = base_grupos+"_"+user_id+".txt";
		
		//pedir grupos
		Map<Integer, Integer> groups = comm_interface.getUserGroups(user_id);
		
		//escribir archivo de grupos
		generateGroupsFile(ruta_grupos, groups);
		
		//copiar archivos (ambos)
		//esto quedara comentado por ahora, asumiendo que todo esta local
		//...copiar grupos
		//...copiar indice (esto no deberia ser necesario por invariante)
		
		//crear conexion
		ServiceConnection conexion = new ServiceConnection();
		
		List<Machine> maquinas = comm_interface.getUserMachines(user_id);
		
		//para cada servicio
		for(Machine m : maquinas){
			//conextar y enviar mensaje
			
			//copiar archivos
			if(m.getHost().compareTo("localhost") != 0){
				copyFile(ruta_indice, m.getHost());
			}
			
			conexion.connect(m);
			conexion.write("us "+ruta_indice+" "+ruta_grupos+" 0 "+user_id);
			conexion.close();
		}
		
	}
	
	//Recibe la ruta ABSOLUTA del archivo
	//Asume que la ruta puede usarse en ambas maquinas
	public void copyFile(String file, String host){
		try {
			Process p = Runtime.getRuntime().exec("scp "+file+" "+host+":"+file+" ");
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line=reader.readLine();

			while (line != null) {	
				System.out.println(line);
				line = reader.readLine();
			}

		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void generateGroupsFile(String ruta_grupos, Map<Integer, Integer> groups){
		
		PrintWriter salida=null;
		
		try{
			salida=new PrintWriter(new OutputStreamWriter(new FileOutputStream(ruta_grupos), "UTF-8"));
			
			salida.println(groups.size());
			
			for(Map.Entry<Integer, Integer> par : groups.entrySet()){
				salida.println(par.getKey()+" "+par.getValue());
			}
			
			salida.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
}
