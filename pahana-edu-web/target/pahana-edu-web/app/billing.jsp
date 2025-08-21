<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*" %>
<%
  // Expecting the servlet to set request attribute "items" as List<Map<String,Object>>
  // Example item keys: id/itemId, name/title, price/unitPrice
  List<Map<String,Object>> items = (List<Map<String,Object>>) request.getAttribute("items");
  if (items == null) items = new ArrayList<>();

  // Try to detect the correct keys automatically
  String idKey = "id", nameKey = "name", priceKey = "price";
  if (!items.isEmpty()) {
    Map<String,Object> s = items.get(0);
    if (!s.containsKey(idKey))     idKey   = s.containsKey("itemId")   ? "itemId"   : (s.containsKey("code") ? "code" : idKey);
    if (!s.containsKey(nameKey))   nameKey = s.containsKey("title")    ? "title"    : nameKey;
    if (!s.containsKey(priceKey))  priceKey= s.containsKey("unitPrice")? "unitPrice": priceKey;
  }
%>
<!doctype html>
<html>
<head>
  <title>Create Bill</title>
  <style>
    body{font-family:system-ui,Arial;background:#f6f8fb;margin:0}
    header{background:#2b3a67;color:#fff;padding:12px 16px;display:flex;justify-content:space-between;align-items:center}
    header a{color:#fff;text-decoration:none}
    .card{background:#fff;margin:20px auto;max-width:980px;border-radius:16px;box-shadow:0 10px 30px rgba(0,0,0,.08);padding:24px}
    input,select{padding:8px;border:1px solid #cfd6e3;border-radius:8px;width:100%}
    table{width:100%;border-collapse:collapse;margin-top:12px}
    th,td{padding:10px;border-bottom:1px solid #e9edf5;text-align:left}
    th{background:#f2f4fa}
    .btn{padding:10px 14px;border:0;border-radius:10px;background:#2b6cb0;color:#fff;cursor:pointer}
    .btn.ghost{background:#eef2ff;color:#2b3a67}
    .row-actions{display:flex;gap:8px;justify-content:flex-start}
    .muted{color:#6b7280;font-size:12px}
    .grid{display:grid;grid-template-columns:1fr 1fr;gap:12px;max-width:420px}
  </style>
</head>
<body>
  <header>
    <strong>Billing</strong>
    <a href="<%= request.getContextPath() %>/logout">Logout</a>
  </header>

  <div class="card">
    <h2>Create Bill</h2>

    <!-- IMPORTANT: post to the servlet mapping -->
    <form method="post" action="<%= request.getContextPath() %>/app/billing">
      <div class="grid">
        <div>
          <label>Customer ID</label>
          <input name="customerId" type="number" required />
        </div>
      </div>

      <table id="lines">
        <thead>
          <tr>
            <th style="width:200px">Item ID</th>
            <th>Description</th>
            <th style="width:140px">Unit Price</th>
            <th style="width:100px">Qty</th>
            <th style="width:90px"></th>
          </tr>
        </thead>
        <tbody>
          <!-- initial line -->
          <tr>
            <td>
              <select name="itemId" class="item-select" required>
                <option value="">-- select --</option>
                <% for (Map<String,Object> it : items) {
                     Object vid = it.get(idKey);
                     Object vname = it.get(nameKey);
                     Object vprice = it.get(priceKey);
                     String label = String.valueOf(vid) + (vname!=null? " - " + String.valueOf(vname):"");
                %>
                  <option value="<%= vid %>" data-name="<%= vname %>" data-price="<%= vprice %>"><%= label %></option>
                <% } %>
              </select>
            </td>
            <td><input name="desc" class="desc" placeholder="Description" /></td>
            <td><input name="unitPrice" class="unitPrice" type="number" step="0.01" required /></td>
            <td><input name="qty" class="qty" type="number" value="1" min="1" required /></td>
            <td class="row-actions">
              <button type="button" class="btn ghost add-line">+</button>
              <button type="button" class="btn ghost remove-line">–</button>
            </td>
          </tr>
        </tbody>
      </table>

      <p class="muted" style="margin-top:8px">
        
        <!-- If you want price auto-fill too, see the JS below. -->
      </p>

      <button type="submit" class="btn" style="margin-top:10px">Create Bill</button>
    </form>
  </div>

  <!-- hidden template row for cloning -->
  <table style="display:none">
    <tbody id="row-template">
      <tr>
        <td>
          <select name="itemId" class="item-select" required>
            <option value="">-- select --</option>
            <% for (Map<String,Object> it : items) {
                 Object vid = it.get(idKey);
                 Object vname = it.get(nameKey);
                 Object vprice = it.get(priceKey);
                 String label = String.valueOf(vid) + (vname!=null? " - " + String.valueOf(vname):"");
            %>
              <option value="<%= vid %>" data-name="<%= vname %>" data-price="<%= vprice %>"><%= label %></option>
            <% } %>
          </select>
        </td>
        <td><input name="desc" class="desc" placeholder="Description" /></td>
        <td><input name="unitPrice" class="unitPrice" type="number" step="0.01" required /></td>
        <td><input name="qty" class="qty" type="number" value="1" min="1" required /></td>
        <td class="row-actions">
          <button type="button" class="btn ghost add-line">+</button>
          <button type="button" class="btn ghost remove-line">–</button>
        </td>
      </tr>
    </tbody>
  </table>

  <script>
    const tbody = document.querySelector('#lines tbody');
    const tplRow = document.querySelector('#row-template tr');

    function wireRow(tr){
      const select = tr.querySelector('.item-select');
      const desc   = tr.querySelector('.desc');
      const price  = tr.querySelector('.unitPrice');

      // Auto-fill description (and optionally price) when item changes
      select.addEventListener('change', () => {
        const opt = select.options[select.selectedIndex];
        desc.value  = opt.getAttribute('data-name') || '';
        // Uncomment next line if you want Unit Price to auto-fill too:
        // price.value = opt.getAttribute('data-price') || '';
      });

      tr.querySelector('.add-line').addEventListener('click', () => {
        const clone = tplRow.cloneNode(true);
        wireRow(clone);
        tbody.appendChild(clone);
      });

      tr.querySelector('.remove-line').addEventListener('click', () => {
        if (tbody.rows.length > 1) tr.remove();
      });
    }

    // wire the first row
    wireRow(tbody.querySelector('tr'));
  </script>
</body>
</html>
