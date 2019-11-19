package jp.co.soramitsu.iroha.android.sample.view.pendingtransaction;

import java.util.List;

import jp.co.soramitsu.iroha.android.sample.data.PendingTransaction;

public interface PendingTransactionView {
    void showError(Throwable e);

    void showInfo(String msg);

    void showLoading();

    void refreshTransaction(PendingTransaction pendingTransactions);

    void clearTransaction();
}
