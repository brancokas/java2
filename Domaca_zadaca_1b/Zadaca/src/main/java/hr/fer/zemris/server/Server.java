package hr.fer.zemris.server;

import java.io.*;
import java.net.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class Server {
    private final int port;
    private DatagramSocket socket;
    private ExecutorService pool;
    private long uidNumber;
    private Map<Pair, Long> clientRandKeyMap;
    private Map<Long, ClientData> clientUIDMap;
    private long noOfPacket = 0;
    private final boolean drop;
    public static class Pair {
        private Long randKey;
        private SocketAddress socketAddress;

        public Pair(Long randKey, SocketAddress socketAddress) {
            this.randKey = randKey;
            this.socketAddress = socketAddress;
        }

        public Long getRandKey() {
            return randKey;
        }

        public SocketAddress getSocketAddress() {
            return socketAddress;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pair pair = (Pair) o;
            return Objects.equals(randKey, pair.randKey) && Objects.equals(socketAddress, pair.socketAddress);
        }

        @Override
        public int hashCode() {
            return Objects.hash(randKey, socketAddress);
        }
    }

    public Server(int port, boolean drop) {
        this.port = port;
        this.drop = drop;
        uidNumber = new Random().nextLong();
    }

    public void serve() {
        pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        clientRandKeyMap = new HashMap<>();
        clientUIDMap = new HashMap<>();
        try {
            socket = new DatagramSocket(new InetSocketAddress((InetAddress) null, port));
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        while (true) {
            byte[] buf = new byte[1024];
            DatagramPacket incomingPacket = new DatagramPacket(buf, buf.length);

            try {
                socket.receive(incomingPacket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (drop) {
                if (Math.random() > 0.5)
                    continue;
            }

            byte[] data = incomingPacket.getData();

            byte typeOfMessage = data[0];
            if (typeOfMessage == 1) {
                System.out.println("Primljen packet HELLO: " + incomingPacket.getSocketAddress());
                pool.submit(() -> hello(data, incomingPacket.getSocketAddress()));
            } else if (typeOfMessage == 2) {
                System.out.println("Primljen packet ACK: " + incomingPacket.getSocketAddress());
                pool.submit(() -> ack(data));
            } else if (typeOfMessage == 3) {
                System.out.println("Primljen packet BYE: " + incomingPacket.getSocketAddress());
                pool.submit(() -> bye(data, incomingPacket.getSocketAddress()));
            } else if (typeOfMessage == 4) {
                System.out.println("Primljen packet OUTMSG: " + incomingPacket.getSocketAddress());
                pool.submit(() -> outmsg(data, incomingPacket.getSocketAddress()));
            } else {
                System.out.println("Primljen krivi packet: " + incomingPacket.getSocketAddress());
            }
        }
    }

    private void ack(byte[] data) {
        long number, uid;

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));

        try {
            dis.readByte();
            number = dis.readLong();
            uid = dis.readLong();
            dis.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (clientUIDMap.containsKey(uid))
            clientUIDMap.get(uid).addIncomingPackets(number);
    }

    private void outmsg(byte[] data, SocketAddress socketAddress) {
        long number, uid;
        String text;

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));

        try {
            dis.readByte();
            number = dis.readLong();
            uid = dis.readLong();
            text = dis.readUTF();
            dis.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        ClientData clientData = clientUIDMap.get(uid);
        if (clientData == null) return;

        sendAck(number, uid, socketAddress);

        if (!clientData.isMessagedRecived(number)) {
            clientData.addRecivedMessage(number);
            inmsg(clientData.getName(), text);
        }
    }

    private void inmsg(String name, String text) {
        Collection<ClientData> collection = clientUIDMap.values();

        for (ClientData clientData : collection) {

            pool.submit(() -> {

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
                //make every client send insmg and
                long number;
                synchronized (collection) {
                    number = noOfPacket++;
                }
                try {
                    dos.writeByte(5);
                    dos.writeLong(number);
                    dos.writeUTF(name);
                    dos.writeUTF(text);
                    dos.close();
                } catch (IOException e) {}

                DatagramPacket outgoingPacket = new DatagramPacket(bos.toByteArray(), bos.size());
                outgoingPacket.setSocketAddress(clientData.getPair().getSocketAddress());


                byte[] buf = new byte[40];
                DatagramPacket incomingPacket = new DatagramPacket(buf, buf.length);

                int brojac = 0;
outside:        while (brojac < 10) {
                    brojac++;
                    try {
                        socket.send(outgoingPacket);
                        LocalDateTime t0 = LocalDateTime.now();
                        while (true) {
                            if (clientData.packetArrived(number)) {
                                clientData.removePacket(number);
                                break outside;
                            }
                            LocalDateTime t1 = LocalDateTime.now();
                            long delta = Duration.between(t1,t0).toSeconds();
                            if (delta >= 5) break;
                        }
                    } catch (IOException e) {}
                }
                if (brojac == 10) {
                    System.out.println("Nije poslana poruka useru: " + clientData.getName());
                }
            });
        }

    }

    private void bye(byte[] data, SocketAddress socketAddress) {
        long number, uid;

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));

        try {
            dis.readByte();
            number = dis.readLong();
            uid = dis.readLong();
            dis.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        Pair pair = clientUIDMap.get(uid).getPair();
        if (pair != null) {
            clientUIDMap.remove(uid);
            clientRandKeyMap.remove(pair);
        }

        sendAck(number, uid, socketAddress);
    }

    private void hello(byte[] data, SocketAddress socketAddress) {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
        long number;
        String name;
        long randkey;
        long uid;

        try {
            dis.readByte();
            number = dis.readLong();
            name = dis.readUTF();
            randkey = dis.readLong();
            dis.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        System.out.println(name);

        Pair pair = new Pair(randkey, socketAddress);
        if (clientRandKeyMap.containsKey(pair)) {
            uid = clientRandKeyMap.get(pair);
        } else {
            uid = this.uidNumber++;
            clientRandKeyMap.put(pair, uid);
            clientUIDMap.put(uid, new ClientData(pair, name));
        }

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
