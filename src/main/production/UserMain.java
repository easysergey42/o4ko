package main.production;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.HashSet;
import java.util.Set;

public class UserMain {
    final static String INET_ADDR = "224.0.0.3";
    final static int PORT = 8888;



    public static void main(String[] args) {

        try (MulticastSocket socket = new MulticastSocket(PORT);
             DatagramSocket UDPSocket = new DatagramSocket();
             BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)))
        {
            //socket.setSoTimeout(500);
            InetSocketAddress address = new InetSocketAddress(INET_ADDR, PORT);
            socket.joinGroup(address, null);
//            System.out.println("Choose your ID");

//            int id = Integer.parseInt(reader.readLine());
            int id = UDPSocket.getLocalPort();
            Set<TimedMember<SocketAddress>> validUsers = new HashSet<>();


            SocketAddress my = new InetSocketAddress(
                    InetAddress.getLocalHost().getHostAddress(),
                    UDPSocket.getLocalPort());
            UserListener listener = new UserListener(socket, my, validUsers);
            listener.start();

            UserSender sender = new UserSender(UDPSocket, id, address);
            sender.start();

            String command = "";
            System.out.println("enter <see> to see active users\nenter <stop> to disconnect ");
            while (!command.equals("stop")) {
                command = reader.readLine();
                if (command.equals("stop")) {
                    sender.stop();
                    listener.close();
                    System.out.println("Disconnected\nList: " + validUsers);
                }
                else if(command.equals("see")){
                    System.out.println("Active users: " + validUsers);
                }
            }


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
