<%@ page contentType="text/html;charset=UTF-8" %>
<%
  jakarta.servlet.http.HttpSession ses = request.getSession(false);
  String role = (ses == null) ? null : (String) ses.getAttribute("role");
  if (role == null) {
    response.sendRedirect(request.getContextPath() + "/login.jsp");
    return;
  }
  if ("ADMIN".equals(role)) {
    request.getRequestDispatcher("admin_dashboard.jsp").forward(request, response);
  } else {
    request.getRequestDispatcher("cashier_dashboard.jsp").forward(request, response);
  }
%>
