package edu.pahana.web.servlets;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.pahana.web.util.ServiceClient;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.*;

public class BillingServlet extends HttpServlet {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    // ---------- Helpers ----------
    /** try a few endpoints until one works; returns response body or throws last error */
    private String tryGetAny(String token, String... paths) throws Exception {
        Exception last = null;
        for (String p : paths) {
            try {
                return ServiceClient.getJson(p, token, String.class);
            } catch (Exception e) {
                last = e;
            }
        }
        if (last != null) throw last;
        throw new IOException("No items endpoint responded");
    }

    /** Extract a list of items (as maps) from either an array or an object with a list field. */
    private List<Map<String,Object>> extractList(String json) throws Exception {
        JsonNode root = MAPPER.readTree(json);
        List<Map<String,Object>> list = new ArrayList<>();
        if (root.isArray()) {
            list = MAPPER.convertValue(root, new TypeReference<List<Map<String,Object>>>(){});
        } else if (root.isObject()) {
            // common wrappers: content, data, items, results
            for (String k : new String[]{"content","data","items","results","rows"}) {
                JsonNode n = root.get(k);
                if (n != null && n.isArray()) {
                    list = MAPPER.convertValue(n, new TypeReference<List<Map<String,Object>>>(){});
                    break;
                }
            }
            // If still empty but object looks like single item, wrap it
            if (list.isEmpty() && root.hasNonNull("id")) {
                list.add(MAPPER.convertValue(root, new TypeReference<Map<String,Object>>(){ }));
            }
        }
        return list;
    }

    /** Normalize raw item into {id,name,price} with best-effort key detection. */
    private Map<String,Object> normalizeItem(Map<String,Object> it) {
        Map<String,Object> out = new HashMap<>();
        Object id = firstNonNull(it.get("id"), it.get("itemId"), it.get("code"));
        Object name = firstNonNull(it.get("name"), it.get("title"), it.get("label"));
        Object price = firstNonNull(it.get("price"), it.get("unitPrice"), it.get("sellingPrice"), it.get("rate"));
        out.put("id", id);
        out.put("name", name);
        out.put("price", price);
        return out;
    }

    private static Object firstNonNull(Object... vals){
        for (Object v : vals) if (v != null) return v;
        return null;
    }
    private static String firstNonBlank(String... vals) {
        if (vals == null) return null;
        for (String v : vals) if (v != null && !v.isBlank()) return v.trim();
        return null;
    }
    private static String at(String[] arr, int i) {
        return (arr != null && i >= 0 && i < arr.length) ? arr[i] : null;
    }

    // ---------- Load items for the dropdown ----------
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession ses = req.getSession(false);
        String token = ses == null ? null : (String) ses.getAttribute("token");
        if (token == null) { resp.sendRedirect(req.getContextPath()+"/login.jsp"); return; }

        try {
            // Try several common endpoints so it works with your service
            String json = tryGetAny(token,
                    "/items?size=1000",
                    "/items",
                    "/items/list",
                    "/items/all"
            );
            List<Map<String,Object>> raw = extractList(json);

            // Normalize to {id,name,price} for the JSP
            List<Map<String,Object>> norm = new ArrayList<>();
            for (Map<String,Object> it : raw) norm.add(normalizeItem(it));

            req.setAttribute("items", norm);
        } catch (Exception e) {
            req.setAttribute("items", Collections.emptyList());
            req.setAttribute("error", "Couldn't load items: " + e.getMessage());
        }
        req.getRequestDispatcher("/app/billing.jsp").forward(req, resp);
    }

    // ---------- Create bill (robust to many field name styles) ----------
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession ses = req.getSession(false);
        String token = ses == null ? null : (String) ses.getAttribute("token");
        if (token == null) { resp.sendRedirect(req.getContextPath()+"/login.jsp"); return; }

        try {
            int customerId = Integer.parseInt(req.getParameter("customerId"));

            String[] itemIds = req.getParameterValues("itemId");
            String[] qtys    = req.getParameterValues("qty");
            String[] prices  = req.getParameterValues("unitPrice");
            String[] descs   = req.getParameterValues("desc");

            if (itemIds == null || itemIds.length == 0)
                throw new IllegalArgumentException("No items submitted.");

            List<Map<String,Object>> items = new ArrayList<>();
            double sub = 0.0;

            for (int i = 0; i < itemIds.length; i++) {
                if (itemIds[i] == null || itemIds[i].isBlank()) continue;

                int id = Integer.parseInt(itemIds[i].trim());
                int q  = 1;
                if (at(qtys, i) != null && !at(qtys, i).isBlank()) q = Integer.parseInt(at(qtys, i).trim());

                // Read unit price/desc from various naming schemes:
                String priceStr = firstNonBlank(
                        req.getParameter("unitPrice_" + id),
                        req.getParameter("unitPrice_" + (i+1)),
                        at(prices, i),
                        req.getParameter("unitPrice")
                );
                String desc = firstNonBlank(
                        req.getParameter("desc_" + id),
                        req.getParameter("desc_" + (i+1)),
                        at(descs, i),
                        req.getParameter("desc")
                );

                // If price/desc still missing, fetch item details once
                if (priceStr == null || desc == null) {
                    try {
                        String detailJson = tryGetAny(token, "/items/" + id, "/item/" + id);
                        Map<String,Object> detail =
                                MAPPER.readValue(detailJson, new TypeReference<Map<String,Object>>(){});
                        Map<String,Object> norm = normalizeItem(detail);
                        if (priceStr == null && norm.get("price") != null)
                            priceStr = String.valueOf(norm.get("price"));
                        if (desc == null && norm.get("name") != null)
                            desc = String.valueOf(norm.get("name"));
                    } catch (Exception ignore) {
                        // keep going; we'll error if still missing
                    }
                }

                if (priceStr == null) throw new IllegalArgumentException("Missing unit price for row " + (i+1));
                double price = Double.parseDouble(priceStr);

                Map<String,Object> bi = new HashMap<>();
                bi.put("itemId", id);
                bi.put("quantity", q);
                bi.put("unitPrice", price);
                bi.put("description", desc);
                items.add(bi);

                sub += price * q;
            }

            if (items.isEmpty()) throw new IllegalArgumentException("No valid items submitted.");

            double tax = sub * 0.08;
            double grand = sub + tax;

            Map<String,Object> bill = new HashMap<>();
            bill.put("customerId", customerId);
            bill.put("items", items);
            bill.put("subTotal", sub);
            bill.put("tax", tax);
            bill.put("grandTotal", grand);

            String json = ServiceClient.postJson("/billing/create", bill, token, String.class);
            req.setAttribute("bill", MAPPER.readTree(json));
            req.getRequestDispatcher("/app/bill_success.jsp").forward(req, resp);

        } catch (Exception e) {
            req.setAttribute("error", "Create bill failed: " + (e.getMessage()==null? e.getClass().getSimpleName(): e.getMessage()));
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
        }
    }
}
