package main.production;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

public class UserSender {
    DatagramSocket UDPSocket;
    final int id;
    Thread sender;
    AtomicBoolean isAlive;
    InetSocketAddress address;
    static DatagramPacket toMessagePacket(MyEvent msg, InetSocketAddress address) throws IOException {
        return new DatagramPacket(msg.getBytes(),
                msg.getBytes().length, address);
    }
    public UserSender(DatagramSocket s, int id_, InetSocketAddress address_){
        UDPSocket = s;
        id = id_;
        isAlive = new AtomicBoolean(true);
        address = address_;
        sender = new Thread(()->{
            DatagramPacket msgPacket;

            try {
                //Connect

                msgPacket = toMessagePacket(new MyEvent(id, MyEvent.State.JOIN), address_);
                UDPSocket.send(msgPacket);
                //ping
                msgPacket = toMessagePacket(new MyEvent(id, MyEvent.State.PING), address_);
                while(isAlive.get()){
                    UDPSocket.send(msgPacket);
                    Thread.sleep(200);
                }
                //disconnect
                msgPacket = toMessagePacket(new MyEvent(id, MyEvent.State.DISCONNECT), address_);
                UDPSocket.send(msgPacket);

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        });
    }
    public void start(){
        sender.start();
    }

    public void stop() throws InterruptedException {
        isAlive.set(false);
        sender.join();
    }
}
