package fun.txy.btc;

import fun.txy.utils.MyByte;
import fun.txy.utils.VarInt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static fun.txy.btc.BtcOperation.*;
import static fun.txy.btc.BtcUtils.hash160;

public class BtcScript extends BtcBaseData {

  private static final Trie trie;

  static {
    trie = Trie.newRoot();
    for (Type type : Type.values()) {
      trie.insert(type);
    }
    trie.add(Type.NULL_DATA, OP_RETURN, OP_PUSH_OMNI_TRANSFER_COINS);
  }

  protected Type type;
  private List<BtcOperation> data;

  static BtcScript nullData(byte[] data) {
    BtcScript s = new BtcScript();
    s.type = Type.NULL_DATA;
    s.data = new ArrayList<>();
    s.data.add(BtcOperation.create(OP_RETURN, null));
    s.data.add(BtcOperation.create((byte) data.length, data));
    return s;
  }

  static BtcScript cxcAssetData(byte[] address, byte[] data) {
    BtcScript script = createHash160Script(address, Type.P2PKH);
    script.addOperation(CXC_ASSET_MARK, data);
    script.addOperation(OP_DROP);
    return script;
  }

  static BtcScript p2pkh(byte[] address) {
    return createHash160Script(address, Type.P2PKH);
  }

  public static BtcScript createHash160Script(byte[] hash160, Type type) {
    if (hash160 == null || hash160.length != (int) OP_PUSH_HASH160) {
      throw new IllegalArgumentException("hash160必须为20byte");
    }
    BtcScript s = new BtcScript();
    s.type = type;
    s.data = new ArrayList<>();
    for (byte op : type.getOperations()) {
      s.data.add(BtcOperation.create(op, op == OP_PUSH_HASH160 ? hash160 : null));
    }
    return s;
  }

  public static BtcScript createScriptPubKey(BtcAddress address) {
    switch (address.getType()) {
      case P2PKH:
        return p2pkh(address.getRaw());
      case P2SH:
        return createHash160Script(address.getRaw(), Type.P2SH);
      case P2WPKH:
        return createHash160Script(address.getRaw(), Type.P2WPKH);
      case P2WSH:
        break;
    }
    throw new IllegalArgumentException("unsupported output address type: " + address.getType());
  }

  public static BtcScript fromRaw(byte[] raw) {
    return fromRaw(raw, 0, raw.length);
  }

  static BtcScript fromRaw(byte[] raw, int offset, int length) {
    BtcScript script = new BtcScript();
    int initOffset = offset;
    try {
      script.data = new ArrayList<>();
      int end = offset + length;
      while (offset < end) {
        byte opCode = raw[offset++];
        if (opCode >= 1 && opCode <= OP_PUSHDATA4) {
          int l;
          if (opCode == OP_PUSHDATA4) {
            l = (int) VarInt.readUint32(raw, offset) + 4;
          } else if (opCode == OP_PUSHDATA2) {
            l = VarInt.readUint16(raw, offset) + 2;
          } else if (opCode == OP_PUSHDATA1) {
            l = (raw[offset] & 0xff) + 1;
          } else {
            l = opCode;
          }
          script.addOperation(opCode, MyByte.copyBytes(raw, offset, l));
          offset += l;
        } else {
          script.addOperation(opCode);
        }
      }
      if (offset != end) {
        throw new RuntimeException("offset(" + offset + ") != end(" + end + ")");
      }
      script.setupScriptType();
    } catch (Exception e) {
      System.out.println("bitcoin script (" + MyByte.toHex(MyByte.copyBytes(raw, initOffset, length)) + ") phase error: {}" + e.getMessage());
      script.data = new ArrayList<>();
      script.addOperation(raw[initOffset], MyByte.copyBytes(raw, initOffset + 1, length - 1));
    }
    return script;
  }

  private void setupScriptType() {
    type = trie.getScriptType(this);
  }

  private void addOperation(byte operation) {
    addOperation(operation, null);
  }

  private void addOperation(byte operation, byte[] data) {
    this.data.add(BtcOperation.create(operation, data));
  }

  @Override
  byte[] createRaw() {
    MyByte.BuildList builder = MyByte.builder();
    for (BtcOperation s : data) {
      builder.copy(s.getCode());
      if (s.getData() != null) {
        builder.copy(s.getData());
      }
    }
    return raw = builder.getData();
  }

  public Type getType() {
    return type;
  }

  public List<BtcOperation> getData() {
    return data;
  }

  public BtcAddress getAddress(BtcNet net) {
    if (type == null) {
      return null;
    }
    switch (type) {
      case COIN_BASE:
        break;
      case P2PKH:
        return BtcAddress.fromRaw(data.get(2).getData(), net, BtcAddress.Type.P2PKH);
      case P2PK:
      case P2PKc:
        return BtcAddress.fromRaw(hash160(data.get(0).getData()), net, BtcAddress.Type.P2PKH);
      case P2SH:
        return BtcAddress.fromRaw(data.get(1).getData(), net, BtcAddress.Type.P2SH);
      case P2WPKH:
        return BtcAddress.fromRaw(data.get(1).getData(), net, BtcAddress.Type.P2WPKH);
      case P2WSH:
        break;
    }
    return null;
  }

  public enum Type {
    COIN_BASE,
    // Pay to Public Key Hash
    P2PKH(OP_DUP, OP_HASH160, OP_PUSH_HASH160, OP_EQUALVERIFY, OP_CHECKSIG),
    // Pay to Public Key
    P2PK(OP_PUSH_PUBLIC_KEY, OP_CHECKSIG),
    P2PKc(OP_PUSH_PUBLIC_KEY_COMPRESSED, OP_CHECKSIG), // compressed public key
    // Pay to Script Hash
    P2SH(OP_HASH160, OP_PUSH_HASH160, OP_EQUAL),
    // Pay to Witness Public Key Hash
    P2WPKH(OP_SEG_WIT_VER_0, OP_PUSH_HASH160),
    // Pay to Witness Script Hash
    P2WSH,
    //
    NULL_DATA,//omni
    CXC_ASSET_DATA(OP_DUP, OP_HASH160, OP_PUSH_HASH160, OP_EQUALVERIFY, OP_CHECKSIG,
        CXC_ASSET_MARK, OP_DROP),//cxc发行资产
    ;
    private final byte[] operations;

    Type(byte... operations) {
      this.operations = operations;
    }

    public byte[] getOperations() {
      return operations;
    }
  }
}


class Trie {

  private BtcScript.Type type;
  private HashMap<Byte, Trie> children = new HashMap<>();

  private Trie() {
  }

  static Trie newRoot() {
    return new Trie();
  }

  void insert(BtcScript.Type type) {
    if (type.getOperations() == null || type.getOperations().length == 0) {
      return;
    }
    add(type, type.getOperations());
  }

  void add(BtcScript.Type type, byte... operations) {
    Trie node = this;
    for (byte o : operations) {
      if (node.children.containsKey(o)) {
        node = node.children.get(o);
      } else {
        Trie t = new Trie();
        node.children.put(o, t);
        node = t;
      }
    }
    node.type = type;
  }

  BtcScript.Type getScriptType(BtcScript script) {
    Trie node = this;
    for (BtcOperation o : script.getData()) {
      node = node.children.get(o.getCode());
      if (node == null) {
        return null;
      }
    }
    return node.type;
  }
}