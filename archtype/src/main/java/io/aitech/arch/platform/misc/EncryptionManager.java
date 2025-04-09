package io.aitech.arch.platform.misc;

import io.avaje.inject.Component;
import lombok.SneakyThrows;
import lombok.val;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class EncryptionManager {

  /**
   * Initializes the BouncyCastleProvider and sets the unlimited policy for the provider.
   * This is necessary for the signing and verification of the requests.
   */
  public EncryptionManager() {
    Security.addProvider(new BouncyCastleProvider());
    Security.setProperty("crypto.policy", "unlimited");
  }

  @SneakyThrows
  public RSAPrivateKey getRSAPrivateKey(String privateKey) {
    val factory = KeyFactory.getInstance("RSA", "BC");
    val finalPrivateKey = cleanBase64(privateKey);
    val pkcs8 = Base64.getDecoder().decode(finalPrivateKey);
    val privKeySpec = new PKCS8EncodedKeySpec(pkcs8);
    return (RSAPrivateKey) factory.generatePrivate(privKeySpec);
  }

  @SneakyThrows
  public RSAPublicKey getRSAPublicKey(PrivateKey privateKey) {
    val keyFactory = KeyFactory.getInstance("RSA");
    val privateCrtKey = (RSAPrivateCrtKey) privateKey;
    val publicKeySpec = new RSAPublicKeySpec(
      privateCrtKey.getModulus(),
      privateCrtKey.getPublicExponent()
    );
    return (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
  }

  @SneakyThrows
  public RSAPublicKey getRSAPublicKey(String publicKey) {
    val finalPublicKey = cleanBase64(publicKey);
    val factory = KeyFactory.getInstance("RSA", "BC");
    val x509 = base64Decode(finalPublicKey);
    val pubkeySpec = new X509EncodedKeySpec(x509);
    return (RSAPublicKey) factory.generatePublic(pubkeySpec);
  }

  @SneakyThrows
  public String convertPublicKeyToPEM(PublicKey publicKey) {
    val stringWriter = new StringWriter();
    try (val pemWriter = new PemWriter(stringWriter)) {
      pemWriter.writeObject(new PemObject("PUBLIC KEY", publicKey.getEncoded()));
    }
    return stringWriter.toString();
  }

  @SneakyThrows
  public String signRSA256(RSAPrivateKey rsaPrivateKey, String message) {
    val sig = Signature.getInstance("SHA256withRSA", "BC");
    sig.initSign(rsaPrivateKey);
    sig.update(message.getBytes(StandardCharsets.UTF_8));
    val signature = sig.sign();
    return Base64.getEncoder().encodeToString(signature);
  }

  @SneakyThrows
  public byte[] sha256(String msg) {
    return sha256(msg.getBytes(StandardCharsets.UTF_8));
  }

  @SneakyThrows
  public byte[] sha256(byte[] msg) {
    val md = MessageDigest.getInstance("SHA-256", "BC");
    return md.digest(msg);
  }

  public boolean verify(RSAPublicKey publicKey, String message, String signature) {
    try {
      Signature sig = Signature.getInstance("SHA256withRSA", "BC");
      sig.initVerify(publicKey);
      sig.update(message.getBytes(StandardCharsets.UTF_8));
      return sig.verify(base64Decode(signature));
    } catch (Throwable e) {
      return false;
    }
  }


  public String base16Encode(byte[] bytes) {
    return HexFormat.of().formatHex(bytes).toLowerCase(Locale.ROOT);
  }

  public byte[] base64Decode(String base64) {
    return Base64.getDecoder().decode(base64);
  }


  private String cleanBase64(String str) {
    return str.lines().filter(s -> !s.contains("-")).map(s -> {
        val sb = new StringBuilder(str.length());
        val charArray = s.toCharArray();
        for (val c : charArray) {
          if (c != '\n' && c != '\f' && c != '\t' && c != '\b' && c != '\r') {
            sb.append(c);
          }
        }
        return sb.toString();
      })
      .collect(Collectors.joining());
  }

}
