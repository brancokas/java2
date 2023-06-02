package hr.fer.zemris.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientGUI extends JFrame {
    private Client client;
    private ExecutorService pool;
    public ClientGUI(Client client) {
        this.client = client;
        pool = Executors.newCachedThreadPool();

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        JFrame frame = this;
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (JOptionPane.showConfirmDialog(frame, "Zelite li izaci iz chata?",
                        "Zatvori prozor?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_NO_OPTION) {
                    client.disconnect();
                    System.exit(0);
                }
            }
        });

        setLocation(50,50);
        setSize(500,500);
        setTitle("Chat client: " + client.getName());

        initGui();

        Thread messages = new Thread(() -> client.waitForNewMessages());
        messages.setDaemon(true);
        messages.start();
    }

    private void initGui() {
        BorderLayout borderLayout = new BorderLayout();
        JTextArea textArea = new JTextArea();
        JTextField textField = new JTextField();

        textArea.setEditable(false);

        getContentPane().setLayout(borderLayout);

        getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER);
        getContentPane().add(textField, BorderLayout.NORTH);

        textField.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = ((JTextField)e.getSource()).getText();
                textField.setText(null);
                pool.submit(() -> client.sendMessage(message));
            }
        });

        client.addModelListener((text -> textArea.append(text + '\n')));

    }
}
