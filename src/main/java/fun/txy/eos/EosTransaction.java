package fun.txy.eos;

import com.alibaba.fastjson.JSONObject;
import fun.txy.utils.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EosTransaction {
  private LocalDateTime expiration;
  private Long refBlockNum;
  private Long refBlockPrefix;
  private Integer maxNetUsageWords;
  private Integer maxCpuUsageMs;
  private Integer delaySec;
  private List<EosAction> contextFreeActions = new ArrayList<>();
  private List<EosAction> actions = new ArrayList<>();
  private List<String> transactionExtensions = new ArrayList<>();
  private List<String> contextFreeData = new ArrayList<>();
  private List<String> signatures;

  public static String create(
      EosPrivateKey pk,
      String from,
      String to,
      BigDecimal amount,
      Integer precision,
      String symbol,
      String memo,
      Long expireSec,
      EosChainInfo chainInfo,
      EosBlock lastIrreversibleBlock) {
    EosTransaction t = new EosTransaction();
    t.setExpiration(chainInfo.getHeadBlockTime().plusSeconds(expireSec));
    t.setRefBlockNum(lastIrreversibleBlock.getBlockNum());
    t.setRefBlockPrefix(lastIrreversibleBlock.getRefBlockPrefix());
    t.setMaxNetUsageWords(0);
    t.setMaxCpuUsageMs(0);
    t.setDelaySec(0);
    EosTransferQuantity quantity = EosTransferQuantity.fromStr(amount, precision, symbol);
//     test create 2 actions
//    t.setActions(Arrays.asList(EosAction.transfer(from, to, quantity, memo), EosAction.transfer(from, to, quantity, memo)));
    t.setActions(Collections.singletonList(EosAction.transfer(from, to, quantity, memo)));
    JSONObject data = new JSONObject();
    data.put("compression", "none");
    data.put("transaction", t);
    data.put("signatures", Collections.singletonList(t.sign(pk, chainInfo.getChainId())));
    return StringUtil.jsonKeyCamelCase2SnakeCase(data.toJSONString());
  }

  private void setActions(List<EosAction> actions) {
    this.actions = actions;
  }

  private static final byte[] K1 = "K1".getBytes();

  public String sign(EccPrivateKey pk, String chainId) {
    MyByte.BuildList b = MyByte.builder();
    b.copy(MyByte.fromHex(chainId))
     .copy(expiration.toEpochSecond(ZoneOffset.ofHours(0)), 4)
     .copy(refBlockNum, 2)
     .copy(refBlockPrefix, 4)
     .copySize(maxNetUsageWords)
     .copy(maxCpuUsageMs, 1)
     .copySize(delaySec)
     .copySize(contextFreeActions.size()) // always 0
     .copySize(actions.size());
    actions.forEach(a -> a.packInto(b));
    b.copy(0, 33);
//    return KeyUtil.signHash(pk.getD(), b.getData());
    byte[] sign = ECC.eosSign(pk, SHA._256hash(b.getData()));
    sign = MyByte.builder().copy(sign).copy(
        RIPEMD160.hash(MyByte.builder().copy(sign).copy(K1).getData()), 4).getData();
    return "SIG_K1_" + Base58.encode(sign);
  }

  public void setExpiration(LocalDateTime expiration) {
    this.expiration = expiration;
  }

  public void setRefBlockNum(Long refBlockNum) {
    this.refBlockNum = refBlockNum;
  }

  public void setRefBlockPrefix(Long refBlockPrefix) {
    this.refBlockPrefix = refBlockPrefix;
  }

  public void setMaxNetUsageWords(Integer maxNetUsageWords) {
    this.maxNetUsageWords = maxNetUsageWords;
  }

  public void setMaxCpuUsageMs(Integer maxCpuUsageMs) {
    this.maxCpuUsageMs = maxCpuUsageMs;
  }

  public void setDelaySec(Integer delaySec) {
    this.delaySec = delaySec;
  }
}