package edu.pahana.service.rest;

import edu.pahana.service.dao.ReportDAO;
import edu.pahana.service.dto.AggregatePoint;
import edu.pahana.service.dto.BillSummaryResponse;
import edu.pahana.service.dto.CustomerReportRow;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.List;

@Path("/reports")
@Produces(MediaType.APPLICATION_JSON)
public class ReportResource {

    @GET
    @Path("/customers")
    public Response customers(@QueryParam("filter") @DefaultValue("all") String filter,
                              @QueryParam("days") @DefaultValue("30") int days){
        try{
            ReportDAO dao = new ReportDAO();
            List<CustomerReportRow> rows;
            switch (filter.toLowerCase()){
                case "new":
                case "newly":
                case "newly_registered":
                    rows = dao.customersNewlyRegistered(days); break;
                case "active":
                    rows = dao.customersActiveWithinDays(days); break;
                default:
                    rows = dao.customersAll();
            }
            return Response.ok(rows).build();
        }catch(Exception e){
            String msg = e.getMessage()==null? "Server error": e.getMessage().replace("\"", "\\\"");
            return Response.serverError().entity("{\"error\":\""+msg+"\"}").build();
        }
    }

    @GET
    @Path("/billing/customer")
    public Response billsByAccount(@QueryParam("account") String accountNumber){
        if(accountNumber == null || accountNumber.isBlank()){
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"account is required\"}").build();
        }
        try{
            ReportDAO dao = new ReportDAO();
            return Response.ok(dao.billsByAccount(accountNumber)).build();
        }catch(Exception e){
            String msg = e.getMessage()==null? "Server error": e.getMessage().replace("\"", "\\\"");
            return Response.serverError().entity("{\"error\":\""+msg+"\"}").build();
        }
    }

    @GET
    @Path("/billing/summary")
    public Response billingSummary(@QueryParam("from") String from,
                                   @QueryParam("to") String to){
        try{
            LocalDate f = (from==null || from.isBlank()) ? LocalDate.now().minusDays(7) : LocalDate.parse(from);
            LocalDate t = (to==null || to.isBlank()) ? LocalDate.now() : LocalDate.parse(to);
            ReportDAO dao = new ReportDAO();
            BillSummaryResponse resp = dao.summary(f, t);
            return Response.ok(resp).build();
        }catch(Exception e){
            String msg = e.getMessage()==null? "Server error": e.getMessage().replace("\"", "\\\"");
            return Response.serverError().entity("{\"error\":\""+msg+"\"}").build();
        }
    }

    @GET
    @Path("/revenue")
    public Response revenue(@QueryParam("granularity") @DefaultValue("day") String granularity,
                            @QueryParam("from") String from,
                            @QueryParam("to") String to){
        try{
            LocalDate f = (from==null || from.isBlank()) ? LocalDate.now() : LocalDate.parse(from);
            LocalDate t = (to==null || to.isBlank()) ? f : LocalDate.parse(to);
            ReportDAO dao = new ReportDAO();
            return Response.ok(dao.revenue(f, t, granularity)).build();
        }catch(Exception e){
            String msg = e.getMessage()==null? "Server error": e.getMessage().replace("\"", "\\\"");
            return Response.serverError().entity("{\"error\":\""+msg+"\"}").build();
        }
    }
}
