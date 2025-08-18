package edu.pahana.service.rest;

import edu.pahana.service.dao.ItemDAO;
import edu.pahana.service.dto.PagedResult;
import edu.pahana.service.model.Item;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItemResource {

    private boolean isAdmin(String auth){ return parseRole(auth).equals("ADMIN"); }
    private String parseRole(String auth){
        if(auth==null) return "";
        try{
            String decoded = new String(java.util.Base64.getDecoder().decode(auth.replace("Bearer ","")));
            String[] parts = decoded.split(":");
            return parts.length>1?parts[1]:"";
        }catch(Exception e){ return ""; }
    }

    @GET
    public Response list(){
        try{
            List<Item> all = new ItemDAO().list();
            return Response.ok(all).build();
        }catch(Exception e){
            return Response.serverError().entity(jsonError(e.getMessage())).build();
        }
    }

    // NEW: paged + searchable endpoint
    // GET /items/page?q=math&page=1&size=10
    @GET
    @Path("/page")
    public Response page(@QueryParam("q") String q,
                         @DefaultValue("1") @QueryParam("page") int page,
                         @DefaultValue("10") @QueryParam("size") int size){
        try{
            if(page < 1) page = 1;
            if(size < 1) size = 10;
            int offset = (page - 1) * size;

            ItemDAO dao = new ItemDAO();
            int total = dao.count(q);
            List<Item> data = dao.listPaged(q, offset, size);

            PagedResult<Item> pr = new PagedResult<>(total, page, size, data);
            return Response.ok(pr).build();
        }catch(Exception e){
            return Response.serverError().entity(jsonError(e.getMessage())).build();
        }
    }

    @GET @Path("/{id}")
    public Response get(@PathParam("id") int id){
        try{
            Item i = new ItemDAO().get(id);
            if(i==null) return Response.status(404).build();
            return Response.ok(i).build();
        }catch(Exception e){
            return Response.serverError().entity(jsonError(e.getMessage())).build();
        }
    }

    @POST
    public Response create(Item i, @HeaderParam("Authorization") String auth){
        if(!isAdmin(auth)) return Response.status(Response.Status.FORBIDDEN).build();
        try{
            int id = new ItemDAO().create(i);
            i.setId(id);
            return Response.status(201).entity(i).build();
        }catch(Exception e){
            return Response.serverError().entity(jsonError(e.getMessage())).build();
        }
    }

    @PUT @Path("/{id}")
    public Response update(@PathParam("id") int id, Item i, @HeaderParam("Authorization") String auth){
        if(!isAdmin(auth)) return Response.status(Response.Status.FORBIDDEN).build();
        try{
            i.setId(id);
            boolean ok = new ItemDAO().update(i);
            if(!ok) return Response.status(404).build();
            return Response.ok(i).build();
        }catch(Exception e){
            return Response.serverError().entity(jsonError(e.getMessage())).build();
        }
    }

    @DELETE @Path("/{id}")
    public Response delete(@PathParam("id") int id, @HeaderParam("Authorization") String auth){
        if(!isAdmin(auth)) return Response.status(Response.Status.FORBIDDEN).build();
        try{
            boolean ok = new ItemDAO().delete(id);
            if(!ok) return Response.status(404).build();
            return Response.noContent().build();
        }catch(Exception e){
            return Response.serverError().entity(jsonError(e.getMessage())).build();
        }
    }

    private String jsonError(String msg){
        if(msg == null) msg = "Server error";
        return "{\"error\":\"" + msg.replace("\"","\\\"") + "\"}";
    }
}
