package oprpp2.hw03.servlets;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "powers", urlPatterns = "/powers")
public class Power extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String aParam = req.getParameter("a");
        String bParam = req.getParameter("b");
        String nParam = req.getParameter("n");
        int a=0, b=0, n=0;

        if (aParam == null || bParam == null || nParam == null)
            req.getRequestDispatcher("/WEB-INF/pages/error.jsp").forward(req, resp);
        try {
            a = Integer.parseInt(aParam);
            b = Integer.parseInt(bParam);
            n = Integer.parseInt(nParam);
        } catch (NumberFormatException e) {
            req.getRequestDispatcher("/WEB-INF/pages/error.jsp").forward(req, resp);
        }
        if (a < -100 || a > 100 || b < -100 || b > 100 || n < 1 || n > 5)
            req.getRequestDispatcher("/WEB-INF/pages/error.jsp").forward(req, resp);

        HSSFWorkbook workbook = new HSSFWorkbook();
        for (int i = 0; i < n; i++) {
            HSSFSheet sheet = workbook.createSheet(String.valueOf(i+1));
            if (a <= b) {
                for (int j = a; j <= b; j++) {
                    makeRow(sheet, a, i, j);
                }
            } else {
                for (int j = a; j >= b; j--) {
                    makeRow(sheet, a, i, j);
                }
            }
        }
        resp.setContentType("application/vnd.ms-excel");
        resp.setHeader("Content-Disposition", "attachment; filename=\"tablica.xls\"");
        resp.getOutputStream().write(workbook.getBytes());

    }

    private void makeRow(HSSFSheet sheet, int a, int i, int j) {
        HSSFRow row = sheet.createRow(Math.abs(j - a));
        row.createCell(0).setCellValue(j);
        row.createCell(1).setCellValue(Math.pow(j, i+1));
    }
}
