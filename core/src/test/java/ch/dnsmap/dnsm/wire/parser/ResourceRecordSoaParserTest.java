package ch.dnsmap.dnsm.wire.parser;

import static org.assertj.core.api.Assertions.assertThat;

import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.Uint32;
import ch.dnsmap.dnsm.record.type.Soa;
import ch.dnsmap.dnsm.wire.bytes.NetworkByteBuffer;
import org.junit.jupiter.api.Test;

class ResourceRecordSoaParserTest {

  private static final Domain MNAME = Domain.of("ns1.gandi.net");
  private static final Domain RNAME = Domain.of("hostmaster.gandi.net");
  private static final Uint32 SERIAL = Uint32.of(1664409600);
  private static final Uint32 REFRESH = Uint32.of(10800);
  private static final Uint32 RETRY = Uint32.of(3600);
  private static final Uint32 EXPIRE = Uint32.of(604800);
  private static final Uint32 MINIMUM = Uint32.of(10800);

  @Test
  void testFromWire() {
    ResourceRecordSoaParser soaParser = new ResourceRecordSoaParser(new DomainParser());

    var networkBuffer = NetworkByteBuffer.of(soaBytes());

    Soa soa = soaParser.fromWire(networkBuffer, 48);

    assertThat(soa.mname()).isEqualTo(MNAME);
    assertThat(soa.rname()).isEqualTo(RNAME);
    assertThat(soa.serial()).isEqualTo(SERIAL);
    assertThat(soa.refresh()).isEqualTo(REFRESH);
    assertThat(soa.retry()).isEqualTo(RETRY);
    assertThat(soa.expire()).isEqualTo(EXPIRE);
    assertThat(soa.minimum()).isEqualTo(MINIMUM);
  }

  @Test
  void testToWire() {
    var networkBuffer = NetworkByteBuffer.of(48);
    ResourceRecordSoaParser soaParser = new ResourceRecordSoaParser(new DomainParser());

    var bytesWritten = soaParser.toWire(networkBuffer, soa());
    networkBuffer.jumpToPosition(0);

    assertThat(bytesWritten).isEqualTo(48);
    assertThat(networkBuffer.readData(48)).isEqualTo(soaBytes());
  }

  private static Soa soa() {
    return new Soa(MNAME, RNAME, SERIAL, REFRESH, RETRY, EXPIRE, MINIMUM);
  }

  private static byte[] soaBytes() {
    return new byte[]{
        (byte) 0x03, (byte) 0x6e, (byte) 0x73, (byte) 0x31, (byte) 0x05, (byte) 0x67, (byte) 0x61,
        (byte) 0x6e, (byte) 0x64, (byte) 0x69, (byte) 0x03, (byte) 0x6e, (byte) 0x65, (byte) 0x74,
        (byte) 0x00, (byte) 0x0a, (byte) 0x68, (byte) 0x6f, (byte) 0x73, (byte) 0x74, (byte) 0x6d,
        (byte) 0x61, (byte) 0x73, (byte) 0x74, (byte) 0x65, (byte) 0x72, (byte) 0xc0, (byte) 0x04,
        (byte) 0x63, (byte) 0x34, (byte) 0xe0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x2a,
        (byte) 0x30, (byte) 0x00, (byte) 0x00, (byte) 0x0e, (byte) 0x10, (byte) 0x00, (byte) 0x09,
        (byte) 0x3a, (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x2a, (byte) 0x30
    };
  }
}
