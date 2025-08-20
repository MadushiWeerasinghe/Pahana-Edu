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

public class ItemServlet extends HttpServlet {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // Existing list (used as a fallback for POST error redisplay)
    private List<Map<String,Object>> fetchItems(String token) throws Exception {
        String json = ServiceClient.getJson("/items", token, String.class);
        return MAPPER.readValue(json, new TypeReference<List<Map<String,Object>>>(){});
    }

    private Map<String,Object> fetchItem(String token, String id) throws Exception {
        String one = ServiceClient.getJson("/items/" + id, token, String.class);
        return MAPPER.readValue(one, new TypeReference<Map<String,Object>>(){});
    }

    // NEW: paged fetch for search + pagination
    private Map<String,Object> fetchItemsPage(String token, String q, int page, int size) throws Exception {
        String qs = "?page=" + page + "&size=" + size;
        if (q != null && !q.isBlank()) {
            qs += "&q=" + URLEncoder.encode(q, StandardCharsets.UTF_8);
        }
        String json = ServiceClient.getJson("/items/page" + qs, token, String.class);
        return MAPPER.readValue(json, new TypeReference<Map<String,Object>>(){});
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = (String) req.getSession().getAttribute("token");
        String action = req.getParameter("action");
        String q = req.getParameter("q");
        int page = 1, size = 10;
        try { page = Integer.parseInt(req.getParameter("page")); } catch (Exception ignored) {}
        try { size = Integer.parseInt(req.getParameter("size")); } catch (Exception ignored) {}

        try {
            Map<String,Object> pr = fetchItemsPage(token, q, page, size);
            req.setAttribute("q", q == null ? "" : q);
            req.setAttribute("page", ((Number)pr.get("page")).intValue());
            req.setAttribute("size", ((Number)pr.get("size")).intValue());
            req.setAttribute("total", ((Number)pr.get("total")).intValue());
            @SuppressWarnings("unchecked")
            List<Map<String,Object>> data = (List<Map<String,Object>>) pr.get("data");
            req.setAttribute("items", data);

            if ("edit".equalsIgnoreCase(action)) {
                String id = req.getParameter("id");
                if (id != null && !id.isBlank()) {
                    req.setAttribute("editItem", fetchItem(token, id));
                }
            }
            req.getRequestDispatcher("/app/items.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession ses = req.getSession();
        String role = (String) ses.getAttribute("role");
        String token = (String) ses.getAttribute("token");

        if (!"ADMIN".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String action = req.getParameter("action");

        try {
            if ("create".equalsIgnoreCase(action)) {
                String code = req.getParameter("code");
                String description = req.getParameter("description");
                String unitPrice = req.getParameter("unitPrice");

                Map<String,String> errors = Validator.validateItem(code, description, unitPrice);
                if (!errors.isEmpty()) {
                    req.setAttribute("errors", errors);
                    Map<String,Object> formData = new HashMap<>();
                    formData.put("code", code);
                    formData.put("description", description);
                    formData.put("unitPrice", unitPrice);
                    req.setAttribute("formData", formData);
                    // Fallback to non-paged list for redisplay
                    req.setAttribute("items", fetchItems(token));
                    req.getRequestDispatcher("/app/items.jsp").forward(req, resp);
                    return;
                }

                Map<String,Object> body = new HashMap<>();
                body.put("code", code);
                body.put("description", description);
                body.put("unitPrice", Double.parseDouble(unitPrice));
                ServiceClient.postJson("/items", body, token, String.class);
                resp.sendRedirect(req.getContextPath() + "/app/items");
                return;

            } else if ("update".equalsIgnoreCase(action)) {
                String id = req.getParameter("id");
                String code = req.getParameter("code");
                String description = req.getParameter("description");
                String unitPrice = req.getParameter("unitPrice");

                Map<String,String> errors = Validator.validateItem(code, description, unitPrice);
                if (!errors.isEmpty()) {
                    req.setAttribute("errorsEdit", errors);
                    Map<String,Object> editItem = new HashMap<>();
                    editItem.put("id", Integer.parseInt(id));
                    editItem.put("code", code);
                    editItem.put("description", description);
                    editItem.put("unitPrice", unitPrice);
                    req.setAttribute("editItem", editItem);
                    // Fallback to non-paged list for redisplay
                    req.setAttribute("items", fetchItems(token));
                    req.getRequestDispatcher("/app/items.jsp").forward(req, resp);
                    return;
                }

                Map<String,Object> body = new HashMap<>();
                body.put("code", code);
                body.put("description", description);
                body.put("unitPrice", Double.parseDouble(unitPrice));
                ServiceClient.putJson("/items/" + id, body, token, String.class);
                resp.sendRedirect(req.getContextPath() + "/app/items");
                return;

            } else if ("delete".equalsIgnoreCase(action)) {
                String id = req.getParameter("id");
                ServiceClient.delete("/items/" + id, token, String.class);
                resp.sendRedirect(req.getContextPath() + "/app/items");
                return;
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action");
            }
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            try {
                req.setAttribute("items", fetchItems(token));
            } catch (Exception ignored) {}
            req.getRequestDispatcher("/app/items.jsp").forward(req, resp);
        }
    }
}
