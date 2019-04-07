package com.aukustomx.simpleapp.user.rest;

import com.aukustomx.simpleapp.user.model.UserRequest;
import com.aukustomx.simpleapp.user.service.UserService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static com.aukustomx.simpleapp.common.rest.ResponseUtils.responseOf;

@Path("/users")
public class UserEndpoint {

    @Inject
    private UserService userService;

    @GET
    public Response all() {
        return responseOf(userService.users());
    }

    @GET
    @Path("/{id}")
    public Response byId(@PathParam("id") int id) {
        return responseOf(userService.byId(id));
    }

    @POST
    public Response add(@Valid UserRequest req) {
        return responseOf(userService.add(req));
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") int id) {
        return responseOf(userService.delete(id));
    }
}
