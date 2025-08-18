package edu.pahana.web.servlets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.pahana.web.util.ServiceClient;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.awt.Color; // ✅ needed for background color

// OpenPDF (iText 2)
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

public class BillPdfServlet extends HttpServlet {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance(new Locale("en", "LK"));

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = (String) req.getSession().getAttribute("token");
        if (token == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        String idStr = req.getParameter("id");
        if (idStr == null || idStr.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing bill id");
            return;
        }

        try {
            String json = ServiceClient.getJson("/billing/" + idStr, token, String.class);
            JsonNode bill = MAPPER.readTree(json);

            // PDF headers
            resp.setContentType("application/pdf");
            resp.setHeader("Content-Disposition", "attachment; filename=invoice-"+idStr+".pdf");

            // Build PDF
            Document doc = new Document(PageSize.A4, 36, 36, 48, 36);
            PdfWriter.getInstance(doc, resp.getOutputStream());
            doc.open();

            // Header
            Font h1 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font h2 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font base = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Paragraph title = new Paragraph("Pahana Edu - Invoice", h1);
            title.setAlignment(Element.ALIGN_CENTER);
            doc.add(title);
            doc.add(Chunk.NEWLINE);

            // Meta
            int billId = bill.get("id").asInt();
            int customerId = bill.get("customerId").asInt();
            String createdAt = bill.hasNonNull("createdAt")
                    ? bill.get("createdAt").asText()
                    : TS_FMT.format(LocalDateTime.now());

            PdfPTable meta = new PdfPTable(2);
            meta.setWidthPercentage(100);
            meta.setWidths(new float[]{1.2f, 2f});
            meta.addCell(cell("Invoice #", h2, Rectangle.NO_BORDER));
            meta.addCell(cell(String.valueOf(billId), base, Rectangle.NO_BORDER));
            meta.addCell(cell("Customer ID", h2, Rectangle.NO_BORDER));
            meta.addCell(cell(String.valueOf(customerId), base, Rectangle.NO_BORDER));
            meta.addCell(cell("Date", h2, Rectangle.NO_BORDER));
            meta.addCell(cell(createdAt, base, Rectangle.NO_BORDER));
            doc.add(meta);

            doc.add(Chunk.NEWLINE);

            // Table
            PdfPTable t = new PdfPTable(4);
            t.setWidthPercentage(100);
            t.setWidths(new float[]{4f, 1.2f, 1.6f, 1.6f});
            t.addCell(head("Description"));
            t.addCell(head("Qty"));
            t.addCell(head("Unit Price"));
            t.addCell(head("Line Total"));

            double sub = 0.0;
            for (JsonNode it : bill.get("items")) {
                String desc = textOr(it, "description", "");
                int qty = it.get("quantity").asInt();
                double price = it.get("unitPrice").asDouble();
                double line = qty * price;
                sub += line;

                t.addCell(cell(desc, base, Rectangle.BOX));
                t.addCell(cell(String.valueOf(qty), base, Rectangle.BOX));
                t.addCell(cell(CURRENCY.format(price), base, Rectangle.BOX));
                t.addCell(cell(CURRENCY.format(line), base, Rectangle.BOX));
            }
            doc.add(t);

            doc.add(Chunk.NEWLINE);

            double subTotal = bill.hasNonNull("subTotal") ? bill.get("subTotal").asDouble() : sub;
            double tax = bill.hasNonNull("tax") ? bill.get("tax").asDouble() : Math.round(subTotal * 0.08 * 100.0) / 100.0;
            double grand = bill.hasNonNull("grandTotal") ? bill.get("grandTotal").asDouble() : (subTotal + tax);

            PdfPTable totals = new PdfPTable(2);
            totals.setWidthPercentage(40);
            totals.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totals.setWidths(new float[]{2f, 2f});
            totals.addCell(cell("Sub Total", h2, Rectangle.NO_BORDER));
            totals.addCell(cell(CURRENCY.format(subTotal), base, Rectangle.NO_BORDER));
            totals.addCell(cell("Tax", h2, Rectangle.NO_BORDER));
            totals.addCell(cell(CURRENCY.format(tax), base, Rectangle.NO_BORDER));
            totals.addCell(cell("Grand Total", h2, Rectangle.TOP));
            totals.addCell(cell(CURRENCY.format(grand), h2, Rectangle.TOP));
            doc.add(totals);

            doc.close();
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private static PdfPCell head(String s){
        PdfPCell c = new PdfPCell(new Phrase(s, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        c.setHorizontalAlignment(Element.ALIGN_LEFT);
        c.setBackgroundColor(new Color(240, 240, 240));
        return c;
    }
    private static PdfPCell cell(String s, Font f, int border){
        PdfPCell c = new PdfPCell(new Phrase(s, f));
        c.setBorder(border);
        return c;
    }
    private static String textOr(JsonNode n, String field, String def){
        return n.hasNonNull(field) ? n.get(field).asText() : def;
    }
}
