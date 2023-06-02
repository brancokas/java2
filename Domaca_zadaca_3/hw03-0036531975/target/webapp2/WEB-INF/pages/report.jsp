<%@ page session="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head>
  <title>OS usage</title>
</head>
<%!
  public String getColor(HttpSession session) {
    Object color = session.getAttribute("pickedBgCol");
    if (color == null)
      return "white";
    return color.toString();
  }
%>
  <body style="background-color: <% out.print(getColor(session));%>">
    <h1>OS usage</h1>
    <p style="font-size: large">Here are the results of OS usage in survey that we completed.</p>

    <img src="/webapp2/reportedImage">
  </body>
</html>