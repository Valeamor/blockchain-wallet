package fun.txy.btc;

import fun.txy.utils.Bech32;
import fun.txy.utils.BtcBase58;
import fun.txy.utils.MyByte;
import fun.txy.utils.SHA;

import static fun.txy.btc.BtcScript.createHash160Script;
import static fun.txy.btc.BtcUtils.USING_COMPRESSED_PUBLIC_KEY;
import static fun.txy.btc.BtcUtils.hash160;

public class BtcAddress {

  private static final int CHECKSUM_LEN = 4;

  private final byte[] raw;
  private final Type type;
  private final BtcNet net;
  private String str;


  private BtcAddress(byte[] raw, BtcNet net, Type type) {
    this.raw = raw;
    this.net = net;
    this.type = type;
  }

  public byte[] getRaw() {
    return raw;
  }

  public Type getType() {
    return type;
  }

  public BtcNet getNet() {
    return net;
  }

  public String getStr() {
    if (str == null) {
      str = type.isSegWit() ? getBech32Str() : getBase58Str();
    }
    return str;
  }

  private String getBase58Str() {
    byte[] data = MyByte.builder()
        .copy(type.getValue(net))
        .copy(raw)
        .getData();
    return BtcBase58.encode(
        MyByte.builder().copy(data).copy(SHA._256hashTwice(data), 4).getData());
  }

  private String getBech32Str() {
    return Bech32.segwitToBech32(net.getBech32AddressPrefix(), 0, raw);
  }

  public enum Type {
    P2PKH(0x00, 0x6f), // Pay to Public Key Hash
    P2SH(0x05, 0xc4),  // Pay to Public Key Script

    P2WPKH(0x06, 0x03), // Pay to Witness Public Key Hash
    P2WSH(0x0a, 0x28), // Pay to Witness Public Script Hash
    ;

    private final byte[] values;

    Type(int... values) {
      this.values = new byte[values.length];
      for (int i = 0; i < values.length; i++) {
        this.values[i] = (byte) values[i];
      }
    }

    public boolean isSegWit() {
      switch (this) {
        case P2WPKH:
        case P2WSH:
          return true;
      }
      return false;
    }

    public byte getValue(BtcNet net) {
      return values[net.ordinal()];
    }
  }

  public static BtcAddress fromRaw(byte[] raw, BtcNet net, Type type) {
    return new BtcAddress(raw, net, type);
  }

  public static BtcAddress fromPrivateKey(BtcPrivateKey pk, BtcNet net, Type type) {
    return new BtcAddress(getRawFromPrivateKey(pk, type), net, type);
  }

  private static byte[] getRawFromPrivateKey(BtcPrivateKey pk, Type type) {
    switch (type) {
      case P2PKH:
      case P2WPKH:
        return pk.getAddressRaw(USING_COMPRESSED_PUBLIC_KEY);
      case P2SH:
        BtcScript script = createHash160Script(pk.getAddressRaw(USING_COMPRESSED_PUBLIC_KEY), BtcScript.Type.P2WPKH);
        return hash160(script.getRaw());
      case P2WSH:
        break;
    }
    throw new IllegalArgumentException("getRawFromPrivateKey unsupported address type: " + type);
  }

  public static BtcAddress convert(String addressStr) {
    try {
      return convertBech32Address(addressStr);
    } catch (Exception ignore) {
    }
    return convertBase58Address(addressStr);
  }

  private static BtcAddress convertBech32Address(String addressStr) {
    try {
      Object[] objects = Bech32.bech32ToSegwit(addressStr);
      byte[] raw = (byte[]) objects[2];
      if (raw.length != 20) {
        throw new IllegalArgumentException("raw data length must be 20");
      }
      BtcAddress address = new BtcAddress(raw, BtcNet.fromBech32AddressPrefix((String) objects[0]), Type.P2WPKH);
      address.str = addressStr;
      return address;
    } catch (Exception e) {
      throw new IllegalArgumentException("\"" + addressStr + "\" " + e.getMessage());
    }
  }

  private static BtcAddress convertBase58Address(String addressStr) {
    byte[] addressDecode;
    // 检查是否Base58编码
    try {
      addressDecode = BtcBase58.decode(addressStr);
    } catch (Exception e) {
      throw new IllegalArgumentException("\"" + addressStr + "\" is not a base58 input");
    }
    if (addressDecode.length != 25) {
      throw new IllegalArgumentException("\"" + addressStr + "\" raw data length must be 25");
    }
    NetAndType netAndType = explain(addressDecode[0]);
    // checksum
    byte[] checksum = MyByte.copyBytes(SHA._256hashTwice(MyByte.copyBytes(addressDecode, 21)), CHECKSUM_LEN);
    for (int i = 0; i < CHECKSUM_LEN; i++) {
      if (addressDecode[21 + i] != checksum[i]) {
        throw new IllegalArgumentException("\"" + addressStr + "\" checksum is incorrect");
      }
    }
    BtcAddress address = new BtcAddress(MyByte.copyBytes(addressDecode, 1, 20), netAndType.net, netAndType.type);
    address.str = addressStr;
    return address;
  }

  private static NetAndType explain(byte first) {
    for (BtcNet net : BtcNet.values()) {
      for (Type type : Type.values()) {
        if (type.getValue(net) == first) {
          NetAndType r = new NetAndType();
          r.net = net;
          r.type = type;
          return r;
        }
      }
    }
    throw new RuntimeException("unsupported address version: " + first);
  }
}

class NetAndType {
  BtcNet net;
  BtcAddress.Type type;
}
