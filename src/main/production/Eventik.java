package main.production;

import java.io.*;

public class Eventik implements Serializable {
    int senderId;
    public enum State{
        JOIN,
        PING,
        DISCONNECT
    }
    State message;
    public Eventik(int id, State msg){
        senderId = id;
        message = msg;
    }

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream objStream = new ObjectOutputStream(byteStream);
        objStream.writeObject(this);
        return byteStream.toByteArray();
    }

    public static Eventik getEventik(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objStream = new ObjectInputStream(byteStream);
        return (Eventik) objStream.readObject();
    }

    @Override
    public String toString(){
        return "SenderId = " + senderId + ", message = " + message;
    }
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Eventik e = new Eventik(5, State.JOIN);
        System.out.println(e);
        byte[] buf = new byte[256];
        buf = e.getBytes();
        Eventik e2 = Eventik.getEventik(buf);

        System.out.println(e2);
    }
}
