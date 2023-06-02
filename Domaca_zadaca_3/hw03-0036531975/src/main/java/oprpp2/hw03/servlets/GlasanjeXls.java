package oprpp2.hw03.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import oprpp2.hw03.servlets.GlasanjeRezultatiServlet.Atom;
import oprpp2.hw03.servlets.Glasanje.TriAtom;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

@WebServlet(urlPatterns = "/glasanje-xls")
public class GlasanjeXls extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fileName = req.getServletContext().getRealPath("/WEB-INF/glasanje/glasanje-rezultati.txt");
        String fileDef = req.getServletContext().getRealPath("/WEB-INF/glasanje/glasanje-definicija.txt");

        List<TriAtom> atoms = Glasanje.getDefinitions(fileDef);
        Map<Integer, Integer> votes = Glasanje.getResults(fileName);

        List<Atom> results = new ArrayList<>();
        for (TriAtom atom : atoms) {
            if (votes.containsKey(atom.getId())) {
                results.add(new Atom(atom.getName(), votes.get(atom.getId())));
            }
        }
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Results of votes");
        int brojac = 1;
        for (Atom atom : results) {
            HSSFRow row = sheet.createRow(brojac++);
            row.createCell(0).setCellValue(atom.getName());
            row.createCell(1).setCellValue(atom.getValue());
        }

        resp.setContentType("application/vnd.ms-excel");
        resp.setHeader("Content-Disposition", "attachment; filename=\"tablica-votes.xls\"");
        resp.getOutputStream().write(workbook.getBytes());

    }
}
