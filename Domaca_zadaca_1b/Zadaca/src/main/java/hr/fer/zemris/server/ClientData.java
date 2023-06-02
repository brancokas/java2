package hr.fer.zemris.server;

import java.net.DatagramPacket;
import java.util.*;

public class ClientData {
    private Server.Pair pair;
    private String name;
    private Set<Long> recivedMessages;
    private Set<Long> incomingPackets;

    public ClientData(Server.Pair pair, String name) {
        this.pair = pair;
        this.name = name;
        recivedMessages = new HashSet<>();
        incomingPackets = new HashSet<>();
    }

    public Server.Pair getPair() {
        return pair;
    }

    public String getName() {
        return name;
    }

    public boolean isMessagedRecived(long number) {
        return recivedMessages.contains(number);
    }

    public void addRecivedMessage(long number) {
        recivedMessages.add(number);
    }

    public void removePacket(long noPacket) {
        incomingPackets.remove(noPacket);
    }

    public void addIncomingPackets(long noPacket) {
        incomingPackets.add(noPacket);
    }

    public boolean packetArrived(long noPacket) {
        return incomingPackets.contains(noPacket);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientData that = (ClientData) o;
        return Objects.equals(pair, that.pair) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pair, name);
    }
}
