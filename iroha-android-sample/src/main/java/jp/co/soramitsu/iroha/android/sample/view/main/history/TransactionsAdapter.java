package jp.co.soramitsu.iroha.android.sample.view.main.history;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

import jp.co.soramitsu.iroha.android.sample.R;
import jp.co.soramitsu.iroha.android.sample.SampleApplication;
import jp.co.soramitsu.iroha.android.sample.data.Payload;
import jp.co.soramitsu.iroha.android.sample.data.Transaction;
import jp.co.soramitsu.iroha.android.sample.entity.TransactionEntity;
import jp.co.soramitsu.iroha.android.sample.interactor.basicsavingaction.PerformSaveOfflineInteractor;
import lombok.Getter;
import lombok.Setter;

public class TransactionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    @Getter
    @Setter
    private List transactions;

    PerformSaveOfflineInteractor performSaveOfflineInteractor;

    HistoryPresenter presenter;

    TransactionsAdapter(PerformSaveOfflineInteractor performSaveOfflineInteractor, HistoryPresenter presenter) {
        this.performSaveOfflineInteractor = performSaveOfflineInteractor;
        this.transactions = new ArrayList<>();
        this.presenter = presenter;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.transaction_item, parent, false);
            return new TransactionItem(v);
        } else if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.transaction_header_item, parent, false);
            return new TransactionHeaderItem(v);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TransactionItem) {
            TransactionVM transaction = (TransactionVM) transactions.get(position);
            TransactionItem transactionItem = (TransactionItem) holder;
            transactionItem.username.setText(transaction.username);
            transactionItem.type.setText(transaction.transactionType);
            transactionItem.status.setText(transaction.transactionStatus);
            if (transaction.transactionType.equals("OFFLINE") && !transaction.transactionStatus.equals("SYNCED")){
                transactionItem.syncButton.setVisibility(View.VISIBLE);
                transactionItem.syncButton.setEnabled(true);
                transactionItem.syncButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TransactionEntity entity = SugarRecord.findById(TransactionEntity.class, transaction.id);
                        presenter.showLoading();
                        performSaveOfflineInteractor.execute(new Gson().fromJson(entity.getTransactionPayload(), Transaction.class), httpResult -> {
                            entity.setCommited(true);
                            entity.save();
                            presenter.hideLoading();
                            presenter.getTransactions();
                            presenter.successSync(httpResult.getMessage());
                        }, throwable -> {
                            presenter.showError(throwable);
                        });
                    }
                });
            }else transactionItem.syncButton.setVisibility(View.GONE);
            if (transaction.actionType == Payload.PayloadType.WITHDRAW) {
                transactionItem.amount.setText("- " + transaction.prettyAmount);
                transactionItem.amount.setTextColor(SampleApplication.instance.getResources().getColor(R.color.negativeAmount));
            } else {
                transactionItem.amount.setText("+ " + transaction.prettyAmount);
                transactionItem.amount.setTextColor(SampleApplication.instance.getResources().getColor(R.color.positiveAmount));
            }
            transactionItem.date.setText(transaction.prettyDate);
        } else if (holder instanceof TransactionHeaderItem) {
            TransactionHeaderItem headerItem = (TransactionHeaderItem) holder;
            headerItem.date.setText(transactions.get(position).toString());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (transactions.get(position) instanceof TransactionVM) {
            return TYPE_ITEM;
        }
        return TYPE_HEADER;
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    static class TransactionItem extends RecyclerView.ViewHolder {

        TextView username;
        TextView date;
        TextView amount;
        TextView status;
        TextView type;
        Button syncButton;

        TransactionItem(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            date = itemView.findViewById(R.id.date);
            amount = itemView.findViewById(R.id.amount);
            status = itemView.findViewById(R.id.txStatus);
            type = itemView.findViewById(R.id.txType);
            syncButton = itemView.findViewById(R.id.btnSync);
        }
    }

    static class TransactionHeaderItem extends RecyclerView.ViewHolder {

        TextView date;

        TransactionHeaderItem(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
        }
    }
}