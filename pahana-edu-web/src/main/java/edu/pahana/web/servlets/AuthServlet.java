package edu.pahana.web.servlets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.pahana.web.util.ServiceClient;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AuthServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        Map<String,String> payload = new HashMap<>();
        payload.put("username", username);
        payload.put("password", password);

        try{
            String json = ServiceClient.postJson("/auth/login", payload, null, String.class);
            JsonNode node = new ObjectMapper().readTree(json);
            String token = node.get("token").asText();
            String role = node.get("role").asText();
            HttpSession ses = req.getSession(true);
            ses.setAttribute("token", token);
            ses.setAttribute("role", role);
            ses.setAttribute("username", node.get("username").asText());
            resp.sendRedirect(req.getContextPath()+"/app/dashboard.jsp");
        }catch(Exception e){
            req.setAttribute("error", "Login failed: " + e.getMessage());
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }
}
