package jp.co.soramitsu.iroha.android.sample;

import android.util.Base64;

import com.google.gson.Gson;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import javax.xml.bind.DatatypeConverter;

import jp.co.soramitsu.crypto.ed25519.Ed25519Sha3;
import jp.co.soramitsu.crypto.ed25519.EdDSAPrivateKey;
import jp.co.soramitsu.crypto.ed25519.EdDSAPublicKey;
import jp.co.soramitsu.crypto.ed25519.spec.EdDSANamedCurveTable;
import jp.co.soramitsu.crypto.ed25519.spec.EdDSAPublicKeySpec;

public class MyUtils {
    public static <T extends Object> byte[] sign(T t, KeyPair kp) {
        return  new Ed25519Sha3().rawSign(new Gson().toJson(t).getBytes(), kp);
    }

    public static <T extends Object> boolean verify(T t, byte[] sig, PublicKey pk) throws  Throwable{
            if (new Ed25519Sha3().rawVerify(new Gson().toJson(t).getBytes(), sig, pk) )
                return true;
            else throw new Throwable("Invalid signature");

    }


    public static String bytesToString(byte[] b) {
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static byte[] stringToBytes(String hexString) {
        return Base64.decode(hexString, Base64.DEFAULT);
    }

    public static String bytesToHex(byte[] b) {
        return DatatypeConverter.printHexBinary(b);
    }

    public static byte[] hexToBytes(String hexString) {
        return DatatypeConverter.parseHexBinary(hexString);
    }

    public static KeyPair keyPairFromPrivate(PrivateKey privateKey) {
        EdDSAPrivateKey privk = (EdDSAPrivateKey) privateKey;
        EdDSAPublicKey publicKey = new EdDSAPublicKey(new EdDSAPublicKeySpec(privk.getAbyte(), EdDSANamedCurveTable.getByName("Ed25519")));
        return new KeyPair(publicKey, privk);
    }

    public static PrivateKey privateKeyFromString(String pKey){
        return Ed25519Sha3.privateKeyFromBytes(DatatypeConverter.parseHexBinary(pKey));
    }

    public static String formatIDR(Long amount){
        NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setMaximumFractionDigits(0);
        Locale locale = new Locale("in", "ID");
        format.setCurrency(Currency.getInstance(locale));
        return format.format(amount);
    }
}
