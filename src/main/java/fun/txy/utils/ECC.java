package fun.txy.utils;


import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERSequenceGenerator;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9IntegerConverter;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

import javax.crypto.KeyAgreement;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import static fun.txy.utils.MyByte.to32bytes;

public class ECC {
  private static final ECDomainParameters CURVE;
  private static final ECParameterSpec EC_PARAMS_SPEC;
  private static final SecureRandom SECURE_RANDOM;
  private static final KeyFactory KEY_FACTORY;
  private static final KeyAgreement KEY_AGREEMENT;
  private static final KeyPairGenerator KEY_PAIR_GENERATOR;
  private static final BigInteger HALF_CURVE_ORDER;

  static {
    String ECDSAParam = "secp256k1";
    String ECDSAType = "EC";
    String agreement = "ECDH";
    Provider provider = new BouncyCastleProvider();
    X9ECParameters params = SECNamedCurves.getByName(ECDSAParam);

    CURVE = new ECDomainParameters(params.getCurve(), params.getG(), params.getN(), params.getH());
    EC_PARAMS_SPEC = ECNamedCurveTable.getParameterSpec(ECDSAParam);
    SECURE_RANDOM = new SecureRandom();
    HALF_CURVE_ORDER = params.getN().shiftRight(1);
    try {
      KEY_FACTORY = KeyFactory.getInstance(ECDSAType, provider);
      KEY_PAIR_GENERATOR = KeyPairGenerator.getInstance(ECDSAType, provider);
      KEY_PAIR_GENERATOR.initialize(new ECGenParameterSpec(ECDSAParam), SECURE_RANDOM);
      KEY_AGREEMENT = KeyAgreement.getInstance(agreement, provider);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static byte[] signCompact(EccPrivateKey prv, byte[] data) {
    BigInteger[] sign = signCore(prv.getD(), data);
    return MyByte.builder()
        .copy((byte) (generateSignV(prv.getPublicKey(false), data, sign[0], sign[1]) + 31))
        .copy(to32bytes(sign[0].toByteArray()))
        .copy(to32bytes(sign[1].toByteArray()))
        .getData();
  }


  // 白币、eos均使用此方法进行签名
  public static byte[] eosSign(EccPrivateKey prv, byte[] data) {
    int nonce = 0;
    while (nonce <= 10_000) {
      BigInteger[] sign = deterministicGenerateK(data, prv, nonce);
      BigInteger sb = cut(sign[1]);
      byte[] r = sign[0].toByteArray();
      byte[] s = sb.toByteArray();
      if (r.length == 32 && s.length == 32) {
        return MyByte.builder()
            .copy((byte) (generateSignV(prv.getPublicKey(false), data, sign[0], sb) + 31))
            .copy(r)
            .copy(s)
            .getData();
      }
      ++nonce;
    }
    throw new RuntimeException("eosSign fail to create signature");
  }

  public static BigInteger cut(BigInteger s) {
    if (s.compareTo(HALF_CURVE_ORDER) > 0) {
      s = CURVE.getN().subtract(s);
    }
    return s;
  }

  public static byte[] sign(BigInteger prv, byte[] data) {
    BigInteger[] sign = signCore(prv, data);
    BigInteger r = sign[0];
    BigInteger s = sign[1];
    try {
      ByteArrayOutputStream bAOS = new ByteArrayOutputStream();
      DERSequenceGenerator derGen = new DERSequenceGenerator(bAOS);
      derGen.addObject(new ASN1Integer(r));
      derGen.addObject(new ASN1Integer(s));
      derGen.close();
      return bAOS.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static BigInteger[] signCore(BigInteger prv, byte[] data) {
    synchronized (CURVE) {
      ECDSASigner signer = new ECDSASigner();
      signer.init(true, new ParametersWithRandom(new ECPrivateKeyParameters(prv, CURVE), SECURE_RANDOM));
      return signer.generateSignature(data);
    }
  }

  public static BigInteger[] ethSignCore(BigInteger prv, byte[] data) {
    synchronized (CURVE) {
      ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));
      signer.init(true, new ParametersWithRandom(new ECPrivateKeyParameters(prv, CURVE), SECURE_RANDOM));
      return signer.generateSignature(data);
    }
  }

  public static byte[] btcSignCore(BigInteger prv, byte[] data) {
    synchronized (CURVE) {
      ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));
      signer.init(true, new ECPrivateKeyParameters(prv, CURVE));
      BigInteger[] rs = signer.generateSignature(data);
      try {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(72);
        DERSequenceGenerator seq = new DERSequenceGenerator(bos);
        seq.addObject(new ASN1Integer(rs[0]));
        seq.addObject(new ASN1Integer(cut(rs[1])));
        seq.close();
        return bos.toByteArray();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public boolean verify(byte[] pub, byte[] data, byte[] signature) {
    try {
      ASN1InputStream decoder = new ASN1InputStream(signature);
      DLSequence seq = (DLSequence) decoder.readObject();
      ASN1Integer r = (ASN1Integer) seq.getObjectAt(0);
      ASN1Integer s = (ASN1Integer) seq.getObjectAt(1);
      decoder.close();
      return _verifyCore(data, pub, r.getValue(), s.getValue());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static boolean _verifyCore(byte[] data, byte[] pub, BigInteger... sign) {
    synchronized (CURVE) {
      ECDSASigner signer = new ECDSASigner();
      ECPublicKeyParameters params = new ECPublicKeyParameters(
          CURVE.getCurve().decodePoint(pub), CURVE);
      signer.init(false, params);
      return signer.verifySignature(data, sign[0], sign[1]);
    }
  }

  public static ECPublicKey loadPublicKey(byte[] data) {
    synchronized (EC_PARAMS_SPEC) {
      try {
        return (ECPublicKey) KEY_FACTORY.generatePublic(
            new ECPublicKeySpec(EC_PARAMS_SPEC.getCurve().decodePoint(data), EC_PARAMS_SPEC));
      } catch (InvalidKeySpecException e) {
        throw new RuntimeException(e);
      }
      /*new X509EncodedKeySpec(data)*/
    }
  }

  public static ECPrivateKey loadPrivateKey(byte[] data) {
    synchronized (EC_PARAMS_SPEC) {
      try {
        return (ECPrivateKey) KEY_FACTORY.generatePrivate(
            new ECPrivateKeySpec(new BigInteger(data), EC_PARAMS_SPEC));
      } catch (InvalidKeySpecException e) {
        throw new RuntimeException(e);
      }
      /*new PKCS8EncodedKeySpec(data)*/
    }
  }

  public static byte[] calculatePublicKey(BigInteger prv, boolean compressed) {
    synchronized (CURVE) {
      return CURVE.getG().multiply(prv).getEncoded(compressed);
    }
  }

  public static byte[] generateSharedSecret(PrivateKey privateKey, PublicKey publicKey) {
    synchronized (KEY_AGREEMENT) {
      try {
        KEY_AGREEMENT.init(privateKey);
        KEY_AGREEMENT.doPhase(publicKey, true);
      } catch (InvalidKeyException e) {
        throw new RuntimeException(e);
      }
      return KEY_AGREEMENT.generateSecret();
    }
  }

  public static KeyPair generate() {
    synchronized (KEY_PAIR_GENERATOR) {
      return KEY_PAIR_GENERATOR.generateKeyPair();
    }
  }

  public static int generateSignV(byte[] pub, byte[] data, BigInteger r, BigInteger s) {
    for (int i = 0; i < 4; i++) {
      byte[] k = recoverPubBytesFromSignature(i, data, r, s);
      if (k != null && Arrays.equals(k, pub)) {
        return i;
      }
    }
    throw new RuntimeException("Could not construct a recoverable key. This should never happen.");
  }

  public static byte[] recoverPubBytesFromSignature(int recId, byte[] data, BigInteger r, BigInteger s) {
    synchronized (CURVE) {
      BigInteger n = CURVE.getN();
      BigInteger i = BigInteger.valueOf(recId / 2);
      BigInteger x = r.add(i.multiply(n));
      ECCurve.Fp curve = (ECCurve.Fp) CURVE.getCurve();
      BigInteger prime = curve.getQ();
      if (x.compareTo(prime) >= 0) {
        return null;
      }
      ECPoint R = decompressKey(x, (recId & 1) == 1);
      if (!R.multiply(n).isInfinity()) {
        return null;
      }
      BigInteger e = new BigInteger(1, data);
      BigInteger eInv = e.negate().mod(n);
      BigInteger rInv = r.modInverse(n);
      BigInteger srInv = rInv.multiply(s).mod(n);
      BigInteger eInvRInv = rInv.multiply(eInv).mod(n);
      ECPoint.Fp q = (ECPoint.Fp) ECAlgorithms.sumOfTwoMultiplies(CURVE.getG(), eInvRInv, R, srInv);
      if (q.isInfinity()) {
        return null;
      }
      return q.getEncoded(false);
    }
  }

  private static ECPoint decompressKey(BigInteger xBN, boolean yBit) {
    synchronized (CURVE) {
      X9IntegerConverter x9 = new X9IntegerConverter();
      byte[] compEnc = x9.integerToBytes(xBN, 1 + x9.getByteLength(CURVE.getCurve()));
      compEnc[0] = (byte) (yBit ? 0x03 : 0x02);
      return CURVE.getCurve().decodePoint(compEnc);
    }
  }

  public static byte[] decompressKey(byte[] publicKey) {
    return decompressKey(new BigInteger(MyByte.copyBytes(publicKey, 1, 32)), publicKey[0] == 3)
        .getEncoded(false);
  }

  public static byte[] compressPublicKey(byte[] publicKey) {
    if (publicKey == null || publicKey.length != 65) {
      throw new IllegalArgumentException("uncompressed public key length should be 65");
    }
    return MyByte.builder()
        .copy((byte) ((publicKey[64] & 1) == 1 ? 3 : 2))
        .copy(publicKey, 1, 32)
        .getData();
  }

  // 我也不晓得这是在搞什么飞机
  private static BigInteger[] deterministicGenerateK(byte[] data, EccPrivateKey pk, int nonce) {
    byte[] hash = nonce == 0 ? data : SHA._256hash(MyByte.concat(data, new byte[nonce]));
    byte[] x = pk.getEncoded();
    byte[] k = new byte[32];
    byte[] v = new byte[32];
    Arrays.fill(k, (byte) 0);
    Arrays.fill(v, (byte) 1);
    k = SHA.hmacSha256(MyByte.concat(v, new byte[]{0}, x, hash), k); // Step D
    v = SHA.hmacSha256(v, k); // Step E
    k = SHA.hmacSha256(MyByte.concat(v, new byte[]{1}, x, hash), k); // Step F
    v = SHA.hmacSha256(v, k); // Step G
    v = SHA.hmacSha256(v, k); // Step H2B
    BigInteger T = new BigInteger(MyByte.toHex(v), 16);
    BigInteger e = new BigInteger(MyByte.toHex(data), 16);
    BigInteger[] rs = new BigInteger[3];
    Boolean check = checkSig(T, pk.getD(), e, rs);
    while (T.signum() <= 0 || T.compareTo(CURVE.getN()) >= 0 || !check) {
      k = SHA.hmacSha256(MyByte.concat(v, new byte[]{0}), k);
      v = SHA.hmacSha256(v, k);
      v = SHA.hmacSha256(v, k);
      T = new BigInteger(v);  // Step H2B again
    }
    return rs;
  }

  private static Boolean checkSig(BigInteger k, BigInteger d, BigInteger e, BigInteger[] rs) {
    ECPoint q = CURVE.getG().multiply(k).normalize();
    if (q.isInfinity()) {
      return false;
    }
    BigInteger r = q.getXCoord().toBigInteger().mod(CURVE.getN());
    rs[0] = r;
    if (r.signum() == 0) {
      return false;
    }
    BigInteger s = k.modInverse(CURVE.getN()).multiply(e.add(d.multiply(r))).mod(CURVE.getN());
    rs[1] = s;
    return s.signum() != 0;
  }
}