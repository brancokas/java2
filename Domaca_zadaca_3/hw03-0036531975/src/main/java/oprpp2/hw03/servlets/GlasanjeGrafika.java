package oprpp2.hw03.servlets;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oprpp2.hw03.servlets.GlasanjeRezultatiServlet.Atom;
@WebServlet(urlPatterns = "/glasanje-grafika")
public class GlasanjeGrafika extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fileName = req.getServletContext().getRealPath("/WEB-INF/glasanje/glasanje-rezultati.txt");
        String fileDef = req.getServletContext().getRealPath("/WEB-INF/glasanje/glasanje-definicija.txt");

        List<Atom> atoms = getAtoms(fileDef, fileName);

        resp.setContentType("image/png");

        JFreeChart chart = getChart(atoms);

        OutputStream stream = resp.getOutputStream();
        int width = 500;
        int height = 500;
        ChartUtils.writeChartAsPNG(stream, chart, width, height);
    }

    private List<Atom> getAtoms(String fileDef, String fileName) throws IOException {
        List<String> definitions = Files.readAllLines(Path.of(fileDef));

        Map<Integer, Integer> valMap = Glasanje.getResults(fileName);

        List<Atom> atoms = new ArrayList<>();
        for (String definition : definitions) {
            String[] attr = definition.split("\t");
            int id = Integer.valueOf(attr[0]);
            if (valMap.containsKey(id)) {
                atoms.add(new Atom(attr[1], valMap.get(id)));
            }
        }

        return atoms;
    }

    public JFreeChart getChart(List<Atom> atoms) {
        DefaultPieDataset dataset = new DefaultPieDataset<>();

        for (Atom atom : atoms) {
            dataset.setValue(atom.getName(), atom.getValue());
        }


        boolean legend = true;
        boolean tooltips = false;
        boolean urls = false;

        JFreeChart chart = ChartFactory.createPieChart("OS Usage", dataset, legend, tooltips, urls);

        chart.setBorderPaint(Color.YELLOW);
        chart.setBorderStroke(new BasicStroke(5.0f));
        chart.setBorderVisible(true);

        return chart;
    }
}
