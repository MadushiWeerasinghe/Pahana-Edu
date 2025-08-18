package edu.pahana.service.rest;

import edu.pahana.service.dao.CustomerDAO;
import edu.pahana.service.dto.PagedResult;
import edu.pahana.service.model.Customer;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {

    private boolean isAdmin(String auth){ return parseRole(auth).equals("ADMIN"); }
    private String parseRole(String auth){
        if(auth==null) return "";
        try{
            String decoded = new String(java.util.Base64.getDecoder().decode(auth.replace("Bearer ","")));
            String[] parts = decoded.split(":");
            return parts.length>1?parts[1]:"";
        }catch(Exception e){ return ""; }
    }

    // ---------------- Existing endpoints ----------------

    @GET
    public Response list(@HeaderParam("Authorization") String auth) {
        try{
            List<Customer> all = new CustomerDAO().list();
            return Response.ok(all).build();
        }catch(Exception e){
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") int id){
        try{
            Customer c = new CustomerDAO().get(id);
            if(c==null) return Response.status(404).build();
            return Response.ok(c).build();
        }catch(Exception e){
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    public Response create(Customer c, @HeaderParam("Authorization") String auth){
        if(!isAdmin(auth)) return Response.status(Response.Status.FORBIDDEN).build();
        try{
            int id = new CustomerDAO().create(c);
            c.setId(id);
            return Response.status(201).entity(c).build();
        }catch(Exception e){
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") int id, Customer c, @HeaderParam("Authorization") String auth){
        if(!isAdmin(auth)) return Response.status(Response.Status.FORBIDDEN).build();
        try{
            c.setId(id);
            boolean ok = new CustomerDAO().update(c);
            if(!ok) return Response.status(404).build();
            return Response.ok(c).build();
        }catch(Exception e){
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") int id, @HeaderParam("Authorization") String auth){
        if(!isAdmin(auth)) return Response.status(Response.Status.FORBIDDEN).build();
        try{
            boolean ok = new CustomerDAO().delete(id);
            if(!ok) return Response.status(404).build();
            return Response.noContent().build();
        }catch(Exception e){
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    // ---------------- New: paged + search endpoint ----------------

    /**
     * Paged listing with optional search.
     * Example:
     *   GET /customers/page?q=nimal&page=1&size=10
     */
    @GET
    @Path("/page")
    public Response page(@QueryParam("q") String q,
                         @DefaultValue("1") @QueryParam("page") int page,
                         @DefaultValue("10") @QueryParam("size") int size) {
        try {
            if (page < 1) page = 1;
            if (size < 1) size = 10;
            int offset = (page - 1) * size;

            CustomerDAO dao = new CustomerDAO();
            int total = dao.count(q);
            List<Customer> data = dao.listPaged(q, offset, size);

            PagedResult<Customer> pr = new PagedResult<>(total, page, size, data);
            return Response.ok(pr).build();
        } catch (Exception e) {
            String msg = e.getMessage() == null ? "Server error" : e.getMessage().replace("\"","\\\"");
            return Response.serverError().entity("{\"error\":\"" + msg + "\"}").build();
        }
    }
}
