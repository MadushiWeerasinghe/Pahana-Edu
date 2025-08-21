<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*" %>
<!doctype html>
<html>
<head>
  <title>Items</title>
  <style>
    body{font-family:system-ui,Arial;background:#f6f8fb;margin:0}
    header{background:#2b3a67;color:#fff;padding:16px}
    table{width:100%;border-collapse:collapse}
    th,td{padding:10px;border-bottom:1px solid #eee;vertical-align:top}
    .card{background:#fff;margin:20px auto;max-width:1100px;border-radius:16px;box-shadow:0 10px 30px rgba(0,0,0,.08);padding:24px}
    .row{display:flex;gap:16px;flex-wrap:wrap}
    input[type=text],input[type=number]{padding:8px;border:1px solid #ddd;border-radius:8px}
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
    function checkItemCreate(form){
      if(!form.reportValidity()) return false;
      return true;
    }
    function checkItemUpdate(form){
      if(!form.reportValidity()) return false;
      return true;
    }
  </script>
</head>
<body>
<header>
  <div style="display:flex;justify-content:space-between;align-items:center;">
    <h2 style="margin:0;">Items</h2>
    <a style="color:#fff;text-decoration:none;" href="<%= request.getContextPath() %>/logout">Logout</a>
  </div>
</header>

<div class="card">
  <%
    String role = (String) session.getAttribute("role");
    boolean isAdmin = "ADMIN".equals(role);

    List<Map<String,Object>> items = (List<Map<String,Object>>) request.getAttribute("items");
    Map<String,Object> editItem = (Map<String,Object>) request.getAttribute("editItem");

    Map<String,String> errors = (Map<String,String>) request.getAttribute("errors");
    Map<String,Object> formData = (Map<String,Object>) request.getAttribute("formData");

    Map<String,String> errorsEdit = (Map<String,String>) request.getAttribute("errorsEdit");

    // ===== A) Variables for search & pagination (rename to avoid JSP implicit 'page') =====
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
  <!-- New Item -->
  <div>
    <div class="section-title">Add New Item</div>
    <form method="post" action="<%= request.getContextPath() %>/app/items" class="row" onsubmit="return checkItemCreate(this)">
      <input type="hidden" name="action" value="create" />
      <div>
        <input type="text" name="code" placeholder="Code (unique)"
               value="<%= formData!=null? String.valueOf(formData.getOrDefault("code","")) : "" %>"
               required pattern="[A-Za-z0-9-]{2,30}" title="2-30 chars, letters/digits/dash" />
        <% if (errors!=null && errors.get("code")!=null) { %><div class="err"><%= errors.get("code") %></div><% } %>
      </div>
      <div>
        <input type="text" name="description" placeholder="Description"
               value="<%= formData!=null? String.valueOf(formData.getOrDefault("description","")) : "" %>"
               required minlength="2" maxlength="255" />
        <% if (errors!=null && errors.get("description")!=null) { %><div class="err"><%= errors.get("description") %></div><% } %>
      </div>
      <div>
        <input type="number" name="unitPrice" placeholder="Unit Price"
               value="<%= formData!=null? String.valueOf(formData.getOrDefault("unitPrice","")) : "" %>"
               step="0.01" min="0" required />
        <% if (errors!=null && errors.get("unitPrice")!=null) { %><div class="err"><%= errors.get("unitPrice") %></div><% } %>
      </div>
      <button class="primary" type="submit">Save</button>
    </form>
  </div>
  <hr style="margin:20px 0;border:0;border-top:1px solid #eee" />
  <% } %>

  <!-- ===== B) Search + page size bar ===== -->
  <form method="get" action="<%= request.getContextPath() %>/app/items" class="row" style="margin-bottom:12px;">
    <input type="text" name="q" value="<%= qStr %>" placeholder="Search by code or description" style="min-width:260px;" />
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
      <th>ID</th><th>Code</th><th>Description</th><th>Unit Price</th><th style="width:200px;">Actions</th>
    </tr>
    <%
      if (items != null) {
        for (Map<String,Object> it: items) {
          int id = ((Number)it.get("id")).intValue();
    %>
    <tr>
      <td><%= id %></td>
      <td><%= it.get("code") %></td>
      <td><%= it.get("description") %></td>
      <td><%= it.get("unitPrice") %></td>
      <td>
        <% if (isAdmin) { %>
          <a class="btn muted" href="<%= request.getContextPath() %>/app/items?action=edit&id=<%= id %>">Edit</a>
          <form class="inline" method="post" action="<%= request.getContextPath() %>/app/items"
                onsubmit="return confirm('Delete item #<%= id %>?');">
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
        String base = request.getContextPath()+"/app/items?size="+size+"&q="+java.net.URLEncoder.encode(qStr, java.nio.charset.StandardCharsets.UTF_8);
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

  <% if (isAdmin && editItem != null) { %>
  <!-- Edit form -->
  <hr style="margin:20px 0;border:0;border-top:1px solid #eee" />
  <div>
    <div class="section-title">Edit Item #<%= editItem.get("id") %></div>
    <form method="post" action="<%= request.getContextPath() %>/app/items" class="row" onsubmit="return checkItemUpdate(this)">
      <input type="hidden" name="action" value="update" />
      <input type="hidden" name="id" value="<%= editItem.get("id") %>" />
      <div>
        <input type="text" name="code" value="<%= editItem.get("code") %>"
               required pattern="[A-Za-z0-9-]{2,30}" title="2-30 chars, letters/digits/dash" />
        <% if (errorsEdit!=null && errorsEdit.get("code")!=null) { %><div class="err"><%= errorsEdit.get("code") %></div><% } %>
      </div>
      <div>
        <input type="text" name="description" value="<%= editItem.get("description") %>"
               required minlength="2" maxlength="255" />
        <% if (errorsEdit!=null && errorsEdit.get("description")!=null) { %><div class="err"><%= errorsEdit.get("description") %></div><% } %>
      </div>
      <div>
        <input type="number" name="unitPrice" value="<%= editItem.get("unitPrice") %>"
               step="0.01" min="0" required />
        <% if (errorsEdit!=null && errorsEdit.get("unitPrice")!=null) { %><div class="err"><%= errorsEdit.get("unitPrice") %></div><% } %>
      </div>
      <button class="primary" type="submit">Update</button>
      <a class="btn muted" href="<%= request.getContextPath() %>/app/items">Cancel</a>
    </form>
  </div>
  <% } %>
</div>
</body>
</html>
