package com.brainydroid.daydreaming.network;

import android.util.Log;
import com.brainydroid.daydreaming.ui.Config;
import com.google.inject.Singleton;
import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.util.encoders.Base64;
import org.spongycastle.util.encoders.UrlBase64;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Enumeration;

@Singleton
public class Crypto {

    private static String TAG = "Crypto";

    private static final String PROVIDER = "SC";
    private static final String KEYGEN_ALG = "ECDSA";
    private static final String SIGN_ALG = "SHA256withECDSA";

    private static final String BEGIN_KEY_BLOCK = "-----BEGIN PUBLIC KEY-----";
    private static final String END_KEY_BLOCK = "-----END PUBLIC KEY-----";
    private static final int LINEWIDTH = 64;

    private KeyFactory kf;
    private KeyPairGenerator kpg;
    private Signature sg;

    static {
        Security.addProvider(new org.spongycastle.jce.provider.BouncyCastleProvider());
    }

    public Crypto() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] Crypto");
        }

        try {
            kf = KeyFactory.getInstance(KEYGEN_ALG, PROVIDER);
            kpg = KeyPairGenerator.getInstance(KEYGEN_ALG, PROVIDER);
            sg = Signature.getInstance(SIGN_ALG, PROVIDER);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public synchronized Enumeration<String> getAvailableCurveNames() {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getAvailableCurveNames");
        }

        //noinspection unchecked
        return ECNamedCurveTable.getNames();
    }

    public synchronized KeyPair generateKeyPairNamedCurve(String curveName) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] generateKeyPairNamedCurve");
        }

        try {
            ECGenParameterSpec ecParamSpec = new ECGenParameterSpec(curveName);
            kpg.initialize(ecParamSpec);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }

        return kpg.generateKeyPair();
    }

    @SuppressWarnings("UnusedDeclaration")
    public synchronized PublicKey readPublicKey(String keyStr) throws InvalidKeySpecException {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] readPublicKey (from String)");
        }

        X509EncodedKeySpec x509ks = new X509EncodedKeySpec(
                Base64.decode(keyStr));
        return kf.generatePublic(x509ks);
    }

    @SuppressWarnings("UnusedDeclaration")
    public synchronized PublicKey readPublicKey(byte[] key) throws InvalidKeySpecException {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] readPublicKey (from byte[])");
        }

        X509EncodedKeySpec x509ks = new X509EncodedKeySpec(key);
        return kf.generatePublic(x509ks);
    }

    public synchronized PrivateKey readPrivateKey(String keyStr) throws InvalidKeySpecException {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] readPrivateKey (from String)");
        }

        PKCS8EncodedKeySpec p8ks = new PKCS8EncodedKeySpec(
                Base64.decode(keyStr));
        return kf.generatePrivate(p8ks);
    }

    public synchronized PrivateKey readPrivateKey(byte[] key) throws InvalidKeySpecException {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] readPrivateKey (from byte[])");
        }

        PKCS8EncodedKeySpec p8ks = new PKCS8EncodedKeySpec(key);
        return kf.generatePrivate(p8ks);
    }

    @SuppressWarnings("UnusedDeclaration")
    public synchronized KeyPair readKeyPair(String pubKeyStr, String privKeyStr) throws InvalidKeySpecException {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] readKeyPair (from String, String)");
        }

        return new KeyPair(readPublicKey(pubKeyStr), readPrivateKey(privKeyStr));
    }

    @SuppressWarnings("UnusedDeclaration")
    public synchronized KeyPair readKeyPair(byte[] pubKey, byte[] privKey) throws InvalidKeySpecException {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] readKeyPair (from byte[], byte[])");
        }

        return new KeyPair(readPublicKey(pubKey), readPrivateKey(privKey));
    }

    public synchronized byte[] sign(PrivateKey privateKey, byte[] data)
            throws InvalidKeyException {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] sign");
        }

        try {
            sg.initSign(privateKey);
            sg.update(data);
            return sg.sign();
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        }
    }

    private static String wrapString(String str, int lineWidth) {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] wrapString");
        }

        if (str.length() <= lineWidth) {
            return str;
        } else {
            return str.substring(0, lineWidth) + "\n" + wrapString(str.substring(lineWidth), lineWidth);
        }
    }

    private static String formatKeyString(String keyString) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] formatKeyString");
        }

        return BEGIN_KEY_BLOCK + "\n" + wrapString(keyString, LINEWIDTH) + "\n" + END_KEY_BLOCK + "\n";
    }

    public static String armorPublicKey(PublicKey publicKey) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] armorPublicKey");
        }

        return formatKeyString(base64Encode(publicKey.getEncoded()));
    }

    public static String base64Encode(byte[] b) {

        // Verbose
        if (Config.LOGV) {
            Log.v(TAG, "[fn] base64Encode");
        }

        try {
            return new String(Base64.encode(b), "ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String base64urlEncode(byte[] data) {

        // Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] base64urlEncode");
        }

        String padded_b64url = new String(UrlBase64.encode(data));

        // Remove padding
        return padded_b64url.replace(".", "");
    }

}
