package com.taskforce.moneyapp.services;

import com.taskforce.moneyapp.dao.DAOFactory;
import com.taskforce.moneyapp.exception.DAOException;
import com.taskforce.moneyapp.objectModel.UserTransaction;
import com.taskforce.moneyapp.utilities.MoneyUtil;
import org.apache.log4j.Logger;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/money-app/transaction")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionService {
    private final DAOFactory daoFactory = DAOFactory.getDAOFactory(DAOFactory.H2);
    static Logger log = Logger.getLogger(TransactionService.class);

    @POST
    public void transferFund(UserTransaction transaction) throws DAOException {

        if(log.isDebugEnabled())
            log.debug("TransferFund invoked with parameter : " + transaction);

        String currency = transaction.getCurrencyCode();
        if (MoneyUtil.validateCcyCode(currency)) {
            daoFactory.getAccountDAO().transferAccountBalance(transaction);

        } else {
            throw new WebApplicationException("Currency Code Invalid ", Response.Status.BAD_REQUEST);
        }


    }

}
