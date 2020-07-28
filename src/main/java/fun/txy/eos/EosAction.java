package fun.txy.eos;

import fun.txy.eos.utils.EosUtil;
import fun.txy.utils.MyByte;

import java.util.Collections;
import java.util.List;

public class EosAction {
  private String account;
  private String name;
  private List<EosAuthorization> authorization;
  private String data;
  private String hexData;

  void packInto(MyByte.BuildList b) {
    b.copy(EosUtil.packName(account))
        .copy(EosUtil.packName(name));
    // copy authorization
    b.copySize(authorization.size());
    authorization.forEach(a -> a.packInto(b));
    // copy data
    byte[] bd = MyByte.fromHex(data);
    b.copySize(bd.length);
    b.copy(bd);
  }

  static EosAction transfer(String from, String to, EosTransferQuantity quantity, String memo) {
    EosAction a = new EosAction();
    a.account = "eosio.token";
    a.name = "transfer";
    a.authorization = Collections.singletonList(EosAuthorization.active(from));
//    JSONObject j = new JSONObject();
//    j.put("from", from);
//    j.put("to", to);
//    j.put("quantity", quantity.toPlainString());
//    j.put("memo", memo);
//    a.data = j.toJSONString();
    a.data = MyByte.toHex(
        MyByte.builder()
            .copy(EosUtil.packName(from))
            .copy(EosUtil.packName(to))
            .copy(quantity.getAmount())
            .copy(quantity.getPrecision())
            .copy(quantity.getSymbol())
            .copy(memo)
            .getData()
    );
    return a;
  }
}
