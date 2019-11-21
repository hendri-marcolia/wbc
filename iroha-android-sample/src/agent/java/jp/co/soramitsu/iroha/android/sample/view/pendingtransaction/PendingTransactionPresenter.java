package jp.co.soramitsu.iroha.android.sample.view.pendingtransaction;

import com.google.gson.Gson;
import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

import javax.inject.Inject;

import jp.co.soramitsu.iroha.android.sample.PreferencesUtil;
import jp.co.soramitsu.iroha.android.sample.data.PendingTransaction;
import jp.co.soramitsu.iroha.android.sample.data.PerformSavePayload;
import jp.co.soramitsu.iroha.android.sample.data.Transaction;
import jp.co.soramitsu.iroha.android.sample.entity.TransactionEntity;
import jp.co.soramitsu.iroha.android.sample.interactor.GetPendingTransactionInteractor;
import jp.co.soramitsu.iroha.android.sample.interactor.basicsavingaction.AgentPerformSaveOnlineInteractor;
import jp.co.soramitsu.iroha.android.sample.interactor.basicsavingaction.AgentSignPendingTransactionInteractor;
import jp.co.soramitsu.iroha.android.sample.view.scan.ScanFragment;
import lombok.Setter;

public class PendingTransactionPresenter {

    public static final int REQUEST_CODE_QR_SCAN = 102;
    @Setter
    private PendingTransactionFragment fragment;

    private final GetPendingTransactionInteractor getPendingTransactionInteractor;

    private final AgentSignPendingTransactionInteractor agentSignPendingTransactionInteractor;

    private final AgentPerformSaveOnlineInteractor agentPerformSaveOnlineInteractor;

    private final PreferencesUtil preferencesUtil;


    @Inject
    public PendingTransactionPresenter(GetPendingTransactionInteractor getPendingTransactionInteractor,
                                       AgentSignPendingTransactionInteractor agentSignPendingTransactionInteractor, AgentPerformSaveOnlineInteractor agentPerformSaveOnlineInteractor, PreferencesUtil preferencesUtil) {

        this.agentSignPendingTransactionInteractor = agentSignPendingTransactionInteractor;
        this.agentPerformSaveOnlineInteractor = agentPerformSaveOnlineInteractor;
        this.preferencesUtil = preferencesUtil;
        this.getPendingTransactionInteractor = getPendingTransactionInteractor;
    }



    void onDestroy() {
        fragment = null;
    }

    public void refreshPendingTransaction(){
        fragment.setRefreshing(true);
        List<TransactionEntity> transactionEntities = Select.from(TransactionEntity.class)
                .where(Condition.prop("commited").eq(0),Condition.prop("online").eq(1)).list();
        fragment.clearTransaction();
        if(transactionEntities.size() ==0) fragment.setRefreshing(false);
        for( TransactionEntity t: transactionEntities ){
            String accountId = new Gson().fromJson(t.getTransactionPayload(),Transaction.class).getTransactionPayload().getPayload().getCustomerId();
            getPendingTransactionInteractor.execute(accountId, transactions -> {
                fragment.refreshTransaction(new PendingTransaction(transactions, t));
            }, throwable -> {
                if (throwable instanceof EmptyStackException)
                    fragment.setRefreshing(false);
                else
                    fragment.showError(throwable);
            });
        }
    }

    public  void showInfo(String info){
        fragment.showInfo(info);
    }

    public void showLoading(){
        fragment.showLoading();;
    }

    public void showError(Throwable throwable) {
        fragment.showError(throwable);
    }

}