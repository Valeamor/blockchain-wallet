package fun.txy.btc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BtcBlock {
  private String hash;
  private Integer confirmations;
  private Integer strippedsize;
  private Integer size;
  private Integer weight;
  private Integer height;
  private Integer version;
  private String versionHex;
  private String merkleroot;
  private List<String> tx;
  private Integer time;
  private Integer mediantime;
  private Long nonce;
  private String bits;
  private Integer difficulty;
  private String chainwork;
  private String previousblockhash;
  private String nextblockhash;
}
