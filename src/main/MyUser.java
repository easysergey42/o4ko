package main;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class MyUser {
    final static String INET_ADDR = "224.0.0.3";
    final static int PORT = 8888;
    final static SocketAddress mcastAddr = new InetSocketAddress(INET_ADDR, PORT);//hz about static


    static AtomicInteger identity = new AtomicInteger(0);
    static AtomicBoolean isAlive = new AtomicBoolean(true);
    static MulticastSocket socket;
    void setMcastSocket() throws IOException {
        socket = new MulticastSocket(PORT);
        socket.joinGroup(new InetSocketAddress(INET_ADDR, PORT), null);
    }
    void setMcastAddr(String address, int port){}


    public static void main(String[] args) {

        try(MulticastSocket socket = new MulticastSocket(PORT)) {
            socket.joinGroup(new InetSocketAddress(INET_ADDR, PORT), null);
            Thread sender = new Thread(() -> {
                identity.incrementAndGet();
                try {
//                byte[] buf = new byte[256];
//                    socket.joinGroup(new InetSocketAddress(INET_ADDR, PORT), null);
                    DatagramSocket UDPSocket = new DatagramSocket();
                    String msg = identity.toString();
                    DatagramPacket msgPacket = new DatagramPacket(msg.getBytes(),
                            msg.getBytes().length, mcastAddr);
                    while (isAlive.get()) {
//                        synchronized (socket) {
                            UDPSocket.send(msgPacket);
//                        }
                        Thread.sleep(3000);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("something went wrong", e);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
//            sender.start();


//            Thread receiver = new Thread(() -> {
//                byte[] buf = new byte[256];
//                DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
//                try {
//                    while(isAlive.get()) {
////                        synchronized(socket) {
//                            socket.receive(msgPacket);
////                        }
//                        int id = Integer.parseInt(Arrays.toString(buf));
////                    String msg = new String(buf, 0, msgPacket.getLength());
////                    int id = Integer.parseInt(msg);
//                        System.out.println("Id = " + id);
//                    }
//                } catch (IOException e) {
//                    throw new RuntimeException("during receive", e);
//                }
//
//            });
//            receiver.start();

            sender.start();

            byte[] buf = new byte[256];
            DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
                try {
                    while(isAlive.get()) {
//                        synchronized(socket) {
                            socket.receive(msgPacket);
//                        }
//                        int id = Integer.parseInt(Arrays.toString(buf));
                    String msg = new String(buf, 0, msgPacket.getLength());
                    int id = Integer.parseInt(msg);
//                        System.out.println("Id = " + id);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("during receive", e);
                }

        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    ////////////////////COMMANDER
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Type \"stop\"");
        try {
            if (reader.readLine().equals("stop")){
                isAlive.set(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
