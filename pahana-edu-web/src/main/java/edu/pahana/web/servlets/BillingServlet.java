package edu.pahana.web.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.pahana.web.util.ServiceClient;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.*;

public class BillingServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Simple billing: expects customerId, itemId[], qty[]
        String token = (String) req.getSession().getAttribute("token");
        int customerId = Integer.parseInt(req.getParameter("customerId"));
        String[] itemIds = req.getParameterValues("itemId");
        String[] qtys = req.getParameterValues("qty");
        List<Map<String,Object>> items = new ArrayList<>();
        double sub = 0.0;
        for(int i=0;i<itemIds.length;i++){
            int id = Integer.parseInt(itemIds[i]);
            int q = Integer.parseInt(qtys[i]);
            // In a real app, fetch item price. Here we just send with placeholders; web page will include unitPrice in hidden fields.
            double price = Double.parseDouble(req.getParameter("unitPrice_"+id));
            String desc = req.getParameter("desc_"+id);
            Map<String,Object> bi = new HashMap<>();
            bi.put("itemId", id);
            bi.put("quantity", q);
            bi.put("unitPrice", price);
            bi.put("description", desc);
            items.add(bi);
            sub += price * q;
        }
        double tax = sub * 0.08;
        double grand = sub + tax;

        Map<String,Object> bill = new HashMap<>();
        bill.put("customerId", customerId);
        bill.put("items", items);
        bill.put("subTotal", sub);
        bill.put("tax", tax);
        bill.put("grandTotal", grand);

        try{
            String json = ServiceClient.postJson("/billing/create", bill, token, String.class);
            req.setAttribute("bill", new ObjectMapper().readTree(json));
            req.getRequestDispatcher("/app/bill_success.jsp").forward(req, resp);
        }catch(Exception e){
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
        }
    }
}
