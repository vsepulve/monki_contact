/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.tygabytes.deduping.dto;

import cl.tygabytes.deduping.dto.mysql.MySqlDAOFactory;
/**
 *
 * @author Claudio
 */
public abstract class DAOFactory {

    public abstract IUContactDTO getContactDTO();
    public abstract IUEmailDTO getEmailDTO();
    public abstract IUNamesDTO getNamesDTO();
    public abstract IUPhoneNumberDTO getPhoneNumberDTO();
    public abstract IUPostalAddressDTO getPostalAddressDTO();

    public static DAOFactory getDAOFactory(){
        return new MySqlDAOFactory();
    }

}
