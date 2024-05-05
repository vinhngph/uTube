<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>JSP - Hello World</title>
</head>
<body>
<h1><%= "Hello World!" %>
</h1>
<br/>
<%
    Cookie[] cookies = request.getCookies();
    String name = null;
    if (cookies != null) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("user_name")) {
                name = cookie.getValue();
            }
        }
    }
%>

<% if (name != null) { %>
<h1>Hello <%= name%>
</h1>
<% } else { %>
<a href="./login">Login</a>
<br>
<a href="./register">Register</a>
<% } %>
</body>
</html>