import fun.txy.achain.ACTAddress;
import fun.txy.achain.ActPrivateKey;
import fun.txy.achain.ActTransaction;
import fun.txy.achain.Contract;
import fun.txy.utils.MyByte;
import org.junit.jupiter.api.Test;

public class ActTest {
  /**
   * 发送eth时 参数：发送量，发送单位（wei,gwei），token
   * 发送合约代币时：参数：发送量，token
   */

  @Test
  public void testAddress() {
    System.out.println(ACTAddress.check(
        "ACTHJUPooFtbYjMAvH22obWohgvxGS9xNUXy0cf99091e73d4b14979214d9f6da27a6",
        ACTAddress.Type.ADDRESS));
  }

  @Test
  public void testTransfer() {
    ActPrivateKey p = new ActPrivateKey(MyByte.fromHex(""));
    ActTransaction t = ActTransaction.normal(
        p,
        MyByte.fromHex("private_key_hex"),
        100000L,
        "ACTQHyUW7aFyqQCzC5JqXYwzqPhtdn2SP6nB",
        null
    );
    System.out.println(t.toJSONString());
    t = ActTransaction.callContractTransferTo(
        p,
        MyByte.fromHex("private_key_hex"),
        Contract.create("JUsz8YBVzDyQzKcrD7RYEVr77st4Uuywv").transferTo("ACTQHyUW7aFyqQCzC5JqXYwzqPhtdn2SP6nB", 1L),
        5000
    );
    System.out.println(t.toJSONString());
  }
}