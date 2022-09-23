package ch.dnsmap.dnsm.wire.bytes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class NetworkByteBufferTest {

  @Nested
  class SharedBufferTest {

    @Test
    void testReadValidRange() {
      var networkByte = networkByteBuffer();
      var data = networkByte.range(0, 5);
      assertThat(data).isEqualTo(
          new byte[] {(byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04});
      assertThat(networkByte.getRemaining()).isEqualTo(16);
    }

    @Test
    void testReadEmptyRange() {
      var networkByte = networkByteBuffer();
      var data = networkByte.range(5, 5);
      assertThat(data).isEqualTo(new byte[0]);
      assertThat(networkByte.getRemaining()).isEqualTo(16);
    }

    @Test
    void testReadInvalidRange() {
      var networkByte = networkByteBuffer();
      assertThatThrownBy(() -> networkByte.range(-1, 5))
          .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    void testRestore() {
      var networkByte = networkByteBuffer();

      networkByte.readUInt32();
      assertThat(networkByte.getPosition()).isEqualTo(4);

      var restorePosition = networkByte.createRestorePosition();
      assertThat(restorePosition).isEqualTo(4);

      networkByte.readUInt32();
      assertThat(networkByte.getPosition()).isEqualTo(8);

      networkByte.restorePosition(restorePosition);
      assertThat(networkByte.getPosition()).isEqualTo(4);
    }

    private static ReadableByteBuffer networkByteBuffer() {
      var data =
          new byte[] {
              (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05,
              (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x0A, (byte) 0x0B,
              (byte) 0x0C, (byte) 0x0D, (byte) 0x0E, (byte) 0x0F};
      return NetworkByteBuffer.of(data);
    }
  }

  @Nested
  class ReaderBufferTest {

    @Test
    void testPeakUInt8() {
      var networkByte = networkByteBuffer();
      var data = networkByte.peakUInt8();
      assertThat(data).isEqualTo(0x00);
    }

    @Test
    void testPeakUInt16() {
      var networkByte = networkByteBuffer();
      var data = networkByte.peakUInt16();
      assertThat(data).isEqualTo(0x0001);
    }

    @Test
    void testPeakUInt32() {
      var networkByte = networkByteBuffer();
      var data = networkByte.peakUInt32();
      assertThat(data).isEqualTo(0x00010203);
    }

    @Test
    void testReadUInt8() {
      var networkByte = networkByteBuffer();
      var data = networkByte.readUInt8();
      assertThat(data).isEqualTo(0x00);
    }

    @Test
    void testReadUInt16() {
      var networkByte = networkByteBuffer();
      var data = networkByte.readUInt16();
      assertThat(data).isEqualTo(0x0001);
    }

    @Test
    void testReadUInt32() {
      var networkByte = networkByteBuffer();
      var data = networkByte.readUInt32();
      assertThat(data).isEqualTo(0x00010203);
    }

    @Test
    void testReadMinimalByteData() {
      var networkByte = networkByteBuffer();
      var data = networkByte.readData(0);
      assertThat(data).isEqualTo(new byte[0]);
    }

    @Test
    void testReadNineByteData() {
      var networkByte = networkByteBuffer();
      var data = networkByte.readData(9);
      assertThat(data).isEqualTo(new byte[] {
          (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05,
          (byte) 0x06,
          (byte) 0x07, (byte) 0x08});
    }

    @Test
    void testReadMaximalByteData() {
      var networkByte = networkByteBuffer();
      var data = networkByte.readData(16);
      assertThat(data).isEqualTo(new byte[] {
          (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05,
          (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x0A, (byte) 0x0B,
          (byte) 0x0C, (byte) 0x0D, (byte) 0x0E, (byte) 0x0F});
    }

    @Test
    void testReadInvalidNegativeLength() {
      var networkByte = networkByteBuffer();
      assertThatThrownBy(() -> networkByte.readData(-1))
          .isInstanceOf(NegativeArraySizeException.class);
    }

    @Test
    void testReadInvalidTooLargeLength() {
      var networkByte = networkByteBuffer();
      assertThatThrownBy(() -> networkByte.readData(17))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("length longer than buffer capacity");
    }

    @Test
    void testReadDataWithHeaderUInt8() {
      var networkByte = networkByteBuffer();
      networkByte.jumpToPosition(4);
      var data = networkByte.readData8();
      assertThat(data).isEqualTo(new byte[] {(byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08});
    }

    @Test
    void testZeroHeaderReadDataWithHeaderUInt8() {
      var networkByte = emptyNetworkByteBuffer();
      var data = networkByte.readData8();
      assertThat(data).isEqualTo(new byte[0]);
    }

    @Test
    void testReadDataWithHeaderUInt16() {
      var networkByte = networkByteBuffer();
      var data = networkByte.readData16();
      assertThat(data).isEqualTo(new byte[] {(byte) 0x02});
    }

    @Test
    void testZeroHeaderReadDataWithHeaderUInt16() {
      var networkByte = emptyNetworkByteBuffer();
      var data = networkByte.readData16();
      assertThat(data).isEqualTo(new byte[0]);
    }

    private static ReadableByteBuffer networkByteBuffer() {
      var data =
          new byte[] {
              (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05,
              (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x0A, (byte) 0x0B,
              (byte) 0x0C, (byte) 0x0D, (byte) 0x0E, (byte) 0x0F};
      return NetworkByteBuffer.of(data);
    }

    private static ReadableByteBuffer emptyNetworkByteBuffer() {
      return NetworkByteBuffer.of(new byte[] {(byte) 0x00, (byte) 0x00});
    }
  }

  @Nested
  class WriterBufferTest {

    @Test
    void testWriteUInt8() {
      var networkByte = NetworkByteBuffer.of(1);

      var bytesWritten = networkByte.writeUInt8(0x23);

      networkByte.jumpToPosition(0);
      assertThat(bytesWritten).isEqualTo(1);
      assertThat(networkByte.readUInt8()).isEqualTo(0x23);
      assertThat(networkByte.getPosition()).isEqualTo(1);
    }

    @Test
    void testNegativeWriteUInt8() {
      var networkByte = NetworkByteBuffer.of(1);

      var bytesWritten = networkByte.writeUInt8(-23);

      networkByte.jumpToPosition(0);
      assertThat(bytesWritten).isEqualTo(1);
      assertThat(networkByte.readUInt8()).isEqualTo(0xE9);
      assertThat(networkByte.getPosition()).isEqualTo(1);
    }

    @Test
    void testWriteUInt8ValidMax() {
      var networkByte = NetworkByteBuffer.of(1);

      var bytesWritten = networkByte.writeUInt8((int) (Math.pow(2, 8) - 1));

      networkByte.jumpToPosition(0);
      assertThat(bytesWritten).isEqualTo(1);
      assertThat(networkByte.readUInt8()).isEqualTo(255);
      assertThat(networkByte.getPosition()).isEqualTo(1);
    }

    @Test
    void testWriteUInt8AboveMax() {
      var networkByte = NetworkByteBuffer.of(1);

      var bytesWritten = networkByte.writeUInt8((int) (Math.pow(2, 8)));

      networkByte.jumpToPosition(0);
      assertThat(bytesWritten).isEqualTo(1);
      assertThat(networkByte.readUInt8()).isEqualTo(0);
      assertThat(networkByte.getPosition()).isEqualTo(1);
    }

    @Test
    void testWriteUInt8ValidMin() {
      var networkByte = NetworkByteBuffer.of(1);

      var bytesWritten = networkByte.writeUInt8(0);

      networkByte.jumpToPosition(0);
      assertThat(bytesWritten).isEqualTo(1);
      assertThat(networkByte.readUInt8()).isEqualTo(0);
      assertThat(networkByte.getPosition()).isEqualTo(1);
    }

    @Test
    void testWriteUInt16() {
      var networkByte = NetworkByteBuffer.of(2);

      var bytesWritten = networkByte.writeUInt16(0x2342);

      networkByte.jumpToPosition(0);
      assertThat(bytesWritten).isEqualTo(2);
      assertThat(networkByte.readUInt16()).isEqualTo(0x2342);
      assertThat(networkByte.getPosition()).isEqualTo(2);
    }

    @Test
    void testNegativeWriteUInt16() {
      var networkByte = NetworkByteBuffer.of(2);

      var bytesWritten = networkByte.writeUInt16(-2342);

      networkByte.jumpToPosition(0);
      assertThat(bytesWritten).isEqualTo(2);
      assertThat(networkByte.readUInt16()).isEqualTo(0xF6DA);
      assertThat(networkByte.getPosition()).isEqualTo(2);
    }

    @Test
    void testWriteUInt16ValidMax() {
      var networkByte = NetworkByteBuffer.of(2);

      var bytesWritten = networkByte.writeUInt16((int) (Math.pow(2, 16) - 1));

      networkByte.jumpToPosition(0);
      assertThat(bytesWritten).isEqualTo(2);
      assertThat(networkByte.readUInt16()).isEqualTo(65535);
      assertThat(networkByte.getPosition()).isEqualTo(2);
    }

    @Test
    void testWriteUInt16AboveMax() {
      var networkByte = NetworkByteBuffer.of(2);

      var bytesWritten = networkByte.writeUInt16((int) (Math.pow(2, 16)));

      networkByte.jumpToPosition(0);
      assertThat(bytesWritten).isEqualTo(2);
      assertThat(networkByte.readUInt16()).isEqualTo(0);
      assertThat(networkByte.getPosition()).isEqualTo(2);
    }

    @Test
    void testWriteUInt16ValidMin() {
      var networkByte = NetworkByteBuffer.of(2);

      var bytesWritten = networkByte.writeUInt16(0);

      networkByte.jumpToPosition(0);
      assertThat(bytesWritten).isEqualTo(2);
      assertThat(networkByte.readUInt16()).isEqualTo(0);
      assertThat(networkByte.getPosition()).isEqualTo(2);
    }

    @Test
    void testWriteUInt32() {
      var networkByte = NetworkByteBuffer.of(4);

      var bytesWritten = networkByte.writeUInt32(0x23421337);

      networkByte.jumpToPosition(0);
      assertThat(bytesWritten).isEqualTo(4);
      assertThat(networkByte.readUInt32()).isEqualTo(0x23421337);
      assertThat(networkByte.getPosition()).isEqualTo(4);
    }

    @Test
    void testNegativeWriteUInt32() {
      var networkByte = NetworkByteBuffer.of(4);

      var bytesWritten = networkByte.writeUInt32(-23421337L);

      networkByte.jumpToPosition(0);
      assertThat(bytesWritten).isEqualTo(4);
      assertThat(networkByte.readUInt32()).isEqualTo(4271545959L);
      assertThat(networkByte.getPosition()).isEqualTo(4);
    }

    @Test
    void testWriteUInt32ValidMax() {
      var networkByte = NetworkByteBuffer.of(4);

      var bytesWritten = networkByte.writeUInt32((long) (Math.pow(2, 32) - 1));

      networkByte.jumpToPosition(0);
      assertThat(bytesWritten).isEqualTo(4);
      assertThat(networkByte.readUInt32()).isEqualTo(4294967295L);
      assertThat(networkByte.getPosition()).isEqualTo(4);
    }

    @Test
    void testWriteUInt32AboveMax() {
      var networkByte = NetworkByteBuffer.of(4);

      var bytesWritten = networkByte.writeUInt32((long) (Math.pow(2, 32)));

      networkByte.jumpToPosition(0);
      assertThat(bytesWritten).isEqualTo(4);
      assertThat(networkByte.readUInt32()).isEqualTo(0);
      assertThat(networkByte.getPosition()).isEqualTo(4);
    }

    @Test
    void testWriteUInt32ValidMin() {
      var networkByte = NetworkByteBuffer.of(4);

      var bytesWritten = networkByte.writeUInt32(0);

      networkByte.jumpToPosition(0);
      assertThat(bytesWritten).isEqualTo(4);
      assertThat(networkByte.readUInt32()).isEqualTo(0);
      assertThat(networkByte.getPosition()).isEqualTo(4);
    }

    @Test
    void testWriteData() {
      var networkByte = NetworkByteBuffer.of(16);
      var data =
          new byte[] {
              (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05,
              (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x0A, (byte) 0x0B,
              (byte) 0x0C, (byte) 0x0D, (byte) 0x0E, (byte) 0x0F};

      var bytesWritten = networkByte.writeData(data);

      networkByte.jumpToPosition(0);
      assertThat(bytesWritten).isEqualTo(16);
      assertThat(networkByte.readData(16)).isEqualTo(data);
      assertThat(networkByte.getPosition()).isEqualTo(16);
    }

    @Test
    void testWriteEmptyData() {
      var networkByte = NetworkByteBuffer.of(16);
      var data = new byte[0];

      var bytesWritten = networkByte.writeData(data);

      networkByte.jumpToPosition(0);
      assertThat(bytesWritten).isEqualTo(0);
      assertThat(networkByte.readData(16)).isEqualTo(new byte[16]);
      assertThat(networkByte.getPosition()).isEqualTo(16);
    }

    @Test
    void testWriteData8() {
      var networkByte = NetworkByteBuffer.of(17);
      var data =
          new byte[] {
              (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05,
              (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x0A, (byte) 0x0B,
              (byte) 0x0C, (byte) 0x0D, (byte) 0x0E, (byte) 0x0F};

      var bytesWritten = networkByte.writeData8(data);

      networkByte.jumpToPosition(0);
      assertThat(bytesWritten).isEqualTo(17);
      assertThat(networkByte.readUInt8()).isEqualTo(16);
      assertThat(networkByte.readData(16)).isEqualTo(data);
      assertThat(networkByte.getPosition()).isEqualTo(17);
    }

    @Test
    void testWriteEmptyData8() {
      var networkByte = NetworkByteBuffer.of(17);
      var data = new byte[0];

      var bytesWritten = networkByte.writeData8(data);

      networkByte.jumpToPosition(0);
      assertThat(bytesWritten).isEqualTo(1);
      assertThat(networkByte.readUInt8()).isEqualTo(0);
      assertThat(networkByte.readData(16)).isEqualTo(new byte[16]);
      assertThat(networkByte.getPosition()).isEqualTo(17);
    }

    @Test
    void testWriteData16() {
      var networkByte = NetworkByteBuffer.of(18);
      var data =
          new byte[] {
              (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05,
              (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x0A, (byte) 0x0B,
              (byte) 0x0C, (byte) 0x0D, (byte) 0x0E, (byte) 0x0F};

      var bytesWritten = networkByte.writeData16(data);

      networkByte.jumpToPosition(0);
      assertThat(bytesWritten).isEqualTo(18);
      assertThat(networkByte.readUInt16()).isEqualTo(16);
      assertThat(networkByte.readData(16)).isEqualTo(data);
      assertThat(networkByte.getPosition()).isEqualTo(18);
    }

    @Test
    void testWriteEmptyData16() {
      var networkByte = NetworkByteBuffer.of(18);
      var data = new byte[0];

      var bytesWritten = networkByte.writeData16(data);

      networkByte.jumpToPosition(0);
      assertThat(bytesWritten).isEqualTo(2);
      assertThat(networkByte.readUInt16()).isEqualTo(0);
      assertThat(networkByte.readData(16)).isEqualTo(new byte[16]);
      assertThat(networkByte.getPosition()).isEqualTo(18);
    }
  }
}
