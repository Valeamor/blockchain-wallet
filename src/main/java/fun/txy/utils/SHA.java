package fun.txy.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA {
  public static byte[] _512hash(byte[] orig) {
    return _hash(orig, "512");
  }

  public static byte[] _256hash(byte[] orig) {
    return _hash(orig, "256");
  }

  public static byte[] _256hashBE(byte[] orig) {
    return MyByte.reverse(_hash(orig, "256"));
  }

  public static byte[] _256hashTwice(byte[] input) {
    MessageDigest digest = newDigest("256");
    digest.update(input, 0, input.length);
    return digest.digest(digest.digest());
  }

  public static byte[] hmacSha256(byte[] data, byte[] key) {
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(key, "HmacSHA256"));
      return mac.doFinal(data);
    } catch (InvalidKeyException | NoSuchAlgorithmException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  public static byte[] _256hashTwiceBE(byte[] orig) {
    return MyByte.reverse(_256hashTwice(orig));
  }

  private static byte[] _hash(byte[] orig, String b) {
    return newDigest(b).digest(orig);
  }

  private static MessageDigest newDigest(String b) {
    try {
      return MessageDigest.getInstance("SHA-" + b);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e.getMessage(), e);  // Can't happen.
    }
  }
}
