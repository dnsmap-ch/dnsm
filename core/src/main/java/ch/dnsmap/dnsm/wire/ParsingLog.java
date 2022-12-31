package ch.dnsmap.dnsm.wire;

import static ch.dnsmap.dnsm.wire.ParsingLog.Type.ERROR;
import static ch.dnsmap.dnsm.wire.ParsingLog.Type.WARN;

public class ParsingLog {

  private final Type type;
  private final String message;

  private ParsingLog(Type type, String message) {
    this.type = type;
    this.message = message;
  }

  public static ParsingLog error(String message) {
    return new ParsingLog(ERROR, message);
  }

  public static ParsingLog warn(String message) {
    return new ParsingLog(WARN, message);
  }

  public String formatted() {
    return type + ": " + message;
  }

  enum Type {
    ERROR,
    WARN
  }
}
