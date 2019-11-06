package jp.co.soramitsu.iroha.android.sample.data;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

public class Registration {

    @Setter
    @Getter
    @SerializedName("account_id")
    private String accountId;

    @Setter
    @Getter
    @SerializedName("ktp_id")
    private String ktp;

    @Setter
    @Getter
    @SerializedName("account_public_key")
    private String accountPublicKey;

    @Setter
    @Getter
    @SerializedName("fcm_id")
    private String fcmId;

    @Setter
    @Getter
    @SerializedName("full_name")
    private String fullName;

    @Setter
    @Getter
    @SerializedName("bank_account")
    private String bankAccount;

    public Registration() {}
}
