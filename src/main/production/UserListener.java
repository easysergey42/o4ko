package main.production;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.util.Set;

public class UserListener {

    static MulticastSocket socket;
    Thread receiver;
//    final int id;
    final SocketAddress mySocketAddress;
//    Map<Integer, Integer> userValidityMap;
    Set<TimedMember<SocketAddress>> validUsers;// Integer ->
    public UserListener(MulticastSocket s, SocketAddress sa, Set<TimedMember<SocketAddress>> set){
        if(socket == null) socket = s;
        mySocketAddress = sa;
//        userValidityMap = m;
        validUsers = set;
        receiver = new Thread(() -> {
            byte[] buf = new byte[256];
            DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
            try {
                while(true) {
                    socket.receive(msgPacket);
//                    System.out.println(msgPacket.getSocketAddress());//- это должно быть полем TimedMember
                    MyEvent msg = MyEvent.getEventik(buf);
                    SocketAddress received = msgPacket.getSocketAddress();
                    if(msg.message == MyEvent.State.DISCONNECT) {
//                        userValidityMap.remove(msg.senderId);
                        validUsers.remove(new TimedMember<SocketAddress>(received));
                        if (received.equals(mySocketAddress)) break;
                        System.out.println("User " + received + " just disconnected." +
                                "\nActive users: " + validUsers);
//                        System.out.println("Received:" + received + "\nMy address:" + mySocketAddress);
                    }
                    else if (msg.message == MyEvent.State.JOIN || msg.message == MyEvent.State.PING){
                        new TimedMember<SocketAddress>(received).addToSet(validUsers);
                        if (msg.message == MyEvent.State.JOIN) {
                            System.out.println("User " + received + " just joined!" +
                                    "\nActive users: " + validUsers);
                            System.out.flush();
                        }
                    }
                }

                System.out.println("Receiver ended");
            }
            catch (IOException e) {
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
