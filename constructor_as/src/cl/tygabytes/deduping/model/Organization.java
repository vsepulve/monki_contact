/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.tygabytes.deduping.model;

import cl.tygabytes.deduping.modelinterface.IOrganization;

/**
 *
 * @author claudio
 */
public class Organization implements IOrganization{
    
  private Integer OrgID;
  private Integer UserId; 
  private Integer ContactId;
  private String Label;
  private String Type; 
  private Integer Primary; 
  private String OrgName; 
  private String Department; 
  private String OrgTitle;
  private String StartDate;
  private String endDate;
  private String jobStatus;
  private String Location; 
  private String Description; 
  private String Industry; 
  private String Source; 
  private Integer InsertDate;
  private String accountSrc;

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }
    
    
    public String getAccountSrc() {
        return accountSrc;
    }

    public void setAccountSrc(String accountSrc) {
        this.accountSrc = accountSrc;
    }
  
    public Integer getContactId() {
        return ContactId;
    }

    public void setContactId(Integer ContactId) {
        this.ContactId = ContactId;
    }

    public String getDepartment() {
        return Department;
    }

    public void setDepartment(String Department) {
        this.Department = Department;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public String getIndustry() {
        return Industry;
    }

    public void setIndustry(String Industry) {
        this.Industry = Industry;
    }

    public Integer getInsertDate() {
        return InsertDate;
    }

    public void setInsertDate(Integer InsertDate) {
        this.InsertDate = InsertDate;
    }

    public String getLabel() {
        return Label;
    }

    public void setLabel(String Label) {
        this.Label = Label;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String Location) {
        this.Location = Location;
    }

    public Integer getOrgID() {
        return OrgID;
    }

    public void setOrgID(Integer OrgID) {
        this.OrgID = OrgID;
    }

    public String getOrgName() {
        return OrgName;
    }

    public void setOrgName(String OrgName) {
        this.OrgName = OrgName;
    }

    public String getOrgTitle() {
        return OrgTitle;
    }

    public void setOrgTitle(String OrgTitle) {
        this.OrgTitle = OrgTitle;
    }

    public Integer getPrimary() {
        return Primary;
    }

    public void setPrimary(Integer Primary) {
        this.Primary = Primary;
    }

    public String getSource() {
        return Source;
    }

    public void setSource(String Source) {
        this.Source = Source;
    }

    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String StartDate) {
        this.StartDate = StartDate;
    }

    public String getType() {
        return Type;
    }

    public void setType(String Type) {
        this.Type = Type;
    }

    public Integer getUserId() {
        return UserId;
    }

    public void setUserId(Integer UserId) {
        this.UserId = UserId;
    }
  
  
  
    
}
