package com.taskforce.moneyapp.services;

import com.taskforce.moneyapp.exception.DAOErrorResponse;
import com.taskforce.moneyapp.exception.DAOException;
import org.apache.log4j.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class ServiceExceptionMapper implements
        ExceptionMapper<DAOException> {
    private static Logger log = Logger.getLogger(ServiceExceptionMapper.class);
    public ServiceExceptionMapper() {
    }

    public Response toResponse(
            DAOException daoException) {
        if(log.isDebugEnabled()){
            log.debug("Mapping exception to Response....");
        }
        DAOErrorResponse errorResponse = new DAOErrorResponse();
        errorResponse.setErrorCode(daoException.getMessage());

        //return internal server error for DAO exceptions
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                errorResponse).type(
                MediaType.APPLICATION_JSON).build();

    }

}


