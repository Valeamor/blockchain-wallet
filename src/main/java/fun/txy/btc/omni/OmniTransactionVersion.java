package fun.txy.btc.omni;


/*
    Let's have a look at an Omni transaction before we create our own. An Omni transaction is a
  bitcoin transaction that has some extra data attached to it. Take your favorite block explorer and
  search for 50f8ef01e6fa06ef28bb3a7e36df9c01eab25ff65edf4d091bac341e6bf0112e.

    The third output script is an OP_RETURN script. It serves two purposes. Firstly, it marks the
  output as unspendable, which allows the bitcoin nodes to forget it. Secondly, it also allows to
  attach some data to a transaction that can be used for other protocols, in this case the Omni
  protocol. The attached data is the hex number 6f6d6e69000000000000001f000000002b752ee0. The first
  part 6f6d6e69 is ASCII for “omni” and marks this as a transaction following the Omni protocol.
  Then comes the type of transaction 00000000 which stands for “simple send”. There are other
  transaction types to create new currencies, mint new coins, trade coins, etc, which we will not
  look into. The next part 0000001f is hex for 31, which is the code for TetherUS. The final part
  000000002b752ee0 is hex for 729100000, which is the amount of Tether (measured in microcents),
  so it stands for 7.29100000 USDT.

  https://github.com/OmniLayer/spec#transfer-coins-simple-send
 */
public class OmniTransactionVersion {
  public static int TV_TRANSFERCOINS = 0;
}
