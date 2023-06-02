package hr.fer.zemris.java.webserver.workers;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

public class SumWorker implements IWebWorker {
    @Override
    public void processRequest(RequestContext requestContext) throws Exception {
        Integer a = 1, b = 2;
        String varA = requestContext.getParameter("a");
        String varB = requestContext.getParameter("b");

        try {
            a = varA == null ? 1 : Integer.parseInt(varA);
            b = varB == null ? 2 : Integer.parseInt(varB);
        } catch (NumberFormatException e) {}

        Integer zbroj = a+b;

        requestContext.setTemporaryParameter("varA", a.toString());
        requestContext.setTemporaryParameter("varB", b.toString());
        requestContext.setTemporaryParameter("zbroj", zbroj.toString());

        String imgPathDef = "./../../images/";
        String imgName = zbroj % 2 == 0 ? imgPathDef + "poz.png" : imgPathDef + "neg.png";

        requestContext.setTemporaryParameter("imgName", imgName);

        requestContext.getDispatcher().dispatchRequest("/private/pages/calc.smscr");
    }
}
