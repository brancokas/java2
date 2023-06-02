package hr.fer.zemris.java.webserver.workers;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

import java.util.Set;

public class EchoParams implements IWebWorker {
    @Override
    public void processRequest(RequestContext requestContext) throws Exception {
        Set<String> userParameters = requestContext.getParameterNames();
        requestContext.setMimeType("text/html");

        synchronized (requestContext) {
            requestContext.write("<html><body>");
            requestContext.write("<table border=\"2\">" +
                    "<thead><tr><th>Key</th><th>Value</th></tr></thead><tbody>");
            for (String key : userParameters) {
                requestContext.write("<tr><td>" + key + "</td><td>" +
                        requestContext.getParameter(key) + "</td></tr>");
            }
            requestContext.write("</tbody></table>");
            requestContext.write("</body></html>");
        }
    }
}
