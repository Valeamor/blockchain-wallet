package fun.txy.eos;

import java.time.LocalDateTime;
import java.util.List;

public class EosBlock {
  private String actionMroot;
  private Long blockNum;
  private Long confirmed;
  private String id;
  private String previous;
  private String producer;
  private String producerSignature;
  private Long refBlockPrefix;
  private Long scheduleVersion;
  private LocalDateTime timestamp;
  private String transactionMroot;
  private String actionMerkleRoot;
  private String blockMerkleRoot;
  private EosBlockNewProducer newProducers;
  private List<EosBlockTransaction> transactions;
  private List<String> headerExtensions;
  private List<String> blockExtensions;

  public String getActionMroot() {
    return actionMroot;
  }

  public void setActionMroot(String actionMroot) {
    this.actionMroot = actionMroot;
  }

  public Long getBlockNum() {
    return blockNum;
  }

  public void setBlockNum(Long blockNum) {
    this.blockNum = blockNum;
  }

  public Long getConfirmed() {
    return confirmed;
  }

  public void setConfirmed(Long confirmed) {
    this.confirmed = confirmed;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getPrevious() {
    return previous;
  }

  public void setPrevious(String previous) {
    this.previous = previous;
  }

  public String getProducer() {
    return producer;
  }

  public void setProducer(String producer) {
    this.producer = producer;
  }

  public String getProducerSignature() {
    return producerSignature;
  }

  public void setProducerSignature(String producerSignature) {
    this.producerSignature = producerSignature;
  }

  public Long getRefBlockPrefix() {
    return refBlockPrefix;
  }

  public void setRefBlockPrefix(Long refBlockPrefix) {
    this.refBlockPrefix = refBlockPrefix;
  }

  public Long getScheduleVersion() {
    return scheduleVersion;
  }

  public void setScheduleVersion(Long scheduleVersion) {
    this.scheduleVersion = scheduleVersion;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public String getTransactionMroot() {
    return transactionMroot;
  }

  public void setTransactionMroot(String transactionMroot) {
    this.transactionMroot = transactionMroot;
  }

  public String getActionMerkleRoot() {
    return actionMerkleRoot;
  }

  public void setActionMerkleRoot(String actionMerkleRoot) {
    this.actionMerkleRoot = actionMerkleRoot;
  }

  public String getBlockMerkleRoot() {
    return blockMerkleRoot;
  }

  public void setBlockMerkleRoot(String blockMerkleRoot) {
    this.blockMerkleRoot = blockMerkleRoot;
  }

  public EosBlockNewProducer getNewProducers() {
    return newProducers;
  }

  public void setNewProducers(EosBlockNewProducer newProducers) {
    this.newProducers = newProducers;
  }

  public List<EosBlockTransaction> getTransactions() {
    return transactions;
  }

  public void setTransactions(List<EosBlockTransaction> transactions) {
    this.transactions = transactions;
  }

  public List<String> getHeaderExtensions() {
    return headerExtensions;
  }

  public void setHeaderExtensions(List<String> headerExtensions) {
    this.headerExtensions = headerExtensions;
  }

  public List<String> getBlockExtensions() {
    return blockExtensions;
  }

  public void setBlockExtensions(List<String> blockExtensions) {
    this.blockExtensions = blockExtensions;
  }
}
