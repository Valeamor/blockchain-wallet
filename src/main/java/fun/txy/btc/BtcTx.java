package fun.txy.btc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BtcTx {
  private String txid;
  private String hash;
  private Long version;
  private Long size;
  private Long vsize;
  private Long weight;
  private Long locktime;
  private List<Vin> vin;
  private List<Vout> vout;
  private String hex;
  private String blockhash;
  private Long confirmations;
  private Long time;
  private Long blocktime;

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Vin {
    private String txid;
    private Long vout;
    private ScriptSig scriptSig;
    private Long sequence;
    private List<String> txinwitness;
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class ScriptSig {
    private String asm;
    private String hex;
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Vout {
    private BigDecimal value;
    private Long n;
    private ScriptPubKey scriptPubKey;
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class ScriptPubKey {
    private String asm;
    private String hex;
    private Integer reqSigs;
    private String type;
    private List<String> addresses;
  }
}
