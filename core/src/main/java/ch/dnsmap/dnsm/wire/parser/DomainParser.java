package ch.dnsmap.dnsm.wire.parser;

import static java.nio.charset.StandardCharsets.UTF_8;

import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.Label;
import ch.dnsmap.dnsm.wire.DomainCompression;
import ch.dnsmap.dnsm.wire.bytes.ReadableByte;
import ch.dnsmap.dnsm.wire.bytes.WriteableByte;
import java.util.ArrayList;
import java.util.List;

public final class DomainParser implements ByteParser<Domain> {

  private static final int END_OF_DOMAIN = 0;
  private static final int DNS_DOMAIN_NAME_TERMINATION_BYTE_LENGTH = 1;
  private static final int DNS_POINTER_BYTES_LENGTH = 2;
  private static final int DNS_LABEL_FIELD_LENGTH = 1;

  private DomainCompression domainCompression;

  public void setDomainPositionMap(DomainCompression domainCompression) {
    this.domainCompression = domainCompression;
  }

  @Override
  public Domain fromWire(ReadableByte wireData) {
    int restorePoint = wireData.createRestorePosition();
    int length = calculateDomainLength(wireData);
    wireData.restorePosition(restorePoint);
    return fromWire(wireData, length);
  }

  @Override
  public Domain fromWire(ReadableByte wireData, int length) {
    List<Label> labels = new ArrayList<>(length);
    int bytesRead = 0;

    int peakByte = wireData.peakUInt8();
    if (peakByte == 0) {
      wireData.readUInt8();
      return Domain.of(labels);
    }

    if (isPointerCompression(peakByte)) {
      Domain domain = readPointer(wireData);
      labels.addAll(domain.getLabels());
      return Domain.of(labels);
    }

    int labelLength = wireData.readUInt8();
    bytesRead += labelLength;
    if (bytesRead > length) {
      // TODO add logging this is a special case where the domain is potential longer than bytes to
      //  read
      return Domain.of(labels);
    }

    byte[] labelBytes = wireData.readByte(labelLength);
    labels.add(Label.of(labelBytes));

    Domain subdomain = fromWire(wireData, length - bytesRead);
    labels.addAll(subdomain.getLabels());
    return Domain.of(labels);
  }

  private static int calculateDomainLength(ReadableByte wireData) {
    int labelLength = wireData.peakUInt8();
    if (labelLength == END_OF_DOMAIN) {
      return 1;
    }

    if (isPointerCompression(labelLength)) {
      int length = 2;
      int restorePosition = wireData.createRestorePosition();
      int positionOfDomain = resolvePointerPosition(wireData.readUInt16());
      wireData.jumpToPosition(positionOfDomain);
      length += calculateDomainLength(wireData);
      wireData.restorePosition(restorePosition);
      return length;
    }

    int length = 1;
    length += labelLength;
    int restorePosition = wireData.createRestorePosition();
    wireData.jumpToPosition(restorePosition + length);
    length += calculateDomainLength(wireData);
    wireData.restorePosition(restorePosition);
    return length;
  }

  private Domain readPointer(ReadableByte wireData) {
    int jumpToLabelPosition = resolvePointerPosition(wireData.readUInt16());
    int restorePosition = wireData.createRestorePosition();
    wireData.jumpToPosition(jumpToLabelPosition);
    int length = calculateDomainLength(wireData);
    Domain domain = fromWire(wireData, length);
    wireData.restorePosition(restorePosition);
    return domain;
  }

  private static boolean isPointerCompression(int labelLength) {
    return (labelLength & 0xC0) == 0xC0;
  }

  private static int resolvePointerPosition(int pointerPosition) {
    return pointerPosition & 0x3FFF;
  }

  @Override
  public int toWire(WriteableByte wireData, Domain data) {

    if (domainCompression == null) {
      return writeFullDomain(wireData, data);
    }

    if (domainCompression.contains(data)) {
      return writePointerToDomain(wireData, domainCompression.getPointer(data));
    }

    domainCompression.addDomain(data, wireData.getPosition());
    int bytesWritten = writeLabel(wireData, data.getFirstLabel());

    return bytesWritten + toWire(wireData, data.getDomainWithoutFirstLabel());
  }

  private static int writeFullDomain(WriteableByte wireData, Domain data) {
    int bytesWritten = data.getLabels().stream()
        .map(label -> writeLabel(wireData, label))
        .reduce(0, Integer::sum);
    bytesWritten += wireData.writeUInt8(END_OF_DOMAIN);
    return bytesWritten;
  }

  private static int writePointerToDomain(WriteableByte wireData, int pointer) {
    return wireData.writeUInt16(pointer);
  }

  private static int writeLabel(WriteableByte wireData, Label label) {
    int bytesWritten = wireData.writeUInt8(label.length());
    bytesWritten += wireData.writeByte(label.getLabel().getBytes(UTF_8));
    return bytesWritten;
  }

  /**
   * Count amount of bytes a domain name requires to be written to the network. Each label has a one
   * byte size prefix, a domain name has an ending zero byte and compression pointers have neither
   * of both.
   *
   * @param data domain name to count its labels total length
   * @return the length in bytes of a domains label
   */
  @Override
  public int bytesToWrite(Domain data) {

    if (domainCompression == null) {
      return countDomainWithoutCompression(data);
    }

    if (domainCompression.contains(data)) {
      return DNS_POINTER_BYTES_LENGTH;
    }

    int bytesToWrite = DNS_LABEL_FIELD_LENGTH + data.getFirstLabel().length();
    bytesToWrite += bytesToWrite(data.getDomainWithoutFirstLabel());
    return bytesToWrite;
  }

  private static int countDomainWithoutCompression(Domain data) {
    int bytesToWrite = data.getLabels().stream()
        .map(label -> {
          int length = DNS_LABEL_FIELD_LENGTH;
          length += label.getLabel().length();
          return length;
        })
        .reduce(0, Integer::sum);
    bytesToWrite += DNS_DOMAIN_NAME_TERMINATION_BYTE_LENGTH;
    return bytesToWrite;
  }
}