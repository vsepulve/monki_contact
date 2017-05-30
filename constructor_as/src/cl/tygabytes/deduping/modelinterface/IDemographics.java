/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.tygabytes.deduping.modelinterface;

/**
 *
 * @author claudio
 */
public interface IDemographics {
    
    public Integer getDemoId();
    public Integer getUserId();
    public String getAccountSrc();
    public Integer getContactId();
    public String getHometown();
    public String getLocation();
    public String getGender();
    public String getWebsite();
    public String getSource();
    
    public void setDemoId(Integer demoId);
    public void setUserId(Integer userId);
    public void setAccountSrc(String accountSrc);
    public void setContactId(Integer contactId);
    public void setHometown(String hometown);
    public void setLocation(String location);
    public void setGender(String gender);
    public void setWebsite(String website);
    public void setSource(String source);
    
}
