package hr.fer.zemris.java.webserver.workers;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

public class Home implements IWebWorker {
    @Override
    public void processRequest(RequestContext requestContext) throws Exception {
        String color = requestContext.getPersistentParameter("bgcolor");
        if (color == null)
            color = "7F7F7F";
        requestContext.setTemporaryParameter("background", color);

        requestContext.getDispatcher().dispatchRequest("./private/pages/home.smscr");
    }
}
