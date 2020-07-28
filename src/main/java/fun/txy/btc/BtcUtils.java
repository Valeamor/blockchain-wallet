package fun.txy.btc;


import fun.txy.utils.RIPEMD160;
import fun.txy.utils.SHA;

import java.math.BigDecimal;

public abstract class BtcUtils {
  static final boolean USING_COMPRESSED_PUBLIC_KEY = true;
  private static final long FEE_RATE_M = 1_000_000;
  private static final long MIN_FEE_RATE = calculateFeeRate(1L, 4L);
  private static final BigDecimal _100_000_000 = new BigDecimal("100000000");
  private static final BigDecimal _100_000_0 = new BigDecimal("1000000");

  public static long btcToSatoshi(String amount) {
    return btcToSatoshi(new BigDecimal(amount));
  }

  public static long btcToSatoshi(BigDecimal amount) {
    return amount.multiply(_100_000_000).longValueExact();
  }

  public static BigDecimal satoshiToBtc(long satoshis) {
    return new BigDecimal(satoshis).divide(_100_000_000, 10, BigDecimal.ROUND_DOWN);
  }

  public static long cxcToSatoshi(BigDecimal amount) {
    return amount.multiply(_100_000_0).longValueExact();
  }

  public static BigDecimal satoshiToCxc(long satoshis) {
    return new BigDecimal(satoshis).divide(_100_000_0, 10, BigDecimal.ROUND_DOWN);
  }

  public static byte[] hash160(byte[] data) {
    if (data == null) {
      throw new IllegalArgumentException("data is null");
    }
    return RIPEMD160.hash(SHA._256hash(data));
  }

  public static long calculateFeeRate(long fee, long weight) {
    return fee * FEE_RATE_M / weight;
  }

  public static long calculateFee(long feeRate, long weight) {
    return feeRate * weight / FEE_RATE_M;
  }

  public static String feeRateTuHumanValue(long feeRate) {
    return calculateFee(feeRate, 4L) + " Satoshis/vbyte";
  }

  // 最小手续费设置为 1 Satoshis/vbyte
  public static long adjustFeeRate(long feeRate) {
    return Math.max(feeRate, MIN_FEE_RATE);
  }

  public static long getMaxBlockWeight() {
    return 4_000_000;
  }
}
