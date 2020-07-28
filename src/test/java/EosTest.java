import fun.txy.eos.EosPrivateKey;
import org.junit.jupiter.api.Test;

public class EosTest {
  @Test
  public void testCreateKey() {
    EosPrivateKey eosPrivateKey = EosPrivateKey.fromKeyStr("5K54gk6i4uBkiVGMGy4wzTzQeFcxkxeiYpGxA5Atko8WCdJwJs2");
    String privateKey = eosPrivateKey.getPrivateKeyStr();
    System.out.println(eosPrivateKey.getPublicKeyStr() + " " + privateKey);
  }

  @Test
  public void testCreateTransaction() {
  }
}
