package fun.txy.btc;


public class BtcOutPointEx extends BtcOutPoint {

  private BtcPrivateKey privateKey;
  private long value;
  private BtcScript script;

  private BtcOutPointEx(byte[] prevHash, long index) {
    super(prevHash, index);
  }

  public BtcOutPointEx() {
    super();
  }

  public static BtcOutPointEx build(BtcTransaction tx, int index, BtcPrivateKey pk) {
    BtcOutPointEx o = new BtcOutPointEx(tx.hash(), index);
    o.privateKey = pk;
    BtcTransactionOutput output = tx.getTransactionOutputs().get(index);
    o.value = output.getValue();
    o.script = output.getScript();
    return o;
  }

  public long getValue() {
    return value;
  }

  public BtcScript getScript() {
    return script;
  }

  public BtcPrivateKey getPrivateKey() {
    return privateKey;
  }
}
