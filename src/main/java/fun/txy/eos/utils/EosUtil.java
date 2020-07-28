package fun.txy.eos.utils;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class EosUtil {
  public static byte[] packName(String name) {
    StringBuilder bits = new StringBuilder(64);
    for (int i = 0; i <= 12; i++) {
      int c = i < name.length() ? charIdx(name.charAt(i)) : 0;
      int bitLen = i < 12 ? 5 : 4;
      String _b = Integer.toBinaryString(c);
      for (int j = 0; j < bitLen - _b.length(); j++) {
        bits.append('0');
      }
      bits.append(_b);
    }
    return ByteBuffer
        .allocate(Long.BYTES)
        .order(ByteOrder.LITTLE_ENDIAN)
        .putLong(new BigInteger(bits.toString(), 2).longValue())
        .array();
  }

  private static int charIdx(char c) {
    if (c >= 'a' && c <= 'z') {
      return c - '[';
    }
    if (c >= '1' && c <= '5') {
      return c - '0';
    }
    return 0;
  }
}
