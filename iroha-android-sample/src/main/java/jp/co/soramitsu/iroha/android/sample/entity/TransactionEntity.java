package jp.co.soramitsu.iroha.android.sample.entity;

import android.content.Context;

import com.orm.SugarRecord;

import lombok.Getter;
import lombok.Setter;

public class TransactionEntity extends SugarRecord {

    @Getter
    @Setter
    String transactionPayload;
    @Getter
    @Setter
    boolean commited;
    @Getter
    boolean online;

    public TransactionEntity(String transactionPayload, boolean commited, boolean online) {
        this.transactionPayload = transactionPayload;
        this.commited = commited;
        this.online = online;
    }

    public TransactionEntity() {
    }

}
