package ch.dnsmap.dnsm.wire.parser;

import static ch.dnsmap.dnsm.Domain.root;
import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.assertThat;

import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.Label;
import ch.dnsmap.dnsm.wire.DomainCompression;
import ch.dnsmap.dnsm.wire.bytes.NetworkByteBuffer;
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
      var networkBytes = NetworkByteBuffer.of(new byte[] {0});
      DomainParser domainParser = DomainParser.parseInput();

      var domain = domainParser.fromWire(networkBytes);

      assertThat(domain).isEqualTo(root());
    }

    @Test
    void testASequenceOfLabelsEndingInAZeroOctetFromWire() {
      var networkBytes = NetworkByteBuffer.of(DOMAIN_BYTES);
      DomainParser domainParser = DomainParser.parseInput();

      var domain = domainParser.fromWire(networkBytes);

      assertThat(domain).isEqualTo(DOMAIN);
    }

    @Test
    void testAPointerFromWire() throws IOException {
      var networkBytes = addEndingPointer();
      DomainParser domainParser = DomainParser.parseInput();

      var domain = domainParser.fromWire(networkBytes);

      assertThat(domain).isEqualTo(DOMAIN);
    }

    @Test
    void testASequenceOfLabelsEndingWithAPointer() throws IOException {
      var networkBytes = addEndingLabelAndPointer();
      DomainParser domainParser = DomainParser.parseInput();

      var domain = domainParser.fromWire(networkBytes);

      assertThat(domain).isEqualTo(HOST);
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
      byteArrayOutputStream.write(DOMAIN_BYTES);
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
      DomainParser domainParser = DomainParser.parseInput();

      var domain = domainParser.fromWire(networkBytes, 1);

      assertThat(domain).isEqualTo(root());
    }

    @Test
    void testASequenceOfLabelsEndingInAZeroOctetFromWire() {
      var networkBytes = NetworkByteBuffer.of(DOMAIN_BYTES);
      DomainParser domainParser = DomainParser.parseInput();

      var domain = domainParser.fromWire(networkBytes, DOMAIN_BYTES.length);

      assertThat(domain).isEqualTo(DOMAIN);
    }

    @Test
    void testASequenceOfLabelsEndingInAZeroOctetFromWireReadOnlyFirstLabel() {
      var networkBytes = NetworkByteBuffer.of(DOMAIN_BYTES);
      DomainParser domainParser = DomainParser.parseInput();

      var domain = domainParser.fromWire(networkBytes, 6);

      assertThat(domain).isEqualTo(Domain.of("dnsmap"));
    }

    @Test
    void testAPointerFromWire() throws IOException {
      var networkBytes = addEndingPointer();
      DomainParser domainParser = DomainParser.parseInput();

      var domain = domainParser.fromWire(networkBytes, 42);

      assertThat(domain).isEqualTo(DOMAIN);
    }

    @Test
    void testASequenceOfLabelsEndingWithAPointer() throws IOException {
      var networkBytes = addEndingLabelAndPointer();
      DomainParser domainParser = DomainParser.parseInput();

      var domain = domainParser.fromWire(networkBytes, 42);

      assertThat(domain).isEqualTo(HOST);
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
      byteArrayOutputStream.write(DOMAIN_BYTES);
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
      var networkBytes = NetworkByteBuffer.of(
          new byte[] {0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42,
              0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42});
      DomainParser domainParser = DomainParser.parseInput();

      var bytes = domainParser.toWire(networkBytes, root());

      assertThat(bytes).isEqualTo(1);
      assertThat(networkBytes.createRestorePosition()).isEqualTo(1);
      networkBytes.jumpToPosition(0);
      assertThat(networkBytes.readData(1)).isEqualTo(new byte[] {0x00});
    }

    @Test
    void testASequenceOfLabelsEndingInAZeroOctetToWire() {
      var networkBytes = NetworkByteBuffer.of(
          new byte[] {0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42,
              0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42});
      DomainParser domainParser = DomainParser.parseInput();

      var bytes = domainParser.toWire(networkBytes, Domain.of("dnsmap.ch"));

      assertThat(bytes).isEqualTo(11);
      assertThat(networkBytes.createRestorePosition()).isEqualTo(11);
      networkBytes.jumpToPosition(0);
      assertThat(networkBytes.readData(11)).isEqualTo(DOMAIN_BYTES);
    }

    @Test
    void testAPointerToWire() {
      var networkBytes = NetworkByteBuffer.of(
          new byte[] {0x03, 0x77, 0x77, 0x77, 0x06, 0x64, 0x6e, 0x73, 0x6d, 0x61, 0x70, 0x02, 0x63,
              0x68, 0x00, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42, 0x42});
      networkBytes.jumpToPosition(16);
      DomainCompression domainCompression = new DomainCompression();
      DomainParser domainParser = DomainParser.parseOutput(domainCompression);
      domainCompression.addDomain(Domain.of("dnsmap.ch"), 5);

      var bytes = domainParser.toWire(networkBytes, Domain.of("asdf.dnsmap.ch"));

      assertThat(bytes).isEqualTo(7);
      assertThat(networkBytes.createRestorePosition()).isEqualTo(23);
      networkBytes.jumpToPosition(16);
      assertThat(networkBytes.readData(7)).isEqualTo(
          new byte[] {0x04, 0x61, 0x73, 0x64, 0x66, (byte) 0xC0, 0x05});
    }
  }

  @Nested
  class BytesToWire {

    @Test
    void testRootDomainWithoutCompression() {
      var domain = root();
      DomainParser domainParser = DomainParser.parseInput();

      var nofBytes = domainParser.bytesToWrite(domain);

      assertThat(nofBytes).isEqualTo(1);
    }

    @Test
    void testSimpleDomainWithoutCompression() {
      var domain = Domain.of("a.bc.def.");
      DomainParser domainParser = DomainParser.parseInput();

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
      var domainParser = domainParserWithCompression("asf.", "ghij.klmno.", "foo.baz.");

      var nofBytes = domainParser.bytesToWrite(domain);

      assertThat(nofBytes).isEqualTo(11);
    }

    private static DomainParser domainParserWithCompression(String... domainCompressionEntries) {
      DomainCompression domainCompression = new DomainCompression();
      DomainParser domainParser = DomainParser.parseOutput(domainCompression);
      stream(domainCompressionEntries).map(Domain::of)
          .forEach(domain -> domainCompression.addDomain(domain, 23));
      return domainParser;
    }
  }
}
