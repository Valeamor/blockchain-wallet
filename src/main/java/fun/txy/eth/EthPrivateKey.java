package fun.txy.eth;


import static fun.txy.eth.EthUtil.sha3;
import static fun.txy.eth.EthUtil.toCheckSumAddress;

import fun.txy.utils.EccPrivateKey;
import fun.txy.utils.MyByte;

public class EthPrivateKey extends EccPrivateKey {
  private String addressStr;
  private byte[] addressBytes;

  public EthPrivateKey(byte[] key) {
    super(key);
  }

  public EthPrivateKey() {
    super();
  }

  public String getAddressStr() {
    if (addressStr == null) {
      addressStr = toCheckSumAddress(MyByte.toHex(getAddressBytes()));
    }
    return addressStr;
  }

  public byte[] getAddressBytes() {
    if (addressBytes == null) {
      byte[] pubKey = getPublicKey(false);
      byte[] hashBytes = sha3(MyByte.copyBytes(pubKey, 1, pubKey.length - 1));
      addressBytes = MyByte.copyBytes(hashBytes, hashBytes.length - 20, 20);
    }
    return addressBytes;
  }
}