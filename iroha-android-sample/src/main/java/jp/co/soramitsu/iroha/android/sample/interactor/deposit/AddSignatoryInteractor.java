package jp.co.soramitsu.iroha.android.sample.interactor.deposit;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Completable;
import io.reactivex.Scheduler;
import iroha.protocol.Endpoint;
import iroha.protocol.Primitive;
import jp.co.soramitsu.iroha.android.sample.PreferencesUtil;
import jp.co.soramitsu.iroha.android.sample.data.BankInfo;
import jp.co.soramitsu.iroha.android.sample.data.UglyPairing;
import jp.co.soramitsu.iroha.android.sample.injection.ApplicationModule;
import jp.co.soramitsu.iroha.android.sample.interactor.CompletableInteractor;
import jp.co.soramitsu.iroha.java.IrohaAPI;
import jp.co.soramitsu.iroha.java.TransactionBuilder;


public class AddSignatoryInteractor extends CompletableInteractor<PublicKey> {

    private final PreferencesUtil preferenceUtils;

    @Inject
    AddSignatoryInteractor(@Named(ApplicationModule.JOB) Scheduler jobScheduler,
                           @Named(ApplicationModule.UI) Scheduler uiScheduler,
                           PreferencesUtil preferencesUtil) {
        super(jobScheduler, uiScheduler);
        this.preferenceUtils = preferencesUtil;
    }

    @Override
    protected Completable build(PublicKey publicKey) {
        return Completable.create(emitter -> {
            try {
                String username = preferenceUtils.retrieveUsername();
                KeyPair userKeys = preferenceUtils.retrieveKeys();
                String domain = preferenceUtils.retrieveDomain();
                String USR = String.format("%s@%s",username,domain);
                IrohaAPI irohaAPI = getIrohaAPI();

                irohaAPI.transaction(
                        new TransactionBuilder(USR, new Date().getTime())
                                .addSignatory(USR, publicKey)
                                .sign(userKeys).build()
                ).blockingSubscribe(toriiResponse -> {
                            if (toriiResponse.getTxStatus() == Endpoint.TxStatus.COMMITTED ||
                                    toriiResponse.getTxStatus() == Endpoint.TxStatus.STATELESS_VALIDATION_SUCCESS ||
                                    toriiResponse.getTxStatus() == Endpoint.TxStatus.STATEFUL_VALIDATION_SUCCESS ||
                                    toriiResponse.getTxStatus() == Endpoint.TxStatus.ENOUGH_SIGNATURES_COLLECTED){
                                if (toriiResponse.getTxStatus() == Endpoint.TxStatus.COMMITTED )
                                    emitter.onComplete();
                            }
                            else {
                                if (toriiResponse.getTxStatus() == Endpoint.TxStatus.STATEFUL_VALIDATION_FAILED
                                 && toriiResponse.getErrOrCmdName().equals("AddSignatory") && toriiResponse.getErrorCode() == 4)
                                    emitter.onComplete();
                                else
                                    emitter.onError(new RuntimeException("Transaction Failed , TxStatus = " + toriiResponse.getTxStatus()));
                            }
                });
            }catch (Exception e){
                emitter.onError(new RuntimeException("Transaction failed"));
            }
        });
    }
}