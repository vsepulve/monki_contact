/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.tygabytes.deduping.dto;

import cl.tygabytes.deduping.modelinterface.IEmail;
import com.mysql.jdbc.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Claudio
 */
public interface IUEmailDTO {
    public void create(IEmail email) throws SQLException;

    public void delete(IEmail email) throws SQLException;

    public void update(IEmail email) throws SQLException;

    public IEmail retrieve(IEmail email) throws SQLException;

    public List retrieveAll() throws SQLException;

    public List retrieveByAddress(IEmail email, Connection conn) throws SQLException;
}
