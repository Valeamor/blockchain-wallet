package fun.txy.btc;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferTo {

  private BtcAddress address;
  private long value;

  public void setValue(String value) {
    this.value = BtcUtils.btcToSatoshi(value);
  }

  public void setValue(BigDecimal value) {
    this.value = BtcUtils.btcToSatoshi(value);
  }

  public void setValue(long value) {
    this.value = value;
  }

  public long getValue() {
    return value;
  }

  public BtcAddress getAddress() {
    return address;
  }
}
