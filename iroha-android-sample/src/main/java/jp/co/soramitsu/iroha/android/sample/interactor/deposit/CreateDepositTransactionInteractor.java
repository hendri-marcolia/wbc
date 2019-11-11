package jp.co.soramitsu.iroha.android.sample.interactor.deposit;

import java.security.KeyPair;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Completable;
import io.reactivex.Scheduler;
import iroha.protocol.Endpoint;
import iroha.protocol.TransactionOuterClass;
import jp.co.soramitsu.iroha.android.sample.PreferencesUtil;
import jp.co.soramitsu.iroha.android.sample.data.Transaction;
import jp.co.soramitsu.iroha.android.sample.injection.ApplicationModule;
import jp.co.soramitsu.iroha.android.sample.interactor.CompletableInteractor;
import jp.co.soramitsu.iroha.java.IrohaAPI;
import jp.co.soramitsu.iroha.java.TransactionBuilder;
import jp.co.soramitsu.iroha.java.Utils;
import jp.co.soramitsu.iroha.java.subscription.WaitForTerminalStatus;

import static jp.co.soramitsu.iroha.java.Utils.getProtoBatchHashesHex;


public class CreateDepositTransactionInteractor extends CompletableInteractor<Transaction> {

    private final PreferencesUtil preferenceUtils;

    @Inject
    CreateDepositTransactionInteractor(@Named(ApplicationModule.JOB) Scheduler jobScheduler,
                                       @Named(ApplicationModule.UI) Scheduler uiScheduler,
                                       PreferencesUtil preferencesUtil) {
        super(jobScheduler, uiScheduler);
        this.preferenceUtils = preferencesUtil;
    }

    private static Iterable<TransactionOuterClass.Transaction> createBatch(
            Iterable<TransactionOuterClass.Transaction> list, TransactionOuterClass.Transaction.Payload.BatchMeta.BatchType batchType, KeyPair keyPair){
        AtomicInteger counter = new AtomicInteger();
        final Iterable<String> batchHashes = getProtoBatchHashesHex(list);
        return StreamSupport.stream(list.spliterator(), false)
                .map(tx -> {
                            if (counter.getAndIncrement() == 0){
                                return jp.co.soramitsu.iroha.java.Transaction
                                        .parseFrom(tx)
                                        .makeMutable()
                                        .setBatchMeta(batchType, batchHashes)
                                        .sign(keyPair)
                                        .build();
                            } else {
                                return jp.co.soramitsu.iroha.java.Transaction
                                        .parseFrom(tx)
                                        .makeMutable()
                                        .setBatchMeta(batchType, batchHashes)
                                        .build()
                                        .build();
                            }
                        }
                )
                .collect(Collectors.toList());
    }

    @Override
    protected Completable build(Transaction transaction) {
        return Completable.create(emitter -> {
            try {
                String username = preferenceUtils.retrieveUsername();
                KeyPair userKeys = preferenceUtils.retrieveKeys();
                String domain = preferenceUtils.retrieveDomain();
                String USR = String.format("%s@%s",username,domain);
                IrohaAPI irohaAPI = getIrohaAPI();
                List<TransactionOuterClass.Transaction> transactionList
                        = Arrays.asList(
                        new TransactionBuilder(USR, new Date().getTime())
                                .transferAsset(USR, preferenceUtils.retrieveAdminId(),
                                        "offline#"+domain,"customer handover money",
                                        String.valueOf(transaction.getTransactionPayload().getPayload().getAmount()))
                                .setQuorum(2)
                                .sign(userKeys).build()
                                ,
                        new TransactionBuilder(USR, new Date().getTime())
                                .transferAsset(preferenceUtils.retrieveAdminId(), USR,
                                        "online#"+domain,"customer handover money",
                                        String.valueOf(transaction.getTransactionPayload().getPayload().getAmount()))
                                .setCreatorAccountId(preferenceUtils.retrieveAdminId())
                                .build().build()
                        );

                Iterable<TransactionOuterClass.Transaction> atomicBatch = createBatch(transactionList, TransactionOuterClass.Transaction.Payload.BatchMeta.BatchType.ATOMIC , userKeys);
                irohaAPI.transactionListSync(
                        atomicBatch
                );
                WaitForTerminalStatus waiter = new WaitForTerminalStatus();
                AtomicInteger txCount = new AtomicInteger();
                for(TransactionOuterClass.Transaction tx : atomicBatch) {
                    waiter.subscribe(irohaAPI, Utils.hash(tx))
                            .subscribe(toriiResponse -> {
                                if (toriiResponse.getTxStatus() == Endpoint.TxStatus.COMMITTED ||
                                        toriiResponse.getTxStatus() == Endpoint.TxStatus.STATELESS_VALIDATION_SUCCESS ||
                                        toriiResponse.getTxStatus() == Endpoint.TxStatus.STATEFUL_VALIDATION_SUCCESS ||
                                        toriiResponse.getTxStatus() == Endpoint.TxStatus.ENOUGH_SIGNATURES_COLLECTED ||
                                        toriiResponse.getTxStatus() == Endpoint.TxStatus.MST_PENDING){
                                    if (toriiResponse.getTxStatus() == Endpoint.TxStatus.COMMITTED  || toriiResponse.getTxStatus() == Endpoint.TxStatus.MST_PENDING)
                                        if (txCount.incrementAndGet() == transactionList.size()){
                                            if (emitter != null)
                                            emitter.onComplete();
                                        }
                                }
                                else
                                    if (emitter!= null)
                                    emitter.onError(new RuntimeException("Transaction Failed , TxStatus = " + toriiResponse.getTxStatus()));
                            });
                }


            }catch (Exception e){
                emitter.onError(new RuntimeException("Transaction failed"));
            }
        });
    }
}