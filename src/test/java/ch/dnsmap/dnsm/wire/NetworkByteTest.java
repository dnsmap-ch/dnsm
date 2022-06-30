package ch.dnsmap.dnsm.wire;

import ch.dnsmap.dnsm.wire.NetworkByte;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NetworkByteTest {

    @Test
    void testValidUint16() {
        var data = new byte[]{(byte) 0x12, (byte) 0x34};
        var networkByte = NetworkByte.of(data);
        assertThat(networkByte.readUInt16()).isEqualTo(4660);
    }

    @Test
    void testValidMinUint16() {
        var data = new byte[]{(byte) 0x00, (byte) 0x00};
        var networkByte = NetworkByte.of(data);
        assertThat(networkByte.readUInt16()).isEqualTo(0);
    }

    @Test
    void testValidMaxUint16() {
        var data = new byte[]{(byte) 0xff, (byte) 0xff};
        var networkByte = NetworkByte.of(data);
        assertThat(networkByte.readUInt16()).isEqualTo(65535);
    }

    @Test
    void testValidReadByte16() {
        var data = new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0x12, (byte) 0x34, (byte) 0xff};
        var networkByte = NetworkByte.of(data);
        networkByte.readUInt16();
        assertThat(networkByte.readByte16()).isEqualTo(new byte[]{(byte) 0x12, (byte) 0x34});
    }

    @Test
    void testValidReadByteFromLength8() {
        var data = new byte[]{(byte) 0x02, (byte) 0x12, (byte) 0x34, (byte) 0xff};
        var networkByte = NetworkByte.of(data);
        assertThat(networkByte.readByteFromLength8()).isEqualTo(new byte[]{(byte) 0x12, (byte) 0x34});
    }
}
