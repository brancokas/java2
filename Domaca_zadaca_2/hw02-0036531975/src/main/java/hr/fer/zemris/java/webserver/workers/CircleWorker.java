package hr.fer.zemris.java.webserver.workers;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CircleWorker implements IWebWorker {
    @Override
    public void processRequest(RequestContext requestContext) throws Exception {
        BufferedImage bim = new BufferedImage(200, 200, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2d = bim.createGraphics();
        // do drawing...
//        Ellipse2D circle = new Ellipse2D.Double(0,0,200,200);
//            g2d.fill(circle);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0,0,200,200);
        g2d.setColor(Color.RED);
        g2d.fillOval(0,0,200,200);
        g2d.dispose();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bim, "png", bos);
            synchronized (requestContext) {
                requestContext.setMimeType("image/png");
                requestContext.write(bos.toByteArray());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
