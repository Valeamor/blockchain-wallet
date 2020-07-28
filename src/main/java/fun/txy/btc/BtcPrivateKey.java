package fun.txy.btc;

import static fun.txy.btc.BtcUtils.hash160;

import fun.txy.utils.EccPrivateKey;

import java.util.HashMap;

public class BtcPrivateKey extends EccPrivateKey {
  private byte[] addressRaw;
  private byte[] addressRawCompressed;
  private HashMap<BtcAddress.Type, BtcAddress> addresses = new HashMap<>();

  public BtcPrivateKey(byte[] encoded) {
    super(encoded);
  }

  public BtcPrivateKey() {
    super();
  }

  public BtcAddress getAddress(BtcNet net, BtcAddress.Type type) {
    return addresses.computeIfAbsent(type, k -> BtcAddress.fromPrivateKey(this, net, type));
  }

  public byte[] getAddressRaw(boolean compressed) {
    byte[] raw = compressed ? addressRawCompressed : addressRaw;
    if (raw == null) {
      raw = hash160(getPublicKey(compressed));
      if (compressed) {
        addressRawCompressed = raw;
      } else {
        addressRaw = raw;
      }
    }
    return raw;
  }
}
