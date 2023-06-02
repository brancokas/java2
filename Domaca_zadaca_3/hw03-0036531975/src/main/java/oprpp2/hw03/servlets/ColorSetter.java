package oprpp2.hw03.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "color-setter", urlPatterns = "/setcolor/*")
public class ColorSetter extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        String color = "white";
        if (path != null) {
            String colorName = path.substring(1);
            if (colorName.equals("white") || colorName.equals("red") || colorName.equals("green") ||
                    colorName.equals("cyan"))
                color = colorName;
        }
        req.getSession().setAttribute("pickedBgCol", color);
        resp.sendRedirect("/webapp2/");
    }
}
