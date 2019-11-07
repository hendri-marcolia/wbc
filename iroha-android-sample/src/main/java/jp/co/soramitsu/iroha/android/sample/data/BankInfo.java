package jp.co.soramitsu.iroha.android.sample.data;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

public class BankInfo {

    @Setter
    @Getter
    @SerializedName("bank_public_key")
    private String publicKey;

    public BankInfo() {}
}
