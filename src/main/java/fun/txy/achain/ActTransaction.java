package fun.txy.achain;

import fun.txy.achain.ACTAddress.Type;
import fun.txy.utils.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;


public class ActTransaction {
  private long expiration;
  private String alpAccount; // 子地址
  private Asset alpInportAsset; // 子地址资产类型
  private List<Operation> operations;
  private ResultTransactionType resultTrxType;
  private VoteType voteType;
  private List<byte[]> signatures;
  //
  private byte[] bytes;
  private byte[] toSignBytes;
  private ActPrivateKey actPrivateKey;
  private ACTAddress toAddress;
  private byte[] chainId;
  private String jsonStr;
  private byte[] id;
  //
  public static final String ACT_SYMBOL = "ACT";
  public static final String CONTRACT_SYMBOL = "CON";
  public static final Long requiredFees = 1000L; // 转账手续费 0.01 * 100000
  private static final Long _transactionExpiration = 3_600_000L; // 3,600,000ms / 1h
  private static final int _scale = 6;
  private static final BigDecimal _2bd = new BigDecimal("10").pow(_scale - 1);

  public long getExpiration() {
    return expiration;
  }

  public String toJSONString() {
    if (jsonStr == null) {
      jsonStr = JSON.build()
          .add("expiration", new Date(expiration))
          .add("alp_account", alpAccount)
          .add("alp_inport_asset", alpInportAsset.toJSON())
          .add("operations", operations.stream().map(Operation::toJSON).collect(toList()))
          .add("signatures", signatures.stream().map(MyByte::toHex).collect(toList()))
          .get().toJSONString();
    }
    return jsonStr;
  }

  public byte[] toBytes() {
    if (bytes == null) {
      bytes = MyByte.builder()
          .copy(toSign(), toSign().length - chainId.length)
          .copy(signatures)
          .getData();
    }
    return bytes;
  }

  public byte[] getId() {
    if (id == null) {
      id = RIPEMD160.hash(SHA._512hash(toBytes()));
    }
    return id;
  }

  private byte[] toSign() {
    if (toSignBytes == null) {
      toSignBytes = MyByte.builder()
          .copy(expiration / 1000L, 4)
          .copy(0, 1) // reserved optional<uint64_t> wtf?
          .copy(alpAccount)
          .copy(alpInportAsset.toBytes())
          .copy(operations.stream().map(Operation::toBytes).collect(toList()))
          .copy(resultTrxType._byte)
          .copy(0, 20)
          .copy(chainId)
          .getData();
    }
    return toSignBytes;
  }

  public static long bigDecimalToLong(BigDecimal v) {
    return v.multiply(_2bd).longValue();
  }

  /**
   * 普通转账
   */
  public static ActTransaction normal(
      ActPrivateKey actPrivateKey, // 转出者的私钥
      byte[] chainId,
      Long amount,           // 转出数额 * 100000
      String toAddressStr,   // 目标地址
      String remark) {
    return assertTransfer(actPrivateKey, 0, chainId, amount, toAddressStr, remark);
  }

  /**
   * 普通转账
   */
  public static ActTransaction assertTransfer(
      ActPrivateKey actPrivateKey, // 转出者的私钥
      int assertId, // 资产id 0 代表 act
      byte[] chainId,
      Long amount,           // 转出数额 * 100000
      String toAddressStr,   // 目标地址
      String remark) {
    ActTransaction trx = new ActTransaction(actPrivateKey, chainId);
    trx.setAddress(toAddressStr, amount, assertId);
    trx.setTransferOperations(amount, remark);
    trx.sign();
    return trx;
  }

  /**
   * 转账到合约
   */
  public static ActTransaction toContract(
      ActPrivateKey actPrivateKey,
      byte[] chainId,
      ACTAddress contractAddress,
      long amount,
      long maxCallContractCost) {
    ActTransaction trx = new ActTransaction(actPrivateKey, chainId);
    trx.setTransferToContractOperations(contractAddress, amount, maxCallContractCost);
    trx.sign();
    return trx;
  }

  /**
   * 合约调用
   */
  public static ActTransaction callContract(
      ActPrivateKey actPrivateKey,
      byte[] chainId,
      Contract.Call contractCall,
      long maxCallContractCost) {
    ActTransaction trx = new ActTransaction(actPrivateKey, chainId);
    trx.setCallContractOperations(contractCall, new Asset(maxCallContractCost));
    trx.sign();
    return trx;
  }

  /**
   * 合约 transfer_to 调用
   */
  public static ActTransaction callContractTransferTo(
      ActPrivateKey actPrivateKey,
      byte[] chainId,
      Contract.TransferToCall transferToCall,
      long maxCallContractCost) {
    ActTransaction trx = new ActTransaction(actPrivateKey, chainId);
    trx.setAddress(transferToCall.getToAddress(), transferToCall.getAmount());
    trx.setCallContractOperations(transferToCall, new Asset(maxCallContractCost));
    trx.sign();
    return trx;
  }


  private ActTransaction(ActPrivateKey actPrivateKey, byte[] chainId) {
    if (actPrivateKey == null) {
      throw new IllegalArgumentException("actPrivateKey 不能为 null");
    }
    this.chainId = chainId;
    this.operations = new ArrayList<>();
    this.signatures = new ArrayList<>();
    this.actPrivateKey = actPrivateKey;
    this.voteType = VoteType.VOTE_NONE; // 最简方式
    this.expiration = System.currentTimeMillis() + _transactionExpiration;
    this.resultTrxType = ResultTransactionType.ORIGIN_TRANSACTION;
    this.alpAccount = "";
    this.alpInportAsset = new Asset(0L);
  }

  private void sign() {
    this.signatures.add(ECC.signCompact(actPrivateKey, SHA._256hash(toSign())));
  }

  private void checkArguments(long amount, String toAddressStr) {
    if (amount <= 0) {
      throw new IllegalArgumentException("转出数量必须大于0");
    } else if (toAddressStr == null) {
      throw new IllegalArgumentException("转出地址参数未填");
    }
  }

  private void setAddress(String toAddressStr, Long amount) {
    setAddress(toAddressStr, amount, 0);
  }

  private void setAddress(String toAddressStr, Long amount, Integer assertId) {
    this.checkArguments(amount, toAddressStr);
    if (toAddressStr.startsWith(ACT_SYMBOL)) {
      toAddressStr = toAddressStr.substring(3);
    } else {
      throw new IllegalArgumentException("address format error");
    }
    if (toAddressStr.length() >= 60) { //获取子地址
      String sub = toAddressStr.substring(toAddressStr.length() - 32);
      if (!sub.equals("ffffffffffffffffffffffffffffffff")) {
        if (!ACTAddress.checkAlpSubAddress(sub)) {
          throw new IllegalArgumentException("address format error");
        }
        alpAccount = ACT_SYMBOL + toAddressStr;
        alpInportAsset = new Asset(amount, assertId);
      }
      toAddressStr = toAddressStr.substring(0, toAddressStr.length() - 32);
    }
    this.toAddress = new ACTAddress(toAddressStr, Type.ADDRESS);
  }

  private void setTransferToContractOperations(
      ACTAddress contractAddress,
      long amount,
      long maxCallContractCost) {
    operations.add(
        Operation.createTransferToContract(
            actPrivateKey,
            contractAddress,
            new Asset(amount),
            new Asset(maxCallContractCost)
        )
    );
  }

  private void setTransferOperations(long amount, String remark) {
    operations.add(Operation.createWithdraw(actPrivateKey, amount + requiredFees));
    operations.add(Operation.createDeposit(toAddress, amount));
    if (remark != null && remark.length() > 0) {
      operations.add(Operation.createIMessage(remark));
    }
  }

  private void setCallContractOperations(Contract.Call contractCall, Asset costLimit) {
    operations.add(Operation.createCallContract(actPrivateKey, contractCall, costLimit));
  }

  private enum ResultTransactionType {
    ORIGIN_TRANSACTION(0),
    COMPLETE_RESULT_TRANSACTION(1),
    INCOMPLETE_RESULT_TRANSACTION(2),
    ;

    private byte _byte;

    ResultTransactionType(int _byte) {
      this._byte = (byte) _byte;
    }
  }

  public enum VoteType {
    // 不投票
    VOTE_NONE("vote_none"),
    // 投所有人最多108人
    VOTE_ALL("vote_all"),
    // 随机投票，从支持者中随机选取一定的人数进行投票最多不超过36人
    VOTE_RANDOM("vote_radom"),
    // 根据已经选择的投票人进行投票，如果选择的投票人的publish_data中有其他投票策略加入到自己的投票策略中
    VORE_RECOMMENDED("vote_recommended"),
    ;

    private String value;

    VoteType(String value) {
      this.value = value;
    }
  }
}