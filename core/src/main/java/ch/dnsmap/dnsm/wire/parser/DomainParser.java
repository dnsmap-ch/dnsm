package ch.dnsmap.dnsm.wire.parser;

import static ch.dnsmap.dnsm.wire.ParsingLog.error;
import static ch.dnsmap.dnsm.wire.ParsingLog.warn;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.Label;
import ch.dnsmap.dnsm.wire.ParserOptions;
import ch.dnsmap.dnsm.wire.bytes.ReadableByteBuffer;
import ch.dnsmap.dnsm.wire.bytes.WriteableByteBuffer;
import ch.dnsmap.dnsm.wire.parser.DomainCompression.Position;
import java.util.ArrayList;
import java.util.List;

public final class DomainParser
    implements WireWritable<Domain>, WireReadable<Domain>, WireTypeReadable<Domain> {

  private static final int END_OF_DOMAIN = 0;

  private final DomainCompression domainCompression;
  private final ParserOptions options;

  public DomainParser() {
    this(ParserOptions.Builder.builder().build());
  }

  public DomainParser(ParserOptions options) {
    this.domainCompression = new DomainCompression();
    this.options = options;
  }

  @Override
  public Domain fromWire(ReadableByteBuffer wireData) {
    int restorePoint = wireData.createRestorePosition();
    int length = calculateDomainLength(wireData);
    wireData.restorePosition(restorePoint);
    return fromWire(wireData, length);
  }

  @Override
  public Domain fromWire(ReadableByteBuffer wireData, int length) {
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
      Domain domain = Domain.of(labels);
      options.log(error(format("domain '%s' longer than message length", domain)));
      return domain;
    }

    byte[] labelBytes = wireData.readData(labelLength);
    labels.add(labelFromBytes(labelBytes));

    Domain subdomain = fromWire(wireData, length - bytesRead);
    labels.addAll(subdomain.getLabels());
    return Domain.of(labels);
  }

  private Label labelFromBytes(byte[] labelBytes) {
    Label label;
    if (options.isDomainLabelTolerant()) {
      try {
        label = Label.of(labelBytes);
      } catch (IllegalArgumentException e) {
        options.log(warn(e.getMessage()));
        label = Label.tolerantOf(labelBytes);
      }
    } else {
      label = Label.of(labelBytes);
    }
    return label;
  }

  private static int calculateDomainLength(ReadableByteBuffer wireData) {
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

  private Domain readPointer(ReadableByteBuffer wireData) {
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
  public int toWire(WriteableByteBuffer wireData, Domain data) {

    if (data.getLabels().isEmpty()) {
      return wireData.writeUInt8(END_OF_DOMAIN);
    }

    if (domainCompression.getPointer(data).isPresent()) {
      return writePointerToDomain(wireData, domainCompression.getPointer(data).get());
    }

    domainCompression.addDomain(data, wireData.getPosition());
    int bytesWritten = writeLabel(wireData, data.getFirstLabel());

    return bytesWritten + toWire(wireData, data.getDomainWithoutFirstLabel());
  }

  private static int writePointerToDomain(WriteableByteBuffer wireData, Position position) {
    int positionPointer = position.value() | 0xC000;
    return wireData.writeUInt16(positionPointer);
  }

  private static int writeLabel(WriteableByteBuffer wireData, Label label) {
    int bytesWritten = wireData.writeUInt8(label.length());
    bytesWritten += wireData.writeData(label.getLabel().getBytes(UTF_8));
    return bytesWritten;
  }
}
