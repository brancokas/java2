package oprpp2.hw03.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "trigonometry", urlPatterns = "/trigonometric")
public class Trigonometry extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int a = 0, b = 360;
        String aParam = req.getParameter("a");
        String bParam = req.getParameter("b");
        try {
            a = Integer.parseInt(aParam);
        } catch (NumberFormatException e) {}
        try {
            b = Integer.parseInt(bParam);
        } catch (NumberFormatException e) {}
        if (a > b) {
            int pom = b;
            b = a;
            a = pom;
        }
        if (b > a+720) {
            b = a + 720;
        }
        List<Double> sin = new ArrayList<>(b-a+1), cos = new ArrayList<>(b-a+1);
        List<Integer> numbers = new ArrayList<>(b-a+1);
        for (int i = a; i <= b; i++) {
            sin.add(Math.sin(i));
            cos.add(Math.cos(i));
            numbers.add(i);
        }

        req.setAttribute("sin", sin);
        req.setAttribute("cos", cos);
        req.setAttribute("numbers", numbers);

        req.getRequestDispatcher("/WEB-INF/pages/trigonometric.jsp").forward(req, resp);

    }
}
