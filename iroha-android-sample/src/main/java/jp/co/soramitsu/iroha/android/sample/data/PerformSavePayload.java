package jp.co.soramitsu.iroha.android.sample.data;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

public class PerformSavePayload {
    @Setter
    @Getter
    @SerializedName("account_id")
    private String accountId;

    @Setter
    @Getter
    @SerializedName("account_public_key")
    private String accountPublicKey;

    @Setter
    @Getter
    @SerializedName("agent_id")
    private String agentId;

    @Setter
    @Getter
    @SerializedName("agent_public_key")
    private String agentPublicKey;

    @Setter
    @Getter
    @SerializedName("amount")
    private String amount;

    // Should use ignore strategy here
    @Setter
    @Getter
    private Payload.PayloadType actionType;
}
