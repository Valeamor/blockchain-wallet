package fun.txy.eos;

import java.util.List;

public class EosBlockNewProducer {
  private String version;
  private List<EosBlockProducer> producers;

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public List<EosBlockProducer> getProducers() {
    return producers;
  }

  public void setProducers(List<EosBlockProducer> producers) {
    this.producers = producers;
  }
}
