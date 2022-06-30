package ch.dnsmap.dnsm.wire;

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
}
