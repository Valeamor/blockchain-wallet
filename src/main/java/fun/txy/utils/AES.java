package fun.txy.utils;



import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {
  private static final Charset CHARSET_UTF_8 = StandardCharsets.UTF_8;

  private static final Set<String> allowMode;
  private static final Set<String> allowPadding;

  static {
    allowMode = new HashSet<>();
    allowMode.add("CFB");
    allowMode.add("CTR");
    allowMode.add("OFB");
    allowMode.add("PCBC");
    allowMode.add("ECB");
    allowPadding = new HashSet<>();
    allowPadding.add("NoPadding");
    allowPadding.add("PKCS1Padding");
    allowPadding.add("PKCS5Padding");
  }

  public static boolean isAllowMode(String mode) {
    String[] m = mode.split("/");
    return m.length == 2 && allowMode.contains(m[0]) && allowPadding.contains(m[1]);
  }

  public static class KEY {
    private byte[] key;
    private byte[] iv;

    public byte[] getKey() {
      return key;
    }

    public byte[] getIv() {
      return iv;
    }

    KEY(byte[] data) {
      this(data, 0);
    }

    KEY(byte[] data, int l) {
      key = new byte[16];
      iv = new byte[16];
      System.arraycopy(data, l, key, 0, 16);
      System.arraycopy(data, l + 16, iv, 0, 16);
    }
  }

  private static class AESCipher {
    private Cipher cipher;

    AESCipher(int mode, byte[] key, byte[] iv, String type) {
      try {
        cipher = Cipher.getInstance("AES/" + type);
        cipher.init(mode, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    synchronized byte[] doFinal(byte[] content) {
      try {
        return cipher.doFinal(content);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static Encoder createEncoder(byte[] key, byte[] iv, String type) {
    return new Encoder(key, iv, type);
  }

  public static Decoder createDecoder(byte[] key, byte[] iv, String type) {
    return new Decoder(key, iv, type);
  }

  public static class Encoder extends AESCipher {
    private Encoder(byte[] key, byte[] iv, String type) {
      super(Cipher.ENCRYPT_MODE, key, iv, type);
    }

    public byte[] encrypt(byte[] content) {
      return doFinal(content);
    }

    public byte[] encrypt(String content) {
      return encrypt(content.getBytes(CHARSET_UTF_8));
    }
  }


  public static class Decoder extends AESCipher {
    private Decoder(byte[] key, byte[] iv, String type) {
      super(Cipher.DECRYPT_MODE, key, iv, type);
    }

    public byte[] decrypt(byte[] content) {
      return doFinal(content);
    }

    public String decryptToString(byte[] content) {
      return new String(decrypt(content), CHARSET_UTF_8);
    }
  }
}
