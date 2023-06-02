package hr.fer.zemris.client;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        if (args.length != 3) {
            System.out.println("Ocekivao sam ip port string");
        }
        int port;
        String ipAdress, name;
        ipAdress = args[0];
        port = Integer.valueOf(args[1]);
        name = args[2];

        Client client = new Client(ipAdress, port, name);
        boolean connected = client.connect();
        if (connected)
            SwingUtilities.invokeLater(() -> new ClientGUI(client).setVisible(true));
    }
}
