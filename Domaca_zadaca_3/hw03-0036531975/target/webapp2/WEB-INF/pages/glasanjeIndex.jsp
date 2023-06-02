<%@ page import="java.util.Map" %><%--
  Created by IntelliJ IDEA.
  User: bstankovic
  Date: 24.05.2023.
  Time: 04:54
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <h1>Glasanje za omiljeni bend:</h1>
    <p>Od sljedećih bendova, koji Vam je bend najdraži? Kliknite na link kako biste glasali!</p>
    <ol>
    <%
        Map<Integer, String> names = (Map<Integer, String>) request.getAttribute("bandNames");
        for (Integer id : names.keySet()) {
            String ispis = String.format("<li><a href=\"glasanje-glasaj?id=%d\">%s</a></li>", id, names.get(id));
            out.print(ispis);
        }
    %>
    </ol>
</body>
</html>
