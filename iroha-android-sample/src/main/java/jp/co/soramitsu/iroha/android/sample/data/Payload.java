package jp.co.soramitsu.iroha.android.sample.data;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class Payload {
    public enum PayloadType {
        DEPOSIT,
        WITHDRAW
    }

    @Getter
    @SerializedName("p1")
    String txId;
    @Getter
    @SerializedName("p2")
    Long amount;
    @Getter
    @SerializedName("p3")
    String customerId;
    @Getter
    @SerializedName("p4")
    PayloadType type;
    @Getter
    @SerializedName("p5")
    Long timestamp;

    public Payload(Long amount, String customerId, PayloadType type) {
        this.txId = UUID.randomUUID().toString();
        this.amount = amount;
        this.customerId = customerId;
        this.timestamp = System.currentTimeMillis();
        this.type = type;
    }
}