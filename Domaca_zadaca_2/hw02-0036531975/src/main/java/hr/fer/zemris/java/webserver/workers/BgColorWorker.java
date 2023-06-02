package hr.fer.zemris.java.webserver.workers;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

public class BgColorWorker implements IWebWorker {
    @Override
    public void processRequest(RequestContext requestContext) throws Exception {
        String colorValue = requestContext.getParameter("bgcolor");
        if (!(colorValue == null || colorValue.length() != 6 || !isHexColor(colorValue)))
            requestContext.setPersistentParameter("bgcolor", colorValue);

        requestContext.getDispatcher().dispatchRequest("/index2.html");
    }

    private boolean isHexColor(String colorValue) {
        colorValue = colorValue.toUpperCase();
        for (int i = 1; i < colorValue.length(); i++) {
            if (!(colorValue.charAt(i) >= '0' && colorValue.charAt(i) <= '9' ||
                colorValue.charAt(i) == 'A' || colorValue.charAt(i) == 'B' ||
                colorValue.charAt(i) == 'C' || colorValue.charAt(i) == 'D' ||
                    colorValue.charAt(i) == 'E' || colorValue.charAt(i) == 'F'))
                return false;
        }
        return true;
    }
}
