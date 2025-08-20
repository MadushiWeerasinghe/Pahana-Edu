<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.time.*, java.time.format.*" %>
<!doctype html>
<html>
<head>
  <title>Admin Dashboard</title>
  <style>
    :root{
      --bg:#f6f8fb;
      --nav:#24355a;
      --card:#ffffff;
      --text:#1f2937;
      --muted:#6b7280;
      --accent1:#7c3aed;   /* purple */
      --accent2:#06b6d4;   /* cyan   */
      --accent3:#22c55e;   /* green  */
      --accent4:#f59e0b;   /* amber  */
      --ring: rgba(12, 74, 110, .15);
    }
    *{box-sizing:border-box}
    body{margin:0;background:var(--bg);font-family:system-ui,Segoe UI,Roboto,Arial}
    .topbar{background:var(--nav);color:#fff;padding:14px 18px}
    .wrap{max-width:1180px;margin:24px auto;padding:0 16px}
    .hero{
      background:linear-gradient(135deg, rgba(124,58,237,.12), rgba(6,182,212,.12));
      border:1px solid rgba(124,58,237,.25);
      color:var(--text);
      border-radius:18px;padding:22px 20px;margin-bottom:18px;
      box-shadow:0 20px 40px rgba(124,58,237,.08), 0 6px 12px rgba(6,182,212,.06);
    }
    h1{margin:0 0 6px;font-size:28px}
    .muted{color:var(--muted)}
    .grid{display:grid;gap:14px}
    .kpis{grid-template-columns:repeat(auto-fit,minmax(220px,1fr))}
    .card{
      background:var(--card); border-radius:16px;
      box-shadow:0 10px 30px rgba(0,0,0,.08);
      padding:18px; position:relative; overflow:hidden;
      border:1px solid #e7ecf5;
    }
    .k-title{font-size:13px;color:var(--muted);margin-bottom:6px}
    .k-value{font-size:28px;font-weight:700}
    .pill{display:inline-block;padding:6px 10px;border-radius:999px;font-size:12px}
    .a1{background:rgba(124,58,237,.1);color:#5b21b6}
    .a2{background:rgba(6,182,212,.1);color:#0e7490}
    .a3{background:rgba(34,197,94,.12);color:#166534}
    .a4{background:rgba(245,158,11,.12);color:#92400e}
    .quick{display:flex;gap:10px;flex-wrap:wrap}
    .btn{
      padding:10px 14px;border:0;border-radius:12px;cursor:pointer;
      background:linear-gradient(135deg, var(--accent1), var(--accent2)); color:#fff;
      box-shadow:0 8px 20px var(--ring); text-decoration:none; display:inline-flex; align-items:center; gap:8px;
    }
    .btn.alt{background:linear-gradient(135deg, var(--accent3), var(--accent4))}
    table{width:100%;border-collapse:collapse;font-size:14px}
    th,td{padding:12px;border-bottom:1px solid #eef2f7;text-align:left}
    th{background:#f1f5fb;color:#374151}
    .two{display:grid;gap:14px;grid-template-columns:2fr 1.2fr}
    .bars{display:flex;gap:8px;align-items:flex-end;height:120px}
    .bar{flex:1;background:linear-gradient(180deg, rgba(124,58,237,.8), rgba(6,182,212,.8));border-radius:10px}
    .bar small{display:block;text-align:center;color:var(--muted);margin-top:6px}
  </style>
</head>
<body>
  <div class="topbar">
    <%-- Reuse your nav include if you have one --%>
    <jsp:include page="_nav.jsp" flush="true"/>
  </div>

  <div class="wrap">
    <div class="hero">
      <h1>Admin Dashboard</h1>
      <div class="muted">
        Welcome back — <%= LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) %>
      </div>
    </div>

    <!-- KPI row -->
    <div class="grid kpis">
      <div class="card">
        <div class="pill a1">Today</div>
        <div class="k-title">Sales (LKR)</div>
        <div class="k-value" id="k1">27,000</div>
      </div>
      <div class="card">
        <div class="pill a2">This Week</div>
        <div class="k-title">Revenue (LKR)</div>
        <div class="k-value" id="k2">152,450</div>
      </div>
      <div class="card">
        <div class="pill a3">Customers</div>
        <div class="k-title">Total</div>
        <div class="k-value" id="k3">1,248</div>
      </div>
      <div class="card">
        <div class="pill a4">Items</div>
        <div class="k-title">Active SKUs</div>
        <div class="k-value" id="k4">312</div>
      </div>
    </div>

    <!-- Quick actions -->
    <div class="card" style="margin-top:14px">
      <div class="quick">
        <a class="btn" href="<%= request.getContextPath() %>/app/customers">Manage Customers</a>
        <a class="btn alt" href="<%= request.getContextPath() %>/app/items">Manage Items</a>
        <a class="btn" href="<%= request.getContextPath() %>/app/reports">Open Reports</a>
        <a class="btn alt" href="billing.jsp">Create Bill</a>
      </div>
    </div>

    <!-- Split: recent bills + tiny bar overview -->
    <div class="two" style="margin-top:14px">
      <div class="card">
        <h3 style="margin:0 0 10px">Recent Bills</h3>
        <table>
          <thead><tr><th>ID</th><th>Customer</th><th>Date</th><th>Total (LKR)</th></tr></thead>
          <tbody>
            <tr><td>1027</td><td>AC1003</td><td>2025-08-19</td><td><strong>12,500</strong></td></tr>
            <tr><td>1026</td><td>AC1002</td><td>2025-08-19</td><td><strong>7,500</strong></td></tr>
            <tr><td>1025</td><td>AC1001</td><td>2025-08-19</td><td><strong>7,000</strong></td></tr>
          </tbody>
        </table>
        <div class="muted" style="margin-top:8px">* Replace rows with live data if you want.</div>
      </div>

      <div class="card">
        <h3 style="margin:0 0 10px">Sales (last 7 days)</h3>
        <div class="bars">
          <div style="flex:1">
            <div class="bar" style="height:70%"></div><small>Mon</small>
          </div>
          <div style="flex:1">
            <div class="bar" style="height:45%"></div><small>Tue</small>
          </div>
          <div style="flex:1">
            <div class="bar" style="height:85%"></div><small>Wed</small>
          </div>
          <div style="flex:1">
            <div class="bar" style="height:30%"></div><small>Thu</small>
          </div>
          <div style="flex:1">
            <div class="bar" style="height:95%"></div><small>Fri</small>
          </div>
          <div style="flex:1">
            <div class="bar" style="height:60%"></div><small>Sat</small>
          </div>
          <div style="flex:1">
            <div class="bar" style="height:40%"></div><small>Sun</small>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- cute count-up animation (optional, no libs) -->
  <script>
    function countUp(id, target){
      const el = document.getElementById(id); if(!el) return;
      const t = parseInt(String(target).replace(/[^0-9]/g,''),10) || 0;
      let v = 0, step = Math.max(1, Math.floor(t/60));
      const int = setInterval(()=>{ v += step; if(v>=t){ v=t; clearInterval(int); }
        el.textContent = v.toLocaleString(); }, 16);
    }
    countUp('k1', 27000); countUp('k2', 152450); countUp('k3', 1248); countUp('k4', 312);
  </script>
</body>
</html>
