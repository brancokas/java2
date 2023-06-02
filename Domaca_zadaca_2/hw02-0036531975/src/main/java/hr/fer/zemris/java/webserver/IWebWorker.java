package hr.fer.zemris.java.webserver;

public interface IWebWorker {

    void processRequest(RequestContext requestContext) throws Exception;
}
