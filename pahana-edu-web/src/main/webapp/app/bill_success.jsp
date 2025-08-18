<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.fasterxml.jackson.databind.JsonNode" %>
<%
  JsonNode bill = (JsonNode) request.getAttribute("bill");
%>
<!doctype html>
<html>
<head>
  <title>Bill Created</title>
  <style>
    body{font-family:system-ui,Arial;background:#f6f8fb;margin:0}
    .topbar{background:#2b3a67;color:#fff;padding:12px 16px;display:flex;justify-content:space-between;align-items:center;}
    .card{background:#fff;margin:20px auto;max-width:700px;border-radius:16px;box-shadow:0 10px 30px rgba(0,0,0,.08);padding:24px}
    table{width:100%;border-collapse:collapse}
    th,td{padding:8px;border-bottom:1px solid #eee;text-align:left}
    .actions a, .actions button{padding:8px 12px;border:0;border-radius:8px;cursor:pointer;text-decoration:none;margin-left:8px}
    .primary{background:#2b6cb0;color:#fff}
    .muted{background:#e9eef7;color:#2b3a67}
    @media print{
      .topbar, .actions{ display:none !important; }
      body{ background:#fff; }
      .card{ box-shadow:none; margin:0; border-radius:0; }
      a[href]:after { content: ""; } /* remove link printing */
    }
  </style>
  <script>
    function doPrint(){ window.print(); }
  </script>
</head>
<body>
<div class="topbar">
  <strong>Invoice</strong>
  <div class="actions">
    <button class="muted" onclick="doPrint()">Print</button>
    <a class="primary" href="<%= request.getContextPath() %>/app/bill/pdf?id=<%= bill.get("id").asInt() %>">Download PDF</a>
  </div>
</div>

<div class="card">
  <h2>Bill #<%= bill.get("id").asInt() %></h2>
  <p>Customer ID: <%= bill.get("customerId").asInt() %></p>

  <table>
    <tr><th>Description</th><th>Qty</th><th>Unit Price</th><th>Total</th></tr>
    <%
      double sub = 0.0;
      for(JsonNode it: bill.get("items")){
        int q = it.get("quantity").asInt();
        double up = it.get("unitPrice").asDouble();
        double line = q * up; sub += line;
    %>
    <tr>
      <td><%= it.get("description").asText() %></td>
      <td><%= q %></td>
      <td><%= up %></td>
      <td><%= line %></td>
    </tr>
    <% } %>
    <tr><td colspan="3" style="text-align:right">Sub Total</td><td><%= bill.has("subTotal")? bill.get("subTotal").asDouble() : sub %></td></tr>
    <%
      double tax = bill.has("tax") ? bill.get("tax").asDouble() : Math.round(sub * 0.08 * 100.0) / 100.0;
      double grand = bill.has("grandTotal") ? bill.get("grandTotal").asDouble() : (sub + tax);
    %>
    <tr><td colspan="3" style="text-align:right">Tax</td><td><%= tax %></td></tr>
    <tr><td colspan="3" style="text-align:right"><strong>Grand Total</strong></td><td><strong><%= grand %></strong></td></tr>
  </table>
</div>
</body>
</html>
