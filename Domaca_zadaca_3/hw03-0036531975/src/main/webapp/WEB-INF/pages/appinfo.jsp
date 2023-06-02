<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" session="true" %>
<%@ page import="java.time.Duration" %>
<html>
<head>
  <title>App Info</title>
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
  <%
    Long startTime = (Long) application.getAttribute("startTime");
    Long currentTime = System.currentTimeMillis();

    long durationInMillis = currentTime - startTime;
    long millis = durationInMillis % 1000;
    long second = (durationInMillis / 1000) % 60;
    long minute = (durationInMillis / (1000 * 60)) % 60;
    long hour = (durationInMillis / (1000 * 60 * 60)) % 24;

    String time = String.format("%02d:%02d:%02d.%d", hour, minute, second, millis);
    out.print("<h1>Proteklo vrijeme od pocetka rada aplikacije je: " + time + "</h1>");
  %>
</body>
</html>