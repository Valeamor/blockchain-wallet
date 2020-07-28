package fun.txy.btc.rpc;


import fun.txy.btc.omni.OmniBalance;
import fun.txy.btc.omni.OmniTransaction;

public class OmniRPC extends BitcoinRPC {

  private static final String METHOD_GET_TRANSACTION = "omni_gettransaction";
  private static final String METHOD_GET_BALANCE = "omni_getbalance";

  private OmniRPC(String... configs) {
    super(configs);
  }

  public static OmniRPC create(String... configs) {
    return new OmniRPC(configs);
  }

  public OmniTransaction getOmniTransaction(String txHash) {
    return __call(METHOD_GET_TRANSACTION, OmniTransaction.class, txHash);
  }

  public OmniBalance getOmniBalance(String address, Long propertyId) {
    return __call(METHOD_GET_BALANCE, OmniBalance.class, address, propertyId);
  }
}