package ch.dnsmap.dnsm.record.type;

import static java.net.InetAddress.getByAddress;
import static java.net.InetAddress.getByName;

import java.net.Inet6Address;
import java.net.UnknownHostException;
import java.util.Objects;

public final class Ip6 {

  private final Inet6Address ip;

  private Ip6(Inet6Address ip) {
    this.ip = ip;
  }

  public static Ip6 of(byte[] ip6bytes) {
    try {
      Inet6Address ip6 = (Inet6Address) getByAddress(ip6bytes);
      return new Ip6(ip6);
    } catch (UnknownHostException e) {
      throw new RuntimeException(e);
    }
  }

  public static Ip6 of(String ip6String) {
    try {
      Inet6Address ip6 = (Inet6Address) getByName(ip6String);
      return new Ip6(ip6);
    } catch (UnknownHostException e) {
      throw new RuntimeException(e);
    }
  }

  public Inet6Address getIp() {
    return ip;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null || obj.getClass() != this.getClass()) {
      return false;
    }
    var that = (Ip6) obj;
    return Objects.equals(this.ip, that.ip);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ip);
  }

  @Override
  public String toString() {
    return "Ip6[" + "ip=" + ip + ']';
  }
}
