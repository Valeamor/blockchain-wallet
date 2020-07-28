
import fun.txy.btc.*;
import fun.txy.btc.omni.OmniTransferCoins;
import fun.txy.utils.Base58;
import fun.txy.utils.MyByte;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class BtcTest {

  @Test
  public void getAddress() {
    BtcPrivateKey btcPrivateKey = new BtcPrivateKey(MyByte.fromHex("private_key_hex"));
    for (BtcAddress.Type type : BtcAddress.Type.values()) {
      try {
        BtcAddress address = btcPrivateKey.getAddress(BtcNet.MAIN, type);
        System.out.println(type + ": " + MyByte.toHex(address.getRaw()));
        System.out.println(type + ": " + address.getStr());
      } catch (Exception ignore) {
      }
    }
  }

  @Test
  public void generatePrivateKey() {
    BtcPrivateKey btcPrivateKey = new BtcPrivateKey();
    System.out.println(MyByte.toHex(btcPrivateKey.getEncoded()));
  }

  @Test
  public void checkAddress() {
//    String originAddressString = "n2mhpJXqjAbJQVGMfiPD8KzDFCjUf17uyX";
    BtcAddress address = BtcAddress.fromRaw(MyByte.fromHex("e924cb7cc80c7cc991d50a18a8166be0c76c4373"), BtcNet.TEST, BtcAddress.Type.P2WPKH);
    System.out.println(address.getStr());
    System.out.println(address.getNet() + "." + address.getType());
    System.out.println(MyByte.toHex(address.getRaw()));
  }

  @Test
  public void testTransactionFromRaw() {
    BtcTransaction tx = BtcTransaction.fromRaw(
        "010000000001027bf47524bc443524d224d5a49066eeeddecfc31a2ff03a7ac87556ede40bf95e0000000017160014a4c36ab0f4ad704507763230d0c10f9d947ca2d0ffffffff0598ddffaee33639d8e17ae2b530ef244058a46c2eb427042818c2bb30aed66c0000000017160014a4c36ab0f4ad704507763230d0c10f9d947ca2d0ffffffff0440ad12000000000017a91459180d42f072783d631312f8e6a602656795acae8720f7c90000000000160014e924cb7cc80c7cc991d50a18a8166be0c76c437310270000000000001976a914e924cb7cc80c7cc991d50a18a8166be0c76c437388ace6477c0000000000160014c0a9e413915bac02f09770c5177fe10561ec32b902483045022100fb5fbcf3788805ee671b69d90ea64c58bad4f5d522f3e50363697cb147fd597b022071f122d03287e97e7a960180b6aac9111c2845e417b1ab7ed0fb81c6f5612b330121035cafe76d2128bc178807f8492700a9ba5930e5f90f469d47617e32cbc91ab27a0247304402204aba478c076ce0d011e4c5dcb523e5f1101cef1cba26328134f24c7c167a482802206ca35028460a19686dc56a3f7bffdf5e114b06ed7f5e9b165a1effdf21ee6bdf0121035cafe76d2128bc178807f8492700a9ba5930e5f90f469d47617e32cbc91ab27a00000000");
    System.out.println(tx.getTransactionOutputs().get(1).getScript().getAddress(BtcNet.TEST).getStr());
    System.out.println(tx.getLockTime());
  }

  @Test
  public void testCreateRawTransaction() {
    BtcPrivateKey pk = new BtcPrivateKey(MyByte.fromHex("private_key_hex"));
    System.out.println(pk.getAddress(BtcNet.TEST, BtcAddress.Type.P2WPKH).getStr());
    System.out.println(MyByte.toHex(pk.getAddressRaw(true)));
    // 钱从哪来
    List<BtcOutPointEx> outPoints = new ArrayList<>();
    BtcTransaction tx = BtcTransaction.fromRaw(
        "0100000000010348b6e2315e225f47ffadd4e665f0a9ffa6377657bccc14dba8e895fd018082bd0000000017160014a4c36ab0f4ad704507763230d0c10f9d947ca2d0ffffffff11bce0f1ce3e500754aed829bd7c4c0de9691b1b8cdfa92c5bbfd71c7018c89a0200000017160014c0a9e413915bac02f09770c5177fe10561ec32b9ffffffff6dfc85355be7f3d4351a0250e40d58e9d1cb2932f5b1c5cdc6d6faa99deaec860000000017160014a4c36ab0f4ad704507763230d0c10f9d947ca2d0ffffffff025f7bad010000000017a914e836b1a88e713b857f02e03d02df4ae9a7305b36875038a9000000000017a9141ec4905ab3fa247a555a40cb9772d25d62b4fcbc87024730440220302ff1dcc0eac925f4415b26a1c2e61b1cda0fbf348813c2032f1c15f437532402205a816de2bc418da2bc79da8704669ba4c43f836de3575e981bbdd1662e8afaf00121035cafe76d2128bc178807f8492700a9ba5930e5f90f469d47617e32cbc91ab27a02483045022100c61cf99c60b43467f84c0df3a19f045cb65e81b41941543eed9626410c630bd102202f6e85c0d08ce1eaeb84c995f55741877b14adb2d51ec3de21f5f348233639f0012103f836fae12a54a79262771cc2db219cdfc96d8b443af5a154760a3173a32eff1802483045022100dc95f50db49a9e467ec61c43b960acb05f85900afbfe327fdd154e96e78d93f00220655e523e55743d968962775aea5c774ed7ffe238f5e44ad888ca1492c199e6110121035cafe76d2128bc178807f8492700a9ba5930e5f90f469d47617e32cbc91ab27a00000000");
    outPoints.add(BtcOutPointEx.build(tx, 1, pk));
    tx = BtcTransaction.fromRaw(
        "010000000001059fea728b5b421f8f3b9af9ab726f462a51974bc3051811a96599150f5407b5980000000017160014a4c36ab0f4ad704507763230d0c10f9d947ca2d0ffffffff8d0d485a0ed8e43765b90968abd52fadaeb5476cf3ace5f2682cb7123ff8fa910300000017160014c0a9e413915bac02f09770c5177fe10561ec32b9ffffffffb2a2bbe070febf53712e5ee90127fa74f5508c733d43dcc89eb7b9365e87705d0000000017160014a4c36ab0f4ad704507763230d0c10f9d947ca2d0ffffffff9a46c73b743890009553bc6d60c3b0a1c5b74a1a37b99f6b3d29e602b17c3bc80200000017160014c0a9e413915bac02f09770c5177fe10561ec32b9ffffffff14d5359a2c65765c14503f0053363a8cddf4ead8767bef10be3057ad0592a6ca0000000017160014a4c36ab0f4ad704507763230d0c10f9d947ca2d0ffffffff03ff5f46020000000017a914e836b1a88e713b857f02e03d02df4ae9a7305b3687669f85000000000017a9141ec4905ab3fa247a555a40cb9772d25d62b4fcbc87905f01000000000017a9141ec4905ab3fa247a555a40cb9772d25d62b4fcbc870248304502210094c5d442284196e78062bc651bcef92e4f7ad2467f95307a37d20c5802de1fc8022014aab38aa4e0b79349f880c16387a232e714777178cd37dd6c4d34d391f28dfa0121035cafe76d2128bc178807f8492700a9ba5930e5f90f469d47617e32cbc91ab27a0247304402207d9bbc017e6c183e3176d8e57488d74bd2aa518f3fbe5059966849ad2c7b8b1d0220104de1d6f9edee2e1f41d538634bbbf5e1c32de7afdf47bb6d514692c4e086aa012103f836fae12a54a79262771cc2db219cdfc96d8b443af5a154760a3173a32eff180248304502210096c397ce4daed278cef1c364a761f7cfe67ed6b3e9b4535eaaa085ac91832fb502207fbf89622cd3ce66b7996478040f569cf9fe9071677da91f72ed0a491f26ac370121035cafe76d2128bc178807f8492700a9ba5930e5f90f469d47617e32cbc91ab27a02473044022030090ed573100329bc92fd6d0eb60b0d69568fdad8879d7f5445e57981777c4702200f3ad5b014a1ed3b0cb29e837093af34cc88c7bfdfd32b09eea41a5ab37c27cb012103f836fae12a54a79262771cc2db219cdfc96d8b443af5a154760a3173a32eff1802483045022100926d22f6550018104abebcf319d02e249027075969904058cb44cb91ca499da802203c0bb84c7843b45d3f7520e561f870689433755dab8bad4d000d5f034990d7cf0121035cafe76d2128bc178807f8492700a9ba5930e5f90f469d47617e32cbc91ab27a00000000");
    outPoints.add(BtcOutPointEx.build(tx, 1, pk));
    tx = BtcTransaction.fromRaw(
        "01000000000103f81557bd518f4b2dd659fea6e3de7c2a53cedf501e1013a3b63108fc4b8104e40000000017160014a4c36ab0f4ad704507763230d0c10f9d947ca2d0ffffffff6103fb18612f8555c2c8b4cf9741de5626bc9827a7a3e72ff969af25190f3fd10000000017160014a4c36ab0f4ad704507763230d0c10f9d947ca2d0ffffffff03dd9fee940935776fc637c279a490ed57d472fcf6f69ba3cbd102e6494ab3b70100000017160014c0a9e413915bac02f09770c5177fe10561ec32b9ffffffff03afe016010000000017a914e836b1a88e713b857f02e03d02df4ae9a7305b368721335e000000000017a9141ec4905ab3fa247a555a40cb9772d25d62b4fcbc87905f01000000000017a9141ec4905ab3fa247a555a40cb9772d25d62b4fcbc8702473044022001456746ee238a160d5de318e81ceb138dc578e4f9cbf7e08bd44c62db2659ff022042d5055d294f07d5aa9c9637ab71af29d7b71429739c3a299d8f0034fe2654dd0121035cafe76d2128bc178807f8492700a9ba5930e5f90f469d47617e32cbc91ab27a02483045022100dd5d8d2a26aea31f5bd617787fbead6c01e828a8dfcc9312b4906d097b409460022006ebac54b5e46e625fb3cf964e338915b1590002c66a3a628af26ade0da1f46b0121035cafe76d2128bc178807f8492700a9ba5930e5f90f469d47617e32cbc91ab27a02483045022100bee06d6a0ce80b26b0295508b25a82e0f2f6de29a6e72e5cbdadbb751b73d3cc022034f6cd02fb75d75e314dcda81c4ae4893be9684a95774826202eeaedf7a6d17f012103f836fae12a54a79262771cc2db219cdfc96d8b443af5a154760a3173a32eff1800000000");
    outPoints.add(BtcOutPointEx.build(tx, 1, pk));
    long total = outPoints.stream().mapToLong(BtcOutPointEx::getValue).sum();
    System.out.println("total:" + BtcUtils.satoshiToBtc(total));
    // 钱给谁
    List<TransferTo> transferToList = new ArrayList<>();
    TransferTo to = new TransferTo();
    to.setAddress(BtcAddress.convert("19MnYNXDAid6hZBZbr1HN2nE3VniyEAiRG"));
    to.setValue("0.26010567");
    transferToList.add(to);
    // 创建交易
    BtcTransaction normalTransaction = BtcTransaction.normalTransfer(
        BtcNet.TEST, outPoints, transferToList, "2N1NJwWGSPwnqjv4AwpBRhsC5XCw75caCk7", "0.0001");

    System.out.println(normalTransaction.rawText());
    System.out.println(
        BtcUtils.satoshiToBtc(normalTransaction.getTransactionOutputs().get(1).getValue()));
  }

  @Test
  public void testCreateOmniRawTransaction() {
    BtcPrivateKey pk = new BtcPrivateKey(MyByte.fromHex("private_key_hex"));
    // 钱从哪来
    List<BtcOutPointEx> outPoints = new ArrayList<>();
    BtcTransaction tx = BtcTransaction.fromRaw(
        "010000000001014cadf38c0165a6a6b530bc3bb6392ced2c0923c01b838add113b03e6dd224b9300000000171600145ad2de00e85f5dd2d371c0e59d4c950e3ef883ecffffffff0322020000000000001976a9140f014ff2ec388c17242b2fd1b2b58e969f59859788ac6c8f00000000000017a914c11bc63e4406ac2d03dac090ab7411a456f81b50870000000000000000166a146f6d6e69000000000000001f0000000008f0d180024730440220064f8fdf966a627d67483ef746d2452ecdfc1f932d062838af407fa248d8caf7022072df19c47cdfade075ebdd918b27303375e756e8105f344105308596e59ded62012103d793c3f5ec2faee24ec239689d8d9481394f00dbf29925164bfcf2b0ce5c09f600000000");
    outPoints.add(BtcOutPointEx.build(tx, 1, pk));
//    tx = BtcTransaction.fromRaw("");
//    outPoints.add(BtcOutPointEx.build(tx, 1, pk));
//    tx = BtcTransaction.fromRaw("");
//    outPoints.add(BtcOutPointEx.build(tx, 2, pk));
    // 钱给谁
    List<TransferTo> transferToList = new ArrayList<>();
    TransferTo to = new TransferTo();
    to.setAddress(BtcAddress.convert("19MnYNXDAid6hZBZbr1HN2nE3VniyEAiRG"));
    to.setValue("0.00000546"); // over the dust
    transferToList.add(to);
    // 创建交易
    BtcTransaction normalTransaction = BtcTransaction.omniTransfer(
        BtcNet.MAIN, outPoints, transferToList, "3KJ5cN99QfBsnRgxQ4UaFvTGUXQDRSSiRu", 31L, "4",
        "0.0001");

    System.out.println(normalTransaction.rawText());
    System.out.println(normalTransaction.txHash());
  }

  @Test
  public void testScript() {
    BtcScript script = BtcScript.fromRaw(MyByte.fromHex("48304502210082ac0d7840e08cc457ead7d66fb86710cde8c8dd8fd31be05ad1b062b8859d630220459a0cd6665cf846c8ac90c45b35bf6aa971b640b5dca0b0fb2f916ad9551a04012103b16ac8c3821b24d5071d26b2a123031515c65e026bbf53b3d7a2ddd5e4270950"));
    System.out.println(MyByte.toHex(script.getData().get(0).getData()));
    System.out.println(MyByte.toHex(script.getData().get(1).getData()));
  }

  @Test
  public void testOmniSimpleTransfer() {
    OmniTransferCoins t = OmniTransferCoins.fromRaw(MyByte.fromHex("6f6d6e69000000000000001f00004a941cdf2000"));
    System.out.println(t.getNumberOfCoins());
    System.out.println(MyByte.toHex(t.toRaw()));
  }

  @Test
  public void convertBase58TxHash() {
    System.out.println(MyByte.toHex(Base58.decode("1qd61jHPpUZhtHF3p4mUMYK5JSoMZPai1me27eufm4Q")));
  }
}
