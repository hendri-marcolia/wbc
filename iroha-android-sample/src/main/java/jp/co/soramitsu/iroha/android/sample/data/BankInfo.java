package jp.co.soramitsu.iroha.android.sample.data;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

public class BankInfo {

//    @Setter
//    @Getter
//    @SerializedName("bank_public_key")
//    private String publicKey;

    @Setter
    @Getter
    @SerializedName("bank_account_id")
    private String bankAccountId;

    @Setter
    @Getter
    @SerializedName("domain")
    private String bankDomain;

    public BankInfo() {}
}
