package fun.txy.achain.rpc;

import com.alibaba.fastjson.JSON;
import org.bouncycastle.util.encoders.Base64;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


public enum AchainRPC {
  INFO,
  NETWORK_BROADCAST_TRANSACTION,
  BLOCKCHAIN_GET_BLOCK_COUNT,
  BLOCKCHAIN_GET_BLOCK,
  BLOCKCHAIN_GET_TRANSACTION,
  BLOCKCHAIN_GET_PRETTY_TRANSACTION,
  BLOCKCHAIN_GET_PRETTY_CONTRACT_TRANSACTION,

  //
  ;

  public static final List<WalletConfig> configs = new ArrayList<>();
  public static int defaultConfigIndex = 0;

  private static class WalletConfig {
    private WalletConfig(String... cfg) {
      this.url = cfg[0];
      this.auth = "000000" + Base64.toBase64String((cfg[1]).getBytes());
    }

    private String url;
    private String auth;
  }

  public static void init(String configStr) {
    for (String cfg : configStr.split("\\|\\|")) {
      configs.add(new WalletConfig(cfg.split("\\|")));
    }
  }

  public static Response mCall(String method, String... params) {
    return mCall(defaultConfigIndex, method, params);
  }

  /*
   curl \
   -H "Content-Type: application/json" \
   -H "Authorization: 000000Yml5b25nOjFKYjVzSzlYanNhMTJKTkFi" \
   -X POST -d '{"jsonrpc":"2.0","params":["0b5b13439a6d43ebcbac5abafe2cbc0fdcae58cb"],"id":"2","method":"blockchain_get_transaction"}' \
   http://172.31.17.38:9999/rpc


   curl \
   -H "Content-Type: application/json" \
   -H "Authorization: 000000Yml5b25nUnBjVXNlcjpCaSMlZmFzZDUw" \
   -X POST -d '{"jsonrpc":"2.0","params":["1"],"id":"2","method":"blockchain_get_block"}' \
   http://172.31.22.41:17777/rpc
   */
  public static Response mCall(int ci, String method, String... params) {
    try {
      WalletConfig cfg = configs.get(ci);
      HttpURLConnection connection = (HttpURLConnection) new URL(cfg.url).openConnection();
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setRequestProperty("Authorization", cfg.auth);
      connection.setReadTimeout(60_000);
      connection.setConnectTimeout(3_000);
      connection.setDoOutput(true);
      DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
      wr.writeBytes("{\"jsonrpc\":\"2.0\",\"params\":" + paramsToString(params) +
          ",\"id\":\"" + new Random().nextInt(1024) +
          "\",\"method\":\"" + method.toLowerCase() + "\"}");
      wr.flush();
      wr.close();
      int responseCode = connection.getResponseCode();
      BufferedReader in = new BufferedReader(
          new InputStreamReader(200 == responseCode
              ? connection.getInputStream()
              : connection.getErrorStream()));
      String inputLine;
      StringBuilder response = new StringBuilder();
      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();
      return new Response(responseCode, response.toString());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static String paramsToString(String... params) {
    return "[" + Arrays.stream(params)
        .map(s -> s.startsWith("{") ? s : "\"" + s + "\"")
        .collect(Collectors.joining(",")) + "]";
  }

  public String call(String... params) {
    return call(defaultConfigIndex, params);
  }

  public String call(int ci, String... params) {
    Response response = mCall(ci, name(), params);
    if (response.code == 200) {
      return JSON.parseObject(response.message).getString("result");
    } else {
      throw new RuntimeException(response.toString());
    }
  }

  public static class Response {
    private int code;
    private String message;

    public int getCode() {
      return code;
    }

    public String getMessage() {
      return message;
    }

    Response(int code, String message) {
      this.code = code;
      this.message = message;
    }

    @Override
    public String toString() {
      return "RPC.Response(code:" + code + "; message:" + message + ")";
    }
  }
}
