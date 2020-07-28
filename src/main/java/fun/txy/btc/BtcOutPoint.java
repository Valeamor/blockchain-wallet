package fun.txy.btc;

import fun.txy.utils.MyByte;
import fun.txy.utils.VarInt;

import static fun.txy.utils.MyByte.reverse;
import static fun.txy.utils.MyByte.toHex;

public class BtcOutPoint extends BtcBaseData {

  private byte[] txId; // char[32]
  private long index;

  public BtcOutPoint() {
  }

  static BtcOutPoint fromRaw(byte[] raw, int offset) {
    return new BtcOutPoint(
        MyByte.copyBytes(raw, offset, 32),
        VarInt.readUint32(raw, 32 + offset));
  }

  public boolean isCoinbaseInput() {
    for (byte b : txId) {
      if (b != (byte) 0) {
        return false;
      }
    }
    return index == 0xFFFFFFFFL;
  }

  public String getPrevTransactionHash() {
    return toHex(reverse(txId));
  }

  public long getIndex() {
    return index;
  }

  public BtcOutPoint(byte[] txId, long index) {
    this.txId = txId;
    this.index = index;
  }

  static final int LENGTH = 36;

  @Override
  byte[] createRaw() {
    return raw = MyByte.builder().copy(txId).copy(index, 4).getData();
  }
}
