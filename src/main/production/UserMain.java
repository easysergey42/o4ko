package main.production;

import javax.naming.ldap.StartTlsRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class UserMain {
    final static String INET_ADDR = "224.0.0.3";
    final static int PORT = 8888;

//    public static MulticastSocket getSocket() throws IOException {
//        MulticastSocket socket = new MulticastSocket(PORT);
//        socket.joinGroup(new InetSocketAddress(INET_ADDR, PORT), null);
//        return socket;
//    }


    public static void main(String[] args) {

        try (MulticastSocket socket = new MulticastSocket(PORT);
             DatagramSocket UDPSocket = new DatagramSocket();
             BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)))
        {
            socket.joinGroup(new InetSocketAddress(INET_ADDR, PORT), null);
            System.out.println("Choose your ID");
            int id = Integer.parseInt(reader.readLine());
            Map<Integer, Integer> userValidityMap = new HashMap<>();
            UserListener listener = new UserListener(socket, id, userValidityMap);
            listener.start();

            Eventik msg;
            DatagramPacket msgPacket;

            msg = new Eventik(id, Eventik.State.JOIN);
            msgPacket = new DatagramPacket(msg.getBytes(),
                    msg.getBytes().length, new InetSocketAddress(INET_ADDR, PORT));
            UDPSocket.send(msgPacket);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(userValidityMap.keySet());

            msg = new Eventik(id, Eventik.State.DISCONNECT);
            msgPacket = new DatagramPacket(msg.getBytes(),
                    msg.getBytes().length, new InetSocketAddress(INET_ADDR, PORT));
            UDPSocket.send(msgPacket);


            System.out.println("DONE, map: " + userValidityMap.keySet());


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
