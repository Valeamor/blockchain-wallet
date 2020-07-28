package fun.txy.btc;

import fun.txy.btc.omni.OmniTransferCoins;
import fun.txy.utils.ECC;
import fun.txy.utils.MyByte;
import fun.txy.utils.SHA;
import fun.txy.utils.VarInt;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static fun.txy.btc.BtcOperation.*;
import static fun.txy.btc.BtcUtils.USING_COMPRESSED_PUBLIC_KEY;
import static java.util.stream.Collectors.toList;


/**
 * https://en.bitcoin.it/wiki/Transaction
 */
public class BtcTransaction extends BtcBaseData {
  private long version;
  private Bip144Flag bip144Flag;
  private VarInt inputSize;
  private List<BtcTransactionInput> transactionInputs;
  private VarInt outputSize;
  private List<BtcTransactionOutput> transactionOutputs;
  private long lockTime;

  private byte[] sigWitRaw;

  public int getWeight() {
    return getSigWitRaw().length + getRaw().length * 3;
  }

  public static BtcTransaction omniTransfer(
      BtcNet net,
      List<BtcOutPointEx> outPoints,
      List<TransferTo> transferToList,
      String recycleAddressStr,
      Long currencyIdentifier,
      String numberOfCoinsStr,
      String feeStr
  ) {
    return omniTransfer(
        net, outPoints, transferToList, BtcAddress.convert(recycleAddressStr),
        currencyIdentifier, new BigDecimal(numberOfCoinsStr), BtcUtils.btcToSatoshi(feeStr));
  }

  public static BtcTransaction omniTransfer(
      BtcNet net,
      List<BtcOutPointEx> outPoints,
      List<TransferTo> transferToList,
      BtcAddress recycleAddress,
      Long currencyIdentifier,
      BigDecimal numberOfCoins,
      Long fee
  ) {
    return TransactionBuilder
        .build()
        .setNet(net)
        .setIn(outPoints)
        .setOut(transferToList)
        .setRecycle(recycleAddress)
        .setFee(fee)
        .set(OmniTransferCoins.create(currencyIdentifier, numberOfCoins))
        .finish();
  }

  public static BtcTransaction normalTransfer(
      BtcNet net,
      List<BtcOutPointEx> outPoints,
      List<TransferTo> transferToList,
      String recycleAddressStr,
      String feeStr
  ) {
    return normalTransfer(
        net,
        outPoints,
        transferToList,
        BtcAddress.convert(recycleAddressStr),
        BtcUtils.btcToSatoshi(feeStr));
  }

  public static BtcTransaction normalTransfer(
      BtcNet net,
      List<BtcOutPointEx> outPoints,
      List<TransferTo> transferToList,
      BtcAddress recycleAddress,
      Long fee
  ) {
    return TransactionBuilder
        .build()
        .setNet(net)
        .setIn(outPoints)
        .setOut(transferToList)
        .setRecycle(recycleAddress)
        .setFee(fee)
        .finish();
  }


  void segWit() {
    if (bip144Flag == null) {
      bip144Flag = new Bip144Flag((byte) 0x01);
    }
  }

  public List<BtcTransactionOutput> getTransactionOutputs() {
    return transactionOutputs;
  }

  public List<BtcTransactionInput> getTransactionInputs() {
    return transactionInputs;
  }

  public Long getLockTime() {
    return lockTime;
  }

  public String txHash() {
    return MyByte.toHex(hashBE());
  }

  public byte[] hash() {
    return SHA._256hashTwice(getRaw());
  }

  public byte[] hashBE() {
    return SHA._256hashTwiceBE(getRaw());
  }

  public static BtcTransaction fromRaw(String raw) {
    return fromRaw(MyByte.fromHex(raw));
  }

  private static BtcTransaction fromRaw(byte[] raw) {
    int offset = 0;
    BtcTransaction tx = new BtcTransaction();
    tx.version = VarInt.readUint32(raw, offset);
    offset += 4;
    if (raw[offset] == 0) {
      // https://github.com/bitcoin/bips/blob/master/bip-0144.mediawiki
      tx.bip144Flag = new Bip144Flag(raw[offset + 1]);
      offset += 2;
    }
    // process input data
    tx.inputSize = new VarInt(raw, offset);
    offset += tx.inputSize.getOriginalSizeInBytes();
    tx.transactionInputs = new ArrayList<>();
    for (long i = 0; i < tx.inputSize.value; i++) {
      BtcTransactionInput input = BtcTransactionInput.fromRaw(raw, offset);
      tx.transactionInputs.add(input);
      offset += input.getRaw().length;
    }
    // process output data
    tx.outputSize = new VarInt(raw, offset);
    offset += tx.outputSize.getOriginalSizeInBytes();
    tx.transactionOutputs = new ArrayList<>();
    for (long i = 0; i < tx.outputSize.value; i++) {
      BtcTransactionOutput output = BtcTransactionOutput.fromRaw(raw, offset);
      tx.transactionOutputs.add(output);
      offset += output.getRaw().length;
    }
    if (tx.bip144Flag != null) {
      for (BtcTransactionInput input : tx.transactionInputs) {
        BtcWitness witness = BtcWitness.fromRaw(raw, offset);
        input.setWitness(witness);
        offset += witness.getRaw().length;
      }
    }
    tx.lockTime = VarInt.readUint32(raw, offset);
    return tx;
  }


  public String rawText() {
    return MyByte.toHex(getSigWitRaw());
  }

  byte[] getSigWitHashPart1() {
    return MyByte
        .builder()
        .copy(version, 4)
        .copy(hashPrevOuts())
        .copy(hashSequence())
        .getData();
  }


  byte[] getSigWitHashPart2() {
    return MyByte
        .builder()
        .copy(hashOutputs())
        .copy(lockTime, 4)
        .getData();
  }

  private byte[] hashPrevOuts() {
    MyByte.BuildList builder = MyByte.builder();
    transactionInputs.forEach(r -> builder.copy(r.getOutPoint().createRaw()));
    return SHA._256hashTwice(builder.getData());
  }

  private byte[] hashSequence() {
    MyByte.BuildList builder = MyByte.builder();
    transactionInputs.forEach(r -> builder.copy(r.getSequence(), 4));
    return SHA._256hashTwice(builder.getData());
  }

  private byte[] hashOutputs() {
    MyByte.BuildList builder = MyByte.builder();
    transactionOutputs.forEach(r -> builder.copy(r.getRaw()));
    return SHA._256hashTwice(builder.getData());
  }

  VarInt getInputSize() {
    return inputSize;
  }

  public byte[] getSigWitRaw() {
    if (bip144Flag == null) {
      return getRaw();
    }
    if (sigWitRaw == null) {
      return createSigWitRaw();
    }
    return sigWitRaw;
  }

  byte[] inputEmptyScriptRaw() {
    MyByte.BuildList builder = MyByte.builder();
    builder.copy(version, 4)
        .copy(inputSize.encode());
    transactionInputs.forEach(r -> builder.copy(r.emptyScriptRaw()));
    builder.copy(outputSize.encode());
    transactionOutputs.forEach(r -> builder.copy(r.createRaw()));
    builder.copy(lockTime, 4);
    return builder.getData();
  }

  @Override
  byte[] createRaw() {
    MyByte.BuildList builder = MyByte.builder();
    builder.copy(version, 4)
        .copy(inputSize.encode());
    transactionInputs.forEach(r -> builder.copy(r.createRaw()));
    builder.copy(outputSize.encode());
    transactionOutputs.forEach(r -> builder.copy(r.createRaw()));
    builder.copy(lockTime, 4);
    return raw = builder.getData();
  }

  private byte[] createSigWitRaw() {
    MyByte.BuildList builder = MyByte.builder();
    builder.copy(version, 4);
    if (bip144Flag != null) {
      builder.copy(bip144Flag.getRaw());
    }
    builder.copy(inputSize.encode());
    transactionInputs.forEach(r -> builder.copy(r.createRaw()));
    builder.copy(outputSize.encode());
    transactionOutputs.forEach(r -> builder.copy(r.createRaw()));
    if (bip144Flag != null) {
      transactionInputs.forEach(r -> builder.copy(r.getWitness().getRaw()));
    }
    builder.copy(lockTime, 4);
    return sigWitRaw = builder.getData();
  }

  void prepareForSign(
      List<BtcOutPointEx> outPoints,
      List<TransferTo> transferToList,
      OmniTransferCoins omniTransferCoins) {
    version = 1L;
    transactionInputs = outPoints.stream().map(BtcTransactionInput::convert).collect(toList());
    inputSize = new VarInt(transactionInputs.size());
    transactionOutputs = transferToList.stream().map(BtcTransactionOutput::convert).collect(toList());
    if (omniTransferCoins != null) {
      transactionOutputs.add(BtcTransactionOutput.convert(omniTransferCoins));
    }
    outputSize = new VarInt(transactionOutputs.size());
    lockTime = 0L;
  }
}

class TransactionBuilder {

  private final BtcTransaction t;
  private BtcNet net;
  private List<BtcOutPointEx> outPoints;
  private List<TransferTo> transferToList;
  private BtcAddress recycleAddress;
  private Long fee;
  private OmniTransferCoins omniTransferCoins;

  static TransactionBuilder build() {
    return new TransactionBuilder();
  }

  private TransactionBuilder() {
    t = new BtcTransaction();
  }

  TransactionBuilder setNet(BtcNet net) {
    this.net = net;
    return this;
  }

  TransactionBuilder setIn(List<BtcOutPointEx> outPoints) {
    this.outPoints = new LinkedList<>(outPoints);
    return this;
  }

  TransactionBuilder setOut(List<TransferTo> transferToList) {
    this.transferToList = new LinkedList<>(transferToList);
    return this;
  }

  TransactionBuilder setRecycle(BtcAddress recycleAddress) {
    this.recycleAddress = recycleAddress;
    return this;
  }

  TransactionBuilder setFee(Long fee) {
    this.fee = fee;
    return this;
  }

  TransactionBuilder set(OmniTransferCoins omniTransferCoins) {
    this.omniTransferCoins = omniTransferCoins;
    return this;
  }

  BtcTransaction finish() {
    addReceiver(omniTransferCoins != null);
    t.prepareForSign(outPoints, transferToList, omniTransferCoins);
    Signer.sign(t);
    return t;
  }

  // 剩下的BTC打入某个账户
  private void addReceiver(boolean mergeRecycle) {
    long recycle = 0;
    for (BtcOutPointEx from : outPoints) {
      recycle += from.getValue();
    }
    for (TransferTo to : transferToList) {
      recycle -= to.getValue();
      if (!net.equals(to.getAddress().getNet())) {
        throw new IllegalArgumentException("转出地址网络不一致: " + net + "/" + to.getAddress().getNet());
      }
    }
    recycle -= fee;
    if (recycle < 0) {
      StringBuilder sb = new StringBuilder();
      sb.append(" from");
      for (BtcOutPointEx from : outPoints) {
        sb.append("|").append(from.getPrevTransactionHash()).append("_").append(from.getIndex())
            .append(":").append(from.getValue());
      }
      sb.append(" to");
      for (TransferTo to : transferToList) {
        sb.append("|").append(to.getAddress().getStr())
            .append(":").append(to.getValue());
      }
      sb.append(" fee:").append(fee);
      throw new IllegalArgumentException("转入转出数额不匹配" + sb.toString());
    } else if (recycle == 0) {
      // 手续费正好
      return;
    }
    if (recycleAddress == null) {
      throw new IllegalArgumentException("未配置回收地址");
    }
    if (mergeRecycle) {
      for (TransferTo to : transferToList) {
        if (to.getAddress().getStr().equals(recycleAddress.getStr())) {
          // 收款地址包含回收地址
          to.setValue(to.getValue() + recycle);
          return;
        }
      }
    }
    // 收款地址未包含回收地址
    TransferTo to = new TransferTo();
    to.setAddress(recycleAddress);
    to.setValue(recycle);
    transferToList.add(to);
  }
}

class Signer {
  private static byte[] signAll = MyByte.builder().copy(BtcSigHashType.ALL.getValue(), 4).getData();
  private static byte[] _0x160014 = MyByte.builder().copy((byte) 0x16).copy(OP_SEG_WIT_VER_0).copy((byte) 0x14).getData();

  static void sign(BtcTransaction tx) {
    new Signer(tx).sign();
  }

  private byte[] inputEmptyScriptRaw;
  private int offset;
  private byte[] sigWitHashPart1;
  private byte[] sigWitHashPart2;
  private BtcTransaction tx;

  private Signer(BtcTransaction tx) {
    this.tx = tx;
    this.offset = 4 + tx.getInputSize().getOriginalSizeInBytes();
  }

  private void sign() {
    for (BtcTransactionInput input : tx.getTransactionInputs()) {
      offset += BtcOutPoint.LENGTH;
      createSigScript(input);
      offset += 5;
    }
  }

  private void createSigScript(BtcTransactionInput input) {
    BtcOutPointEx output = (BtcOutPointEx) input.getOutPoint();
    BtcScript.Type type = output.getScript().type;
    if (type == null) {
      throw new IllegalArgumentException("sign, unsupported output type: null");
    }
    BtcPrivateKey pk = output.getPrivateKey();
    switch (type) {
      case P2PKH:
        input.setupScript(createSigScript(pk, createLegacySignData(output.getScript().getRaw())));
        return;
      case P2PK:
      case P2PKc:
        break;
      case P2SH:
        // unlock with segregated witness
        input.setupScript(BtcScript.fromRaw(
            MyByte.builder().copy(_0x160014).copy(pk.getAddressRaw(USING_COMPRESSED_PUBLIC_KEY)).getData()));
      case P2WPKH:
        byte[] scriptCode = BtcScript.p2pkh(pk.getAddressRaw(USING_COMPRESSED_PUBLIC_KEY)).getRaw();
        byte[] segWithSignData = createSegWithSignData(output, input.getSequence(), scriptCode);
        input.setWitness(BtcWitness.fromSigScript(createSigScript(pk, segWithSignData)));
        tx.segWit();
        return;
      case P2WSH:
        break;
    }
    throw new IllegalArgumentException("sign, unsupported output type: " + type);
  }

  private BtcScript createSigScript(BtcPrivateKey pk, byte[] data) {
    byte[] sign = ECC.btcSignCore(pk.getD(), SHA._256hashTwice(data));
    byte[] sigScript = MyByte
        .builder()
        .copy((byte) (sign.length + 1))
        .copy(sign).copy(BtcSigHashType.ALL.getValue())
        .copy(USING_COMPRESSED_PUBLIC_KEY ? OP_PUSH_PUBLIC_KEY_COMPRESSED : OP_PUSH_PUBLIC_KEY)
        .copy(pk.getPublicKey(USING_COMPRESSED_PUBLIC_KEY))
        .getData();
    return BtcScript.fromRaw(sigScript);
  }

  private byte[] createLegacySignData(byte[] scriptPublicKey) {
    byte[] signData = getInputEmptyScriptRaw();
    return MyByte.builder()
        .copy(signData, offset)
        .copy(new VarInt(scriptPublicKey.length).encode())
        .copy(scriptPublicKey)
        .copy(signData, offset + 1, signData.length - offset - 1)
        .copy(signAll)
        .getData();
  }

  private byte[] createSegWithSignData(BtcOutPointEx output, long sequence, byte[] scriptCode) {
    return MyByte
        .builder()
        .copy(getSigWitHashPart1())
        .copy(output.getRaw())
        .copy(new VarInt(scriptCode.length).encode())
        .copy(scriptCode)
        .copy(output.getValue(), 8)
        .copy(sequence, 4)
        .copy(getSigWitHashPart2())
        .copy(signAll)
        .getData();
  }

  private byte[] getInputEmptyScriptRaw() {
    if (inputEmptyScriptRaw == null) {
      inputEmptyScriptRaw = tx.inputEmptyScriptRaw();
    }
    return inputEmptyScriptRaw;
  }

  private byte[] getSigWitHashPart1() {
    if (sigWitHashPart1 == null) {
      sigWitHashPart1 = tx.getSigWitHashPart1();
    }
    return sigWitHashPart1;
  }

  private byte[] getSigWitHashPart2() {
    if (sigWitHashPart2 == null) {
      sigWitHashPart2 = tx.getSigWitHashPart2();
    }
    return sigWitHashPart2;
  }
}
