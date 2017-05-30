/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.tygabytes.deduping.modelinterface;

/**
 *
 * @author claudio
 */
public interface IOrganization {
    
    
  public Integer getOrgID();
  public Integer getUserId(); 
  public Integer getContactId();
  public String getLabel();
  public String getType(); 
  public Integer getPrimary(); 
  public String getOrgName(); 
  public String getDepartment(); 
  public String getOrgTitle();
  public String getStartDate(); 
  public String getEndDate(); 
  public String getJobStatus(); 
  public String getLocation(); 
  public String getDescription(); 
  public String getIndustry(); 
  public String getSource(); 
  public Integer getInsertDate();
  
  
  public void setOrgID(Integer orgID);
  public void setUserId(Integer userId); 
  public void setContactId(Integer contactId);
  public void setLabel(String label);
  public void setType(String type); 
  public void setPrimary(Integer primary); 
  public void setOrgName(String orgName); 
  public void setDepartment(String department); 
  public void setOrgTitle(String orgTitle);
  public void setStartDate(String startDate);
  public void setEndDate(String endDate); 
  public void setJobStatus(String jobStatus); 
  public void setLocation(String location); 
  public void setDescription(String description); 
  public void setIndustry(String industry); 
  public void setSource(String source); 
  public void setInsertDate(Integer insertDate);
    
    
}
