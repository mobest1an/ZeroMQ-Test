package ZeroMQ;

import org.zeromq.ZMQ;

import java.io.*;

public class DataManager {

    private int sendCount = 0;
    private int receiveCount = 0;

    public byte[] createData(int size) { //bit
        byte[] bytes = new byte[size / 8];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (Math.random() * 1000);
        }
        return bytes;
    }

    public void send(ZMQ.Socket publisher, DataObject dataObject) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(dataObject);
        objectOutputStream.flush();
        byte[] bytes = byteArrayOutputStream.toByteArray();
        publisher.send(bytes);
        sendCount++;
    }

    public DataObject recv(ZMQ.Socket updates) throws IOException, ClassNotFoundException {
        byte[] bytes = updates.recv(0);
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
        receiveCount++;
        return (DataObject) objectInputStream.readObject();
    }

    public int getSendCount() {
        return sendCount;
    }

    public int getReceiveCount() {
        return receiveCount;
    }
}
