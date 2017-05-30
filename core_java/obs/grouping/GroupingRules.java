package obs.grouping;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.Collection;
import java.util.Date;

import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;

import obs.grouping.Group;
import obs.grouping.Grouping;
import obs.grouping.Feature;
import obs.grouping.FeatureString;

public class GroupingRules{
	
	public GroupingRules(){
	}
	
	public Set<Grouping> execute(Map<Integer, Group> news, 
									Map<Integer, Group> candidates, 
									Map<String, List<Integer>> index){
		
		System.out.println("GroupingRules - start");
		Date d_ini;
		Date d_end;
		int id_processing_group;
		Group processing_group;
		Set<Integer> converted_candidates = new TreeSet<Integer>();
		List<String> common_data = new LinkedList<String>();
		int added_mails = 0;
		int added_phones = 0;
		int added_names = 0;
		int added_orgs = 0;
		Collection<Feature> mails_i = null;
		Collection<Feature> mails_j = null;
		Collection<Feature> phones_i = null;
		Collection<Feature> phones_j = null;
		Collection<Feature> names_i = null;
		Collection<Feature> names_j = null;
		Collection<Feature> orgs_i = null;
		Collection<Feature> orgs_j = null;
		
		d_ini = new Date();
		
		Set<Integer> current_candidates = new TreeSet<Integer>();
		Iterator<Integer> it_current_candidates;

		int id_candidate;
		Group candidate;
		Feature feature_processing;
		Feature feature_candidate;

		Set<Integer> eliminated = new TreeSet<Integer>();
//		Map<Integer, Integer> convert = new TreeMap<Integer, Integer>();
		Set<Grouping> convert = new TreeSet<Grouping>();
		String word;
		
		Map<Integer, Integer> rules_used = new TreeMap<Integer, Integer>();
		boolean make_grouping = false;
		
		//para cada grupo processing
		for(Map.Entry<Integer,Group> pair_processing_group: news.entrySet()){
			id_processing_group = pair_processing_group.getKey();
			if(eliminated.contains(id_processing_group))
				continue;
			processing_group = pair_processing_group.getValue();
			//converted_candidates.add(id_processing_group);//puede convertirse en indice menor o serlo, hay que considerarlo al convertir

			mails_i = processing_group.getMails();
			phones_i = processing_group.getPhoneNumbers();
			names_i = processing_group.getNames();
			orgs_i = processing_group.getOrgs();
		
			current_candidates.clear();
			
			for(Feature mail : mails_i){
				word = mail.toString();
				if(index.containsKey(word)){
					current_candidates.addAll( index.get(word) );
				}
			}

			for(Feature name : names_i){
				word = name.toString();
				if(index.containsKey(word)){
					current_candidates.addAll( index.get(word) );
				}
			}
			
			it_current_candidates = current_candidates.iterator();
			//System.out.print("candidatos de "+ id_processing_group +" : ");
			//para cada candidato de grupo actual
			while(it_current_candidates.hasNext()){

				id_candidate = it_current_candidates.next();
				//System.out.print(id_candidate+", ");
				if(id_candidate!=id_processing_group && !eliminated.contains(id_candidate)){
					//probar grupos
					added_mails = 0;
					added_phones = 0;
					added_names = 0;
					added_orgs = 0;

					candidate = candidates.get(id_candidate);
					if(candidate == null){
						System.out.println("Warning: candidate is null");
						continue;
					}

					mails_j = candidate.getMails();
					phones_j = candidate.getPhoneNumbers();
					names_j = candidate.getNames();
					orgs_j = candidate.getOrgs();

					for(Feature mail : mails_j){
						if(mails_i.contains(mail)){
							common_data.add(mail.toString());
							added_mails++;
						}
					}
					
					for(Feature phone : phones_j){
						if(phones_i.contains(phone)){
							common_data.add(phone.toString());
							added_phones++;
						}
					}
					
					for(Feature name : names_j){
						if(names_i.contains(name)){
							common_data.add(name.toString());
							added_names++;
						}
					}
					
					for(Feature org : orgs_j){
						boolean found = false;
						String org_s = org.toString();
						if(orgs_i.contains(org)){
							common_data.add(org_s);
							added_orgs++;
						}

						for(Feature mail : mails_i){
							String mail_s = mail.toString();
							if(found)
								break;
							if(mail_s.indexOf("@")>0){
								mail_s = mail_s.substring(mail_s.indexOf("@")+1);
							}
							else{
								mail_s = "";
							}
							if(mail_s.indexOf(".")>0){
								mail_s = mail_s.substring(0, mail_s.indexOf("."));
							}
							else{
								mail_s = "";
							}
							if(mail_s.length()>org_s.length()){
								mail_s = mail_s.substring(0, org_s.length());
							}

							if(org_s.indexOf(mail_s)==0){
								common_data.add(org_s);
								added_orgs++;
								found = true;
							}
						}
						
					}
					
					make_grouping = false;
					if(added_mails >= 2){
						rules_used.put(id_candidate, 1);
						if(converted_candidates.size()==0){
							rules_used.put(id_processing_group, 1);
						}
						make_grouping = true;
					}
					else if(added_phones >= 1 && added_names >= 1){
						rules_used.put(id_candidate, 2);
						if(converted_candidates.size()==0){
							rules_used.put(id_processing_group, 2);
						}
						make_grouping = true;
					}
					else if(added_mails >= 1 && added_names >= 1){
						rules_used.put(id_candidate, 3);
						if(converted_candidates.size()==0){
							rules_used.put(id_processing_group, 3);
						}
						make_grouping = true;
					}
					else if(added_orgs >= 1 && added_names >= 1){
						rules_used.put(id_candidate, 4);
						if(converted_candidates.size()==0){
							rules_used.put(id_processing_group, 4);
						}
						make_grouping = true;
					}
					else if(added_mails >= 1 && (names_i.size()==0 || names_j.size()==0)){
						rules_used.put(id_candidate, 5);
						if(converted_candidates.size()==0){
							rules_used.put(id_processing_group, 5);
						}
						make_grouping = true;
					}
					
					if(make_grouping){
						
						converted_candidates.add(id_candidate);
						eliminated.add(id_candidate);
						
						//traer los candidatos que agrega la informacion de grupo j		
						for(Feature mail : mails_j){
							word = mail.toString();
							if(index.containsKey(word)){
								current_candidates.addAll( index.get(word) );
							}
						}
						for(Feature name : names_j){
							word = name.toString();							
							if(index.containsKey(word)){
								current_candidates.addAll( index.get(word) );
							}
						}
						//resetear el iterador
						it_current_candidates = current_candidates.iterator();
						//agregar informacion de j a i
						
						mails_i.addAll(mails_j);
						phones_i.addAll(phones_j);
						names_i.addAll(names_j);
						orgs_i.addAll(orgs_j);
						
					}
					common_data.clear();					
				}
			}//forech grupo j

			//agrupar asignando indice menor a los candidatos convertidos

//			int id;	
//			Iterator<Integer> it = converted_candidates.iterator();
			
			int i = 0;	
			int id_menor = 0;		
			if(converted_candidates.size() != 0){
				//System.out.println("convertidos: ");
				converted_candidates.add(id_processing_group);
			}
			for(int id2: converted_candidates){
				//System.out.println("candidato convertido:"+id);
				if(i==0){
					id_menor = id2;
					++i;
				}
				else{
					//System.out.println(id+" -> "+id_menor);
					if(rules_used.containsKey(id2)){
						convert.add(new Grouping(id2, id_menor, rules_used.get(id2)));
					}
					else{
						convert.add(new Grouping(id2, id_menor, -1));
					}
				}
			}
			converted_candidates.clear();
			
		}//foreach grupo i
		
		d_end = new Date();
		
		System.out.println("GroupingRules - end "+(long)(d_end.getTime()-d_ini.getTime())/1000+" s");
		
		return convert;
	}
}
