package fun.txy.btc;

import fun.txy.utils.MyByte;
import fun.txy.utils.VarInt;

import java.util.ArrayList;
import java.util.List;

public class BtcWitness extends BtcBaseData {
  private VarInt itemSize;
  private List<Item> items;

  static BtcWitness fromSigScript(BtcScript script) {
    BtcWitness w = new BtcWitness();
    w.itemSize = new VarInt(script.getData().size());
    w.items = new ArrayList<>();
    for (BtcOperation op : script.getData()) {
      w.items.add(Item.fromRaw(op.getRaw()));
    }
    return w;
  }

  static BtcWitness fromRaw(byte[] raw, int offset) {
    BtcWitness w = new BtcWitness();
    w.itemSize = new VarInt(raw, offset);
    offset += w.itemSize.getOriginalSizeInBytes();
    w.items = new ArrayList<>();
    for (int i = 0, l = (int) w.itemSize.value; i < l; i++) {
      Item item = Item.fromRaw(raw, offset);
      w.items.add(item);
      offset += item.getRaw().length;
    }
    return w;
  }

  @Override
  byte[] createRaw() {
    MyByte.BuildList builder = MyByte.builder();
    builder.copy(itemSize.encode());
    for (Item item : items) {
      builder.copy(item.getRaw());
    }
    return raw = builder.getData();
  }

  static class Item extends BtcBaseData {
    private VarInt dataSize;
    private byte[] data;

    private static Item fromRaw(byte[] raw) {
      return fromRaw(raw, 0);
    }

    private static Item fromRaw(byte[] raw, int offset) {
      Item i = new Item();
      i.dataSize = new VarInt(raw, offset);
      i.data = MyByte.copyBytes(raw, offset + i.dataSize.getOriginalSizeInBytes(), (int) i.dataSize.value);
      return i;
    }

    @Override
    byte[] createRaw() {
      return raw = MyByte.builder().copy(dataSize.encode()).copy(data).getData();
    }
  }
}
