package fun.txy.btc;


import fun.txy.utils.MyByte;

public class BtcScriptCoinbase extends BtcScript {

  static BtcScript fromRaw(byte[] raw, int offset, int length) {
    BtcScriptCoinbase script = new BtcScriptCoinbase();
    script.raw = MyByte.copyBytes(raw, offset, length);
    script.type = Type.COIN_BASE;
    return script;
  }

  @Override
  byte[] createRaw() {
    return raw;
  }
}