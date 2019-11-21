package jp.co.soramitsu.iroha.android.sample.data;

import com.google.gson.annotations.SerializedName;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
public class Transaction implements Cloneable {

    public enum TransactionType {
        ONLINE,
        OFFLINE
    }
    @EqualsAndHashCode
    public class TransactionPayload {

        @Getter
        @SerializedName("tp1")
        Payload payload;
        @Getter
        @SerializedName("tp2")
        String customerPublicKey;
        @Getter
        @SerializedName("tp3")
        String customerSigning;

        TransactionPayload(Payload payload, String customerPublicKey, String customerSigning){
            this.payload = payload;
            this.customerPublicKey = customerPublicKey;
            this.customerSigning = customerSigning;
        }
    }

    @Getter
    @SerializedName("t1")
    TransactionPayload transactionPayload;


    public Transaction(Payload payload, String customerPublicKey, String customerSigning) {
        this.transactionPayload = new TransactionPayload(payload, customerPublicKey, customerSigning);
    }

    @Getter
    @Setter
    @SerializedName("t2")
    String agentId;
    @Getter
    @Setter
    @SerializedName("t3")
    String agentPublicKey;
    @Getter
    @Setter
    @SerializedName("t4")
    String agentSinging;
    @Getter
    @Setter
    @SerializedName("t5")
    TransactionType transactionType;

}
