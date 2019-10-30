package jp.co.soramitsu.iroha.android.sample;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.VisibleForTesting;

import java.security.KeyPair;
import java.security.PrivateKey;

import javax.inject.Inject;
import javax.xml.bind.DatatypeConverter;

import jp.co.soramitsu.crypto.ed25519.Ed25519Sha3;
import jp.co.soramitsu.crypto.ed25519.EdDSAPrivateKey;
import jp.co.soramitsu.crypto.ed25519.EdDSAPublicKey;
import jp.co.soramitsu.crypto.ed25519.spec.EdDSANamedCurveTable;
import jp.co.soramitsu.crypto.ed25519.spec.EdDSAPublicKeySpec;
import lombok.Getter;

public class PreferencesUtil {

    @VisibleForTesting
    public static final String SHARED_PREFERENCES_FILE = "shared_preferences_file";
    @VisibleForTesting
    public static final String SAVED_USERNAME = "saved_username";
    private static final String SAVED_PRIVATE_KEY = "saved_private_key";
    private static final String SAVED_PUBLIC_KEY = "saved_public_key";

    @Getter
    private final SharedPreferences preferences;

    @Inject
    public PreferencesUtil() {
        preferences = SampleApplication.instance.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
    }

    public void saveUsername(String username) {
        preferences.edit().putString(SAVED_USERNAME, username).apply();
    }

    public String retrieveUsername() {
        return preferences.getString(SAVED_USERNAME, "");
    }

    public void saveKeys(String privateKey) {
        preferences.edit().putString(SAVED_PRIVATE_KEY, privateKey).apply();
    }

    public KeyPair retrieveKeys() {
        try {
            String PRV_KEY = preferences.getString(SAVED_PRIVATE_KEY, "");
            PrivateKey mPrivk = Ed25519Sha3.privateKeyFromBytes(DatatypeConverter.parseHexBinary(PRV_KEY));
            EdDSAPrivateKey privk = (EdDSAPrivateKey) mPrivk;
            EdDSAPublicKey publicKey = new EdDSAPublicKey(new EdDSAPublicKeySpec(privk.getAbyte(), EdDSANamedCurveTable.getByName("Ed25519")));
            return new KeyPair(publicKey, privk);
        }catch (Exception e){
            return null;
        }
    }

    public void clear() {
        preferences.edit().clear().apply();
    }
}