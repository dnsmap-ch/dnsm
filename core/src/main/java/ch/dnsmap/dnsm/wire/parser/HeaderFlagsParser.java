package ch.dnsmap.dnsm.wire.parser;

import static ch.dnsmap.dnsm.header.HeaderBitFlags.AA;
import static ch.dnsmap.dnsm.header.HeaderBitFlags.QR;
import static ch.dnsmap.dnsm.header.HeaderBitFlags.RA;
import static ch.dnsmap.dnsm.header.HeaderBitFlags.RD;
import static ch.dnsmap.dnsm.header.HeaderBitFlags.TC;
import static ch.dnsmap.dnsm.header.HeaderOpcode.IQUERY;
import static ch.dnsmap.dnsm.header.HeaderOpcode.QUERY;
import static ch.dnsmap.dnsm.header.HeaderOpcode.STATUS;
import static ch.dnsmap.dnsm.header.HeaderRcode.FORMAT_ERROR;
import static ch.dnsmap.dnsm.header.HeaderRcode.NAME_ERROR;
import static ch.dnsmap.dnsm.header.HeaderRcode.NOT_IMPLEMENTED;
import static ch.dnsmap.dnsm.header.HeaderRcode.NO_ERROR;
import static ch.dnsmap.dnsm.header.HeaderRcode.REFUSED;
import static ch.dnsmap.dnsm.header.HeaderRcode.SERVER_FAILURE;

import ch.dnsmap.dnsm.header.HeaderBitFlags;
import ch.dnsmap.dnsm.header.HeaderFlags;
import ch.dnsmap.dnsm.header.HeaderOpcode;
import ch.dnsmap.dnsm.header.HeaderRcode;
import ch.dnsmap.dnsm.wire.bytes.ReadableByte;
import ch.dnsmap.dnsm.wire.bytes.WriteableByte;
import java.util.HashSet;
import java.util.Set;

public final class HeaderFlagsParser
    implements WireWritable<HeaderFlags>, WireTypeReadable<HeaderFlags> {

  private static final int OPCODE_BIT_MASK = 0b0111_1000_0000_0000;
  private static final int RCODE_BIT_MASK = 0b0000_0000_0000_1111;
  private static final int QR_FLAG_BIT_MASK = 0b1000_0000_0000_0000;
  private static final int AA_FLAG_BIT_MASK = 0b0000_0100_0000_0000;
  private static final int TC_FLAG_BIT_MASK = 0b0000_0010_0000_0000;
  private static final int RD_FLAG_BIT_MASK = 0b0000_0001_0000_0000;
  private static final int RA_FLAG_BIT_MASK = 0b0000_0000_1000_0000;

  @Override
  public HeaderFlags fromWire(ReadableByte wireData, int length) {
    int rawFlags = wireData.readUInt16();

    HeaderOpcode opcode = opcodeFromInt(rawFlags);
    HeaderRcode rCode = rcodeFromInt(rawFlags);
    Set<HeaderBitFlags> flags = flagsFromInt(rawFlags);

    return new HeaderFlags(opcode, rCode, flags.toArray(new HeaderBitFlags[0]));
  }

  @Override
  public int toWire(WriteableByte wireData, HeaderFlags data) {
    return wireData.writeUInt16(flagsToInt(data));
  }

  @Override
  public int bytesToWrite(HeaderFlags data) {
    return 2;
  }

  private static HeaderOpcode opcodeFromInt(int rawFlags) {
    int opcodeValue = readOpcodeValue(rawFlags);
    return switch (opcodeValue) {
      case 0 -> QUERY;
      case 1 -> IQUERY;
      case 2 -> STATUS;
      default -> throw new IllegalArgumentException("illegal opcode: " + opcodeValue);
    };
  }

  private static HeaderRcode rcodeFromInt(int rawFlags) {
    int rCodeValue = readRCodeValue(rawFlags);
    return switch (rCodeValue) {
      case 0 -> NO_ERROR;
      case 1 -> FORMAT_ERROR;
      case 2 -> SERVER_FAILURE;
      case 3 -> NAME_ERROR;
      case 4 -> NOT_IMPLEMENTED;
      case 5 -> REFUSED;
      default -> throw new IllegalArgumentException("illegal return code: " + rCodeValue);
    };
  }

  private static Set<HeaderBitFlags> flagsFromInt(int rawFlags) {
    Set<HeaderBitFlags> bitFlags = new HashSet<>();

    if (isQueryBitSet(rawFlags)) {
      // if the query bit is set the DNS message is a response message
      bitFlags.add(QR);
    }

    if (isAuthoritativeAnswerBitSet(rawFlags)) {
      bitFlags.add(AA);
    }

    if (isTruncationBitSet(rawFlags)) {
      bitFlags.add(TC);
    }

    if (isRecursionDesiredBitSet(rawFlags)) {
      bitFlags.add(RD);
    }

    if (isRecursionAvailableBitSet(rawFlags)) {
      bitFlags.add(RA);
    }

    return bitFlags;
  }

  private static int flagsToInt(HeaderFlags flags) {
    int rawFlags = 0;
    rawFlags = insertOpcodeValue(rawFlags, flags.getOpcode().ordinal());
    rawFlags = insertRCodeValue(rawFlags, flags.getRcode().ordinal());

    rawFlags = setResponseBit(rawFlags);
    for (HeaderBitFlags flag : flags.getFlags()) {
      switch (flag) {
        case QR -> rawFlags = setResponseBit(rawFlags);
        case AA -> rawFlags = setAuthoritativeAnswerBit(rawFlags);
        case TC -> rawFlags = setTruncationBit(rawFlags);
        case RD -> rawFlags = setRecursionDesiredBit(rawFlags);
        case RA -> rawFlags = setRecursionAvailableBit(rawFlags);
        default -> throw new IllegalArgumentException("illegal header flag: " + flag);
      }
    }
    return rawFlags;
  }

  private static int readOpcodeValue(int rawFlags) {
    return (rawFlags & OPCODE_BIT_MASK) >>> 11;
  }

  private static int insertOpcodeValue(int rawFlags, int opcode) {
    return rawFlags | (opcode << 11);
  }

  private static int readRCodeValue(int rawFlags) {
    return rawFlags & RCODE_BIT_MASK;
  }

  private static int insertRCodeValue(int rawFlags, int opcode) {
    return rawFlags | opcode;
  }

  private static boolean isQueryBitSet(int rawFlags) {
    return (rawFlags & QR_FLAG_BIT_MASK) > 0;
  }

  private static int setResponseBit(int rawFlags) {
    return rawFlags | QR_FLAG_BIT_MASK;
  }

  private static boolean isAuthoritativeAnswerBitSet(int rawFlags) {
    return (rawFlags & AA_FLAG_BIT_MASK) > 0;
  }

  private static int setAuthoritativeAnswerBit(int rawFlags) {
    return rawFlags | AA_FLAG_BIT_MASK;
  }

  private static boolean isTruncationBitSet(int rawFlags) {
    return (rawFlags & TC_FLAG_BIT_MASK) > 0;
  }

  private static int setTruncationBit(int rawFlags) {
    return rawFlags | TC_FLAG_BIT_MASK;
  }

  private static boolean isRecursionDesiredBitSet(int rawFlags) {
    return (rawFlags & RD_FLAG_BIT_MASK) > 0;
  }

  private static int setRecursionDesiredBit(int rawFlags) {
    return rawFlags | RD_FLAG_BIT_MASK;
  }

  private static boolean isRecursionAvailableBitSet(int rawFlags) {
    return (rawFlags & RA_FLAG_BIT_MASK) > 0;
  }

  private static int setRecursionAvailableBit(int rawFlags) {
    return rawFlags | RA_FLAG_BIT_MASK;
  }
}
