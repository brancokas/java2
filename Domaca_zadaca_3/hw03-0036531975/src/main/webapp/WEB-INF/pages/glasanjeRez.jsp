<%@ page import="java.util.List" %>
<%@ page import="oprpp2.hw03.servlets.GlasanjeRezultatiServlet.Atom" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<html>
<head>
    <title>Rezultati glasanja</title>
    <style type="text/css">
        table.rez td {text-align: center;}
    </style>
</head>
<body>
<h1>Rezultati glasanja</h1>
<p>Ovo su rezultati glasanja.</p>
<table border="1" cellspacing="0" class="rez">
    <thead><tr><th>Bend</th><th>Broj glasova</th></tr></thead>
    <tbody>
  <%
      List<Atom> atoms = (List<Atom>) request.getAttribute("rezultati");
      for (Atom atom : atoms) {
          String output = String.format("<tr><td>%s</td><td>%d</td></tr>", atom.getName(), atom.getValue());
          out.print(output);
      }
  %>
    </tbody>
</table>

    <h2>Grafički prikaz rezultata</h2>
    <img alt="Pie-chart" src="/webapp2/glasanje-grafika" width="400" height="400" />
    </br>

    <h2>Rezultati u XLS formatu</h2>
        <p>Rezultati u XLS formatu dostupni su <a href="/webapp2/glasanje-xls">ovdje</a></p>
        <%
            if (atoms != null) {
            out.print("<h2 > Razno </h2 >");
            out.print("<ul>");
                List<Atom> winners = new ArrayList<>();
                int max = atoms.get(0).getValue();
                for (Atom atom : atoms) {
                    if (atom.getValue() == max)
                        winners.add(atom);
                }
                if (winners.size() == 1) {
                    out.print("<p>Primjer pjesme pobjedničkog benda:</p>\n");
                } else {
                    out.print("<p>Primjeri pjesama pobjedničkih bendova:</p>\n");
                }
                for (Atom atom : winners) {
                    String output = String.format("<li><a href=\"%s\">%s</a></li>", atom.getUrl(), atom.getName());
                    out.print(output);
                }
            out.print("</ul");
            }
        %>
</br>
    <a href="glasanje">Klik za novo glasanje</a>
    </br>
    <a href="/webapp2/">Klik za Home stranicu</a>
</body>
</html>
