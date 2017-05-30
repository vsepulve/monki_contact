/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.tygabytes.deduping.dto.mysql;

import cl.tygabytes.deduping.dto.IUNamesDTO;
import cl.tygabytes.deduping.modelinterface.INames;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Claudio
 */
class NamesMySqlDTO implements IUNamesDTO {

    public NamesMySqlDTO() {
    }

    public void create(INames names) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void delete(INames names) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void update(INames names) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public INames retrieve(INames names) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List retrieveAll() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
