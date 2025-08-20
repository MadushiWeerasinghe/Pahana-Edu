<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*, jakarta.servlet.http.*" %>
<!doctype html>
<html>
<head>
  <title>Reports - Admin</title>
  <style>
    body{font-family:system-ui,Arial;background:#f6f8fb;margin:0}
    header{background:#2b3a67;color:#fff;padding:16px;border-bottom:4px solid #415a9c}
    nav a{color:#fff;margin-right:16px;text-decoration:none}
    .wrap{max-width:1100px;margin:20px auto;padding:0 16px}
    .card{background:#fff;border-radius:16px;box-shadow:0 10px 30px rgba(0,0,0,.08);padding:24px;margin-bottom:16px}
    
    /* add vertical gap below the tabs */
.tabs{display:flex; gap:12px; margin:8px 0 20px;}  /* <- this creates the space */
.tabs a{padding:8px 12px;border-radius:8px;background:#eef2ff;color:#2b3a67;text-decoration:none}
.tabs a.active{background:#2b3a67;color:#fff}

    
    table{width:100%;border-collapse:collapse}
    th,td{padding:10px;border-bottom:1px solid #e6e8ee;text-align:left}
    th{background:#f2f4fa}
    .muted{color:#6b7280;font-size:12px}
    .row{display:flex;gap:12px;flex-wrap:wrap}
    .row .col{flex:1;min-width:280px}
    input,select{padding:8px;border:1px solid #c7cfe1;border-radius:8px}
    button{padding:8px 14px;border:0;border-radius:10px;background:#2b3a67;color:#fff;cursor:pointer}
    .error{background:#fde2e1;color:#b42318;padding:10px;border-radius:12px;margin-bottom:12px}
  </style>
</head>
<body>
  <%
    HttpSession ses = request.getSession(false);
    String role = ses == null ? null : (String) ses.getAttribute("role");
  %>
  <header>
    <nav>
      <a href="dashboard.jsp">Dashboard</a>
      <a href="customers">Customers</a>
      <a href="items">Items</a>
      <a href="billing.jsp">Billing</a>
      <% if ("ADMIN".equals(role)) { %>
        <a href="<%= request.getContextPath() %>/app/reports" style="font-weight:bold;text-decoration:underline">Reports</a>
      <% } %>
      <span style="float:right">
        <a href="<%= request.getContextPath() %>/logout" style="color:#fff">Logout</a>
      </span>
    </nav>
  </header>

  <div class="wrap">
    <div class="card">
      <h2>Admin Reports</h2>
      <div class="tabs">
        <a href="?tab=customers" class="<%= "customers".equals(request.getAttribute("tab"))?"active":"" %>">Customers</a>
        <a href="?tab=billing" class="<%= "billing".equals(request.getAttribute("tab"))?"active":"" %>">Billing</a>
        <a href="?tab=revenue" class="<%= "revenue".equals(request.getAttribute("tab"))?"active":"" %>">Revenue</a>
      </div>

      <% if (request.getAttribute("error") != null) { %>
        <div class="error"><%= request.getAttribute("error") %></div>
      <% } %>

      <% String tab = (String) request.getAttribute("tab"); if (tab==null) tab="customers"; %>

      <% if ("customers".equals(tab)) { %>
        <form method="get" action="reports" class="row" style="margin-bottom:12px">
          <input type="hidden" name="tab" value="customers"/>
          <div class="col">
            <label>Filter</label><br/>
            <select name="filter">
              <option value="all" <%= "all".equals(request.getParameter("filter"))?"selected":"" %>>All</option>
              <option value="new" <%= "new".equals(request.getParameter("filter"))?"selected":"" %>>Newly registered</option>
              <option value="active" <%= "active".equals(request.getParameter("filter"))?"selected":"" %>>Active</option>
            </select>
          </div>
          <div class="col">
            <label>Days (for New/Active)</label><br/>
            <input type="number" min="1" name="days" value="<%= request.getParameter("days")!=null? request.getParameter("days"): "30" %>"/>
          </div>
          <div class="col" style="align-self:end">
            <button type="submit">Apply</button>
          </div>
        </form>

        <table>
          <thead>
            <tr>
              <th>Account #</th><th>Name</th><th>Address</th><th>Phone</th><th>Registered</th><th>Last Billed</th>
            </tr>
          </thead>
          <tbody>
            <%
              List<Map<String,Object>> rows = (List<Map<String,Object>>) request.getAttribute("customersRows");
              if (rows != null) {
                for (Map<String,Object> r : rows) {
            %>
            <tr>
              <td><%= r.get("accountNumber") %></td>
              <td><%= r.get("name") %></td>
              <td><%= r.get("address") %></td>
              <td><%= r.get("phone") %></td>
              <td class="muted"><%= r.get("createdAt") %></td>
              <td class="muted"><%= r.get("lastBillAt") %></td>
            </tr>
            <%  } } %>
          </tbody>
        </table>
      <% } else if ("billing".equals(tab)) { %>
      
        <form method="get" action="reports" class="row" style="margin-bottom:12px">
          <input type="hidden" name="tab" value="billing"/>
          <div class="col">
              
            <label>Customer Account #</label><br/>
            <input type="text" name="account" value="<%= request.getAttribute("account")!=null? request.getAttribute("account"): "" %>" placeholder="e.g. AC1234"/>
          </div>
          <div class="col" style="align-self:end">
            <button type="submit">Load Bills</button>
          </div>
        </form>
          
          <form method="get" action="reports" class="row" style="margin-bottom:12px">
  <input type="hidden" name="tab" value="revenue"/>
  <input type="hidden" name="run" value="1"/><!-- tell servlet to actually run -->
  ...
  <button type="submit">Run</button>
</form>

        <%
          List<List<Object>> bills = (List<List<Object>>) request.getAttribute("customerBills");
          if (bills != null) {
        %>
        <div class="card">
          <h3>Bills for account <%= request.getAttribute("account") %></h3>
          <table>
            <thead><tr><th>ID</th><th>Date</th><th>Sub-Total</th><th>Tax</th><th>Grand Total</th></tr></thead>
            <tbody>
            <% for (List<Object> row : bills) { %>
              <tr>
                <td><%= row.get(0) %></td>
                <td><%= row.get(1) %></td>
                <td><%= row.get(2) %></td>
                <td><%= row.get(3) %></td>
                <td><strong><%= row.get(4) %></strong></td>
              </tr>
            <% } %>
            </tbody>
          </table>
        </div>
        <% } %>

        <form method="get" action="reports" class="row" style="margin-top:16px">
          <input type="hidden" name="tab" value="billing"/>
          <div class="col">
            <label>From (yyyy-mm-dd)</label><br/>
            <input type="date" name="from" value="<%= request.getAttribute("from")!=null? request.getAttribute("from"): "" %>"/>
          </div>
          <div class="col">
            <label>To (yyyy-mm-dd)</label><br/>
            <input type="date" name="to" value="<%= request.getAttribute("to")!=null? request.getAttribute("to"): "" %>"/>
          </div>
          <div class="col" style="align-self:end">
            <button type="submit">Load Summary</button>
          </div>
        </form>

        <%
          Map<String,Object> summary = (Map<String,Object>) request.getAttribute("billSummary");
          if (summary != null) {
        %>
        <div class="card">
          <h3>Summary</h3>
          <p class="muted">Bills: <strong><%= summary.get("count") %></strong> &nbsp;|&nbsp;
          Sub-total: <strong><%= summary.get("totalSubTotal") %></strong> &nbsp;|&nbsp;
          Tax: <strong><%= summary.get("totalTax") %></strong> &nbsp;|&nbsp;
          Grand total: <strong><%= summary.get("totalGrandTotal") %></strong></p>
          <table>
            <thead><tr><th>Day</th><th>Total</th></tr></thead>
            <tbody>
            <% List<Map<String,Object>> daily = (List<Map<String,Object>>) summary.get("daily"); 
               if (daily != null) { for (Map<String,Object> p : daily) { %>
               <tr><td><%= p.get("label") %></td><td><%= p.get("total") %></td></tr>
            <% }} %>
            </tbody>
          </table>
        </div>
        <% } %>

      <% } else if ("revenue".equals(tab)) { %>
        <form method="get" action="reports" class="row" style="margin-bottom:12px">
          <input type="hidden" name="tab" value="revenue"/>
          <div class="col">
            <label>Granularity</label><br/>
            <select name="granularity">
              <option value="day" <%= "day".equals(request.getAttribute("granularity"))?"selected":"" %>>Day</option>
              <option value="week" <%= "week".equals(request.getAttribute("granularity"))?"selected":"" %>>Week</option>
              <option value="month" <%= "month".equals(request.getAttribute("granularity"))?"selected":"" %>>Month</option>
            </select>
          </div>
          <div class="col">
            <label>From</label><br/>
            <input type="date" name="from" value="<%= request.getAttribute("from")!=null? request.getAttribute("from"): "" %>"/>
          </div>
          <div class="col">
            <label>To</label><br/>
            <input type="date" name="to" value="<%= request.getAttribute("to")!=null? request.getAttribute("to"): "" %>"/>
          </div>
          <div class="col" style="align-self:end">
            <button type="submit">Run</button>
          </div>
        </form>
        <%
          List<Map<String,Object>> rev = (List<Map<String,Object>>) request.getAttribute("revenueData");
          if (rev != null) {
        %>
        <table>
          <thead><tr><th>Period</th><th>Start</th><th>End</th><th>Total</th></tr></thead>
          <tbody>
            <% for (Map<String,Object> p : rev) { %>
              <tr>
                <td><%= p.get("label") %></td>
                <td class="muted"><%= p.get("periodStart") %></td>
                <td class="muted"><%= p.get("periodEnd") %></td>
                <td><strong><%= p.get("total") %></strong></td>
              </tr>
            <% } %>
          </tbody>
        </table>
        <% } %>
      <% } %>
    </div>
  </div>
</body>
</html>
