package jp.co.soramitsu.iroha.android.sample.data;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

public class Validate {

    @Setter
    @Getter
    @SerializedName("account_id")
    private String accountId;

    public Validate() {}

    public Validate(String accountId) {
        this.accountId = accountId;
    }
}
