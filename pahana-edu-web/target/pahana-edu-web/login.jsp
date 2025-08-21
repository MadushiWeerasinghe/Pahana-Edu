<%@ page contentType="text/html;charset=UTF-8" %>
<!doctype html>
<html>
<head>
  <title>Pahana Edu - Login</title>
  <style>
    body {
      font-family: system-ui, Arial, sans-serif;
      background: #f6f8fb;
      margin: 0;
    }
    .container {
      max-width: 420px;
      margin: 7vh auto;
      background: #fff;
      border-radius: 16px;
      box-shadow: 0 10px 30px rgba(0,0,0,.08);
      padding: 28px;
    }
    h1 {
      font-size: 24px;
      margin: 0 0 20px;
      color: #2b3a67;
    }
    label {
      display: block;
      margin: 12px 0 6px;
      color: #3a4466;
    }
    input[type=text],
    input[type=password] {
      width: 100%;
      padding: 12px;
      border: 1px solid #dce1f0;
      border-radius: 10px;
    }
    button {
      margin-top: 16px;
      padding: 12px 16px;
      border: 0;
      background: #2b6cb0;
      color: #fff;
      border-radius: 10px;
      width: 100%;
    }
    .err {
      color: #b00020;
      margin-top: 10px;
    }
  </style>
</head>
<body>
  <div class="container">
    <h1>Sign in</h1>
    <form method="post" action="auth">
      <label>Username</label>
      <input type="text" name="username" required />
      <label>Password</label>
      <input type="password" name="password" required />
      <button type="submit">Login</button>

      <!-- ✅ Show a clean error message (no HTTP code, no quotes) -->
      <%
        String rawErr = (String) request.getAttribute("error");
        if (rawErr != null) {
          // Remove any leading "Login failed:" and "HTTP ###:" parts, and strip quotes
          String cleaned = rawErr
            .replaceAll("(?i)^\\s*login failed:\\s*", "")
            .replaceAll("(?i)^\\s*http\\s*\\d+\\s*:\\s*", "")
            .replace("\"", "")
            .trim();
      %>
        <div class="err">Login failed: <%= cleaned %></div>
      <%
        }
      %>
    </form>
  </div>
</body>
</html>
