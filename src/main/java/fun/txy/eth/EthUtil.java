package fun.txy.eth;

import fun.txy.utils.MyByte;
import org.bouncycastle.jcajce.provider.digest.Keccak;

import java.util.regex.Pattern;

public class EthUtil {
  private static final Pattern addressPattern = Pattern.compile("^0x[0-9a-f]{40}$");

  public static boolean isValidateAddress(String address) {
    return address != null
        && addressPattern.matcher(address.toLowerCase()).matches()
        && _isValidateAddress(address);
  }

  private static boolean _isValidateAddress(String address) {
    if (address.toLowerCase().equals(address)) {
      return true;
    }
    return toCheckSumAddress(address).equals(address);
  }

  public static byte[] getAddressBytes(String address) {
    address = checkAndGetRawAddressStr(address);
    return MyByte.fromHex(address);
  }

  public static String toCheckSumAddress(String address) {
    address = checkAndGetRawAddressStr(address);
    String hash = MyByte.toHex(sha3(address.toLowerCase().getBytes()));
    StringBuilder sb = new StringBuilder("0x");
    for (int i = 0; i < 40; i++) {
      int intValue = Character.digit(hash.charAt(i), 16);
      String s = address.substring(i, i + 1);
      sb.append(intValue < 8 ? s.toLowerCase() : s.toUpperCase());
    }
    return sb.toString();
  }

  private static String checkAndGetRawAddressStr(String address) {
    address = address.startsWith("0x") ? address.substring(2) : address;
    if (!address.matches("[a-fA-f0-9]{40}")) {
      throw new IllegalArgumentException();
    }
    return address;
  }

  public static byte[] sha3(byte[] source) {
    return new Keccak.Digest256().digest(source);
  }

  public static String trxHashFromRaw(String raw) {
    return "0x" + MyByte.toHex(sha3(MyByte.fromHex(raw.replace("0x", ""))));
  }
}
