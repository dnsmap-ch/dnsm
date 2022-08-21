package ch.dnsmap.dnsm.wire.record;

import static java.nio.charset.StandardCharsets.UTF_8;

import ch.dnsmap.dnsm.record.type.Txt;
import ch.dnsmap.dnsm.wire.ByteParser;
import ch.dnsmap.dnsm.wire.ReadableByte;
import ch.dnsmap.dnsm.wire.WriteableByte;

public final class ResourceRecordTxtParser implements ByteParser<Txt> {

  @Override
  public Txt fromWire(ReadableByte wireData) {
    return null;
  }

  @Override
  public Txt fromWire(ReadableByte wireData, int length) {
    int txtLength = wireData.readUInt8();
    byte[] txtBytes = wireData.readByte(txtLength);
    return new Txt(new String(txtBytes));
  }

  @Override
  public int toWire(WriteableByte wireData, Txt data) {
    int bytesWritten = wireData.writeUInt16(data.txt().length() + 1);
    bytesWritten += wireData.writeUInt8(data.txt().length());
    bytesWritten += wireData.writeByte(data.txt().getBytes(UTF_8));
    return bytesWritten;
  }

  @Override
  public int bytesToWrite(Txt data) {
    return data.txt().length();
  }
}
