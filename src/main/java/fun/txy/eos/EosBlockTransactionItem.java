package fun.txy.eos;

import java.util.List;

public class EosBlockTransactionItem {
  private Long delaySec;
  private String expiration;
  private Long maxCpuUsageMs;
  private Long maxNetUsageWords;
  private Long refBlockNum;
  private Long refBlockPrefix;
  private List<EosAction> actions;
  private List<EosAction> contextFreeActions;

  public Long getDelaySec() {
    return delaySec;
  }

  public void setDelaySec(Long delaySec) {
    this.delaySec = delaySec;
  }

  public String getExpiration() {
    return expiration;
  }

  public void setExpiration(String expiration) {
    this.expiration = expiration;
  }

  public Long getMaxCpuUsageMs() {
    return maxCpuUsageMs;
  }

  public void setMaxCpuUsageMs(Long maxCpuUsageMs) {
    this.maxCpuUsageMs = maxCpuUsageMs;
  }

  public Long getMaxNetUsageWords() {
    return maxNetUsageWords;
  }

  public void setMaxNetUsageWords(Long maxNetUsageWords) {
    this.maxNetUsageWords = maxNetUsageWords;
  }

  public Long getRefBlockNum() {
    return refBlockNum;
  }

  public void setRefBlockNum(Long refBlockNum) {
    this.refBlockNum = refBlockNum;
  }

  public Long getRefBlockPrefix() {
    return refBlockPrefix;
  }

  public void setRefBlockPrefix(Long refBlockPrefix) {
    this.refBlockPrefix = refBlockPrefix;
  }

  public List<EosAction> getActions() {
    return actions;
  }

  public void setActions(List<EosAction> actions) {
    this.actions = actions;
  }

  public List<EosAction> getContextFreeActions() {
    return contextFreeActions;
  }

  public void setContextFreeActions(List<EosAction> contextFreeActions) {
    this.contextFreeActions = contextFreeActions;
  }
}
