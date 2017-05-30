/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.tygabytes.deduping.factory.dedupingfactory;

import cl.tygabytes.deduping.factory.AbstractFactory;
import cl.tygabytes.deduping.modelinterface.IContact;
import cl.tygabytes.deduping.modelinterface.IEmail;
import cl.tygabytes.deduping.modelinterface.INames;
import cl.tygabytes.deduping.modelinterface.IPhoneNumber;
import cl.tygabytes.deduping.modelinterface.IPostalAddress;

/**
 *
 * @author Claudio
 */
public class DedupingFactory extends AbstractFactory {

    @Override
    public IContact getContact() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IEmail getEmail() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public INames getNames() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IPhoneNumber getPhoneNumber() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IPostalAddress getPostalAddress() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
