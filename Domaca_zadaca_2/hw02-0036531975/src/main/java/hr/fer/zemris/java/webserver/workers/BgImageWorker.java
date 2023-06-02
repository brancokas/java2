package hr.fer.zemris.java.webserver.workers;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

public class BgImageWorker implements IWebWorker {
    @Override
    public void processRequest(RequestContext requestContext) throws Exception {
        String img = requestContext.getParameter("bgImage");
        requestContext.setPersistentParameter("image", img);

        requestContext.getDispatcher().dispatchRequest("/mi/vrijeme");
    }
}
