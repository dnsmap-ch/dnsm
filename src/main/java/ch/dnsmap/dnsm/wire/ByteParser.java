package ch.dnsmap.dnsm.wire;

public interface ByteParser<T> {

    /**
     * Parse wire data into DNS objects.
     *
     * @param wireData byte wire data
     * @return a DNS object of type T
     */
    T fromWire(ReadableByte wireData);
}
