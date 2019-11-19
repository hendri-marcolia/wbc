package jp.co.soramitsu.iroha.android.sample.interactor;


import java.security.KeyPair;
import java.util.Date;
import java.util.EmptyStackException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.grpc.ManagedChannel;
import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import iroha.protocol.Endpoint;
import iroha.protocol.QryResponses;
import iroha.protocol.Queries;
import iroha.protocol.TransactionOuterClass;
import jp.co.soramitsu.iroha.android.sample.Constants;
import jp.co.soramitsu.iroha.android.sample.PreferencesUtil;
import jp.co.soramitsu.iroha.android.sample.injection.ApplicationModule;
import jp.co.soramitsu.iroha.java.IrohaAPI;
import jp.co.soramitsu.iroha.java.Query;


public class GetPendingTransactionInteractor extends SingleInteractor<List<TransactionOuterClass.Transaction>,String> {

    private final PreferencesUtil preferenceUtils;

    @Inject
    GetPendingTransactionInteractor(@Named(ApplicationModule.JOB) Scheduler jobScheduler,
                                    @Named(ApplicationModule.UI) Scheduler uiScheduler
                                    , PreferencesUtil preferencesUtil) {
        super(jobScheduler, uiScheduler);
        this.preferenceUtils = preferencesUtil;
    }

    @Override
    protected Single<List<TransactionOuterClass.Transaction>> build(String accountId) {
        return Single.create(emitter -> {
            long currentTime = System.currentTimeMillis();
            try {
                KeyPair userKeys = preferenceUtils.retrieveKeys();
                String domain = preferenceUtils.retrieveDomain();
                String USR = String.format("%s@%s",accountId,domain);
                IrohaAPI irohaAPI = getIrohaAPI();
                Queries.Query q = Query.builder(USR, currentTime, 1)
                        .getPendingTransactions()
                        .buildSigned(userKeys);
                QryResponses.TransactionsResponse response = irohaAPI.query(q).getTransactionsResponse();
                if(response.getTransactionsCount() == 0 ){
                    emitter.onError(new EmptyStackException());
                } else {
                    emitter.onSuccess(response.getTransactionsList());
                }
            }catch (Exception e){
                emitter.onError(new RuntimeException("Transaction failed"));
            }
        });
    }


}