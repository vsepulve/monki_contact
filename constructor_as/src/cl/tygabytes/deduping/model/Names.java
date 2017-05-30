/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.tygabytes.deduping.model;

import cl.tygabytes.deduping.modelinterface.INames;

/**
 *
 * @author Claudio
 */
public class Names implements INames{
    private Integer nameId;
    private Integer contactId;
    private Integer userId;
    private String fullName;
    private String firstName;
    private String lastName;
    private String source;
    private Integer insertDate;
    private Integer active;
    private String accountSrc;
    
    public String getAccountSrc() {
        return accountSrc;
    }

    public void setAccountSrc(String accountSrc) {
        this.accountSrc = accountSrc;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public Integer getContactId() {
        return contactId;
    }

    public void setContactId(Integer contactId) {
        this.contactId = contactId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(Integer insertDate) {
        this.insertDate = insertDate;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getNameId() {
        return nameId;
    }

    public void setNameId(Integer nameId) {
        this.nameId = nameId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }


}
