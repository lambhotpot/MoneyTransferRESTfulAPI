package com.taskforce.moneyapp.services;

import com.taskforce.moneyapp.dao.DAOFactory;
import com.taskforce.moneyapp.objectModel.User;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;




@Path("/money-app/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserService {
    private final DAOFactory daoFactory = DAOFactory.getDAOFactory(DAOFactory.H2);
    static Logger log = Logger.getLogger(UserService.class);


    @GET
    @Path("/{username}")
    public User get(@PathParam("username") String username) {
        log.debug("Request Received for get User "+username);
        final User user = daoFactory.getUserDAO().getUserByName(username);
        if(user == null) {
            throw new WebApplicationException("User Not Found", Response.Status.NOT_FOUND);
        }
        return user;
    }


}
