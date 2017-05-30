/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.tygabytes.deduping.modelinterface;

/**
 *
 * @author claudio
 */
public interface IEducation {
    
    public Integer getEduId();
    public Integer getUserId();
    public String getAccountSrc();
    public Integer getContactId();
    public String getSchoolName();
    public String getDegree();
    public String getFieldOfStudy();
    public Integer getStartYear();
    public Integer getEndYear();
    public Integer getSrcId();
    public String getSource();
    
     public void setEduId(Integer eduId);
     public void setUserId(Integer userId);
     public void setAccountSrc(String accountSrc);
     public void setContactId(Integer contactId);
     public void setSchoolName(String schoolName);
     public void setDegree(String degree);
     public void setFieldOfStudy(String fieldOfStudy);
     public void setStartYear(Integer startYear);
     public void setEndYear(Integer endYear);
     public void setSrcId(Integer srcId);
     public void setSource(String source);
    
}
