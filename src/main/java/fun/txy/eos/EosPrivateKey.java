package fun.txy.eos;

import fun.txy.utils.*;

public class EosPrivateKey extends EccPrivateKey {
  private String privateKeyStr;
  private String publicKeyStr;

  public static EosPrivateKey fromKeyStr(String keyStr) {
    byte[] data;
    try {
      data = Base58.decode(keyStr);
    } catch (Exception e) {
      throw new IllegalArgumentException("not a eos base 58 private key");
    }
    if (data.length != 37) {
      throw new IllegalArgumentException("eos private key with checksum must by 37 bytes");
    }
    if (data[0] != (byte) -128) {
      throw new IllegalArgumentException("eos private key first byte must be -128");
    }
    byte[] checksum = SHA._256hashTwice(MyByte.copyBytes(data, 0, 33));
    for (int i = 0; i < 4; i++) {
      if (checksum[i] != data[33 + i]) {
        throw new IllegalArgumentException("eos private key checksum error");
      }
    }
    EosPrivateKey pk = new EosPrivateKey(MyByte.copyBytes(data, 1, 32));
    pk.privateKeyStr = keyStr;
    return pk;
  }

  public EosPrivateKey(byte[] encoded) {
    super(encoded);
  }

  public EosPrivateKey() {
    super();
  }

  public String getPrivateKeyStr() {
    if (privateKeyStr == null) {
      byte[] data = MyByte.builder().copy((byte) -128).copy(encoded).getData();
      byte[] checksum = SHA._256hashTwice(data);
      return Base58.encode(MyByte.builder().copy(data).copy(checksum, 0, 4).getData());
    }
    return privateKeyStr;
  }

  public String getPublicKeyStr() {
    if (publicKeyStr == null) {
      byte[] publicKey = getPublicKey(true);
      byte[] checksum = RIPEMD160.hash(publicKey);
      publicKeyStr = "EOS" + Base58.encode(MyByte.builder().copy(publicKey).copy(checksum, 0, 4).getData());
    }
    return publicKeyStr;
  }
}
