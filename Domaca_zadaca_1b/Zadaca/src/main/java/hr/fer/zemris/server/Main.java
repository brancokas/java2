package hr.fer.zemris.server;

public class Main {
    public static void main(String[] args) {
        int port;
        boolean drop = false;

        if (args.length != 1 && args.length != 2) {
            System.out.println("Enter valid port number from 0-65535 or/and drop syntax.");
            return;
        }
        try {
            port = Integer.valueOf(args[0]);
        } catch (NumberFormatException exception) {
            System.out.println("Enter valid port number from 0-65535.");
            return;
        }
        if (args.length == 2 && args[1].equals("-d"))
            drop = true;

        Server server = new Server(port, drop);
        server.serve();
    }
}