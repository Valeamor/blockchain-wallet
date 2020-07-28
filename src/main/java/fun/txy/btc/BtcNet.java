package fun.txy.btc;

public enum BtcNet {
  MAIN("bc"),
  TEST("tb"),;

  private final String bech32AddressPrefix;

  BtcNet(String bech32AddressPrefix) {
    this.bech32AddressPrefix = bech32AddressPrefix;
  }

  public String getBech32AddressPrefix() {
    return bech32AddressPrefix;
  }

  public static BtcNet fromBech32AddressPrefix(String bech32AddressPrefix) {
    for (BtcNet btcNet : values()) {
      if (btcNet.getBech32AddressPrefix().equals(bech32AddressPrefix)) {
        return btcNet;
      }
    }
    return null;
  }
}
