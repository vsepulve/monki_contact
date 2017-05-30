/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.tygabytes.deduping.dto.mysql;

import cl.tygabytes.deduping.dto.IUPhoneNumberDTO;
import cl.tygabytes.deduping.modelinterface.IPhoneNumber;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Claudio
 */
class PhoneNumberMySqlDTO implements IUPhoneNumberDTO {

    public PhoneNumberMySqlDTO() {
    }

    public void create(IPhoneNumber phoneNumber) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void delete(IPhoneNumber phoneNumber) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void update(IPhoneNumber phoneNumber) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public IPhoneNumber retrieve(IPhoneNumber phoneNumber) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List retrieveAll() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
