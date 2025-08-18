package edu.pahana.web.servlets;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.pahana.web.util.ServiceClient;
import edu.pahana.web.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CustomerServlet extends HttpServlet {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // -------- Helpers --------
    // Load one customer (for edit)
    private Map<String,Object> fetchCustomer(String token, String id) throws Exception {
        String one = ServiceClient.getJson("/customers/" + id, token, String.class);
        return MAPPER.readValue(one, new TypeReference<Map<String,Object>>(){});
    }

    // Load a paged list from the service: /customers/page?q=&page=&size=
    private Map<String,Object> fetchCustomersPage(String token, String q, int page, int size) throws Exception {
        if (page < 1) page = 1;
        if (size < 1) size = 10;
        String qs = "?page=" + page + "&size=" + size;
        if (q != null && !q.isBlank()) {
            qs += "&q=" + URLEncoder.encode(q, StandardCharsets.UTF_8);
        }
        String json = ServiceClient.getJson("/customers/page" + qs, token, String.class);
        return MAPPER.readValue(json, new TypeReference<Map<String,Object>>(){});
    }

    // Set attributes expected by customers.jsp (q, page, size, total, customers)
    @SuppressWarnings("unchecked")
    private void loadPageIntoRequest(HttpServletRequest req, String token) throws Exception {
        String q = req.getParameter("q");
        int page = 1, size = 10;
        try { page = Integer.parseInt(req.getParameter("page")); } catch (Exception ignored) {}
        try { size = Integer.parseInt(req.getParameter("size")); } catch (Exception ignored) {}

        Map<String,Object> pr = fetchCustomersPage(token, q, page, size);
        req.setAttribute("q", q == null ? "" : q);
        req.setAttribute("page", ((Number) pr.get("page")).intValue());
        req.setAttribute("size", ((Number) pr.get("size")).intValue());
        req.setAttribute("total", ((Number) pr.get("total")).intValue());
        req.setAttribute("customers", (List<Map<String,Object>>) pr.get("data"));
    }

    // -------- HTTP --------
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = (String) req.getSession().getAttribute("token");
        String action = req.getParameter("action");
        try {
            // load paged list + search
            loadPageIntoRequest(req, token);

            // if editing, load one customer as well
            if ("edit".equalsIgnoreCase(action)) {
                String id = req.getParameter("id");
                if (id != null && !id.isBlank()) {
                    req.setAttribute("editCustomer", fetchCustomer(token, id));
                }
            }
            req.getRequestDispatcher("/app/customers.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession ses = req.getSession();
        String role  = (String) ses.getAttribute("role");
        String token = (String) ses.getAttribute("token");

        if (!"ADMIN".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String action = req.getParameter("action");
        try {
            if ("create".equalsIgnoreCase(action)) {
                String accountNumber = req.getParameter("accountNumber");
                String name          = req.getParameter("name");
                String address       = req.getParameter("address");
                String phone         = req.getParameter("phone");

                Map<String,String> errors = Validator.validateCustomer(accountNumber, name, address, phone);
                if (!errors.isEmpty()) {
                    req.setAttribute("errors", errors);
                    Map<String,Object> formData = new HashMap<>();
                    formData.put("accountNumber", accountNumber);
                    formData.put("name", name);
                    formData.put("address", address);
                    formData.put("phone", phone);
                    req.setAttribute("formData", formData);

                    // keep current page/search context
                    loadPageIntoRequest(req, token);
                    req.getRequestDispatcher("/app/customers.jsp").forward(req, resp);
                    return;
                }

                Map<String,Object> body = new HashMap<>();
                body.put("accountNumber", accountNumber);
                body.put("name", name);
                body.put("address", address);
                body.put("phone", phone);
                ServiceClient.postJson("/customers", body, token, String.class);

                // redirect to the listing (page/query not preserved intentionally)
                resp.sendRedirect(req.getContextPath() + "/app/customers");
                return;

            } else if ("update".equalsIgnoreCase(action)) {
                String id            = req.getParameter("id");
                String accountNumber = req.getParameter("accountNumber");
                String name          = req.getParameter("name");
                String address       = req.getParameter("address");
                String phone         = req.getParameter("phone");

                Map<String,String> errors = Validator.validateCustomer(accountNumber, name, address, phone);
                if (!errors.isEmpty()) {
                    req.setAttribute("errorsEdit", errors);
                    Map<String,Object> editCustomer = new HashMap<>();
                    editCustomer.put("id", Integer.parseInt(id));
                    editCustomer.put("accountNumber", accountNumber);
                    editCustomer.put("name", name);
                    editCustomer.put("address", address);
                    editCustomer.put("phone", phone);
                    req.setAttribute("editCustomer", editCustomer);

                    // keep current page/search context
                    loadPageIntoRequest(req, token);
                    req.getRequestDispatcher("/app/customers.jsp").forward(req, resp);
                    return;
                }

                Map<String,Object> body = new HashMap<>();
                body.put("accountNumber", accountNumber);
                body.put("name", name);
                body.put("address", address);
                body.put("phone", phone);
                ServiceClient.putJson("/customers/" + id, body, token, String.class);

                resp.sendRedirect(req.getContextPath() + "/app/customers");
                return;

            } else if ("delete".equalsIgnoreCase(action)) {
                String id = req.getParameter("id");
                ServiceClient.delete("/customers/" + id, token, String.class);
                resp.sendRedirect(req.getContextPath() + "/app/customers");
                return;

            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action");
            }
        } catch (Exception e) {
            // Show a friendly message on the same page and keep the list/pager
            req.setAttribute("error", e.getMessage());
            try { loadPageIntoRequest(req, token); } catch (Exception ignored) {}
            req.getRequestDispatcher("/app/customers.jsp").forward(req, resp);
        }
    }
}
