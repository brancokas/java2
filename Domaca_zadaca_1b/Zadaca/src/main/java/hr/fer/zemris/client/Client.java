package hr.fer.zemris.client;

import java.io.*;
import java.net.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Client implements ModelListener {
    private String ip;
    private int port;
    private String name;
    private DatagramSocket socket;
    private List<MyListener> listeners;
    private long noOfPacket = 0;
    private long uid = new Random().nextLong();
    private Set<Long> recivedMessages;
    private Set<Long> incomingAck;


    public Client(String ip, int port, String name) {
        this.ip = ip;
        this.port = port;
        this.name = name;

        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        listeners = new LinkedList<>();
        recivedMessages = new HashSet<>();
        incomingAck = new HashSet<>();
    }

    public boolean connect() {;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            dos.writeByte(1);
            dos.writeLong(noOfPacket);
            dos.writeUTF(name);
            dos.writeLong(uid);
            dos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        DatagramPacket outgoingPacket = new DatagramPacket(bos.toByteArray(), bos.size());
        try {
            outgoingPacket.setSocketAddress(new InetSocketAddress(InetAddress.getByName(ip), port));
        } catch (UnknownHostException e) {
            System.out.println("Wrong host name.");
            return false;
        }

        byte[] buf = new byte[40], data = null;
        DatagramPacket ackPacket = new DatagramPacket(buf, buf.length);

        try {
            socket.setSoTimeout(5000);
        } catch (SocketException e) {}


            int brojac = 0;
            while (brojac < 10) {
                brojac++;
                System.out.println("Saljem HELLO na posluzitelj " + brojac + "/10...");
                try {
                    socket.send(outgoingPacket);
                    socket.receive(ackPacket);
                } catch (SocketTimeoutException exception) {
                    continue;
                } catch (IOException e) {}

                data = ackPacket.getData();

                DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
                byte kod;
                long number;
                try {
                    kod = dis.readByte();
                    number = dis.readLong();
                    uid = dis.readLong();
                    dis.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                if (kod != (byte) 2 || number != noOfPacket) continue;
                break;
            }

        if (brojac == 10) {
                System.out.println("Nemoguce spajanje s posluziteljom...");
                socket.disconnect();
                return false;
            }
            System.out.println("Spojen na posluzitelja: " + ackPacket.getSocketAddress() + ';' +
                    outgoingPacket.getSocketAddress());

            noOfPacket++;


        try {
            socket.setSoTimeout(0);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public boolean sendMessage(String text) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        long number;
        synchronized (name) {
            number = noOfPacket++;
        }
        try {
            dos.writeByte(4);
            dos.writeLong(number);
            dos.writeLong(uid);
            dos.writeUTF(text);
            dos.close();
        } catch (IOException e) {}

        DatagramPacket outgoingPacket = new DatagramPacket(bos.toByteArray(), bos.size());
        try {
            outgoingPacket.setSocketAddress(new InetSocketAddress(InetAddress.getByName(ip), port));
        } catch (UnknownHostException e) {}

        SocketAddress address = null;
        int brojac = 0;
loop:   while (brojac < 10) {
            brojac++;
            System.out.println("Saljem OUTMSG na: " + outgoingPacket.getSocketAddress() + ": " + brojac + "/10");

            try {
                socket.send(outgoingPacket);
            } catch (IOException ex) {}

            //ACK
            LocalDateTime t0 = LocalDateTime.now();
            while (true) {
                if (incomingAck.contains(number)) {
                    incomingAck.remove(number);
                    break loop;
                }
                LocalDateTime t1 = LocalDateTime.now();
                if (Duration.between(t1, t0).toSeconds() >= 5) break;
            }

        }
        if (brojac == 10) {
            System.out.println("Can not connect to server...\n");
            return false;
        }
        System.out.println("Primio ACK sa: " + address);
        return true;
    }

    public String getName() {
        return name;
    }

    @Override
    public void addModelListener(MyListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeModelListener(MyListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void notifyListeners(String text) {
        for (MyListener listener : listeners)
            listener.update(text);
    }

    public void disconnect() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            dos.writeByte(3);
            dos.writeLong(noOfPacket);
            dos.writeLong(uid);
            dos.close();
        } catch (IOException e) {}

        DatagramPacket outgoingPacket = new DatagramPacket(bos.toByteArray(), bos.size());

        try {
            outgoingPacket.setSocketAddress(new InetSocketAddress(InetAddress.getByName(ip), port));
        } catch (UnknownHostException e) {}

        byte[] data, buf = new byte[40];
        DatagramPacket incomingPacket = new DatagramPacket(buf, buf.length);

        int brojac = 0;
petlja:  while (brojac < 10) {
            brojac++;
            System.out.println("Saljem BYE na posluzitelj " + brojac + "/10...");

            try {
                socket.send(outgoingPacket);
            } catch (IOException e) {}

            LocalDateTime t0 = LocalDateTime.now();
            while (true) {
                if (incomingAck.contains(noOfPacket)) {
                    incomingAck.remove(noOfPacket);
                    break petlja;
                }
                if (Duration.between(LocalDateTime.now(), t0).toSeconds() >= 5)
                    break;
            }

        }
        if (brojac == 10) {
            System.out.println("Kontakt s klijentom nije moguce uspostaviti...");
        } else System.out.println("Gasim klijenta...\nDo vidjenja!");

        socket.close();
    }

    public void waitForNewMessages() {
        while (true) {
            byte[] buff = new byte[1024];
            DatagramPacket incomingPacket = new DatagramPacket(buff, buff.length);

            try {
                socket.receive(incomingPacket);
            } catch (IOException e) {}

            byte[] data = incomingPacket.getData();
            //ACK
            if (data[0] == 2) {
                long number, uid;

                DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));

                try {
                    dis.readByte();
                    number = dis.readLong();
                    uid = dis.readLong();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                if (uid == this.uid)
                    incomingAck.add(number);
            } else if (data[0] == 5)
                inmsg(data, incomingPacket.getSocketAddress());
        }
    }

    private void inmsg(byte[] data, SocketAddress socketAddress) {

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));

        long number;

        try {
            dis.readByte();
            number = dis.readLong();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (recivedMessages.contains(number)) {
            sendAck(number, uid, socketAddress);
            try {
                dis.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        recivedMessages.add(number);

        String incomingName, incomingText;

        try {
            incomingName = dis.readUTF();
            incomingText = dis.readUTF();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String s = "[" + socketAddress + "] Poruka od korisnika: " + incomingName + '\n' + incomingText + '\n';
        notifyListeners(s);

        sendAck(number, uid, socketAddress);
    }

    private void sendAck(long number, long uid, SocketAddress socketAddress) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            dos.writeByte(2);
            dos.writeLong(number);
            dos.writeLong(uid);
            dos.close();
        } catch (IOException e) {}

        DatagramPacket outgoingPacket = new DatagramPacket(bos.toByteArray(), bos.size());
        outgoingPacket.setSocketAddress(socketAddress);

        try {
            socket.send(outgoingPacket);
        } catch (IOException e) {}

        System.out.println("Poslan ACK na adresu: " + socketAddress);
    }

}
