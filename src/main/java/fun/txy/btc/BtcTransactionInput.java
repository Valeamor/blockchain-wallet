package fun.txy.btc;

import fun.txy.utils.MyByte;
import fun.txy.utils.VarInt;

public class BtcTransactionInput extends BtcBaseData {
  private BtcOutPoint outPoint;
  private VarInt scriptSize;
  private BtcScript script;
  private long sequence;
  private BtcWitness witness;

  static BtcTransactionInput fromRaw(byte[] raw, int offset) {
    BtcTransactionInput in = new BtcTransactionInput();
    in.outPoint = BtcOutPoint.fromRaw(raw, offset);
    in.scriptSize = new VarInt(raw, offset += BtcOutPoint.LENGTH);
    int scriptSizeLength = in.scriptSize.getOriginalSizeInBytes();
    offset += scriptSizeLength;
    int scriptLength = (int) in.scriptSize.value;
    in.script = in.outPoint.isCoinbaseInput()
        ? BtcScriptCoinbase.fromRaw(raw, offset, scriptLength)
        : BtcScript.fromRaw(raw, offset, scriptLength);
    in.sequence = VarInt.readUint32(raw, offset += scriptLength);
    offset += 4;
    int l = BtcOutPoint.LENGTH + scriptSizeLength + scriptLength + 4;
    in.raw = MyByte.copyBytes(raw, offset - l, l);
    return in;
  }

  static BtcTransactionInput convert(BtcOutPointEx outPoint) {
    BtcTransactionInput in = new BtcTransactionInput();
    in.outPoint = outPoint;
    in.scriptSize = new VarInt(0);
    in.sequence = 0xFFFFFFFFL;
    return in;
  }

  void setupScript(BtcScript script) {
    this.script = script;
    this.scriptSize = new VarInt(script.getRaw().length);
    this.raw = null;
  }

  public BtcOutPoint getOutPoint() {
    return outPoint;
  }

  long getSequence() {
    return sequence;
  }

  @Override
  byte[] createRaw() {
    MyByte.BuildList builder = MyByte.builder();
    builder.copy(outPoint.getRaw());
    builder.copy(scriptSize.encode());
    if (script != null) {
      builder.copy(script.getRaw());
    }
    builder.copy(sequence, 4);
    return raw = builder.getData();
  }

  byte[] emptyScriptRaw() {
    return MyByte
        .builder()
        .copy(outPoint.getRaw())
        .copy((byte) 0)
        .copy(sequence, 4)
        .getData();
  }

  BtcWitness getWitness() {
    return witness;
  }

  void setWitness(BtcWitness witness) {
    this.witness = witness;
  }
}
