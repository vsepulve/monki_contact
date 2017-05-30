/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.tygabytes.deduping.modelinterface;

/**
 *
 * @author Claudio
 */
public interface IContact {
    public Integer getContactId();
    public Integer getUserId();
    public String getSrcId();
    public String getSrcName();
    public String getTitle();
    public String getLink();
    public String getAllText();
    public Integer getActive();

    public void setContactId(Integer contactId);
    public void setUserId(Integer userId);
    public void setSrcId(String srcId);
    public void setSrcName(String srcName);
    public void setTitle(String title);
    public void setLink(String link);
    public void setAllText(String allText);
    public void setActive(Integer active);
}
