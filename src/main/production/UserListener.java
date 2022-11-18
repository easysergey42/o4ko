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
    Set<TimedMember<Integer>> validUsers;// Integer ->
    public UserListener(MulticastSocket s, int id_, Set<TimedMember<Integer>> set){
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
//                    System.out.println(msgPacket.getSocketAddress()); - это должно быть полем TimedMember
                    Eventik msg = Eventik.getEventik(buf);

                    if(msg.message == Eventik.State.DISCONNECT) {
//                        userValidityMap.remove(msg.senderId);
                        validUsers.remove(new TimedMember<Integer>(msg.senderId));
                        if (msg.senderId == id) break;
                        System.out.println("User " + msg.senderId + " just disconnected." +
                                "\nActive users: " + validUsers);
                    }
                    else if (msg.message == Eventik.State.JOIN || msg.message == Eventik.State.PING){
                        new TimedMember<Integer>(msg.senderId).addToSet(validUsers);
                        if (msg.message == Eventik.State.JOIN)
                            System.out.println("User " + msg.senderId + " just joined!" +
                                    "\nActive users: " + validUsers);
                    }
                }

//                System.out.println("Receiver ended");
            } catch (IOException e) {
//                throw new RuntimeException(e);
                System.out.println("У сокета сдохла мать");
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
