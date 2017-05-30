/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.tygabytes.deduping.modelinterface;

/**
 *
 * @author Claudio
 */
public interface IPostalAddress {

    public Integer getPaID();
    public Integer getContactId();
    public Integer getUserId();
    public String getLabel();
    public String getType();
    public Integer getPrimary();
    public String getText();
    public String getSource();
    public Integer getInsertDate();
    public Integer getActive();


    public void  setPaID(Integer palID);
    public void   setContactId(Integer contactId);
    public void   setUserId(Integer userId);
    public void   setLabel(String label);
    public void   setType(String type);
    public void   setPrimary(Integer primary);
    public void   setText(String text);
    public void   setSource(String source);
    public void   setInsertDate(Integer insertDate);
    public void   setActive(Integer active);
}
