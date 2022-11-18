package main.production;

import java.net.DatagramSocket;
import java.net.SocketException;

public class UserSender {
    DatagramSocket UDPSocket;
    final int id;
    public UserSender(DatagramSocket s, int id_){
        UDPSocket = s;
        id = id_;
    }
    public void send(){
        Thread sender = new Thread(()->{

        });
        sender.start();
    }
}
