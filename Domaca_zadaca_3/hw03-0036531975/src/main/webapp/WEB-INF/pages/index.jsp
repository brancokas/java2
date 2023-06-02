<%@ page session="true" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<html>
<head>
    <title>Pocetna stranica</title>
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
        <a href="/webapp2/colors">
            Background color chooser
        </a>
        </br>
        <a href="trigonometric?b=90">Trigonometry page</a>

        <form action="trigonometric" method="GET">
            Početni kut:<br><input type="number" name="a" min="0" max="360" step="1" value="0"><br>
            Završni kut:<br><input type="number" name="b" min="0" max="360" step="1" value="360"><br>
            <input type="submit" value="Tabeliraj"><input type="reset" value="Reset">
        </form>
    </br>
    <a href="/webapp2/funny-story">Skoci na smijesnu pricu</a>
    </br>
    <a href="/webapp2/report">Pie Chart</a>
    </br>
    <a href="powers?a=1&b=100&n=3">Uzmi tablicu</a>
    </br>
    <a href="appinfo">Vrijeme rada aplikacije</a>
    </body>
</html>