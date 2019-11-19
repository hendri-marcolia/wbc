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
    public static final String SAVED_DOMAIN = "saved_domain";
    public static final String SAVED_ADMIN_ID = "saved_admin_id";
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

    public void saveDomain(String domain) {
        preferences.edit().putString(SAVED_DOMAIN, domain).apply();
    }

    public String retrieveDomain() { return preferences.getString(SAVED_DOMAIN, "");};

    public void saveAdminId(String adminId) {
        preferences.edit().putString(SAVED_ADMIN_ID, adminId).apply();
    }

    public String retrieveAdminId() { return preferences.getString(SAVED_ADMIN_ID, "");};

    public void saveKeys(String privateKey) {
        preferences.edit().putString(SAVED_PRIVATE_KEY, privateKey).apply();
    }

    public KeyPair retrieveKeys() {
        try {
            String PRV_KEY = preferences.getString(SAVED_PRIVATE_KEY, "");
            return  MyUtils.keyPairFromPrivate(MyUtils.privateKeyFromString(PRV_KEY));
        }catch (Exception e){
            return null;
        }
    }

    public void clear() {
        preferences.edit().clear().apply();
    }
}