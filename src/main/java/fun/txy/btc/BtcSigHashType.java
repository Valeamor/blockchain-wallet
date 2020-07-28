package fun.txy.btc;

public enum BtcSigHashType {
  ALL(1),
  NONE(2),
  SINGLE(3),;
  private final byte value;

  BtcSigHashType(int value) {
    this.value = (byte) value;
  }

  public byte getValue() {
    return value;
  }

  public byte getAnyoneCanPayValue() {
    return (byte) (value | 0x80);
  }
}
