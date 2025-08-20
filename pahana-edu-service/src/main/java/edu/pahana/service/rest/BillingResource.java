package edu.pahana.service.rest;

import edu.pahana.service.dao.BillDAO;
import edu.pahana.service.model.Bill;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/billing")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BillingResource {

    @POST
    @Path("/create")
    public Response create(Bill bill) {
        try {
            int id = new BillDAO().create(bill);
            bill.setId(id);
            return Response.status(Response.Status.CREATED).entity(bill).build();
        } catch (Exception e) {
            String msg = e.getMessage() == null ? "Server error" : e.getMessage().replace("\"", "\\\"");
            return Response.serverError().entity("{\"error\":\"" + msg + "\"}").build();
        }
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") int id) {
        try {
            BillDAO dao = new BillDAO();
            Bill b = dao.get(id); // ensure BillDAO has a get(int id) method as added in Step 5
            if (b == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Bill not found\"}")
                        .build();
            }
            return Response.ok(b).build();
        } catch (Exception e) {
            String msg = e.getMessage() == null ? "Server error" : e.getMessage().replace("\"", "\\\"");
            return Response.serverError().entity("{\"error\":\"" + msg + "\"}").build();
        }
    }
}
