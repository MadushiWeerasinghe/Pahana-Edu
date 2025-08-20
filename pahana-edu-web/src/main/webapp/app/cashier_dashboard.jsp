<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.time.*, java.time.format.*" %>
<!doctype html>
<html>
<head>
  <title>Cashier Dashboard</title>
  <style>
    :root{
      --bg:#f7fafc; --nav:#24355a; --card:#ffffff; --text:#111827; --muted:#6b7280;
      --blue:#2563eb; --pink:#ec4899; --lime:#84cc16; --orange:#f97316;
    }
    *{box-sizing:border-box}
    body{margin:0;background:var(--bg);font-family:system-ui,Segoe UI,Roboto}
    .topbar{background:var(--nav);color:#fff;padding:14px 18px}
    .wrap{max-width:1100px;margin:24px auto;padding:0 16px}
    .hero{background:linear-gradient(135deg, rgba(37,99,235,.12), rgba(236,72,153,.12));
      border-radius:18px;padding:18px;border:1px solid rgba(37,99,235,.2); box-shadow:0 20px 40px rgba(37,99,235,.08)}
    h1{margin:0 0 6px}
    .muted{color:var(--muted)}
    .grid{display:grid;gap:14px}
    .kpis{grid-template-columns:repeat(auto-fit,minmax(220px,1fr))}
    .card{background:var(--card);border-radius:16px;box-shadow:0 10px 30px rgba(0,0,0,.08);padding:18px;border:1px solid #e7ecf5}
    .k-title{font-size:13px;color:var(--muted);margin-bottom:6px}
    .k-value{font-size:26px;font-weight:700}
    .two{display:grid;gap:14px;grid-template-columns:1.2fr 1fr}

    /* Buttons row: increased horizontal + vertical spacing */
    .actions{
      display:flex;
      gap:20px;                 /* space BETWEEN the two buttons */
      flex-wrap:wrap;
      align-items:center;
      margin-bottom:26px;       /* vertical gap ABOVE the search block */
    }
    .actions .btn{ min-width:180px } /* keep buttons visually balanced */

    .btn{padding:10px 14px;border:0;border-radius:12px;color:#fff;text-decoration:none;display:inline-block;box-shadow:0 10px 20px rgba(0,0,0,.1)}
    .btn.blue{background:linear-gradient(135deg,#2563eb,#06b6d4)}
    .btn.pink{background:linear-gradient(135deg,#ec4899,#f97316)}
    input[type="text"]{padding:10px;border:1px solid #d6dbe7;border-radius:10px;width:100%}

    /* Search block explicitly starts flush (gap comes from .actions) */
    .search-block{ margin-top:0 }

    /* Centered Shortcuts card */
    .shortcuts{
      min-height:240px;
      display:flex; flex-direction:column; align-items:center; justify-content:center; gap:14px;
    }
    .icon-wrap{
      width:96px;height:96px;border-radius:50%;
      background:linear-gradient(135deg, rgba(37,99,235,.18), rgba(6,182,212,.18));
      display:grid; place-items:center; box-shadow:0 10px 24px rgba(37,99,235,.15);
      border:1px solid rgba(37,99,235,.25);
    }
    .icon-wrap svg{width:46px;height:46px;fill:#2563eb}
  </style>
</head>
<body>
  <div class="topbar">
    <jsp:include page="_nav.jsp" flush="true"/>
  </div>

  <div class="wrap">
    <div class="hero">
      <h1>Cashier Dashboard</h1>
      <div class="muted"><%= LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, yyyy-MM-dd")) %></div>
    </div>

    <!-- KPIs -->
    <div class="grid kpis" style="margin-top:14px">
      <div class="card"><div class="k-title">Bills today</div><div class="k-value" id="c1">0</div></div>
      <div class="card"><div class="k-title">Today’s sales (LKR)</div><div class="k-value" id="c2">0</div></div>
      <div class="card"><div class="k-title">Customers served</div><div class="k-value" id="c3">0</div></div>
    </div>

    <!-- Actions + Shortcuts -->
    <div class="two" style="margin-top:14px">
      <div class="card">
        <h3 style="margin-top:0">Quick Actions</h3>

        <!-- Buttons row -->
        <div class="actions">
          <a class="btn blue" href="<%= request.getContextPath() %>/app/billing">Create New Bill</a>
          <a class="btn pink" href="<%= request.getContextPath() %>/app/customers">View Customers</a>
        </div>

        <!-- Search block (spacing comes from .actions margin-bottom) -->
        <div class="search-block">
          <form method="get" action="<%= request.getContextPath() %>/app/customers">
            <label class="muted">Search customer (name or account #)</label>
            <input type="text" name="q" placeholder="e.g. AC1001 or Nimal" />
            <button class="btn blue" type="submit" style="margin-top:8px">Search</button>
          </form>
        </div>
      </div>

      <div class="card">
        <h3 style="margin-top:0">Shortcuts</h3>
        <div class="shortcuts">
          <div class="icon-wrap" aria-hidden="true">
            <!-- inline SVG icon (box/items) -->
            <svg viewBox="0 0 24 24">
              <path d="M21 7.5l-9-4.5-9 4.5 9 4.5 9-4.5zm-9 6l-9-4.5V18l9 4.5 9-4.5V9l-9 4.5z"/>
            </svg>
          </div>
          <a class="btn blue" href="<%= request.getContextPath() %>/app/items">Items</a>
        </div>
      </div>
    </div>

    <!-- Today’s Bills (placeholder) -->
    <div class="card" style="margin-top:14px">
      <h3 style="margin-top:0">Today’s Bills</h3>
      <div class="muted">No bills yet. Create your first bill for today!</div>
    </div>
  </div>

  <script>
    // Optional mini count-up
    function up(id, n){ const e=document.getElementById(id); if(!e) return;
      let v=0, step=Math.max(1,Math.floor(n/50)); const t=setInterval(()=>{v+=step; if(v>=n){v=n;clearInterval(t)} e.textContent=v.toLocaleString()},16);
    }
    up('c1', 0); up('c2', 0); up('c3', 0);
  </script>
</body>
</html>
