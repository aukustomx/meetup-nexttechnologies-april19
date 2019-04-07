package com.aukustomx.simpleapp.rest;


import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;


@Path("/hello")
public class HelloWorldEndpoint {

	@GET
	@Produces("text/plain")
	public Response doGet() {
		return Response.ok("Hello from Thorntail!").build();
	}

	@GET
	@Path("/{name}")
	@Produces("text/plain")
	public Response greeting(@PathParam("name") String name) {
		return Response
				.ok(name + ", welcome to Thorntail Journey!")
				.build();
	}
}
