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

@WebServlet(name = "image", urlPatterns = "/reportedImage")
public class PieChart extends HttpServlet {


        @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("image/png");

        OutputStream stream = resp.getOutputStream();
        JFreeChart chart = getChart();
        int width = 500;
        int height = 350;
        ChartUtils.writeChartAsPNG(stream, chart, width, height);

    }

    public JFreeChart getChart() {
        DefaultPieDataset dataset = new DefaultPieDataset<>();
        dataset.setValue("Linux", 25);
        dataset.setValue("MACOS", 35);
        dataset.setValue("Windows", 40);

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
