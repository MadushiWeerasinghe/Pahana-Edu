<%@ page contentType="text/html;charset=UTF-8" %>
<!doctype html>
<html>
<head><title>Create Bill</title>
<style>
  body{font-family:system-ui,Arial;background:#f6f8fb;margin:0}
  .card{background:#fff;margin:20px auto;max-width:800px;border-radius:16px;box-shadow:0 10px 30px rgba(0,0,0,.08);padding:24px}
  input,select{padding:8px;border:1px solid #ddd;border-radius:8px}
  table{width:100%;border-collapse:collapse}
  th,td{padding:10px;border-bottom:1px solid #eee}
  button{padding:10px 14px;border:0;background:#2b6cb0;color:#fff;border-radius:8px}
</style>
</head>
<body>
  <div style="background:#2b3a67;color:#fff;padding:12px 16px;display:flex;justify-content:space-between;align-items:center;">
  <strong>Help</strong>
  <a style="color:#fff;text-decoration:none;" href="<%= request.getContextPath() %>/logout">Logout</a>
</div>
    
    
<div class="card">
  <h2>New Bill</h2>
  <form method="post" action="billing">
    <label>Customer ID</label>
    <input name="customerId" type="number" required />
    <table id="items">
      <tr><th>Item ID</th><th>Description</th><th>Unit Price</th><th>Qty</th></tr>
      <tr>
        <td><input name="itemId" required /></td>
        <td><input name="desc_1" placeholder="Description" /></td>
        <td><input name="unitPrice_1" type="number" step="0.01" required /></td>
        <td><input name="qty" type="number" value="1" min="1" required /></td>
      </tr>
    </table>
    <p><em>Tip:</em> For demo add a single item row. You can duplicate rows in HTML for multiple items.</p>
    <button type="submit">Create Bill</button>
  </form>
</div>
</body>
</html>
