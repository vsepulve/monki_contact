/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.tygabytes.deduping.dto.mysql;

import cl.tygabytes.deduping.dto.IUEmailDTO;
import cl.tygabytes.deduping.modelinterface.IContact;
import cl.tygabytes.deduping.modelinterface.IEmail;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Claudio
 */
class EmailMySqlDTO implements IUEmailDTO{

    public EmailMySqlDTO() {
    }

    public void create(IEmail email) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void delete(IEmail email) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void update(IEmail email) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public IEmail retrieve(IEmail email) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List retrieveAll() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List retrieveByAddress(IEmail email, Connection conn) throws SQLException {
        // TODO: Impelmentar la búsqueda de del usuario
        List<Integer> contacts=null;
        Statement stmt = (Statement) conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from email where address="+email.getAddress()+"'" );
        while (rs.next()){
            contacts.add(rs.getInt("contactId"));
            System.out.println("Current contactId:"+rs.getInt("contactId"));
        }
        return contacts;
    }

}
