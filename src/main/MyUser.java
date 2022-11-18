package main;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class MyUser {
    final static String INET_ADDR = "224.0.0.3";
    final static int PORT = 8888;
    final static SocketAddress mcastAddr = new InetSocketAddress(INET_ADDR, PORT);//hz about static


    static AtomicInteger identity = new AtomicInteger(0);
    static AtomicBoolean isAlive = new AtomicBoolean(true);
    static MulticastSocket socket;
    public static void setMcastSocket() throws IOException {
        socket = new MulticastSocket(PORT);
        socket.joinGroup(new InetSocketAddress(INET_ADDR, PORT), null);
    }
    public static void setMcastAddr(String address, int port){}

    public static Set<Integer> ids = new HashSet<>();
    public static Set<SocketAddress> adds = new HashSet<>();
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Enter your id");
        identity.set(Integer.parseInt(reader.readLine()));
        System.out.println("Your id = " + identity);


        setMcastSocket();


            Thread receiver = new Thread(() -> {
                byte[] buf = new byte[256];
                DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
                try {
                    while(isAlive.get()) {
//                        synchronized(socket) {
                            socket.receive(msgPacket);
//                        }
                    String msg = new String(buf, 0, msgPacket.getLength());
                    int id = Integer.parseInt(msg);
                    ids.add(id);
                    adds.add(msgPacket.getSocketAddress());
//                        System.out.println("Id = " + id);
                    }
                    System.out.println("Receiver ended");
                } catch (IOException e) {
                    throw new RuntimeException("during receive", e);
                }

            });
            receiver.start();

        Thread sender = new Thread(() -> {
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
                System.out.println("Sender ended");
            } catch (IOException e) {
                throw new RuntimeException("something went wrong", e);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
            sender.start();

//            byte[] buf = new byte[256];
//            DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
//                try {
//                    while(isAlive.get()) {
////                        synchronized(socket) {
//                            socket.receive(msgPacket);
////                        }
////                        int id = Integer.parseInt(Arrays.toString(buf));
//                    String msg = new String(buf, 0, msgPacket.getLength());
//                    int id = Integer.parseInt(msg);
////                        System.out.println("Id = " + id);
//                    }
//                } catch (IOException e) {
//                    throw new RuntimeException("during receive", e);
//                }

        while(true) {
            System.out.println("Type \"stop/see\"");
            try {
                String command = reader.readLine();
                if (command.equals("stop")) {
                    isAlive.set(false);
//                receiver.interrupt();
                    socket.close();
                    break;
                } else if (command.equals("see")) {
                    System.out.println(ids + "ADDRESSES:" + adds);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
