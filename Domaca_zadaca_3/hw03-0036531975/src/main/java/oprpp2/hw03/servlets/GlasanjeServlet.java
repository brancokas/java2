package oprpp2.hw03.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@WebServlet(name = "glasanje", urlPatterns = "/glasanje")
public class GlasanjeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fileName = req.getServletContext().getRealPath("/WEB-INF/glasanje/glasanje-definicija.txt");

        List<String> bands = Files.readAllLines(Path.of(fileName));

        Map<Integer, String> idNames = new TreeMap<>();
        Map<Integer, String> idVideos = new HashMap<>(bands.size());

        for (String band : bands) {
            String[] attr = band.split("\t");
            int id = Integer.parseInt(attr[0]);
            idNames.put(id, attr[1]);
            idVideos.put(id, attr[2]);
        }

        req.setAttribute("bandNames", idNames);
        req.setAttribute("bandVideos", idVideos);

        req.getRequestDispatcher("/WEB-INF/pages/glasanjeIndex.jsp").forward(req, resp);
    }
}
