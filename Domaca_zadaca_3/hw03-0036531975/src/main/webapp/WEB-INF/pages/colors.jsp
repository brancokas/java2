<%@ page session="true" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>

<html>
<head>
  <title>Izaberi boju</title>
</head>
  <%!
    public String getColor(HttpSession session) {
      Object color = session.getAttribute("pickedBgCol");
      if (color == null)
        return "white";
      return color.toString();
    }
  %>
  <body style="background-color: <% out.print(getColor(session)); %>">
    <a href="/webapp2/setcolor/white">WHITE</a>
    <a href="/webapp2/setcolor/red">RED</a>
    <a href="/webapp2/setcolor/green">GREEN</a>
    <a href="/webapp2/setcolor/cyan">CYAN</a>
  </body>
</html>