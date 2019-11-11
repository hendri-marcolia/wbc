package jp.co.soramitsu.iroha.android.sample.entity;

import android.content.Context;

import com.orm.SugarRecord;

public class TransactionEntity extends SugarRecord {

    String transactionPayload;
    boolean commited;

    public TransactionEntity(String transactionPayload, boolean commited) {
        this.transactionPayload = transactionPayload;
        this.commited = commited;
    }

    public TransactionEntity() {
    }

}
