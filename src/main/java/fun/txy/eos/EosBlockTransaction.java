package fun.txy.eos;


public class EosBlockTransaction {
  private Long cpuUsageUs;
  private Long netUsageWords;
  private String status;
  private EosBlockTrx trx;

  public Long getCpuUsageUs() {
    return cpuUsageUs;
  }

  public void setCpuUsageUs(Long cpuUsageUs) {
    this.cpuUsageUs = cpuUsageUs;
  }

  public Long getNetUsageWords() {
    return netUsageWords;
  }

  public void setNetUsageWords(Long netUsageWords) {
    this.netUsageWords = netUsageWords;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public EosBlockTrx getTrx() {
    return trx;
  }

  public void setTrx(EosBlockTrx trx) {
    this.trx = trx;
  }
}
