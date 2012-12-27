package com.brainydroid.daydreaming.network;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Enumeration;
import java.util.HashMap;

import org.spongycastle.util.encoders.UrlBase64;

import android.content.Context;
import android.util.Log;

import com.brainydroid.daydreaming.ui.Config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CryptoStorage {

	private static String TAG = "CryptoStorage";

	private static final String MAI_ID_FILENAME = "mai_id";
	private static final String PUBLIC_FILENAME = "key.pub";
	private static final String PRIVATE_FILENAME = "key";
	private static final String STORAGE_DIRNAME = "cryptoStorage";

	private static final String NOKP_EXCEPTION_MSG = "No keypair present";

	private static final String DEFAULT_CURVE_NAME = "secp256r1"; // Corresponds to NIST256p in python-ecdsa;
	private static final String JWS_HEADER = "{\"alg\": \"ES256\"}";

	private static Crypto crypto;
	private static ServerTalker serverTalker;
	private static CryptoStorage csInstance;
	private final Gson gson;

	private final File maiIdFile;
	private final File publicFile;
	private final File privateFile;
	private final File storageDir;
	private String curveName;
	private final Context _context;

	public static synchronized CryptoStorage getInstance(Context context, String server,
			CryptoStorageCallback callback) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getInstance");
		}

		if (csInstance == null) {
			csInstance = new CryptoStorage(context, server, callback);
		} else {
			csInstance.initCrypto(callback);
		}

		return csInstance;
	}

	private CryptoStorage(Context context, String server, CryptoStorageCallback callback) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] CryptoStorage");
		}

		crypto = Crypto.getInstance();
		serverTalker = ServerTalker.getInstance(server, this);
		_context = context.getApplicationContext();
		storageDir = _context.getDir(STORAGE_DIRNAME, Context.MODE_PRIVATE);
		gson = new GsonBuilder()
		.excludeFieldsWithoutExposeAnnotation()
		.create();
		maiIdFile = new File(storageDir, MAI_ID_FILENAME);
		publicFile = new File(storageDir, PUBLIC_FILENAME);
		privateFile = new File(storageDir, PRIVATE_FILENAME);
		curveName = DEFAULT_CURVE_NAME;

		initCrypto(callback);
	}

	private void initCrypto(CryptoStorageCallback callback) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] initCrypto");
		}

		if (!hasStoredKeyPairAndMaiId()) {
			register(callback);
		} else {
			callback.onCryptoStorageReady(true);
		}
	}

	public synchronized void setServerName(String s) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] setServerName");
		}

		serverTalker.setServerName(s);
	}

	public synchronized void signAndUploadData(String ea_id, String data,
			HttpConversationCallback callback) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] signeAndUploadData");
		}

		serverTalker.signAndUploadData(ea_id, data, callback);
	}

	private synchronized boolean hasStoredMaiId() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] hasStoredMaiId");
		}

		return maiIdFile.exists();
	}

	private synchronized boolean hasStoredPrivateKey() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] hasStoredPrivateKey");
		}

		return privateFile.exists();
	}

	private synchronized boolean hasStoredPublicKey() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] hasStoredPublicKey");
		}

		return publicFile.exists();
	}

	public synchronized Enumeration<String> getAvailableCurveNames() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getAvailableCurveNames");
		}

		return crypto.getAvailableCurveNames();
	}

	public synchronized void setCurveName(String s) {

		// Verbose
		if (Config.LOGV) {
			Log.v(TAG, "[fn] setCurveName");
		}

		curveName = s;
	}

	public synchronized boolean hasStoredKeyPairAndMaiId() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] hasStoredKeyPairAndMaiId");
		}

		if (hasStoredPrivateKey() && hasStoredPublicKey() && hasStoredMaiId()) {
			return true;
		} else {
			clearStore();
			return false;
		}
	}

	private synchronized byte[] sign(byte[] data) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] sign");
		}

		if (! hasStoredKeyPairAndMaiId()) {
			throw new RuntimeException(NOKP_EXCEPTION_MSG);
		}
		try {
			return crypto.sign(getPrivateKey(), data);
		} catch (InvalidKeyException e) {
			throw new RuntimeException(e);
		}
	}

	public synchronized void register(CryptoStorageCallback callback) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] register");
		}

		final KeyPair kp = crypto.generateKeyPairNamedCurve(curveName);
		final CryptoStorageCallback parentCallback = callback;

		final HttpConversationCallback registrationCallback = new HttpConversationCallback() {

			private final String TAG = "HttpConversationCallback";

			@Override
			public void onHttpConversationFinished(boolean success, String serverAnswer) {

				// Debug
				if (Config.LOGD) {
					Log.d(TAG, "[fn] (registrationCallback) onHttpConversationFinished");
				}

				boolean storageSuccess = false;
				if (success) {
					RegistrationAnswer registrationAnswer = gson.fromJson(serverAnswer,
							RegistrationAnswer.class);
					String maiId = registrationAnswer.getId();
					storageSuccess = storeKeyPairAndMaiId(kp, maiId);
				}

				parentCallback.onCryptoStorageReady(success && storageSuccess);
			}

		};

		serverTalker.register(kp.getPublic(), registrationCallback);
	}

	private synchronized boolean storeKeyPairAndMaiId(KeyPair kp, String maiId) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] storeKeyPairAndMaiId");
		}

		try {
			clearStore();

			maiIdFile.createNewFile();
			BufferedWriter maiIdBuf = new BufferedWriter(new FileWriter(maiIdFile));
			maiIdBuf.write(maiId);
			maiIdBuf.close();

			publicFile.createNewFile();
			BufferedWriter publicBuf = new BufferedWriter(new FileWriter(publicFile));
			publicBuf.write(Crypto.base64Encode(kp.getPublic().getEncoded()));
			publicBuf.close();

			privateFile.createNewFile();
			BufferedWriter privateBuf = new BufferedWriter(new FileWriter(privateFile));
			privateBuf.write(Crypto.base64Encode(kp.getPrivate().getEncoded()));
			privateBuf.close();

			return true;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public synchronized KeyPair getKeyPair() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getKeyPair");
		}

		return new KeyPair(getPublicKey(), getPrivateKey());
	}

	public synchronized String getMaiId() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getMaiId");
		}

		try {
			BufferedReader buf;
			buf = new BufferedReader(new FileReader(maiIdFile));
			String maiId = buf.readLine();
			buf.close();
			return maiId;
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public synchronized PrivateKey getPrivateKey() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getPrivateKey");
		}

		try {
			BufferedReader buf;
			buf = new BufferedReader(new FileReader(privateFile));
			String keyStr = buf.readLine();
			buf.close();
			return crypto.readPrivateKey(keyStr);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InvalidKeySpecException e) {
			throw new RuntimeException(e);
		}
	}

	public synchronized PublicKey getPublicKey() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getPublicKey");
		}

		try {
			BufferedReader buf;
			buf = new BufferedReader(new FileReader(publicFile));
			String keyStr = buf.readLine();
			buf.close();
			return crypto.readPublicKey(keyStr);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InvalidKeySpecException e) {
			throw new RuntimeException(e);
		}
	}

	public synchronized boolean clearStore() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] clearStore");
		}

		return clearPrivateKey() && clearPublicKey() && clearMaiId();
	}

	private synchronized boolean clearMaiId() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] clearMaiId");
		}

		return maiIdFile.delete();
	}

	private synchronized boolean clearPrivateKey() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] clearPrivateKey");
		}

		return privateFile.delete();
	}

	private synchronized boolean clearPublicKey() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] clearPublicKey");
		}

		return publicFile.delete();
	}

	public synchronized String createArmoredPublicKeyJson(PublicKey publicKey) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] createArmoredPublicKeyFile");
		}

		HashMap<String, String> pkMap = new HashMap<String, String>();
		pkMap.put("vk_pem", getArmoredPublicKeyString(publicKey));
		return gson.toJson(pkMap);
	}

	public synchronized String getArmoredPublicKeyString(PublicKey publicKey) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getArmoredPublicKeyString");
		}

		return Crypto.armorPublicKey(publicKey);
	}

	public synchronized String getPrivateKeyString() {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] getPrivateKeyString");
		}

		return Crypto.base64Encode(getPrivateKey().getEncoded());
	}

	public synchronized String signJws(String data) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] signJws");
		}

		String b64Header = base64urlEncode(JWS_HEADER.getBytes());
		String b64Payload = base64urlEncode(data.getBytes());

		String b64Input = b64Header + "." + b64Payload;
		String b64sig = base64urlEncode(sign(b64Input.getBytes()));

		return b64Input + "." + b64sig;
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

	public synchronized SignedDataFiles createSignedDataFiles(String data) {

		// Debug
		if (Config.LOGD) {
			Log.d(TAG, "[fn] createSignedDataFiles");
		}

		try {
			byte[] signature = sign(data.getBytes());

			File dataFile = File.createTempFile("data",	".json", storageDir);
			FileWriter dataFileWriter = new FileWriter(dataFile);
			dataFileWriter.write(data);
			dataFileWriter.close();

			File sigFile;
			sigFile = File.createTempFile("data", ".json.sig", storageDir);
			BufferedOutputStream sigOut = new BufferedOutputStream(new FileOutputStream(sigFile));
			sigOut.write(signature);
			sigOut.close();

			SignedDataFiles sdf = new SignedDataFiles(dataFile, sigFile);
			return sdf;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
