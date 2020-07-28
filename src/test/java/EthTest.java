
import fun.txy.eth.EthPrivateKey;
import fun.txy.eth.EthTransaction;
import fun.txy.utils.MyByte;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static fun.txy.eth.EthUtil.trxHashFromRaw;


public class EthTest {
  @Test
  public void testGetTrxHash() {
    String trxRaw = "0xf86921847735940082520894b51b580f58c4cd311303e997f45371432c01c47b8609184e72a0008025a0baaff4cb240f992cdd28ce1c81e931376f0d8cee4e0f4cf3f9e338b9d3510eb4a02e12f87b2508a3769ed8d11513ada18066308c9684365597aa9f43a006963bcc";
    String hash = trxHashFromRaw(trxRaw);
    System.out.println(hash);
  }

  @Test
  public void testAddress() {
    EthPrivateKey ethPrivateKey = new EthPrivateKey(
        MyByte.fromHex("private_key_hex"));
    System.out.println(ethPrivateKey.getAddressStr());
  }

  @Test
  public void testEthTransaction() {
    EthPrivateKey ethKeyPair = new EthPrivateKey(
        MyByte.fromHex("private_key_hex"));
    String raw = EthTransaction.normal(
        ethKeyPair,
        "0x820af426eb5f2b815657b25d352fee5246e6d905",
        4,
        new BigDecimal("0.00001"),
        BigInteger.valueOf(21000), // gasLimit
        BigInteger.valueOf(1000000000L), // gasPrice
        0L
    ).getRaw();

    System.out.println(raw);
  }

  @Test
  public void testErc20Transaction() {
    EthPrivateKey ethKeyPair = new EthPrivateKey(
        MyByte.fromHex("private_key_hex"));

    String raw = EthTransaction.erc20Transfer(
        ethKeyPair,
        "0x820af426eb5f2b815657b25d352fee5246e6d905",
        4,
        BigInteger.valueOf(60000), // gasLimit
        BigInteger.valueOf(41000000000L), // gasPrice
        0L,
        "0x8af480a5803b71e3d684030719b796a173e430a2",
        new BigDecimal("0.0001"),
        6
    ).getRaw();

    System.out.println(raw);
  }
}