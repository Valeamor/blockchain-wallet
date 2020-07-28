package fun.txy.eos;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class EosTransferQuantity {
  private Long amount;
  private Byte precision;
  private byte[] symbol;

  public String toPlainString() {
    String as = amount.toString();
    int p = as.length() - precision;
    return as.substring(0, p) + "." + as.substring(p) + " " +
           new String(symbol).replace("\u0000", "");
  }

  static EosTransferQuantity fromStr(BigDecimal amount, int precision, String symbol) {
    EosTransferQuantity q = new EosTransferQuantity();
    q.amount = Long.parseLong(amount.setScale(precision, RoundingMode.DOWN).toPlainString().replace(".", ""));
    q.precision = (byte) precision;
    q.symbol = new byte[7];
    byte[] sb = symbol.getBytes();
    System.arraycopy(sb, 0, q.symbol, 0, sb.length);
    return q;
  }
}
