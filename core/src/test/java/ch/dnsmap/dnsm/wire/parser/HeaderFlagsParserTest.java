package ch.dnsmap.dnsm.wire.parser;

import static ch.dnsmap.dnsm.header.HeaderBitFlags.AA;
import static ch.dnsmap.dnsm.header.HeaderBitFlags.QR;
import static ch.dnsmap.dnsm.header.HeaderBitFlags.RA;
import static ch.dnsmap.dnsm.header.HeaderBitFlags.RD;
import static ch.dnsmap.dnsm.header.HeaderBitFlags.TC;
import static ch.dnsmap.dnsm.header.HeaderOpcode.IQUERY;
import static ch.dnsmap.dnsm.header.HeaderOpcode.QUERY;
import static ch.dnsmap.dnsm.header.HeaderOpcode.STATUS;
import static ch.dnsmap.dnsm.header.HeaderRcode.FORMAT_ERROR;
import static ch.dnsmap.dnsm.header.HeaderRcode.NAME_ERROR;
import static ch.dnsmap.dnsm.header.HeaderRcode.NOT_IMPLEMENTED;
import static ch.dnsmap.dnsm.header.HeaderRcode.NO_ERROR;
import static ch.dnsmap.dnsm.header.HeaderRcode.REFUSED;
import static ch.dnsmap.dnsm.header.HeaderRcode.SERVER_FAILURE;
import static org.assertj.core.api.Assertions.assertThat;

import ch.dnsmap.dnsm.header.HeaderFlags;
import ch.dnsmap.dnsm.header.HeaderOpcode;
import ch.dnsmap.dnsm.header.HeaderRcode;
import ch.dnsmap.dnsm.wire.bytes.NetworkByte;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HeaderFlagsParserTest {

  private static final HeaderFlagsParser PARSER = new HeaderFlagsParser();

  @Nested
  class FromWire {

    @Test
    void testMinFlags() {
      var networkBytes = minimalNetworkBytes();
      var headerFlags = PARSER.fromWire(networkBytes, 2);
      assertFlags(headerFlags, QUERY, NO_ERROR);
    }

    @Test
    void testMaxFlags() {
      var networkBytes = maximalNetworkBytes();
      var headerFlags = PARSER.fromWire(networkBytes, 2);
      assertThat(headerFlags.getOpcode()).isEqualTo(STATUS);
      assertThat(headerFlags.getRcode()).isEqualTo(REFUSED);
      assertThat(headerFlags.getFlags()).containsExactlyInAnyOrder(AA, TC, RD, RA, QR);
    }

    @Test
    void testOpcodeQuery() {
      var networkBytes = opcodeQuery();
      var headerFlags = PARSER.fromWire(networkBytes, 2);
      assertFlags(headerFlags, QUERY, NO_ERROR);
    }

    @Test
    void testOpcodeIquery() {
      var networkBytes = opcodeIquery();
      var headerFlags = PARSER.fromWire(networkBytes, 2);
      assertFlags(headerFlags, IQUERY, NO_ERROR);
    }

    @Test
    void testOpcodeStatus() {
      var networkBytes = opcodeStatus();
      var headerFlags = PARSER.fromWire(networkBytes, 2);
      assertFlags(headerFlags, STATUS, NO_ERROR);
    }

    @Test
    void testRcodeNoError() {
      var networkBytes = rcodeNoError();
      var headerFlags = PARSER.fromWire(networkBytes, 2);
      assertFlags(headerFlags, QUERY, NO_ERROR);
    }

    @Test
    void testRcodeFormatError() {
      var networkBytes = rcodeFormatError();
      var headerFlags = PARSER.fromWire(networkBytes, 2);
      assertFlags(headerFlags, QUERY, FORMAT_ERROR);
    }

    @Test
    void testRcodeServerFailure() {
      var networkBytes = rcodeServerFailure();
      var headerFlags = PARSER.fromWire(networkBytes, 2);
      assertFlags(headerFlags, QUERY, SERVER_FAILURE);
    }

    @Test
    void testRcodeNameError() {
      var networkBytes = rcodeNameError();
      var headerFlags = PARSER.fromWire(networkBytes, 2);
      assertFlags(headerFlags, QUERY, NAME_ERROR);
    }

    @Test
    void testRcodeNotImplemented() {
      var networkBytes = rcodeNotImplemented();
      var headerFlags = PARSER.fromWire(networkBytes, 2);
      assertFlags(headerFlags, QUERY, NOT_IMPLEMENTED);
    }

    @Test
    void testRcodeRefused() {
      var networkBytes = rcodeRefused();
      var headerFlags = PARSER.fromWire(networkBytes, 2);
      assertFlags(headerFlags, QUERY, REFUSED);
    }

    private void assertFlags(HeaderFlags headerFlags, HeaderOpcode query, HeaderRcode noError) {
      assertThat(headerFlags.getOpcode()).isEqualTo(query);
      assertThat(headerFlags.getRcode()).isEqualTo(noError);
      assertThat(headerFlags.getFlags()).isEmpty();
    }

    private static NetworkByte minimalNetworkBytes() {
      return NetworkByte.of(new byte[] {0, 0});
    }

    private static NetworkByte maximalNetworkBytes() {
      return NetworkByte.of(new byte[] {(byte) 0b1001_0111, (byte) 0b1000_0101});
    }

    private static NetworkByte opcodeQuery() {
      return NetworkByte.of(new byte[] {(byte) 0b000_0000, (byte) 0b0000_0000});
    }

    private static NetworkByte opcodeIquery() {
      return NetworkByte.of(new byte[] {(byte) 0b000_1000, (byte) 0b0000_0000});
    }

    private static NetworkByte opcodeStatus() {
      return NetworkByte.of(new byte[] {(byte) 0b001_0000, (byte) 0b0000_0000});
    }

    private static NetworkByte rcodeNoError() {
      return NetworkByte.of(new byte[] {(byte) 0b000_0000, (byte) 0b0000_0000});
    }

    private static NetworkByte rcodeFormatError() {
      return NetworkByte.of(new byte[] {(byte) 0b000_0000, (byte) 0b0000_0001});
    }

    private static NetworkByte rcodeServerFailure() {
      return NetworkByte.of(new byte[] {(byte) 0b000_0000, (byte) 0b0000_0010});
    }

    private static NetworkByte rcodeNameError() {
      return NetworkByte.of(new byte[] {(byte) 0b000_0000, (byte) 0b0000_0011});
    }

    private static NetworkByte rcodeNotImplemented() {
      return NetworkByte.of(new byte[] {(byte) 0b000_0000, (byte) 0b0000_0100});
    }

    private static NetworkByte rcodeRefused() {
      return NetworkByte.of(new byte[] {(byte) 0b000_0000, (byte) 0b0000_0101});
    }
  }

  @Nested
  class ToWire {

    @Test
    void testOpcodeQuery() {
      var flags = new HeaderFlags(QUERY, NO_ERROR);
      var networkBytes = NetworkByte.of(2);

      var bytes = PARSER.toWire(networkBytes, flags);

      assertFlags(networkBytes, bytes, new byte[] {(byte) 0x80, (byte) 0x0});
    }

    @Test
    void testOpcodeIquery() {
      var flags = new HeaderFlags(IQUERY, NO_ERROR);
      var networkBytes = NetworkByte.of(2);

      var bytes = PARSER.toWire(networkBytes, flags);

      assertFlags(networkBytes, bytes, new byte[] {(byte) 0x88, (byte) 0x0});
    }

    @Test
    void testOpcodeStatus() {
      var flags = new HeaderFlags(STATUS, NO_ERROR);
      var networkBytes = NetworkByte.of(2);

      var bytes = PARSER.toWire(networkBytes, flags);

      assertFlags(networkBytes, bytes, new byte[] {(byte) 0x90, (byte) 0x0});
    }

    @Test
    void testRcodeNoError() {
      var flags = new HeaderFlags(QUERY, NO_ERROR);
      var networkBytes = NetworkByte.of(2);

      var bytes = PARSER.toWire(networkBytes, flags);

      assertFlags(networkBytes, bytes, new byte[] {(byte) 0x80, (byte) 0x0});
    }

    @Test
    void testRcodeFormatError() {
      var flags = new HeaderFlags(QUERY, FORMAT_ERROR);
      var networkBytes = NetworkByte.of(2);

      var bytes = PARSER.toWire(networkBytes, flags);

      assertFlags(networkBytes, bytes, new byte[] {(byte) 0x80, (byte) 0x01});
    }

    @Test
    void testRcodeServerFailure() {
      var flags = new HeaderFlags(QUERY, SERVER_FAILURE);
      var networkBytes = NetworkByte.of(2);

      var bytes = PARSER.toWire(networkBytes, flags);

      assertFlags(networkBytes, bytes, new byte[] {(byte) 0x80, (byte) 0x02});
    }

    @Test
    void testRcodeNameError() {
      var flags = new HeaderFlags(QUERY, NAME_ERROR);
      var networkBytes = NetworkByte.of(2);

      var bytes = PARSER.toWire(networkBytes, flags);

      assertFlags(networkBytes, bytes, new byte[] {(byte) 0x80, (byte) 0x03});
    }

    @Test
    void testRcodeNotImplemented() {
      var flags = new HeaderFlags(QUERY, NOT_IMPLEMENTED);
      var networkBytes = NetworkByte.of(2);

      var bytes = PARSER.toWire(networkBytes, flags);

      assertFlags(networkBytes, bytes, new byte[] {(byte) 0x80, (byte) 0x04});
    }

    @Test
    void testRcodeRefused() {
      var flags = new HeaderFlags(QUERY, REFUSED);
      var networkBytes = NetworkByte.of(2);

      var bytes = PARSER.toWire(networkBytes, flags);

      assertFlags(networkBytes, bytes, new byte[] {(byte) 0x80, (byte) 0x05});
    }

    @Test
    void testAllFlagsSetInQuery() {
      var flags = new HeaderFlags(QUERY, NO_ERROR, QR, AA, TC, RD, RA);
      var networkBytes = NetworkByte.of(2);

      var bytes = PARSER.toWire(networkBytes, flags);

      assertFlags(networkBytes, bytes, new byte[] {(byte) 0x87, (byte) (byte) 0x80});
    }

    @Test
    void testAllFlagsSetInResponse() {
      var flags = new HeaderFlags(QUERY, NO_ERROR, AA, TC, RD, RA);
      var networkBytes = NetworkByte.of(2);

      var bytes = PARSER.toWire(networkBytes, flags);

      assertFlags(networkBytes, bytes, new byte[] {(byte) 0x87, (byte) (byte) 0x80});
    }

    private void assertFlags(NetworkByte networkBytes, int bytes, byte[] flagBytes) {
      networkBytes.jumpToPosition(0);
      assertThat(bytes).isEqualTo(2);
      assertThat(networkBytes.readByte16()).isEqualTo(flagBytes);
    }
  }

  @Test
  void testBytesToWrite() {
    var flags = new HeaderFlags(QUERY, NO_ERROR, QR);
    var bytes = PARSER.bytesToWrite(flags);
    assertThat(bytes).isEqualTo(2);
  }
}
