<%@ page import="java.util.Random" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Smijesna prica</title>
</head>
<body>
    <p style="color: <%= choose() %>">
        Smijesna prica...
        Otisao covjek kod svog kuma u njemcaku u minken da se vide. Krenuo on prema njemackoj krajem srpnja 1982.
        godine. No u to vrijeme gps nije bilo te jadan covjek nije imao ni kartu vec samo dobru volju. I tako je kruzio i kruzio
        i prode pet dana te covjek i dalje nije nasao kuma. Napokon je zavrsio u stanici i objasnio policajcima rukama i nogama
        gdje je krenuo. Kako jadni covjek nije imao nista za jesti bio je jako gladan te su mu dobri policajci ispekli cijelo pile.
        Policajac ostavi covjeku pile te se vrati da ga obide nakon 10 minuta. No na tanjuru nista, nema ni kosti.
        Stoga ga upita policajac gdje si s kostima, na to ce mu kum: Ma kake kosti sama hrskavica jarane.
    </p>
</body>
</html>
<%!
    private String choose() {
        Integer number = Math.abs(new Random().nextInt());
        switch (number % 5) {
            case 0:
                return "black";
            case 1:
                return "yellow";
            case 2:
                return "red";
            case 3:
                return "blue";
            default:
                return "green";
        }
    }
%>