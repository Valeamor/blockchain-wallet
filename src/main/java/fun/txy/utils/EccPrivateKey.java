package fun.txy.utils;


import org.bouncycastle.jce.interfaces.ECPrivateKey;

import java.math.BigInteger;

import static fun.txy.utils.MyByte.to32bytes;

public class EccPrivateKey {
  private static BigInteger MAX_D = new BigInteger("fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364140", 16);

  protected byte[] encoded;
  private BigInteger d;
  private ECPrivateKey ecPrivateKey;
  private byte[] publicKey;
  private byte[] publicKeyCompressed;

  public EccPrivateKey(byte[] encoded) {
    if (encoded.length != 32) {
      throw new IllegalArgumentException("私钥长度必须为32byte: " + encoded.length);
    }
    this.encoded = encoded;
    checkD();
  }

  public EccPrivateKey() {
    d = ((ECPrivateKey) ECC.generate().getPrivate()).getD();
    checkD();
    encoded = to32bytes(d.toByteArray());
  }

  protected void checkD() {
    BigInteger d = getD();
    if (d.compareTo(MAX_D) > 0 || d.compareTo(BigInteger.ONE) < 0) {
      throw new IllegalArgumentException("an error ecc-key: " + d.toString(16));
    }
  }

  public byte[] getEncoded() {
    return encoded;
  }

  public ECPrivateKey getECPrivateKey() {
    if (ecPrivateKey == null) {
      ecPrivateKey = ECC.loadPrivateKey(encoded);
    }
    return ecPrivateKey;
  }

  //
  public byte[] getPublicKey(boolean compressed) {
    byte[] key = compressed ? publicKeyCompressed : publicKey;
    if (key == null) {
      key = ECC.calculatePublicKey(getD(), compressed);
      if (compressed) {
        publicKeyCompressed = key;
      } else {
        publicKey = key;
      }
    }
    return key;
  }

  public BigInteger getD() {
    if (d == null) {
      d = new BigInteger(1, encoded);
    }
    return d;
  }
}
