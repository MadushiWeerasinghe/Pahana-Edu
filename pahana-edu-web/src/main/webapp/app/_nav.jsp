<nav>
    <a href="<%= request.getContextPath() %>/app/billing">Billing</a>

  <a href="<%= "ADMIN".equals(session.getAttribute("role")) 
              ? "admin_dashboard.jsp" 
              : "cashier_dashboard.jsp" %>">Dashboard</a>

  <a href="<%= request.getContextPath() %>/app/customers">Customers</a>
  <a href="<%= request.getContextPath() %>/app/items">Items</a>
  <a href="billing.jsp">Billing</a>

  <% if ("ADMIN".equals(session.getAttribute("role"))) { %>
    <a href="<%= request.getContextPath() %>/app/reports">Reports</a>
  <% } %>

  <a href="help.jsp">Help</a>
  <span style="float:right">
    <a href="<%= request.getContextPath() %>/logout">Logout</a>
  </span>
</nav>
