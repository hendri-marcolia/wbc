package jp.co.soramitsu.iroha.android.sample.data;

import java.util.ArrayList;
import java.util.List;

import iroha.protocol.TransactionOuterClass;
import jp.co.soramitsu.iroha.android.sample.entity.TransactionEntity;
import lombok.Getter;
import lombok.Setter;

public class PendingTransaction {
    @Getter
    @Setter
    private List<TransactionOuterClass.Transaction> transactions = new ArrayList<>();

    @Getter
    @Setter
    private TransactionEntity transactionEntity;

    public PendingTransaction(List<TransactionOuterClass.Transaction> transactions, TransactionEntity transactionEntity) {
        this.transactions = transactions;
        this.transactionEntity = transactionEntity;
    }
}
