package fun.txy.achain;

import fun.txy.achain.ACTAddress.Type;
import fun.txy.utils.*;

public class ActPrivateKey extends EccPrivateKey {

  private String keyStr;
  private ACTAddress actAddress;

  public static ActPrivateKey fromString(String keyStr) {
    byte[] encoded = Base58.decode(keyStr);
    if (!check(encoded)) {
      throw new RuntimeException("ACT私钥格式错误");
    }
    encoded = MyByte.copyBytes(encoded, 1, 32);
    ActPrivateKey pk = new ActPrivateKey(encoded);
    pk.keyStr = keyStr;
    return pk;
  }

  public ActPrivateKey(byte[] encoded) {
    super(encoded);
  }

  public ActPrivateKey() {
    super();
  }

  private static boolean check(byte[] wifBytes) {
    if (wifBytes.length != 37) {
      return false;
    }
    byte[] checksum = SHA._256hash(MyByte.copyBytes(wifBytes, 33));
    return checksum(wifBytes, checksum) ||
           checksum(wifBytes, SHA._256hash(checksum));
  }

  private static boolean checksum(byte[] wifBytes, byte[] checksum) {
    for (int i = 0; i < 4; i++) {
      if (wifBytes[wifBytes.length - 4 + i] != checksum[i]) {
        return false;
      }
    }
    return true;
  }

  public String getKeyStr() {
    if (keyStr == null) {
      byte[] temp = MyByte.builder().copy((byte) 0x80).copy(encoded).getData();
      keyStr = Base58.encode(
          MyByte.builder()
                .copy(temp)
                .copy(SHA._256hash(SHA._256hash(temp)), 4)
                .getData());
    }
    return keyStr;
  }

  public ACTAddress getAddress() {
    if (actAddress == null) {
      actAddress = new ACTAddress(RIPEMD160.hash(SHA._512hash(getPublicKey(true))), Type.ADDRESS);
    }
    return actAddress;
  }
}
