/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.tygabytes.deduping.dto;

import cl.tygabytes.deduping.modelinterface.IPostalAddress;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Claudio
 */
public interface IUPostalAddressDTO {
    public void create(IPostalAddress postalAddress) throws SQLException;

    public void delete(IPostalAddress postalAddress) throws SQLException;

    public void update(IPostalAddress postalAddress) throws SQLException;

    public IPostalAddress retrieve(IPostalAddress postalAddress) throws SQLException;

    public List retrieveAll() throws SQLException;
}
