<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" session="true" %>
<%!
  public String getColor(HttpSession session) {
    Object color = session.getAttribute("pickedBgCol");
    if (color == null)
      return "white";
    return color.toString();
  }
%>

<%!
  private String getRow(String first, String second, String third) {
    return String.format("<tr><th>%s</th><th>%s</th><th>%s</th></tr>", first, second, third);
  }
%>

<html>
<head>
  <title>Trigonometrijske funkcije</title>
</head>
<body style="background-color: <% out.print(getColor(session));%>">
  <table>
  <%
    List<Double> sin = (List<Double>) request.getAttribute("sin");
    List<Double> cos = (List<Double>) request.getAttribute("cos");
    List<Integer> numbers = (List<Integer>) request.getAttribute("numbers");
    out.print(getRow("X", "Sin(x)", "Cos(x)"));
    for (int i = 0; i < numbers.size(); i++) {
      String row = getRow(numbers.get(i).toString(), sin.get(i).toString(), cos.get(i).toString());
      out.print(row);
    }
  %>
  </table>
</body>
</html>
