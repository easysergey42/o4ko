package main.production;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.sql.Time;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserListener {

    static MulticastSocket socket;
    Thread receiver;
    final int id;
//    Map<Integer, Integer> userValidityMap;
    Set<TimedMember> validUsers;

    public UserListener(MulticastSocket s, int id_, Set<TimedMember> set){
        if(socket == null) socket = s;
        id = id_;
//        userValidityMap = m;
        validUsers = set;
        receiver = new Thread(() -> {
            byte[] buf = new byte[256];
            DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
            try {
                while(true) {
                    socket.receive(msgPacket);
                    Eventik msg = Eventik.getEventik(buf);
                    if(msg.message == Eventik.State.DISCONNECT) {
//                        userValidityMap.remove(msg.senderId);
                        validUsers.remove(new TimedMember(msg.senderId));
                        if (msg.senderId == id) break;
                    }
                    else if (msg.message == Eventik.State.JOIN || msg.message == Eventik.State.PING){
//                        userValidityMap.put(msg.senderId, 1);
                        new TimedMember(msg.senderId).addToSet(validUsers);
                    }
                }

                System.out.println("Receiver ended");
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }
    public void start(){
        receiver.start();
    }

    public void close() throws InterruptedException {
        receiver.join();
    }
}
