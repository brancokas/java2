package oprpp2.hw03.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@WebServlet(urlPatterns = "/glasanje-glasaj")
public class GlasanjeGlasajServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fileName = req.getServletContext().getRealPath("/WEB-INF/glasanje/glasanje-rezultati.txt");

        File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
        }

        Map<Integer, Integer> votesMap = Glasanje.getResults(fileName);

        String id = req.getParameter("id");
        try {
            Integer uid = Integer.parseInt(id);
            if (!votesMap.containsKey(uid))
                votesMap.put(uid, 0);
            votesMap.put(uid, votesMap.get(uid) + 1);

            OutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
            for (Integer key : votesMap.keySet()) {
                String output = String.format("%d\t%d\n", key, votesMap.get(key));
                stream.write(output.getBytes(StandardCharsets.UTF_8));
            }

            stream.flush();
            stream.close();
        } catch (Exception e) {}

        resp.sendRedirect(req.getContextPath() + "/glasanje-rezultati");
    }
}
