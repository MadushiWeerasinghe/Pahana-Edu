<%@ page contentType="text/html;charset=UTF-8" %>
<!doctype html>
<html>
<head><title>Error</title></head>
<body>
  <h2 style="color:#b00020;">Oops: <%= request.getAttribute("error") %></h2>
</body>
</html>
