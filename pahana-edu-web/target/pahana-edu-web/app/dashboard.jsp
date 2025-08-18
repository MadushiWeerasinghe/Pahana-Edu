<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<!doctype html>
<html>
<head>
  <title>Dashboard - Pahana Edu</title>
  <style>
    body{font-family:system-ui,Arial;background:#f6f8fb;margin:0}
    header{background:#2b3a67;color:#fff;padding:16px;border-bottom:4px solid #415a9c}
    nav a{color:#fff;margin-right:16px;text-decoration:none}
    .card{background:#fff;margin:20px auto;max-width:900px;border-radius:16px;box-shadow:0 10px 30px rgba(0,0,0,.08);padding:24px}
  </style>
</head>
<body>
<header>
  <nav>
  <a href="dashboard.jsp">Home</a>
  <a href="<%= request.getContextPath() %>/app/customers">Customers</a>
  <a href="<%= request.getContextPath() %>/app/items">Items</a>
  <a href="billing.jsp">Billing</a>
  <a href="help.jsp">Help</a>
  <span style="float:right">
    <a href="<%= request.getContextPath() %>/logout">Logout</a>
  </span>
</nav>

</header>
<div class="card">
  <h2>Welcome, <%= session.getAttribute("username") %> (<%= session.getAttribute("role") %>)</h2>
  <p>Use the navigation above to manage customers, items, and billing.</p>
</div>
</body>
</html>
