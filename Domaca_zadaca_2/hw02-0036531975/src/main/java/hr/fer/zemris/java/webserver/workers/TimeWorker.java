package hr.fer.zemris.java.webserver.workers;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

import java.time.LocalDateTime;
import java.util.Random;

public class TimeWorker implements IWebWorker {
    @Override
    public void processRequest(RequestContext requestContext) throws Exception {
        LocalDateTime dateTime = LocalDateTime.now();
        String currDate = dateTime.toString();
        double random = new Random().nextLong();


        requestContext.setTemporaryParameter("time", currDate);
        String img = "./../../images/";
        String parImg = requestContext.getPersistentParameter("image");
        if (parImg != null && (parImg.equals("poz.png") || parImg.equals("neg.png")))
            img += parImg;
        else if (random < 0) {
            img += "poz.png";
        } else {
            img += "neg.png";
        }
        requestContext.setTemporaryParameter("imgName", img);
        requestContext.getDispatcher().dispatchRequest("./private/pages/time.smscr");
    }
}
