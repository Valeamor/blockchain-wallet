package fun.txy.btc.rpc;


import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import fun.txy.btc.BtcBlock;
import fun.txy.btc.BtcBlockchainInfo;
import fun.txy.btc.BtcTransaction;
import fun.txy.btc.BtcTx;
import org.bouncycastle.util.encoders.Base64;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;


public class BitcoinRPC {

  private static final String METHOD_GET_BLOCK = "getblock";
  private static final String METHOD_GET_BLOCKCHAIN_INFO = "getblockchaininfo";
  private static final String METHOD_GET_BLOCK_HASH = "getblockhash";
  private static final String METHOD_GET_RAW_TRANSACTION = "getrawtransaction";
  private static final String METHOD_GET_RAW_MEM_POOL = "getrawmempool";
  private static final String METHOD_SEND_RAW_TRANSACTION = "sendrawtransaction";

  private final URL url;
  private final HashMap<String, String> headers = new HashMap<>(1);

  BitcoinRPC(String... configs) {
    try {
      url = new URL(configs[0]);
      headers.put("Authorization", "Basic " + Base64.toBase64String(configs[1].getBytes()));
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  public static BitcoinRPC create(String... configs) {
    return new BitcoinRPC(configs);
  }

  public BtcBlockchainInfo getBlockchainInfo() {
    return __call(METHOD_GET_BLOCKCHAIN_INFO, BtcBlockchainInfo.class);
  }

  public String getBlockHash(Integer blockNumber) {
    return __call(METHOD_GET_BLOCK_HASH, String.class, blockNumber);
  }

  public BtcBlock getBlock(String blockHash) {
    return __call(METHOD_GET_BLOCK, BtcBlock.class, blockHash);
  }

  public String getRawTransaction(String txHash) {
    try {
      return __call(METHOD_GET_RAW_TRANSACTION, String.class, txHash);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  // 读取JSON格式数据
  public BtcTx getBtcTx(String txHash) {
    try {
      return __call(METHOD_GET_RAW_TRANSACTION, BtcTx.class, txHash, 1);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public BtcTransaction getTransaction(String txHash) {
    return BtcTransaction.fromRaw(getRawTransaction(txHash));
  }

  public String sendRawTransaction(String raw) {
    try {
      return __call(METHOD_SEND_RAW_TRANSACTION, String.class, raw);
    } catch (Exception e) {
      if (e.getMessage().equals("transaction already in block chain")) {
        return BtcTransaction.fromRaw(raw).txHash();
      } else {
        throw new RuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  public HashSet<String> getRawMemPool() {
    return __call(METHOD_GET_RAW_MEM_POOL, HashSet.class);
  }


  <T> T __call(String rpcMethod, Class<T> returnType, Object... params) {
    try {
      JsonRpcHttpClient client = new JsonRpcHttpClient(url, headers);
      client.setReadTimeoutMillis(30000);
      client.setConnectionTimeoutMillis(30000);
      return client.invoke(rpcMethod, params, returnType);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }
}