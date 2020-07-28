package fun.txy.achain;

import com.alibaba.fastjson.JSONObject;
import fun.txy.utils.JSON;
import fun.txy.utils.MyByte;

public class Asset {
  private final long amount; // c++ __int64 8bit
  private final int assetId; // c++ int 4bit
  //
  private byte[] toBytes;
  private JSONObject json;

  public byte[] toBytes() {
    if (toBytes == null) {
      toBytes = MyByte.builder()
          .copy(amount)
          .copySize(assetId)
          .getData();
    }
    return toBytes;
  }

  public JSONObject toJSON() {
    if (json == null) {
      json = JSON.build()
          .add("amount", amount)
          .add("asset_id", assetId)
          .get();
    }
    return json;
  }

  public Asset(long amount) {
    this(amount, 0);
  }

  public Asset(long amount, int assertId) {
    this.amount = amount;
    this.assetId = assertId;
  }

  public long getAmount() {
    return amount;
  }

  public int getAssetId() {
    return assetId;
  }
}
