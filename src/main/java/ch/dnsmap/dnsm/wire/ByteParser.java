package ch.dnsmap.dnsm.wire;

public interface ByteParser<T> {

    /**
     * Parse wire data into DNS objects.
     *
     * @param wireData byte wire data
     * @return a DNS object of type T
     */
    T fromWire(ReadableByte wireData);

    /**
     * Parse a DNS object into wire data.
     *
     * @param wireData byte wire data
     * @param data     DNS object of type T
     * @return amount of bytes written
     */
    int toWire(WriteableByte wireData, T data);


    int bytesToWrite(T data);
}
