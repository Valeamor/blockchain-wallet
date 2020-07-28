package fun.txy.eos;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class EosTransactionTrace {
  private String id;
  private Long blockNum;
  private LocalDateTime blockTime;
  private Receipt receipt;
  private Long elapsed;
  private Long netUsage;
  private Boolean scheduled;
  private List<EosActionTrace> actionTraces;

  @Data
  public static class Receipt {
    private String status;
    private Long cpuUsageUs;
    private Long netUsageWords;
  }
}
