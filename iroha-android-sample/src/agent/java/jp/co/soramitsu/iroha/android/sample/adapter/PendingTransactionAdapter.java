package jp.co.soramitsu.iroha.android.sample.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import jp.co.soramitsu.iroha.android.sample.R;
import jp.co.soramitsu.iroha.android.sample.data.PendingTransaction;
import jp.co.soramitsu.iroha.android.sample.data.PerformSavePayload;
import jp.co.soramitsu.iroha.android.sample.data.Transaction;
import jp.co.soramitsu.iroha.android.sample.entity.TransactionEntity;
import jp.co.soramitsu.iroha.android.sample.interactor.deposit.AgentPerformSaveOnlineInteractor;
import jp.co.soramitsu.iroha.android.sample.interactor.deposit.AgentSignPendingTransactionInteractor;
import jp.co.soramitsu.iroha.android.sample.view.pendingtransaction.PendingTransactionPresenter;
import lombok.Getter;
import lombok.Setter;

public class PendingTransactionAdapter extends RecyclerView.Adapter<PendingTransactionAdapter.PendingTransactionVH>{
    @Setter
    @Getter
    private List<PendingTransaction> pendingTransactions = new ArrayList<>();

    final private PendingTransactionPresenter presenter;
    final private AgentPerformSaveOnlineInteractor interactor;
    final private AgentSignPendingTransactionInteractor signPendingTransactionInteractor;

    public PendingTransactionAdapter(PendingTransactionPresenter presenter, AgentPerformSaveOnlineInteractor interactor, AgentSignPendingTransactionInteractor signPendingTransactionInteractor) {
        this.presenter = presenter;
        this.interactor = interactor;
        this.signPendingTransactionInteractor = signPendingTransactionInteractor;
    }

    public void addPendingTransaction(PendingTransaction p){
        pendingTransactions.add(p);
    }

    @NonNull
    @Override
    public PendingTransactionVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pending_transaction_item, parent, false);

        return new PendingTransactionVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingTransactionVH holder, int position) {
        PendingTransaction pendingTransaction = pendingTransactions.get(position);
        Transaction tPayload = new Gson().fromJson(pendingTransaction.getTransactionEntity().getTransactionPayload(), Transaction.class);
        holder.textViewName.setText(tPayload.getTransactionPayload().getPayload().getCustomerId());
        holder.textViewAmount.setText(""+tPayload.getTransactionPayload().getPayload().getAmount());
        holder.btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.showLoading();
                signPendingTransactionInteractor.execute(pendingTransaction.getTransactions(), o -> {

                            // Perform Save
                            PerformSavePayload payload = new PerformSavePayload();
                            payload.setAmount(String.valueOf(tPayload.getTransactionPayload().getPayload().getAmount()));
                            payload.setAgentId(tPayload.getAgentId());
                            payload.setAgentPublicKey(tPayload.getAgentPublicKey());
                            payload.setAccountId(tPayload.getTransactionPayload().getPayload().getCustomerId());
                            payload.setAccountPublicKey(tPayload.getAgentPublicKey());
                            interactor.execute(payload, httpResult -> {

                            }, throwable -> {
                                presenter.showError(throwable);
                            });
                        },
                        () -> {
                            TransactionEntity t = pendingTransaction.getTransactionEntity();
                            t.setCommited(true);
                            t.save();
                            presenter.showInfo("Success Sign Transaction");
                            presenter.refreshPendingTransaction();
                        }, throwable -> {
                            presenter.showError(throwable);
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return pendingTransactions.size();
    }

    public static class PendingTransactionVH extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textViewName;
        public TextView textViewAmount;
        public Button btnSign;
        public PendingTransactionVH(View v) {
            super(v);
            textViewName = v.findViewById(R.id.username);
            textViewAmount = v.findViewById(R.id.amount);
            btnSign = v.findViewById(R.id.btnSign);
        }
    }
}
