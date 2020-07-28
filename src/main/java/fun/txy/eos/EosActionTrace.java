package fun.txy.eos;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class EosActionTrace {
  private Receipt receipt;
  private EosAction act;
  private Boolean contextFree;
  private Integer elapsed;
  private String console;
  private String trxId;
  private Long blockNum;
  private LocalDateTime blockTime;
  private String producerBlockId;
  private List<AccountRamDelta> accountRamDelta;
//  private ? except
  private List<EosActionTrace> inlineTraces;
  // ...

  @Data
  public static class Receipt {
    private String receiver;
    private String actDigest;
    private Long globalSequence;
//    private ? authSequence;
    private Long recvSequence;
    private Integer codeSequence;
    private Integer abiSequence;
  }

  @Data
  public static class AccountRamDelta {
    private String account;
    private Integer delta;
  }
}
