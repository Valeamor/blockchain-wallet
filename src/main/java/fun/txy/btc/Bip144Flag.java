package fun.txy.btc;

public class Bip144Flag extends BtcBaseData {

  private byte flag;

  Bip144Flag(byte flag) {
    this.flag = flag;
  }

  @Override
  byte[] createRaw() {
    return raw = new byte[]{0, flag};
  }
}
