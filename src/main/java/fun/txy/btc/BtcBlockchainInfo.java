package fun.txy.btc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;

public class BtcBlockchainInfo {
  private String chain;
  private Long blocks;
  private Long headers;
  private String bestblockhash;
  private BigDecimal difficulty;
  private Long mediantime;
  private BigDecimal verificationprogress;
  private Boolean initialblockdownload;
  private String chainwork;
  private Long size_on_disk;
  private Boolean pruned;
  private JSONArray softforks;
  private JSONObject bip9_softforks;
  private String warnings;

  public String getChain() {
    return chain;
  }

  public void setChain(String chain) {
    this.chain = chain;
  }

  public Long getBlocks() {
    return blocks;
  }

  public void setBlocks(Long blocks) {
    this.blocks = blocks;
  }

  public Long getHeaders() {
    return headers;
  }

  public void setHeaders(Long headers) {
    this.headers = headers;
  }

  public String getBestblockhash() {
    return bestblockhash;
  }

  public void setBestblockhash(String bestblockhash) {
    this.bestblockhash = bestblockhash;
  }

  public BigDecimal getDifficulty() {
    return difficulty;
  }

  public void setDifficulty(BigDecimal difficulty) {
    this.difficulty = difficulty;
  }

  public Long getMediantime() {
    return mediantime;
  }

  public void setMediantime(Long mediantime) {
    this.mediantime = mediantime;
  }

  public BigDecimal getVerificationprogress() {
    return verificationprogress;
  }

  public void setVerificationprogress(BigDecimal verificationprogress) {
    this.verificationprogress = verificationprogress;
  }

  public Boolean getInitialblockdownload() {
    return initialblockdownload;
  }

  public void setInitialblockdownload(Boolean initialblockdownload) {
    this.initialblockdownload = initialblockdownload;
  }

  public String getChainwork() {
    return chainwork;
  }

  public void setChainwork(String chainwork) {
    this.chainwork = chainwork;
  }

  public Long getSize_on_disk() {
    return size_on_disk;
  }

  public void setSize_on_disk(Long size_on_disk) {
    this.size_on_disk = size_on_disk;
  }

  public Boolean getPruned() {
    return pruned;
  }

  public void setPruned(Boolean pruned) {
    this.pruned = pruned;
  }

  public JSONArray getSoftforks() {
    return softforks;
  }

  public void setSoftforks(JSONArray softforks) {
    this.softforks = softforks;
  }

  public JSONObject getBip9_softforks() {
    return bip9_softforks;
  }

  public void setBip9_softforks(JSONObject bip9_softforks) {
    this.bip9_softforks = bip9_softforks;
  }

  public String getWarnings() {
    return warnings;
  }

  public void setWarnings(String warnings) {
    this.warnings = warnings;
  }
}
