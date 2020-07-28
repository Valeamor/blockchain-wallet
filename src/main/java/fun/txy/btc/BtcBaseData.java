package fun.txy.btc;

public abstract class BtcBaseData {
  protected byte[] raw;

  abstract byte[] createRaw();

  public byte[] getRaw() {
    if (raw == null) {
      raw = createRaw();
    }
    return raw;
  }
}
