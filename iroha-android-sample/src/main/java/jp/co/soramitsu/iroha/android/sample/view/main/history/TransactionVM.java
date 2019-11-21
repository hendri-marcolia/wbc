package jp.co.soramitsu.iroha.android.sample.view.main.history;

import jp.co.soramitsu.iroha.android.sample.data.Payload;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TransactionVM {
    public final long id;
    public final String prettyDate;
    public final String username;
    public final String prettyAmount;
    public final String transactionType;
    public final Payload.PayloadType actionType;
    public final String transactionStatus;
}