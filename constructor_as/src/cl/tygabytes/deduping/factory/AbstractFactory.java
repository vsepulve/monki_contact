/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.tygabytes.deduping.factory;

import cl.tygabytes.deduping.modelinterface.IContact;
import cl.tygabytes.deduping.modelinterface.IEmail;
import cl.tygabytes.deduping.modelinterface.INames;
import cl.tygabytes.deduping.modelinterface.IPhoneNumber;
import cl.tygabytes.deduping.modelinterface.IPostalAddress;

/**
 *
 * @author Claudio
 */
public abstract class AbstractFactory {

    public abstract IContact getContact();
    public abstract IEmail getEmail();
    public abstract INames getNames();
    public abstract IPhoneNumber getPhoneNumber();
    public abstract IPostalAddress getPostalAddress();


    public static AbstractFactory getAbstractFactory(){
        return null;
    }

}
