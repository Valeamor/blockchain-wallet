package fun.txy.eos;

import java.util.List;

public class EosBlockTrx {
  private String compression;
  private String id;
  private String packedContextFreeData;
  private String packedTrx;
  private List<String> signatures;
  private EosBlockTransactionItem transaction;

  public String getCompression() {
    return compression;
  }

  public void setCompression(String compression) {
    this.compression = compression;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getPackedContextFreeData() {
    return packedContextFreeData;
  }

  public void setPackedContextFreeData(String packedContextFreeData) {
    this.packedContextFreeData = packedContextFreeData;
  }

  public String getPackedTrx() {
    return packedTrx;
  }

  public void setPackedTrx(String packedTrx) {
    this.packedTrx = packedTrx;
  }

  public List<String> getSignatures() {
    return signatures;
  }

  public void setSignatures(List<String> signatures) {
    this.signatures = signatures;
  }

  public EosBlockTransactionItem getTransaction() {
    return transaction;
  }

  public void setTransaction(EosBlockTransactionItem transaction) {
    this.transaction = transaction;
  }
}
