package ZeroMQ;

import java.io.Serializable;

public class DataObject implements Serializable {

    private byte[] bytes;
    private long sendTime;

    public DataObject(byte[] bytes, long sendTime) {
        this.bytes = bytes;
        this.sendTime = sendTime;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public long getSendTime() {
        return sendTime;
    }
}
