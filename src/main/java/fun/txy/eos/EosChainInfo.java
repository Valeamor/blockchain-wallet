package fun.txy.eos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EosChainInfo {
  private Long headBlockNum;
  private Long forkDbHeadBlockNum;
  private String chainId;
  private LocalDateTime headBlockTime;
  private Long virtualBlockNetLimit;
  private Long virtualBlockCpuLimit;
  private Long lastIrreversibleBlockNum;
  private String serverVersion;
  private Long blockCpuLimit;
  private String headBlockProducer;
  private String forkDbHeadBlockId;
  private String lastIrreversibleBlockId;
  private Long blockNetLimit;
  private String headBlockId;
  private String serverVersionString;
}
