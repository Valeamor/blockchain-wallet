package fun.txy.btc;


import fun.txy.utils.MyByte;

public class BtcOperation extends BtcBaseData {
  private final byte code;
  private final byte[] data;

  private BtcOperation(byte code, byte[] data) {
    this.code = code;
    this.data = data;
  }

  public byte getCode() {
    return code;
  }

  public byte[] getData() {
    return data;
  }

  @Override
  byte[] createRaw() {
    MyByte.BuildList builder = MyByte.builder().copy(code);
    if (data != null) {
      builder.copy(data);
    }
    return builder.getData();
  }

  public static BtcOperation create(byte code, byte[] data) {
    return new BtcOperation(code, data);
  }


  // 自定义code，让代码更好理解
  public static final byte OP_PUSH_HASH160 = 0x14;
  public static final byte OP_PUSH_PUBLIC_KEY_COMPRESSED = 0x21;
  public static final byte OP_PUSH_PUBLIC_KEY = 0x41;
  public static final byte OP_PUSH_OMNI_TRANSFER_COINS = 0x14;

  public static final byte CXC_ASSET_MARK = 0x1c;

  // Segregated Witness
  public static final byte OP_SEG_WIT_VER_0 = 0x00;

  // *** Constants ***

  // OP_0 An empty array of bytes is pushed onto the stack. (This is not a no-op: an item is added to the stack.)
  public static final byte OP_0 = 0;
  public static final byte OP_FALSE = 0;

  // N/A	1-75	0x01-0x4b	(special)	data	The next opcode bytes is data to be pushed onto the stack

  // The next byte contains the number of bytes to be pushed onto the stack.
  public static final byte OP_PUSHDATA1 = 0x4c;

  // The next two bytes contain the number of bytes to be pushed onto the stack in little endian order.
  public static final byte OP_PUSHDATA2 = 0x4d;

  // The next four bytes contain the number of bytes to be pushed onto the stack in little endian order.
  public static final byte OP_PUSHDATA4 = 0x4e;

  // The number -1 is pushed onto the stack.
  public static final byte OP_1NEGATE = 0x4f;

  // The number 1 is pushed onto the stack.
  public static final byte OP_1 = 0x51;
  public static final byte OP_TRUE = 0x51;

  // OP_2-OP_16	82-96	0x52-0x60	Nothing.	2-16	The number in the word name (2-16) is pushed onto the stack.

  // *** Flow control ***

  // Does nothing.
  public static final byte OP_NOP = 0x61;

  // If the top stack value is not False, the statements are executed. The top stack value is removed.
  public static final byte OP_IF = 0x63;

  // If the top stack value is False, the statements are executed. The top stack value is removed.
  public static final byte OP_NOTIF = 0x64;

  /*
   If the preceding OP_IF or OP_NOTIF or OP_ELSE was not executed then these statements are and
   if the preceding OP_IF or OP_NOTIF or OP_ELSE was executed then these statements are not.
   */
  public static final byte OP_ELSE = 0x67;

  // Ends an if/else block. All blocks must end, or the transaction is invalid. An OP_ENDIF without
  // OP_IF earlier is also invalid.
  public static final byte OP_ENDIF = 0x68;

  // Marks transaction as invalid if top stack value is not true. The top stack value is removed.
  public static final byte OP_VERIFY = 0x69;


  /*
       Marks transaction as invalid. Since bitcoin 0.9, a standard way of attaching extra data to
   transactions is to add a zero-value output with a scriptPubKey consisting of OP_RETURN followed
   by data. Such outputs are provably unspendable and specially discarded from storage in the UTXO
   set, reducing their cost to the network. Since 0.12, standard relay rules allow a single output
   with OP_RETURN, that contains any sequence of push statements (or OP_RESERVED[1]) after the
   OP_RETURN provided the total scriptPubKey length is at most 83 bytes.
   */
  public static final byte OP_RETURN = 0x6a;

  // *** Stack ***

  // Puts the input onto the top of the alt stack. Removes it from the main stack.
  public static final byte OP_TOALTSTACK = 0x6b;

  // Puts the input onto the top of the main stack. Removes it from the alt stack.
  public static final byte OP_FROMALTSTACK = 0x6c;

  // Removes the top two stack items.
  public static final byte OP_2DROP = 0x6d;

  // Duplicates the top two stack items.
  public static final byte OP_2DUP = 0x6e;

  // Duplicates the top three stack items.
  public static final byte OP_3DUP = 0x6f;

  // Copies the pair of items two spaces back in the stack to the front.
  public static final byte OP_2OVER = 0x70;

  // The fifth and sixth items back are moved to the top of the stack.
  public static final byte OP_2ROT = 0x71;

  // Swaps the top two pairs of items.
  public static final byte OP_2SWAP = 0x72;

  // If the top stack value is not 0, duplicate it.
  public static final byte OP_IFDUP = 0x73;

  // Puts the number of stack items onto the stack.
  public static final byte OP_DEPTH = 0x74;

  // Removes the top stack item.
  public static final byte OP_DROP = 0x75;

  // Duplicates the top stack item.
  public static final byte OP_DUP = 0x76;

  // Removes the second-to-top stack item.
  public static final byte OP_NIP = 0x77;

  // Copies the second-to-top stack item to the top.
  public static final byte OP_OVER = 0x78;

  // The item n back in the stack is copied to the top.
  public static final byte OP_PICK = 0x79;

  // The item n back in the stack is moved to the top.
  public static final byte OP_ROLL = 0x7a;

  // The top three items on the stack are rotated to the left.
  public static final byte OP_ROT = 0x7b;

  // The top two items on the stack are swapped.
  public static final byte OP_SWAP = 0x7c;

  // The item at the top of the stack is copied and inserted before the second-to-top item.
  public static final byte OP_TUCK = 0x7d;

  // *** Splice ***

  // Concatenates two strings. disabled.
  @Deprecated
  public static final byte OP_CAT = 0x7e;

  // Returns a section of a string. disabled.
  @Deprecated
  public static final byte OP_SUBSTR = 0x7f;

  // Keeps only characters left of the specified point in a string. disabled.
  @Deprecated
  public static final byte OP_LEFT = (byte) 0x80;

  // Keeps only characters right of the specified point in a string. disabled.
  @Deprecated
  public static final byte OP_RIGHT = (byte) 0x81;

  // Pushes the string length of the top element of the stack (without popping it).
  public static final byte OP_SIZE = (byte) 0x82;

  // *** Bitwise logic ***

  // Flips all of the bits in the input. disabled.
  @Deprecated
  public static final byte OP_INVERT = (byte) 0x83;

  // Boolean and between each bit in the inputs. disabled.
  @Deprecated
  public static final byte OP_AND = (byte) 0x84;

  // Boolean or between each bit in the inputs. disabled.
  @Deprecated
  public static final byte OP_OR = (byte) 0x85;

  // Boolean exclusive or between each bit in the inputs. disabled.
  @Deprecated
  public static final byte OP_XOR = (byte) 0x86;

  // Returns 1 if the inputs are exactly equal, 0 otherwise.
  public static final byte OP_EQUAL = (byte) 0x87;

  // Same as OP_EQUAL, but runs OP_VERIFY afterward.
  public static final byte OP_EQUALVERIFY = (byte) 0x88;

  // *** Arithmetic ***

  // 1 is added to the input.
  public static final byte OP_1ADD = (byte) 0x8b;

  // 1 is subtracted from the input.
  public static final byte OP_1SUB = (byte) 0x8c;

  // The input is multiplied by 2. disabled.
  @Deprecated
  public static final byte OP_2MUL = (byte) 0x8d;

  // The input is divided by 2. disabled.
  public static final byte OP_2DIV = (byte) 0x8e;

  // The sign of the input is flipped.
  public static final byte OP_NEGATE = (byte) 0x8f;

  // The input is made positive.
  public static final byte OP_ABS = (byte) 0x90;

  // If the input is 0 or 1, it is flipped. Otherwise the output will be 0.
  public static final byte OP_NOT = (byte) 0x91;

  // Returns 0 if the input is 0. 1 otherwise.
  public static final byte OP_0NOTEQUAL = (byte) 0x92;

  // a is added to b.
  public static final byte OP_ADD = (byte) 0x93;

  // b is subtracted from a.
  public static final byte OP_SUB = (byte) 0x94;

  // a is multiplied by b. disabled.
  @Deprecated
  public static final byte OP_MUL = (byte) 0x95;

  // a is divided by b. disabled.
  @Deprecated
  public static final byte OP_DIV = (byte) 0x96;

  // Returns the remainder after dividing a by b. disabled.
  @Deprecated
  public static final byte OP_MOD = (byte) 0x97;

  // Shifts a left b bits, preserving sign. disabled.
  @Deprecated
  public static final byte OP_LSHIFT = (byte) 0x98;

  // Shifts a right b bits, preserving sign. disabled.
  @Deprecated
  public static final byte OP_RSHIFT = (byte) 0x99;

  // If both a and b are not "" (null string), the output is 1. Otherwise 0.
  public static final byte OP_BOOLAND = (byte) 0x9a;

  // If a or b is not "" (null string), the output is 1. Otherwise 0.
  public static final byte OP_BOOLOR = (byte) 0x9b;

  // Returns 1 if the numbers are equal, 0 otherwise.
  public static final byte OP_NUMEQUAL = (byte) 0x9c;

  // Same as OP_NUMEQUAL, but runs OP_VERIFY afterward.
  public static final byte OP_NUMEQUALVERIFY = (byte) 0x9d;

  // Returns 1 if the numbers are not equal, 0 otherwise.
  public static final byte OP_NUMNOTEQUAL = (byte) 0x9e;

  // Returns 1 if a is less than b, 0 otherwise.
  public static final byte OP_LESSTHAN = (byte) 0x9f;

  // Returns 1 if a is greater than b, 0 otherwise.
  public static final byte OP_GREATERTHAN = (byte) 0xa0;

  // Returns 1 if a is less than or equal to b, 0 otherwise.
  public static final byte OP_LESSTHANOREQUAL = (byte) 0xa1;

  // Returns 1 if a is greater than or equal to b, 0 otherwise.
  public static final byte OP_GREATERTHANOREQUAL = (byte) 0xa2;

  // Returns the smaller of a and b.
  public static final byte OP_MIN = (byte) 0xa3;

  // Returns the larger of a and b.
  public static final byte OP_MAX = (byte) 0xa4;

  // Returns 1 if x is within the specified range (left-inclusive), 0 otherwise.
  public static final byte OP_WITHIN = (byte) 0xa5;

  // *** Crypto ***

  // The input is hashed using RIPEMD-160.
  public static final byte OP_RIPEMD160 = (byte) 0xa6;

  // The input is hashed using SHA-1.
  public static final byte OP_SHA1 = (byte) 0xa7;

  // The input is hashed using SHA-256.
  public static final byte OP_SHA256 = (byte) 0xa8;

  // The input is hashed twice: first with SHA-256 and then with RIPEMD-160.
  public static final byte OP_HASH160 = (byte) 0xa9;

  // The input is hashed two times with SHA-256.
  public static final byte OP_HASH256 = (byte) 0xaa;

  /*
   All of the signature checking words will only match signatures to the data after the most
   recently-executed OP_CODESEPARATOR.
    */
  public static final byte OP_CODESEPARATOR = (byte) 0xab;

  /*
  The entire transaction's outputs, inputs, and script (from the most recently-executed
  OP_CODESEPARATOR to the end) are hashed. The signature used by OP_CHECKSIG must be a valid
  signature for this hash and public key. If it is, 1 is returned, 0 otherwise.
    */
  public static final byte OP_CHECKSIG = (byte) 0xac;

  // Same as OP_CHECKSIG, but OP_VERIFY is executed afterward.
  public static final byte OP_CHECKSIGVERIFY = (byte) 0xad;

  /*
  input: x sig1 sig2 ... <number of signatures> pub1 pub2 <number of public keys>

  Compares the first signature against each public key until it finds an ECDSA match. Starting with
  the subsequent public key, it compares the second signature against each remaining public key
  until it finds an ECDSA match. The process is repeated until all signatures have been checked or
  not enough public keys remain to produce a successful result. All signatures need to match a
  public key. Because public keys are not checked again if they fail any signature comparison,
  signatures must be placed in the scriptSig using the same order as their corresponding public keys
  were placed in the scriptPubKey or redeemScript. If all signatures are valid, 1 is returned, 0
  otherwise. Due to a bug, one extra unused value is removed from the stack.
   */
  public static final byte OP_CHECKMULTISIG = (byte) 0xae;

  // input: x sig1 sig2 ... <number of signatures> pub1 pub2 ... <number of public keys>
  // Same as OP_CHECKMULTISIG, but OP_VERIFY is executed afterward.
  public static final byte OP_CHECKMULTISIGVERIFY = (byte) 0xaf;

  // *** Locktime ***

  /*
  Marks transaction as invalid if the top stack item is greater than the transaction's nLockTime
  field, otherwise script evaluation continues as though an OP_NOP was executed. Transaction is also
  invalid if 1. the stack is empty; or 2. the top stack item is negative; or 3. the top stack item
  is greater than or equal to 500000000 while the transaction's nLockTime field is less than
  500000000, or vice versa; or 4. the input's nSequence field is equal to 0xffffffff. The precise
  semantics are described in BIP 0065.
   */
  public static final byte OP_CHECKLOCKTIMEVERIFY = (byte) 0xb1;

  /*
  Marks transaction as invalid if the relative lock time of the input (enforced by BIP 0068 with
  nSequence) is not equal to or longer than the value of the top stack item. The precise semantics
  are described in BIP 0112.
   */
  public static final byte OP_CHECKSEQUENCEVERIFY = (byte) 0xb2;

  // *** Pseudo-words ***

  // Represents a public key hashed with OP_HASH160.
  public static final byte OP_PUBKEYHASH = (byte) 0xfd;

  // Represents a public key compatible with OP_CHECKSIG.
  public static final byte OP_PUBKEY = (byte) 0xfe;

  // Matches any opcode that is not yet assigned.
  public static final byte OP_INVALIDOPCODE = (byte) 0xff;


  // *** Reserved words ***

  // Transaction is invalid unless occuring in an unexecuted OP_IF branch
  public static final byte OP_RESERVED = (byte) 0x50;
  public static final byte OP_VER = (byte) 0x62;

  // Transaction is invalid even when occuring in an unexecuted OP_IF branch
  public static final byte OP_VERIF = (byte) 0x65;
  public static final byte OP_VERNOTIF = (byte) 0x66;

  // Transaction is invalid unless occuring in an unexecuted OP_IF branch
  public static final byte OP_RESERVED1 = (byte) 0x89;
  public static final byte OP_RESERVED2 = (byte) 0x8a;

  // OP_NOP1, OP_NOP4-OP_NOP10	176, 179-185	0xb0, 0xb3-0xb9	The word is ignored. Does not mark transaction as invalid.
}
