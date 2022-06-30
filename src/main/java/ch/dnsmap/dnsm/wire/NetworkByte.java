package ch.dnsmap.dnsm.wire;

import java.nio.ByteBuffer;

import static java.nio.ByteBuffer.wrap;

public final class NetworkByte implements ReadableByte {

    private final ByteBuffer byteBuffer;

    private NetworkByte(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    public static NetworkByte of(byte[] data) {
        return new NetworkByte(wrap(data));
    }

    public static NetworkByte of(int capacity) {
        return new NetworkByte(ByteBuffer.allocate(capacity));
    }

    @Override
    public int savePosition() {
        return byteBuffer.position();
    }

    @Override
    public void restorePosition(int restorePosition) {
        jumpToPosition(restorePosition);
    }

    @Override
    public void jumpToPosition(int newPosition) {
        byteBuffer.position(newPosition);
    }

    @Override
    public int readUInt8() {
        return byteBuffer.get() & 0xFF;
    }

    @Override
    public int readUInt16() {
        return byteBuffer.getShort() & 0xFFFF;
    }

    @Override
    public int readInt32() {
        return byteBuffer.getInt();
    }

    @Override
    public byte[] readByte16() {
        return readByte(2);
    }

    @Override
    public byte[] readByteFromLength8() {
        int nofBytes = readUInt8();
        if (nofBytes == 0) {
            return new byte[0];
        }
        return readByte(nofBytes);
    }

    @Override
    public byte[] readByte(int nofBytes) {
        byte[] data = new byte[nofBytes];
        byteBuffer.get(data);
        return data;
    }
}
