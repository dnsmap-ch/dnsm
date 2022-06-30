package ch.dnsmap.dnsm.wire;


import ch.dnsmap.dnsm.Domain;
import ch.dnsmap.dnsm.Label;

import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

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

        int labelLength = wireData.readUInt8();
        if (isPointerCompression(labelLength)) {
            return followCompressionPointer(wireData);
        }
        if (labelLength == 0) {
            return Domain.root();
        }
        List<Label> labels = extractLabels(wireData, labelLength);
        return Domain.of(labels.toArray(new Label[0]));
    }

    private Domain followCompressionPointer(ReadableByte wireData) {
        int jumpToLabelPosition = wireData.readUInt8();
        int restorePosition = wireData.savePosition();
        wireData.jumpToPosition(jumpToLabelPosition);
        Domain domain = fromWire(wireData);
        wireData.restorePosition(restorePosition);
        return domain;
    }

    private List<Label> extractLabels(ReadableByte wireData, int labelLength) {
        List<Label> labels = new ArrayList<>();
        byte[] labelBytes = wireData.readByte(labelLength);
        do {
            String labelString = new String(labelBytes);
            labels.add(new Label(labelString));
        } while ((labelBytes = wireData.readByteFromLength8()).length != 0);
        return labels;
    }

    private boolean isPointerCompression(int labelLength) {
        return (labelLength & 0xC0) != 0;
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
        int bytesToWrite = 0;
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
