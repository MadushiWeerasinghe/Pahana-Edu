package edu.pahana.web.servlets;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.pahana.web.util.ServiceClient;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class ReportsServlet extends HttpServlet {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession ses = req.getSession(false);
        String role = ses == null ? null : (String) ses.getAttribute("role");
        String token = ses == null ? null : (String) ses.getAttribute("token");
        if (token == null || role == null) {
            resp.sendRedirect(req.getContextPath()+"/login.jsp");
            return;
        }
        if (!"ADMIN".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String tab = req.getParameter("tab");
        if (tab == null || tab.isBlank()) tab = "customers";

        try {
            switch (tab) {
                case "customers": {
                    String filter = defaultStr(req.getParameter("filter"), "all");
                    int days = parseInt(req.getParameter("days"), 30);
                    String qs = "?filter=" + url(filter) + "&days=" + days;
                    String json = ServiceClient.getJson("/reports/customers" + qs, token, String.class);
                    List<Map<String,Object>> rows = MAPPER.readValue(json, new TypeReference<List<Map<String,Object>>>(){});
                    req.setAttribute("customersRows", rows);
                    break;
                }
                case "billing": {
                    String account = req.getParameter("account");
                    if (account != null && !account.isBlank()) {
                        String qs = "?account=" + url(account.trim());
                        String json = ServiceClient.getJson("/reports/billing/customer" + qs, token, String.class);
                        List<List<Object>> bills = MAPPER.readValue(json, new TypeReference<List<List<Object>>>(){});
                        req.setAttribute("customerBills", bills);
                        req.setAttribute("account", account.trim());
                    }
                    String from = req.getParameter("from");
                    String to   = req.getParameter("to");
                    if (from != null && !from.isBlank() && to != null && !to.isBlank()) {
                        String qs = "?from=" + url(from) + "&to=" + url(to);
                        String json = ServiceClient.getJson("/reports/billing/summary" + qs, token, String.class);
                        Map<String,Object> summary = MAPPER.readValue(json, new TypeReference<Map<String,Object>>(){});
                        req.setAttribute("billSummary", summary);
                        req.setAttribute("from", from);
                        req.setAttribute("to", to);
                    }
                    break;
                }
                case "revenue": {
    String gran = defaultStr(req.getParameter("granularity"), "day");
    String from = req.getParameter("from");
    String to   = req.getParameter("to");
    boolean run = "1".equals(req.getParameter("run")); // only execute when user clicks Run

    // Keep the current selections in the form
    req.setAttribute("granularity", gran);
    req.setAttribute("from", from == null ? "" : from);
    req.setAttribute("to",   to   == null ? "" : to);

    if (run) {
        String qs = "?granularity=" + url(gran);
        if (from != null && !from.isBlank()) qs += "&from=" + url(from);
        if (to   != null && !to.isBlank())   qs += "&to="   + url(to);

        String json = ServiceClient.getJson("/reports/revenue" + qs, token, String.class);
        List<Map<String,Object>> data = MAPPER.readValue(json, new TypeReference<List<Map<String,Object>>>(){});
        req.setAttribute("revenueData", data);
    }
    break;
}

            }
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
        }
        req.setAttribute("tab", tab);
        req.getRequestDispatcher("/app/reports.jsp").forward(req, resp);
    }

    private static String url(String s){ return URLEncoder.encode(s, StandardCharsets.UTF_8); }
    private static int parseInt(String s, int def){ try { return Integer.parseInt(s); } catch (Exception e) { return def; } }
    private static String defaultStr(String s, String def){ return (s==null || s.isBlank())? def: s; }
}
