package fun.txy.eos;


import fun.txy.eos.utils.EosUtil;
import fun.txy.utils.MyByte;

public class EosAuthorization {
  private String actor;
  private String permission;

  void packInto(MyByte.BuildList b) {
    b.copy(EosUtil.packName(actor))
        .copy(EosUtil.packName(permission));
  }

  static EosAuthorization active(String actor) {
    EosAuthorization a = new EosAuthorization();
    a.actor = actor;
    a.permission = "active";
    return a;
  }
}
