package hr.fer.zemris.java.webserver;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class SmartHttpServerMain {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (args.length != 1) {
            System.out.println("Requested server.properties path");
            return;
        }
        SmartHttpServer httpServer = new SmartHttpServer(args[0]);
        httpServer.start();
    }
}
