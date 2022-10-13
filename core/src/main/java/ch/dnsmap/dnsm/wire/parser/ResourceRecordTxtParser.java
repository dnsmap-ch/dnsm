package ch.dnsmap.dnsm.wire.parser;

import static ch.dnsmap.dnsm.wire.bytes.ReadableWriteableByteBuffer.UINT_8;
import static java.nio.charset.StandardCharsets.UTF_8;

import ch.dnsmap.dnsm.record.type.Txt;
import ch.dnsmap.dnsm.wire.bytes.ReadableByteBuffer;
import ch.dnsmap.dnsm.wire.bytes.WriteableByteBuffer;

public final class ResourceRecordTxtParser implements WireWritable<Txt>,
    WireTypeReadable<Txt> {

  @Override
  public Txt fromWire(ReadableByteBuffer wireData, int length) {
    int txtLength = wireData.readUInt8();
    byte[] txtBytes = wireData.readData(txtLength);
    return new Txt(new String(txtBytes));
  }

  @Override
  public int toWire(WriteableByteBuffer wireData, Txt data) {
    int bytesWritten = wireData.writeUInt16(data.txt().length() + UINT_8);
    bytesWritten += wireData.writeUInt8(data.txt().length());
    bytesWritten += wireData.writeData(data.txt().getBytes(UTF_8));
    return bytesWritten;
  }
}
