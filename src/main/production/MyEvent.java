package main.production;

import java.io.*;

public class MyEvent implements Serializable {
    int senderId;

    public enum State {
        JOIN,
        PING,
        DISCONNECT
    }

    State message;

    public MyEvent(int id, State msg) {
        senderId = id;
        message = msg;
    }

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream objStream = new ObjectOutputStream(byteStream);
        objStream.writeObject(this);
        return byteStream.toByteArray();
    }

    public static MyEvent getEventik(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objStream = new ObjectInputStream(byteStream);
        return (MyEvent) objStream.readObject();
    }

    @Override
    public String toString() {
        return "SenderId = " + senderId + ", message = " + message;
    }
}

