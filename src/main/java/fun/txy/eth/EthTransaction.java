package fun.txy.eth;

import fun.txy.utils.ECC;
import fun.txy.utils.MyByte;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;

import static fun.txy.eth.EthUtil.getAddressBytes;
import static fun.txy.eth.EthUtil.sha3;
import static org.bouncycastle.util.BigIntegers.asUnsignedByteArray;

public class EthTransaction {
  private static final byte[] emptyBytes = new byte[0];
  private static final BigDecimal bd10 = new BigDecimal("10");
  public static final BigDecimal p18 = bd10.pow(18);

  public static EthTransaction normal(
      EthPrivateKey key,
      String to,
      int chainId,
      BigDecimal value,
      BigInteger gasLimit,
      BigInteger gasPrice,
      Long nonce) {
    return new EthTransaction(
        key, to, chainId, value.multiply(p18).toBigInteger(), gasLimit, gasPrice,
        BigInteger.valueOf(nonce), emptyBytes);
  }

  public static EthTransaction callContract(
      EthPrivateKey key,
      String contractAddress,
      int chainId,
      BigDecimal value,
      BigInteger gasLimit,
      BigInteger gasPrice,
      Long nonce,
      ContractFunction function,
      byte[]... args) {
    if (function.argSize != args.length) {
      throw new IllegalArgumentException("参数个数不匹配");
    }
    MyByte.BuildList builder = MyByte.builder();
    builder.copy(function.functionBytes);
    for (byte[] arg : args) {
      if (arg.length > 32) {
        throw new IllegalArgumentException("单个参数 length 不能超过 32 byte");
      }
      builder.copy(new byte[32 - arg.length]).copy(arg);
    }
    return new EthTransaction(
        key, contractAddress, chainId, value.multiply(p18).toBigInteger(), gasLimit, gasPrice,
        BigInteger.valueOf(nonce), builder.getData());
  }

  public static EthTransaction erc20Transfer(
      EthPrivateKey key,
      String contractAddress,
      int chainId,
      BigInteger gasLimit,
      BigInteger gasPrice,
      Long nonce,
      String toAddress,
      BigDecimal value,
      Integer tokenDecimal) {
    return callContract(
        key, contractAddress, chainId, BigDecimal.ZERO, gasLimit, gasPrice, nonce,
        ContractFunction.ERC20_TRANSFER,
        getAddressBytes(toAddress),
        asUnsignedByteArray(value.multiply(bd10.pow(tokenDecimal)).toBigInteger()));
  }

  private int chainId;
  private final EthPrivateKey key;
  private byte[] nonce;
  private byte[] gasPrice;
  private byte[] gasLimit;
  private byte[] to;
  private byte[] value;
  private byte[] data;
  private String raw;

  private EthTransaction(
      EthPrivateKey key,
      String to,
      int chainId,
      BigInteger value,
      BigInteger gasLimit,
      BigInteger gasPrice,
      BigInteger nonce,
      byte[] data) {
    this.key = key;
    this.to = getAddressBytes(to);
    this.chainId = chainId;
    this.value = asUnsignedByteArray(value);
    this.gasLimit = asUnsignedByteArray(gasLimit);
    this.gasPrice = asUnsignedByteArray(gasPrice);
    this.nonce = asUnsignedByteArray(nonce);
    this.data = data;
  }

  public String getRaw() {
    if (raw == null) {
      byte[] rlpHash = sha3(rlpEncode(
          nonce, gasPrice, gasLimit, to, value, data,
          BigInteger.valueOf(chainId).toByteArray(), emptyBytes, emptyBytes));
      BigInteger[] sign = ECC.ethSignCore(key.getD(), rlpHash);
      BigInteger r = sign[0];
      BigInteger s = ECC.cut(sign[1]);
      int intV = ECC.generateSignV(key.getPublicKey(false), rlpHash, r, s) + 35 + chainId * 2;
      raw = "0x" + MyByte.toHex(
          rlpEncode(
              nonce, gasPrice, gasLimit, to, value, data,
              BigInteger.valueOf(intV).toByteArray(), asUnsignedByteArray(r), asUnsignedByteArray(s)));
    }
    return raw;
  }

  private byte[] rlpEncode(byte[]... values) {
    MyByte.BuildList builder = MyByte.builder();
    for (byte[] value : values) {
      if (value.length == 1 && (value[0] & 0xff) < 0x80) {
        builder.copy(value);
      } else {
        builder.copy(rlpEncodeLength(value.length, 0x80)).copy(value);
      }
    }
    byte[] r = builder.getData();
    return MyByte.builder().copy(rlpEncodeLength(r.length, 0xC0)).copy(r).getData();
  }

  private byte[] rlpEncodeLength(int length, int offset) {
    if (length < 56) {
      return new byte[]{(byte) (length + offset)};
    } else {
      byte[] b = MyByte.trimL(ByteBuffer.allocate(4).putInt(length).array());
      return MyByte.builder().copy((byte) (b.length + offset + 55)).copy(b).getData();
    }
  }

  public enum ContractFunction {
    ERC20_TRANSFER("transfer(address,uint256)"),
    //
    ;
    private final byte[] functionBytes;
    private final int argSize;

    ContractFunction(String function) {
      this.functionBytes = MyByte.copyBytes(sha3(function.getBytes()), 4);
      this.argSize = function.split(",").length;
    }
  }
}
