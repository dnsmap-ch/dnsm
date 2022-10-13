package ch.dnsmap.dnsm.wire.parser;

import static ch.dnsmap.dnsm.Domain.root;
import static org.assertj.core.api.Assertions.assertThat;

import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.wire.bytes.NetworkByteBuffer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DomainParserTest {

  private static final byte[] BYTES_DNSMAP_CH = new byte[] {
      /* Value: 6dnsmap2ch0 */
      0x06, 0x64, 0x6e, 0x73, 0x6d, 0x61, 0x70,
      0x02, 0x63, 0x68,
      0x00};
  private static final Domain DOMAIN_ASDF_DNSMAP_CH = Domain.of("asdf.dnsmap.ch");
  private static final Domain DOMAIN_DNSMAP_CH = Domain.of("dnsmap.ch");
  private static final Domain DOMAIN_WWW_DNSMAP_CH = Domain.of("www.dnsmap.ch");

  @Nested
  class FromWire {

    @Test
    void testRootFromWire() {
      var networkBytes = NetworkByteBuffer.of(new byte[] {0});
      DomainParser domainParser = new DomainParser();

      var domain = domainParser.fromWire(networkBytes);

      assertThat(domain).isEqualTo(root());
    }

    @Test
    void testASequenceOfLabelsEndingInAZeroOctetFromWire() {
      var networkBytes = NetworkByteBuffer.of(BYTES_DNSMAP_CH);
      DomainParser domainParser = new DomainParser();

      var domain = domainParser.fromWire(networkBytes);

      assertThat(domain).isEqualTo(DOMAIN_DNSMAP_CH);
    }

    @Test
    void testAPointerFromWire() throws IOException {
      var networkBytes = addEndingPointer();
      DomainParser domainParser = new DomainParser();

      var domain = domainParser.fromWire(networkBytes);

      assertThat(domain).isEqualTo(DOMAIN_DNSMAP_CH);
    }

    @Test
    void testASequenceOfLabelsEndingWithAPointer() throws IOException {
      var networkBytes = addEndingLabelAndPointer();
      DomainParser domainParser = new DomainParser();

      var domain = domainParser.fromWire(networkBytes);

      assertThat(domain).isEqualTo(DOMAIN_WWW_DNSMAP_CH);
    }

    private static NetworkByteBuffer addEndingPointer() throws IOException {
      var byteArrayOutputStream = createByteStreamWithData();
      byteArrayOutputStream.write(new byte[] {(byte) 0xC0, 0x00});
      return setByteStreamPositionAfterData(byteArrayOutputStream);
    }

    private static NetworkByteBuffer addEndingLabelAndPointer() throws IOException {
      var byteArrayOutputStream = createByteStreamWithData();
      byteArrayOutputStream.write(new byte[] {0x03, 0x77, 0x77, 0x77});
      byteArrayOutputStream.write(new byte[] {(byte) 0xC0, 0x00});
      return setByteStreamPositionAfterData(byteArrayOutputStream);
    }

    private static ByteArrayOutputStream createByteStreamWithData() throws IOException {
      var byteArrayOutputStream = new ByteArrayOutputStream();
      byteArrayOutputStream.write(BYTES_DNSMAP_CH);
      return byteArrayOutputStream;
    }

    private static NetworkByteBuffer setByteStreamPositionAfterData(
        ByteArrayOutputStream byteArrayOutputStream) {
      var networkBytes = NetworkByteBuffer.of(byteArrayOutputStream.toByteArray());
      networkBytes.jumpToPosition(11);
      return networkBytes;
    }
  }

  @Nested
  class FromWireWithLength {

    @Test
    void testRootFromWire() {
      var networkBytes = NetworkByteBuffer.of(new byte[] {0});
      DomainParser domainParser = new DomainParser();

      var domain = domainParser.fromWire(networkBytes, 1);

      assertThat(domain).isEqualTo(root());
    }

    @Test
    void testASequenceOfLabelsEndingInAZeroOctetFromWire() {
      var networkBytes = NetworkByteBuffer.of(BYTES_DNSMAP_CH);
      DomainParser domainParser = new DomainParser();

      var domain = domainParser.fromWire(networkBytes, BYTES_DNSMAP_CH.length);

      assertThat(domain).isEqualTo(DOMAIN_DNSMAP_CH);
    }

    @Test
    void testASequenceOfLabelsEndingInAZeroOctetFromWireReadOnlyFirstLabel() {
      var networkBytes = NetworkByteBuffer.of(BYTES_DNSMAP_CH);
      DomainParser domainParser = new DomainParser();

      var domain = domainParser.fromWire(networkBytes, 6);

      assertThat(domain).isEqualTo(Domain.of("dnsmap"));
    }

    @Test
    void testAPointerFromWire() throws IOException {
      var networkBytes = addEndingPointer();
      DomainParser domainParser = new DomainParser();

      var domain = domainParser.fromWire(networkBytes, 42);

      assertThat(domain).isEqualTo(DOMAIN_DNSMAP_CH);
    }

    @Test
    void testASequenceOfLabelsEndingWithAPointer() throws IOException {
      var networkBytes = addEndingLabelAndPointer();
      DomainParser domainParser = new DomainParser();

      var domain = domainParser.fromWire(networkBytes, 42);

      assertThat(domain).isEqualTo(DOMAIN_WWW_DNSMAP_CH);
    }

    private static NetworkByteBuffer addEndingPointer() throws IOException {
      var byteArrayOutputStream = createByteStreamWithData();
      byteArrayOutputStream.write(new byte[] {(byte) 0xC0, 0x00});
      return setByteStreamPositionAfterData(byteArrayOutputStream);
    }

    private static NetworkByteBuffer addEndingLabelAndPointer() throws IOException {
      var byteArrayOutputStream = createByteStreamWithData();
      byteArrayOutputStream.write(new byte[] {0x03, 0x77, 0x77, 0x77});
      byteArrayOutputStream.write(new byte[] {(byte) 0xC0, 0x00});
      return setByteStreamPositionAfterData(byteArrayOutputStream);
    }

    private static ByteArrayOutputStream createByteStreamWithData() throws IOException {
      var byteArrayOutputStream = new ByteArrayOutputStream();
      byteArrayOutputStream.write(BYTES_DNSMAP_CH);
      return byteArrayOutputStream;
    }

    private static NetworkByteBuffer setByteStreamPositionAfterData(
        ByteArrayOutputStream byteArrayOutputStream) {
      var networkBytes = NetworkByteBuffer.of(byteArrayOutputStream.toByteArray());
      networkBytes.jumpToPosition(11);
      return networkBytes;
    }
  }

  @Nested
  class ToWire {

    @Test
    void testRootToWire() {
      var networkBytes = NetworkByteBuffer.of(23);
      DomainParser domainParser = new DomainParser();

      var bytes = domainParser.toWire(networkBytes, root());

      assertThat(bytes).isEqualTo(1);
      assertThat(networkBytes.createRestorePosition()).isEqualTo(1);
      networkBytes.jumpToPosition(0);
      assertThat(networkBytes.readData(1)).isEqualTo(new byte[] {0x00});
    }

    @Test
    void testASequenceOfLabelsEndingInAZeroOctetToWire() {
      var networkBytes = NetworkByteBuffer.of(23);
      DomainParser domainParser = new DomainParser();

      var bytes = domainParser.toWire(networkBytes, DOMAIN_DNSMAP_CH);

      assertThat(bytes).isEqualTo(11);
      assertThat(networkBytes.createRestorePosition()).isEqualTo(11);
      networkBytes.jumpToPosition(0);
      assertThat(networkBytes.readData(11)).isEqualTo(BYTES_DNSMAP_CH);
    }

    @Test
    void testAPointerToWire() {
      var networkBytes = NetworkByteBuffer.of(35);
      DomainParser domainParser = new DomainParser();

      var bytesFirstStep = domainParser.toWire(networkBytes, DOMAIN_DNSMAP_CH);
      assertThat(bytesFirstStep).isEqualTo(11);

      var bytesSecondStep = domainParser.toWire(networkBytes, DOMAIN_ASDF_DNSMAP_CH);

      assertThat(bytesSecondStep).isEqualTo(7);
      assertThat(networkBytes.getPosition()).isEqualTo(18);
      networkBytes.jumpToPosition(11);
      assertThat(networkBytes.readData(7)).isEqualTo(new byte[] {
          /* Value: 4asdfPointer0 */
          (byte) 0x04, (byte) 0x61, (byte) 0x73, (byte) 0x64, (byte) 0x66,
          (byte) 0xC0, (byte) 0x00}
      );
    }
  }
}
