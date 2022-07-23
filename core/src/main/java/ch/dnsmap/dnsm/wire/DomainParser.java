package ch.dnsmap.dnsm.wire;


import static java.nio.charset.StandardCharsets.UTF_8;

import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.Label;
import java.util.ArrayList;
import java.util.List;

public final class DomainParser implements ByteParser<Domain> {

  private static final int DNS_DOMAIN_NAME_TERMINATION = 0;
  private static final int DNS_DOMAIN_NAME_TERMINATION_BYTE_LENGTH = 1;
  private static final int DNS_POINTER_BYTES_LENGTH = 2;
  private static final int DNS_LABEL_FIELD_LENGTH = 1;

  private DomainCompression domainCompression;

  public void setDomainPositionMap(DomainCompression domainCompression) {
    this.domainCompression = domainCompression;
  }

  @Override
  public Domain fromWire(ReadableByte wireData) {
    List<Label> labels = new ArrayList<>();

    int peakByte;
    if ((peakByte = wireData.peakUInt8()) == 0) {
      wireData.readUInt8();
      return Domain.of(labels.toArray(new Label[0]));
    }

    if (isPointerCompression(peakByte)) {
      Domain domain = readPointer(wireData);
      labels.addAll(domain.getLabels());
    } else {
      Domain domain = readLabel(wireData);
      labels.addAll(domain.getLabels());
      Domain subdomain = fromWire(wireData);
      labels.addAll(subdomain.getLabels());
    }
    return Domain.of(labels.toArray(new Label[0]));
  }

  private Domain readPointer(ReadableByte wireData) {
    int jumpToLabelPosition = resolvePointerPosition(wireData.readUInt16());
    int restorePosition = wireData.savePosition();
    wireData.jumpToPosition(jumpToLabelPosition);
    Domain domain = fromWire(wireData);
    wireData.restorePosition(restorePosition);
    return domain;
  }

  private static Domain readLabel(ReadableByte wireData) {
    int labelLength = wireData.readUInt8();
    byte[] labelBytes = wireData.readByte(labelLength);
    String labelString = new String(labelBytes);
    return Domain.of(new Label(labelString));
  }

  private static boolean isPointerCompression(int labelLength) {
    return (labelLength & 0xC0) == 0xC0;
  }

  private static int resolvePointerPosition(int pointerPosition) {
    return pointerPosition & 0x3FFF;
  }

  @Override
  public int toWire(WriteableByte wireData, Domain data) {
    int bytesWritten = 0;

    if (domainCompression != null && domainCompression.contains(data)) {
      int pointer = domainCompression.getPointer(data);
      bytesWritten += wireData.writeUInt16(pointer);
      return bytesWritten;
    }

    bytesWritten = data.getLabels().stream()
        .map(label -> {
          int length = wireData.writeUInt8(label.length());
          length += wireData.writeByte(label.label().getBytes(UTF_8));
          return length;
        })
        .reduce(0, Integer::sum);
    bytesWritten += wireData.writeUInt8(DNS_DOMAIN_NAME_TERMINATION);
    return bytesWritten;
  }

  public int bytesToWrite(Domain data) {
    int bytesToWrite;
    if (domainCompression != null && domainCompression.contains(data)) {
      return DNS_POINTER_BYTES_LENGTH;
    }

    bytesToWrite = data.getLabels().stream()
        .map(label -> {
          int length = DNS_LABEL_FIELD_LENGTH;
          length += label.label().length();
          return length;
        })
        .reduce(0, Integer::sum);
    bytesToWrite += DNS_DOMAIN_NAME_TERMINATION_BYTE_LENGTH;
    return bytesToWrite;
  }
}
