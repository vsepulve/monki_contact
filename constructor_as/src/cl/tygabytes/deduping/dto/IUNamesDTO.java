/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.tygabytes.deduping.dto;

import cl.tygabytes.deduping.modelinterface.INames;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Claudio
 */
public interface IUNamesDTO {
    public void create(INames names) throws SQLException;

    public void delete(INames names) throws SQLException;

    public void update(INames names) throws SQLException;

    public INames retrieve(INames names) throws SQLException;

    public List retrieveAll() throws SQLException;
}
