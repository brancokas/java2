package oprpp2.hw03.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@WebServlet(urlPatterns = "/glasanje-rezultati")
public class GlasanjeRezultatiServlet extends HttpServlet {

    public static class Atom implements Comparable<Atom> {
        private String name;
        private Integer value;
        private String url;

        public Atom(String name, Integer value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public Integer getValue() {
            return value;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public int compareTo(Atom o) {
            if (value > o.value)
                return 1;
            if (value < o.value)
                return -1;
            return -name.compareTo(o.name);
        }
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fileName = req.getServletContext().getRealPath("/WEB-INF/glasanje/glasanje-rezultati.txt");
        String fileDef = req.getServletContext().getRealPath("/WEB-INF/glasanje/glasanje-definicija.txt");

        File file = new File(fileName);
        if (!file.exists())
            file.createNewFile();


        Map<Integer, Integer> map = Glasanje.getResults(fileName);

        List<String> input = Files.readAllLines(Path.of(fileDef));

        List<Atom> atomList = new ArrayList<>();
        for (String redak : input) {
            String[] attr = redak.split("\t");
            int id = Integer.parseInt(attr[0]);
            atomList.add(new Atom(attr[1], map.getOrDefault(id, 0)));
            atomList.get(atomList.size()-1).setUrl(attr[2]);
        }

        Collections.sort(atomList);
        Collections.reverse(atomList);

        req.setAttribute("rezultati", atomList);

        req.getRequestDispatcher("/WEB-INF/pages/glasanjeRez.jsp").forward(req, resp);
    }
}
