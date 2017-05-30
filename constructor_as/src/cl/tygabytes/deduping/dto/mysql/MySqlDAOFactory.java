/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.tygabytes.deduping.dto.mysql;

import cl.tygabytes.deduping.dto.DAOFactory;
import cl.tygabytes.deduping.dto.IUContactDTO;
import cl.tygabytes.deduping.dto.IUEmailDTO;
import cl.tygabytes.deduping.dto.IUNamesDTO;
import cl.tygabytes.deduping.dto.IUPhoneNumberDTO;
import cl.tygabytes.deduping.dto.IUPostalAddressDTO;

/**
 *
 * @author Claudio
 */
public class MySqlDAOFactory extends DAOFactory{

    @Override
    public IUContactDTO getContactDTO() {
        return new ContactMySqlDTO();
    }

    @Override
    public IUEmailDTO getEmailDTO() {
        return new EmailMySqlDTO();
    }

    @Override
    public IUNamesDTO getNamesDTO() {
        return new NamesMySqlDTO();
    }

    @Override
    public IUPhoneNumberDTO getPhoneNumberDTO() {
        return new PhoneNumberMySqlDTO();
    }

    @Override
    public IUPostalAddressDTO getPostalAddressDTO() {
        return new PostalAddressMySqlDTO();
    }

}
