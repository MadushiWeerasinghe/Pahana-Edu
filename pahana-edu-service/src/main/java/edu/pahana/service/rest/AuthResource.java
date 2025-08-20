package edu.pahana.service.rest;

import edu.pahana.service.dao.UserDAO;
import edu.pahana.service.dto.LoginRequest;
import edu.pahana.service.dto.LoginResponse;
import edu.pahana.service.model.User;
import edu.pahana.service.util.PasswordUtil;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Base64;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @POST
    @Path("/login")
    public Response login(LoginRequest req) {
        try {
            UserDAO dao = new UserDAO();
            User u = dao.findByUsername(req.username);
            if (u != null && PasswordUtil.verify(req.password, u.getPasswordHash())) {
                // Simple token (base64 username:role), NOT for production
                String token = Base64.getEncoder()
                        .encodeToString((u.getUsername() + ":" + u.getRole()).getBytes());
                return Response.ok(new LoginResponse(token, u.getRole(), u.getUsername())).build();
            }
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"Invalid credentials\"}")
                    .build();
        } catch (Exception e) {
            String msg = e.getMessage() == null ? "Server error" : e.getMessage().replace("\"", "\\\"");
            return Response.serverError()
                    .entity("{\"error\":\"" + msg + "\"}")
                    .build();
        }
    }
}
