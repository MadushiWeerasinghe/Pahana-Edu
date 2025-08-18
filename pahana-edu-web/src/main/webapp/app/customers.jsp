<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*" %>
<!doctype html>
<html>
<head>
  <title>Customers</title>
  <style>
    body{font-family:system-ui,Arial;background:#f6f8fb;margin:0}
    header{background:#2b3a67;color:#fff;padding:16px}
    table{width:100%;border-collapse:collapse}
    th,td{padding:10px;border-bottom:1px solid #eee;vertical-align:top}
    .card{background:#fff;margin:20px auto;max-width:1100px;border-radius:16px;box-shadow:0 10px 30px rgba(0,0,0,.08);padding:24px}
    .row{display:flex;gap:16px;flex-wrap:wrap}
    input[type=text]{padding:8px;border:1px solid #ddd;border-radius:8px}
    button, .btn{padding:8px 12px;border:0;border-radius:8px;cursor:pointer}
    .btn{display:inline-block;text-decoration:none}
    .primary{background:#2b6cb0;color:#fff}
    .danger{background:#b00020;color:#fff}
    .muted{background:#e9eef7;color:#2b3a67}
    form.inline{display:inline}
    .section-title{margin:8px 0 12px;font-weight:600}
    .err{color:#b00020;font-size:0.9em;margin-top:4px}
    .alert{background:#fff3cd;border:1px solid #ffeeba;color:#856404;padding:10px;border-radius:8px;margin-bottom:12px}
  </style>
  <script>
    // Extra client-side guard before submit (HTML5 will already do most)
    function checkCustomerCreate(form){
      if(!form.reportValidity()) return false;
      return true;
    }
    function checkCustomerUpdate(form){
      if(!form.reportValidity()) return false;
      return true;
    }
  </script>
</head>
<body>
<header>
  <div style="display:flex;justify-content:space-between;align-items:center;">
    <h2 style="margin:0;">Customers</h2>
    <a style="color:#fff;text-decoration:none;" href="<%= request.getContextPath() %>/logout">Logout</a>
  </div>
</header>

<div class="card">
  <%
    String role = (String) session.getAttribute("role");
    boolean isAdmin = "ADMIN".equals(role);

    List<Map<String,Object>> customers = (List<Map<String,Object>>) request.getAttribute("customers");
    Map<String,Object> editCustomer = (Map<String,Object>) request.getAttribute("editCustomer");

    Map<String,String> errors = (Map<String,String>) request.getAttribute("errors");
    Map<String,Object> formData = (Map<String,Object>) request.getAttribute("formData");

    Map<String,String> errorsEdit = (Map<String,String>) request.getAttribute("errorsEdit");

    // ===== A) Paging/search variables (use curPage to avoid clashing with JSP's implicit 'page') =====
    int curPage = request.getAttribute("page")==null?1:((Number)request.getAttribute("page")).intValue();
    int size = request.getAttribute("size")==null?10:((Number)request.getAttribute("size")).intValue();
    int total = request.getAttribute("total")==null?0:((Number)request.getAttribute("total")).intValue();
    String qStr = (String)(request.getAttribute("q")==null? "": request.getAttribute("q"));
    int totalPages = (int)Math.ceil(total / (double) size);
    int start = (curPage-1)*size + 1;
    int end = Math.min(curPage*size, total);
  %>

  <% if (request.getAttribute("error") != null) { %>
    <div class="alert"><%= request.getAttribute("error") %></div>
  <% } %>

  <% if (isAdmin) { %>
  <!-- New Customer -->
  <div>
    <div class="section-title">Register New Customer</div>
    <form method="post" action="<%= request.getContextPath() %>/app/customers" class="row" onsubmit="return checkCustomerCreate(this)">
      <input type="hidden" name="action" value="create" />
      <div>
        <input type="text" name="accountNumber" placeholder="Account Number"
               value="<%= formData!=null? String.valueOf(formData.getOrDefault("accountNumber","")) : "" %>"
               required pattern="[A-Za-z0-9-]{3,30}" title="3-30 chars, letters/digits/dash" />
        <% if (errors!=null && errors.get("accountNumber")!=null) { %><div class="err"><%= errors.get("accountNumber") %></div><% } %>
      </div>
      <div>
        <input type="text" name="name" placeholder="Name"
               value="<%= formData!=null? String.valueOf(formData.getOrDefault("name","")) : "" %>"
               required minlength="2" maxlength="120" />
        <% if (errors!=null && errors.get("name")!=null) { %><div class="err"><%= errors.get("name") %></div><% } %>
      </div>
      <div>
        <input type="text" name="address" placeholder="Address"
               value="<%= formData!=null? String.valueOf(formData.getOrDefault("address","")) : "" %>"
               maxlength="255" />
        <% if (errors!=null && errors.get("address")!=null) { %><div class="err"><%= errors.get("address") %></div><% } %>
      </div>
      <div>
        <input type="text" name="phone" placeholder="Phone (+94 77 1234567)"
               value="<%= formData!=null? String.valueOf(formData.getOrDefault("phone","")) : "" %>"
               pattern="[+0-9][0-9\\-\\s]{6,20}" title="Digits (optional +), 7-21 chars" />
        <% if (errors!=null && errors.get("phone")!=null) { %><div class="err"><%= errors.get("phone") %></div><% } %>
      </div>
      <button class="primary" type="submit">Save</button>
    </form>
  </div>
  <hr style="margin:20px 0;border:0;border-top:1px solid #eee" />
  <% } %>

  <!-- ===== B) Search + page-size bar ===== -->
  <form method="get" action="<%= request.getContextPath() %>/app/customers" class="row" style="margin-bottom:12px;">
    <input type="text" name="q" value="<%= qStr %>" placeholder="Search by account, name, phone" style="min-width:260px;" />
    <label>Page size:
      <select name="size" onchange="this.form.submit()">
        <option <%= size==5?"selected":"" %> value="5">5</option>
        <option <%= size==10?"selected":"" %> value="10">10</option>
        <option <%= size==20?"selected":"" %> value="20">20</option>
        <option <%= size==50?"selected":"" %> value="50">50</option>
      </select>
    </label>
    <button class="primary" type="submit">Search</button>
  </form>

  <p style="margin:6px 0;color:#555;">
    Showing <%= total==0 ? 0 : start %>–<%= end %> of <strong><%= total %></strong>
    <%= qStr!=null && !qStr.isBlank()? " for \""+qStr+"\"" : "" %>
  </p>

  <!-- List -->
  <table>
    <tr>
      <th>ID</th><th>Account</th><th>Name</th><th>Address</th><th>Phone</th><th style="width:200px;">Actions</th>
    </tr>
    <%
      if (customers != null) {
        for (Map<String,Object> c: customers) {
          int id = ((Number)c.get("id")).intValue();
    %>
    <tr>
      <td><%= id %></td>
      <td><%= c.get("accountNumber") %></td>
      <td><%= c.get("name") %></td>
      <td><%= c.get("address") %></td>
      <td><%= c.get("phone") %></td>
      <td>
        <% if (isAdmin) { %>
          <a class="btn muted" href="<%= request.getContextPath() %>/app/customers?action=edit&id=<%= id %>">Edit</a>
          <form class="inline" method="post" action="<%= request.getContextPath() %>/app/customers"
                onsubmit="return confirm('Delete customer #<%= id %>?');">
            <input type="hidden" name="action" value="delete" />
            <input type="hidden" name="id" value="<%= id %>" />
            <button class="danger" type="submit">Delete</button>
          </form>
        <% } else { %>
          <span style="color:#888">View only</span>
        <% } %>
      </td>
    </tr>
    <%  } } %>
  </table>

  <!-- ===== C) Pager ===== -->
  <% if (totalPages > 1) { %>
    <div style="margin-top:12px;display:flex;gap:8px;align-items:center;flex-wrap:wrap;">
      <%
        String base = request.getContextPath()+"/app/customers?size="+size+"&q="+java.net.URLEncoder.encode(qStr, "UTF-8");
        int startPage = Math.max(1, curPage-2);
        int endPage = Math.min(totalPages, curPage+2);
      %>
      <a class="btn muted" style="<%= curPage==1?"pointer-events:none;opacity:.5;":"" %>"
         href="<%= base + "&page=" + (curPage-1) %>">Prev</a>
      <% for (int p = startPage; p <= endPage; p++) { %>
        <a class="btn <%= p==curPage?"primary":"muted" %>"
           href="<%= base + "&page=" + p %>"><%= p %></a>
      <% } %>
      <a class="btn muted" style="<%= curPage==totalPages?"pointer-events:none;opacity:.5;":"" %>"
         href="<%= base + "&page=" + (curPage+1) %>">Next</a>
    </div>
  <% } %>

  <% if (isAdmin && editCustomer != null) { %>
  <!-- Edit form -->
  <hr style="margin:20px 0;border:0;border-top:1px solid #eee" />
  <div>
    <div class="section-title">Edit Customer #<%= editCustomer.get("id") %></div>
    <form method="post" action="<%= request.getContextPath() %>/app/customers" class="row" onsubmit="return checkCustomerUpdate(this)">
      <input type="hidden" name="action" value="update" />
      <input type="hidden" name="id" value="<%= editCustomer.get("id") %>" />
      <div>
        <input type="text" name="accountNumber"
               value="<%= editCustomer.get("accountNumber") %>"
               required pattern="[A-Za-z0-9-]{3,30}" title="3-30 chars, letters/digits/dash" />
        <% if (errorsEdit!=null && errorsEdit.get("accountNumber")!=null) { %><div class="err"><%= errorsEdit.get("accountNumber") %></div><% } %>
      </div>
      <div>
        <input type="text" name="name" value="<%= editCustomer.get("name") %>"
               required minlength="2" maxlength="120" />
        <% if (errorsEdit!=null && errorsEdit.get("name")!=null) { %><div class="err"><%= errorsEdit.get("name") %></div><% } %>
      </div>
      <div>
        <input type="text" name="address" value="<%= editCustomer.get("address") %>" maxlength="255" />
        <% if (errorsEdit!=null && errorsEdit.get("address")!=null) { %><div class="err"><%= errorsEdit.get("address") %></div><% } %>
      </div>
      <div>
        <input type="text" name="phone" value="<%= editCustomer.get("phone") %>"
               pattern="[+0-9][0-9\\-\\s]{6,20}" title="Digits (optional +), 7-21 chars" />
        <% if (errorsEdit!=null && errorsEdit.get("phone")!=null) { %><div class="err"><%= errorsEdit.get("phone") %></div><% } %>
      </div>
      <button class="primary" type="submit">Update</button>
      <a class="btn muted" href="<%= request.getContextPath() %>/app/customers">Cancel</a>
    </form>
  </div>
  <% } %>
</div>
</body>
</html>
