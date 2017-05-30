package obs.comm;

import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.Date;
import java.util.Collection;

import java.sql.*;
import java.sql.SQLException;
import com.mysql.jdbc.Driver;

import java.io.FileOutputStream;
import java.io.PrintWriter;

import obs.comm.Communication;
import obs.comm.Machine;

import obs.grouping.Group;
import obs.grouping.Grouping;
import obs.grouping.Feature;
import obs.grouping.FeatureString;

public class Interface {

    private Communication comm;

    public Interface(String server, String bd, String user, String pass){
        comm = new Communication(server, bd, user, pass);
    }

    public Interface(Communication _comm){
        comm = _comm;
    }

	public void close(){
		comm.close();
	}
	
	public boolean open(){
		return comm.open();
	}

    public Map<Integer, Group> getNewGroups(Set<Integer> processing_contacts){

		Date d_init = null;
		Date d_end = null;

		//System.out.println("getNewGroups - start");
		d_init = new Date();

        Map<Integer, Group> news = new TreeMap<Integer, Group>();
        
        /* Grupos processing */

		Set<Integer> processing_groups = new TreeSet<Integer>();

		for(int contact_id : processing_contacts){
			int group_id = comm.getGroupId(contact_id);
            processing_groups.add(group_id);
		}

		//System.out.println("getNewGroups - Obteniendo informacion de grupos processing");
        Group group;

		for(int group_id : processing_groups){
            group = new Group(group_id);
            group.addNames(comm.getNamesGroup(group_id));
            group.addMails(comm.getMailsGroup(group_id));
            group.addPhoneNumbers(comm.getPhonesGroup(group_id));
            group.addOrganizations(comm.getOrgsGroup(group_id));
            news.put(group_id,group);
        }

		d_end = new Date();
        //System.out.println("getNewGroups - " + news.size() + " grupos processing obtenidos en "+(long)(d_end.getTime()-d_init.getTime())/1000+" s");
		//System.out.println("getNewGroups - end");

        return news;
    }

    public Map<Integer,Group> getCandidates(Collection<Group> news){

		Date d_init = null;
		Date d_end = null;

		//System.out.println("getCandidates - start");
		d_init = new Date();
		
        Map<Integer,Group> candidates = new TreeMap<Integer,Group>();
		Set<Integer> candidate_contacts = new TreeSet<Integer>();
    
        /* Obtengo palabras de los grupos nuevos */
		Collection<Feature> features = new TreeSet<Feature>();
        Set<String> words = new TreeSet<String>();
		String word;
        for(Group group : news){
            features.addAll(group.getNames());
            features.addAll(group.getMails());
        }
        for(Feature f : features){
            if(f!=null){
                word = ((FeatureString)f).getString();
                words.add(word);
            }
        }

		/* Contactos candidatos */
		//System.out.println("getCandidates - Obteniendo contactos candidatos");
		for(String word2 : words){
			candidate_contacts.addAll(comm.getContactsName(word2));
			candidate_contacts.addAll(comm.getContactsMail(word2));
		}
		/* Pasarlos a grupos */
		Set<Integer> candidate_groups = new TreeSet<Integer>();
		for(int contact_id : candidate_contacts){
			int group_id = comm.getGroupId(contact_id);
            candidate_groups.add(group_id);
		}

		//System.out.println("getCandidates - Obteniendo informacion de "+candidate_contacts.size()+" contactos candidatos");
        /* Obtener informacion de grupos candidatos */
        Group group;
		for(int group_id : candidate_groups){
            group = new Group(group_id);    
            group.addNames(comm.getNamesGroup(group_id));
            group.addMails(comm.getMailsGroup(group_id));
            group.addPhoneNumbers(comm.getPhonesGroup(group_id));
            group.addOrganizations(comm.getOrgsGroup(group_id));
            candidates.put(group_id,group);
        }
        /** agregando los processing que faltan a candidates **/
        for(Group new_group : news){//for each processing group
            if(!candidates.containsKey(new_group.getGroupId()))
                //System.out.println("Warning: falta un processing en candidates");
                candidates.put(new_group.getGroupId(),new_group);
        }

		d_end = new Date();
		//System.out.println("getCandidates - "+candidate_groups.size()+" grupos candidatos obtenidos en "+(long)(d_end.getTime()-d_init.getTime())/1000+" s");
		//System.out.println("getCandidates - end");

        return candidates;
    }
	
    public void setNewGroups(Map<Integer, Integer> converted_groups){

		//System.out.println("setNewGroups - Escribiendo grupos modificados ("+converted_groups.size()+" mods)");
		for(Map.Entry<Integer, Integer> pairs_groups : converted_groups.entrySet()){
			comm.updateAsGroup(pairs_groups.getKey(), pairs_groups.getValue());
			comm.updateExactGroups(pairs_groups.getKey(), pairs_groups.getValue());
        }
    }
	
    public void setNewGroups(Set<Grouping> converted_groups){

		//System.out.println("setNewGroups - Escribiendo grupos modificados ("+converted_groups.size()+" mods)");
		int old_group_id;
		int new_group_id;
		for(Grouping g : converted_groups){
			old_group_id = g.getOldId();
			new_group_id = g.getNewId();
			comm.updateAsGroup(old_group_id, new_group_id);
			comm.updateExactGroups(old_group_id, new_group_id);
        }
    }
    
    public void writeLog(int old_group_id, int new_group_id, int contact_id, int module_id, String message){
    	comm.writeLog(old_group_id, new_group_id, contact_id, module_id, message);
    }
    
    public Map<Integer, Integer> getModifiedUsers(Map<Integer, Integer> changed_groups){
    	Map<Integer, Integer> modified_users = new TreeMap<Integer, Integer>();
    	int new_id;
    	Collection<Integer> users;
		for(Map.Entry<Integer, Integer> pairs_groups : changed_groups.entrySet()){
			new_id = pairs_groups.getValue();
			users = comm.getUsersGroup(new_id);
			for(int user_id : users){
				if(modified_users.containsKey(user_id)){
					modified_users.put(user_id, 1+modified_users.get(user_id));
				}
				else{
					modified_users.put(user_id, 1);
				}
			}
		}
    	return modified_users;
    }
    
    public Map<Integer, Integer> getModifiedUsers(Set<Grouping> changed_groups){
    	Map<Integer, Integer> modified_users = new TreeMap<Integer, Integer>();
    	int new_id;
    	Collection<Integer> users;
		for(Grouping g : changed_groups){
			new_id = g.getNewId();
			users = comm.getUsersGroup(new_id);
			for(int user_id : users){
				if(modified_users.containsKey(user_id)){
					modified_users.put(user_id, 1+modified_users.get(user_id));
				}
				else{
					modified_users.put(user_id, 1);
				}
			}
		}//for... cada grouping
    	return modified_users;
    }
    
    public Map<Integer, Integer> getUserGroups(int user_id){
    	return comm.getUserGroups(user_id);
    }
    
    public List<Machine> getUserMachines(int user_id){
    	//pedir datos a la base de datos para generar la lista
    	//por ahora asumo que todo esta local
    	
    	List<Machine> machines = new LinkedList<Machine>();
    	//machines.add(new Machine("localhost", 30001));
    	//machines.add(new Machine("localhost", 31001));
    	machines.add(new Machine("localhost", 32001));
//    	machines.add(new Machine("173.204.95.19", 30001));
//    	machines.add(new Machine("173.204.95.19", 31001));
    	
    	return machines;
    }

	public void changeStatusContacts(Set<Integer> contacts, String status){
        comm.changeStatusContacts(contacts, status);
	}

	public void changeStatusGroup(int group_id, String status){
        comm.changeStatusGroup(group_id, status);
	}
	
	public boolean recurrentBadGroup(int group_id){
		Set<Integer> contact_ids=comm.getContactsGroup(group_id);
		int total=0;
		for(Integer id : contact_ids){
			total=(total+id)%1000000000;
		}
		Map<Integer, Set<Integer>> candidates=comm.getBadGroupsCandidates(total);
		Set<Integer> candidate;
		for(Map.Entry<Integer, Set<Integer>> par : candidates.entrySet()){
			candidate=par.getValue();
			if(candidate.equals(contact_ids)){
				return true;
			}
		}
		return false;
	}
	
	public void markBadGroup(int group_id){
		Set<Integer> contact_ids=comm.getContactsGroup(group_id);
		int total=0;
		for(Integer id : contact_ids){
			total=(total+id)%1000000000;
		}
		comm.insertBadGroupsIndex(total, group_id);
		for(Integer id : contact_ids){
			comm.insertBadGroups(group_id, id);
		}
	}
	
	public boolean groupBreakRules(int group_id){
		Collection<String> mails=comm.getMailsGroup(group_id);
		if(mails.size()>10){
			return true;
		}
		Map<String, Set<String>> src_ids=comm.getSrcIdsGroup(group_id);
		if(src_ids.containsKey("linkedin")){
			Set<String> set_ids=src_ids.get("linkedin");
			if(set_ids.size()>=2){
				return true;
			}
		}
		return false;
	}

}



