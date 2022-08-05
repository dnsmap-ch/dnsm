package ch.dnsmap.dnsm.wire;

import static ch.dnsmap.dnsm.Domain.root;
import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.assertThat;

import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.Label;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DomainParserTest {

  // 6 characters: dnsmap 2 characters: ch ending zero
  private static final byte[] DOMAIN_BYTES =
      new byte[] {0x06, 0x64, 0x6e, 0x73, 0x6d, 0x61, 0x70, 0x02, 0x63, 0x68, 0x00};
  private static final Domain DOMAIN = Domain.of(Label.of("dnsmap"), Label.of("ch"));
  private static final Domain HOST =
      Domain.of(Label.of("www"), Label.of("dnsmap"), Label.of("ch"));

  @Nested
  class FromWire {

    @Test
    void testRootFromWire() {
      var networkBytes = NetworkByte.of(new byte[] {0});
      DomainParser domainParser = new DomainParser();

      var domain = domainParser.fromWire(networkBytes);

      assertThat(domain).isEqualTo(root());
    }

    @Test
    void testASequenceOfLabelsEndingInAZeroOctetFromWire() {
      var networkBytes = NetworkByte.of(DOMAIN_BYTES);
      DomainParser domainParser = new DomainParser();

      var domain = domainParser.fromWire(networkBytes);

      assertThat(domain).isEqualTo(DOMAIN);
    }

    @Test
    void testAPointerFromWire() throws IOException {
      var networkBytes = addEndingPointer();
      DomainParser domainParser = new DomainParser();

      var domain = domainParser.fromWire(networkBytes);

      assertThat(domain).isEqualTo(DOMAIN);
    }

    @Test
    void testASequenceOfLabelsEndingWithAPointer() throws IOException {
      var networkBytes = addEndingLabelAndPointer();
      DomainParser domainParser = new DomainParser();

      var domain = domainParser.fromWire(networkBytes);

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

  @Nested
  class ToWire {

    @Test
    void testRootToWire() {
      var networkBytes = NetworkByte.of(
          new byte[] {0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42,
              0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42});
      DomainParser domainParser = new DomainParser();

      var bytes = domainParser.toWire(networkBytes, root());

      assertThat(bytes).isEqualTo(1);
      assertThat(networkBytes.savePosition()).isEqualTo(1);
      networkBytes.jumpToPosition(0);
      assertThat(networkBytes.readByte(1)).isEqualTo(new byte[] {0x00});
    }

    @Test
    void testASequenceOfLabelsEndingInAZeroOctetToWire() {
      var networkBytes = NetworkByte.of(
          new byte[] {0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42,
              0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42});
      DomainParser domainParser = new DomainParser();

      var bytes = domainParser.toWire(networkBytes, Domain.of("dnsmap.ch"));

      assertThat(bytes).isEqualTo(11);
      assertThat(networkBytes.savePosition()).isEqualTo(11);
      networkBytes.jumpToPosition(0);
      assertThat(networkBytes.readByte(11)).isEqualTo(DOMAIN_BYTES);
    }

    @Test
    void testAPointerToWire() {
      var networkBytes = NetworkByte.of(
          new byte[] {0x03, 0x77, 0x77, 0x77, 0x06, 0x64, 0x6e, 0x73, 0x6d, 0x61, 0x70, 0x02, 0x63,
              0x68, 0x00, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42});
      networkBytes.jumpToPosition(16);
      DomainParser domainParser = new DomainParser();
      DomainCompression domainCompression = new DomainCompression();
      domainCompression.addDomain(Domain.of("dnsmap.ch"), 5);
      domainParser.setDomainPositionMap(domainCompression);

      var bytes = domainParser.toWire(networkBytes, Domain.of("asdf.dnsmap.ch"));

      assertThat(bytes).isEqualTo(7);
      assertThat(networkBytes.savePosition()).isEqualTo(23);
      networkBytes.jumpToPosition(16);
      assertThat(networkBytes.readByte(7)).isEqualTo(
          new byte[] {0x04, 0x61, 0x73, 0x64, 0x66, (byte) 0xC0, 0x05});
    }
  }

  @Nested
  class BytesToWire {

    @Test
    void testRootDomainWithoutCompression() {
      var domain = root();
      DomainParser domainParser = new DomainParser();

      var nofBytes = domainParser.bytesToWrite(domain);

      assertThat(nofBytes).isEqualTo(1);
    }

    @Test
    void testSimpleDomainWithoutCompression() {
      var domain = Domain.of("a.bc.def.");
      DomainParser domainParser = new DomainParser();

      var nofBytes = domainParser.bytesToWrite(domain);

      assertThat(nofBytes).isEqualTo(10);
    }

    @Test
    void testSimpleDomainWithCompression() {
      var domain = Domain.of("a.bc.def.");
      var domainParser = domainParserWithCompression("def.");

      var nofBytes = domainParser.bytesToWrite(domain);

      assertThat(nofBytes).isEqualTo(7);
    }

    @Test
    void testDomainWithCompression() {
      var domain = Domain.of("a.bc.def.ghij.klmno.");
      var domainParser = domainParserWithCompression("asf.", ".ghij.klmno.", "foo.baz.");

      var nofBytes = domainParser.bytesToWrite(domain);

      assertThat(nofBytes).isEqualTo(11);
    }

    private static DomainParser domainParserWithCompression(String... domainCompressionEntries) {
      DomainParser domainParser = new DomainParser();
      setDomainCompression(domainParser, domainCompressionEntries);
      return domainParser;
    }

    private static void setDomainCompression(DomainParser domainParser,
                                             String... domainCompressionEntries) {
      DomainCompression domainCompression = new DomainCompression();
      stream(domainCompressionEntries).map(Domain::of)
          .forEach(domain -> domainCompression.addDomain(domain, 23));
      domainParser.setDomainPositionMap(domainCompression);
    }
  }
}
