package obs.grouping;

import java.util.Set;
import java.util.TreeSet;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;

import obs.grouping.Feature;
import obs.grouping.FeatureString;

public class Group implements Comparable<Group>{
	
	private int groupId;
	private Map<String, Collection<Feature>> attributes;
	
	public Group(int _groupId){
		groupId = _groupId;
		attributes = new TreeMap<String,Collection<Feature>>();
	}
	
	public int getGroupId(){
		return groupId;
	}
	
	public void setGroupId(int _groupId){
		groupId = _groupId;
	}
	
	public Collection<Feature> getNames(){
		if(attributes.containsKey("names"))
			return attributes.get("names");
		else
			return new TreeSet<Feature>();
	}

	public Collection<Feature> getMails(){
		if(attributes.containsKey("mails"))
			return attributes.get("mails");
		else
			return new TreeSet<Feature>();
	}

	public Collection<Feature> getPhoneNumbers(){
		if(attributes.containsKey("phones"))
			return attributes.get("phones");
		else
			return new TreeSet<Feature>();
	}

	public Collection<Feature> getOrgs(){
		if(attributes.containsKey("orgs"))
			return attributes.get("orgs");
		else
			return new TreeSet<Feature>();
	}
   
	public void addName(String _name){
		if(_name == null) return;
		Feature name = new FeatureString(_name);
		if(attributes.containsKey("names")){
			attributes.get("names").add(name);
		}
		else{
			Collection<Feature> names = new TreeSet<Feature>();
			names.add(name);
			attributes.put("names", names);
		}
	}

	public void addNames(Collection<String> names){
		if(names == null) return;
		Iterator<String> it = names.iterator();
		String name;
		while(it.hasNext()){
			name = it.next();
			addName(name);
		}
	}

	public void addMail(String _mail){
		if(_mail == null) return;
		Feature mail = new FeatureString(_mail);
		if(attributes.containsKey("mails")){
			attributes.get("mails").add(mail);
		}
		else{
			Collection<Feature> mails = new TreeSet<Feature>();
			mails.add(mail);
			attributes.put("mails", mails);
		}
	}

	public void addMails(Collection<String> mails){
		if(mails == null) return;
		Iterator<String> it = mails.iterator();
		String mail;
		while(it.hasNext()){
			mail = it.next();
			addMail(mail);
		}
	}

	public void addPhoneNumber(String _phone){
		if(_phone == null) return;
		Feature phone = new FeatureString(_phone);
		if(attributes.containsKey("phones")){
			attributes.get("phones").add(phone);
		}
		else{
			Collection<Feature> phones = new TreeSet<Feature>();
			phones.add(phone);
			attributes.put("phones", phones);
		}
	}

	public void addPhoneNumbers(Collection<String> phones){
		if(phones == null) return;
		Iterator<String> it = phones.iterator();
		String phone;
		while(it.hasNext()){
			phone = it.next();
			addPhoneNumber(phone);
		}
	}

	public void addOrganization(String _org){
		if(_org == null) return;
		Feature org = new FeatureString(_org);
		if(attributes.containsKey("orgs")){
			attributes.get("orgs").add(org);
		}
		else{
			Collection<Feature> orgs = new TreeSet<Feature>();
			orgs.add(org);
			attributes.put("orgs", orgs);
		}
	}

	public void addOrganizations(Collection<String> orgs){
		if(orgs == null) return;
		Iterator<String> it = orgs.iterator();
		String org;
		while(it.hasNext()){
			org = it.next();
			addOrganization(org);
		}
	}

	public int compareTo(Group group){
		int _groupId = group.getGroupId();
		if(groupId > _groupId)
			return 1;
		else if(groupId < _groupId)
			return -1;
		else
			return 0;
	}
}
