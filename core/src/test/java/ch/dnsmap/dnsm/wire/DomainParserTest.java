package ch.dnsmap.dnsm.wire;

import static org.assertj.core.api.Assertions.assertThat;

import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.Label;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class DomainParserTest {

  private static final DomainParser DOMAIN_PARSER = new DomainParser();
  // 6 characters: dnsmap 2 characters: ch ending zero
  private static final byte[] DOMAIN_BYTES =
      new byte[] {0x06, 0x64, 0x6e, 0x73, 0x6d, 0x61, 0x70, 0x02, 0x63, 0x68, 0x00};
  private static final Domain DOMAIN = Domain.of(new Label("dnsmap"), new Label("ch"));
  private static final Domain HOST =
      Domain.of(new Label("www"), new Label("dnsmap"), new Label("ch"));

  @Test
  void testRootFromWire() {
    var networkBytes = NetworkByte.of(new byte[] {0});
    var domain = DOMAIN_PARSER.fromWire(networkBytes);
    assertThat(domain).isEqualTo(Domain.root());
  }

  @Test
  void testASequenceOfLabelsEndingInAZeroOctetFromWire() {
    var networkBytes = NetworkByte.of(DOMAIN_BYTES);
    var domain = DOMAIN_PARSER.fromWire(networkBytes);
    assertThat(domain).isEqualTo(DOMAIN);
  }

  @Test
  void testAPointerFromWire() throws IOException {
    var networkBytes = addEndingPointer();
    var domain = DOMAIN_PARSER.fromWire(networkBytes);
    assertThat(domain).isEqualTo(DOMAIN);
  }

  @Test
  void testASequenceOfLabelsEndingWithAPointer() throws IOException {
    var networkBytes = addEndingLabelAndPointer();
    var domain = DOMAIN_PARSER.fromWire(networkBytes);
    assertThat(domain).isEqualTo(HOST);
  }

  private static NetworkByte addEndingPointer() throws IOException {
    var byteArrayOutputStream = createByteStreamWithData();
    byteArrayOutputStream.write(new byte[] {(byte) 0xC0, 0x00});
    return setByteStreamPositionAfterData(byteArrayOutputStream);
  }

  private static NetworkByte addEndingLabelAndPointer() throws IOException {
    var byteArrayOutputStream = createByteStreamWithData();
    byteArrayOutputStream.write(new byte[] {0x03, 0x77, 0x77, 0x77});
    byteArrayOutputStream.write(new byte[] {(byte) 0xC0, 0x00});
    return setByteStreamPositionAfterData(byteArrayOutputStream);
  }

  private static ByteArrayOutputStream createByteStreamWithData() throws IOException {
    var byteArrayOutputStream = new ByteArrayOutputStream();
    byteArrayOutputStream.write(DOMAIN_BYTES);
    return byteArrayOutputStream;
  }

  private static NetworkByte setByteStreamPositionAfterData(
      ByteArrayOutputStream byteArrayOutputStream) {
    var networkBytes = NetworkByte.of(byteArrayOutputStream.toByteArray());
    networkBytes.jumpToPosition(11);
    return networkBytes;
  }
}
