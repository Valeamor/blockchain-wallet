package fun.txy.btc;

import fun.txy.btc.omni.OmniTransferCoins;
import fun.txy.utils.MyByte;
import fun.txy.utils.VarInt;

public class BtcTransactionOutput extends BtcBaseData {
  private long value;
  private VarInt scriptSize;
  private BtcScript script;

  static BtcTransactionOutput fromRaw(byte[] raw, int offset) {
    BtcTransactionOutput out = new BtcTransactionOutput();
    out.value = VarInt.readInt64(raw, offset);
    out.scriptSize = new VarInt(raw, offset += 8);
    int scriptSizeLength = out.scriptSize.getOriginalSizeInBytes();
    offset += scriptSizeLength;
    int scriptLength = (int) out.scriptSize.value;
    out.script = BtcScript.fromRaw(raw, offset, scriptLength);
    offset += scriptLength;
    int l = 8 + scriptSizeLength + scriptLength;
    out.raw = MyByte.copyBytes(raw, offset - l, l);
    return out;
  }

  static BtcTransactionOutput convert(TransferTo to) {
    BtcTransactionOutput o = new BtcTransactionOutput();
    o.value = to.getValue();
    o.script = BtcScript.createScriptPubKey(to.getAddress());
    o.scriptSize = new VarInt(o.script.getRaw().length);
    return o;
  }

  static BtcTransactionOutput convert(OmniTransferCoins transfer) {
    BtcTransactionOutput o = new BtcTransactionOutput();
    o.value = 0;
    o.script = BtcScript.nullData(transfer.toRaw());
    o.scriptSize = new VarInt(o.script.getRaw().length);
    return o;
  }

  public BtcScript getScript() {
    return script;
  }

  public long getValue() {
    return value;
  }

  @Override
  byte[] createRaw() {
    return raw = MyByte
        .builder()
        .copy(value, 8)
        .copy(scriptSize.encode())
        .copy(script.getRaw())
        .getData();
  }
}
