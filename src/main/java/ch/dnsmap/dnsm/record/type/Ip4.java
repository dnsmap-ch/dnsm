package ch.dnsmap.dnsm.record.type;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Objects;

public final class Ip4 {

    private final Inet4Address ip;

    private Ip4(Inet4Address ip) {
        this.ip = ip;
    }

    public static Ip4 of(byte[] ip4bytes) {
        try {
            Inet4Address ip4 = (Inet4Address) Inet4Address.getByAddress(ip4bytes);
            return new Ip4(ip4);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static Ip4 of(String ip4String) {
        try {
            Inet4Address ip4 = (Inet4Address) Inet4Address.getByName(ip4String);
            return new Ip4(ip4);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public Inet4Address getIp() {
        return ip;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Ip4) obj;
        return Objects.equals(this.ip, that.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip);
    }

    @Override
    public String toString() {
        return "Ip4[" +
                "ip=" + ip + ']';
    }

}
