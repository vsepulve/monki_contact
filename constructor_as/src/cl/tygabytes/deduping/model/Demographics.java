/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.tygabytes.deduping.model;

import cl.tygabytes.deduping.modelinterface.IDemographics;

/**
 *
 * @author claudio
 */
public class Demographics implements IDemographics{
    private Integer demoId;
    private Integer userId;
    private String accountSrc;
    private Integer contactId;
    private String hometown;
    private String location;
    private String gender;
    private String website;
    private String source;

    public String getAccountSrc() {
        return accountSrc;
    }

    public void setAccountSrc(String accountSrc) {
        this.accountSrc = accountSrc;
    }

    public Integer getContactId() {
        return contactId;
    }

    public void setContactId(Integer contactId) {
        this.contactId = contactId;
    }

    public Integer getDemoId() {
        return demoId;
    }

    public void setDemoId(Integer demoId) {
        this.demoId = demoId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
    
    
}
