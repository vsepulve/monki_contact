/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.tygabytes.deduping.modelinterface;

/**
 *
 * @author claudio
 */
public interface IIm {
    
  public Integer getImID();
  public Integer getContactId();
  public Integer getUserId();
  public String getAddress(); 
  public String getLabel();
  public String getType();
  public String getProtocol(); 
  public Integer getPrimary(); 
  public String getSource();
  public Integer getInsertDate();
    

  public void  setImID(Integer imID);
  public void  setContactId(Integer contactId);
  public void  setUserId(Integer userId);
  public void  setAddress(String address); 
  public void  setLabel(String label);
  public void  setType(String type);
  public void  setProtocol(String protocol); 
  public void  setPrimary(Integer primary); 
  public void  setSource(String source);
  public void  setInsertDate(Integer insertDate);
  
}
