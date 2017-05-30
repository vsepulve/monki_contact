/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.tygabytes.deduping.model;

import cl.tygabytes.deduping.modelinterface.IIm;

/**
 *
 * @author claudio
 */
public class Im implements IIm{
    
  private Integer ImID;
  private Integer ContactId;
  private Integer UserId;
  private String Address; 
  private String Label;
  private String Type;
  private String Protocol; 
  private Integer Primary; 
  private String Source;
  private Integer InsertDate;
  private String accountSrc;
    
    public String getAccountSrc() {
        return accountSrc;
    }

    public void setAccountSrc(String accountSrc) {
        this.accountSrc = accountSrc;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String Address) {
        this.Address = Address;
    }

    public Integer getContactId() {
        return ContactId;
    }

    public void setContactId(Integer ContactId) {
        this.ContactId = ContactId;
    }

    public Integer getImID() {
        return ImID;
    }

    public void setImID(Integer ImID) {
        this.ImID = ImID;
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

    public Integer getPrimary() {
        return Primary;
    }

    public void setPrimary(Integer Primary) {
        this.Primary = Primary;
    }

    public String getProtocol() {
        return Protocol;
    }

    public void setProtocol(String Protocol) {
        this.Protocol = Protocol;
    }

    public String getSource() {
        return Source;
    }

    public void setSource(String Source) {
        this.Source = Source;
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
