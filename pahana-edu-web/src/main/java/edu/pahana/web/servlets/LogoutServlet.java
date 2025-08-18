package edu.pahana.web.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;

public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession s = req.getSession(false);
        if (s != null) s.invalidate();

        // Prevent caching of protected pages after logout
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP/1.1
        resp.setHeader("Pragma", "no-cache"); // HTTP/1.0
        resp.setDateHeader("Expires", 0); // Proxies

        resp.sendRedirect(req.getContextPath() + "/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}
