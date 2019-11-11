package jp.co.soramitsu.iroha.android.sample.interactor.register;

import android.util.Pair;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.grpc.ManagedChannel;
import io.reactivex.Completable;
import io.reactivex.Scheduler;
import iroha.protocol.Endpoint;
import iroha.protocol.Primitive;
import jp.co.soramitsu.iroha.android.sample.Constants;
import jp.co.soramitsu.iroha.android.sample.PreferencesUtil;
import jp.co.soramitsu.iroha.android.sample.data.BankInfo;
import jp.co.soramitsu.iroha.android.sample.data.UglyPairing;
import jp.co.soramitsu.iroha.android.sample.injection.ApplicationModule;
import jp.co.soramitsu.iroha.android.sample.interactor.CompletableInteractor;
import jp.co.soramitsu.iroha.java.IrohaAPI;
import jp.co.soramitsu.iroha.java.TransactionBuilder;


public class GrantPermissionInteractor extends CompletableInteractor<UglyPairing> {

    private final PreferencesUtil preferenceUtils;

    @Inject
    GrantPermissionInteractor(@Named(ApplicationModule.JOB) Scheduler jobScheduler,
                              @Named(ApplicationModule.UI) Scheduler uiScheduler,
                              PreferencesUtil preferencesUtil) {
        super(jobScheduler, uiScheduler);
        this.preferenceUtils = preferencesUtil;
    }

    @Override
    protected Completable build(UglyPairing pair) {
        return Completable.create(emitter -> {
            try {
                String username = pair.getAccountId();
                KeyPair userKeys = pair.getKeyPair();
                BankInfo bankInfo = pair.getBankInfo();
                String domain = bankInfo.getBankDomain();
                String USR = String.format("%s@%s",username,domain);
                IrohaAPI irohaAPI = getIrohaAPI();
                List<Primitive.GrantablePermission> permissions = Arrays.asList(
                    Primitive.GrantablePermission.can_add_my_signatory,
                    Primitive.GrantablePermission.can_remove_my_signatory,
                    Primitive.GrantablePermission.can_set_my_account_detail,
                    Primitive.GrantablePermission.can_transfer_my_assets
                );
                irohaAPI.transaction(
                        new TransactionBuilder(USR, new Date().getTime())
                                .grantPermissions(bankInfo.getBankAccountId(), permissions)
                                .sign(userKeys).build()
                ).blockingSubscribe(toriiResponse -> {
                            if (toriiResponse.getTxStatus() == Endpoint.TxStatus.COMMITTED ||
                                    toriiResponse.getTxStatus() == Endpoint.TxStatus.STATELESS_VALIDATION_SUCCESS ||
                                    toriiResponse.getTxStatus() == Endpoint.TxStatus.STATEFUL_VALIDATION_SUCCESS ||
                                    toriiResponse.getTxStatus() == Endpoint.TxStatus.ENOUGH_SIGNATURES_COLLECTED){
                                if (toriiResponse.getTxStatus() == Endpoint.TxStatus.COMMITTED )
                                    emitter.onComplete();
                            }
                            else  emitter.onError(new RuntimeException("Transaction Failed , TxStatus = " + toriiResponse.getTxStatus()));
                });
            }catch (Exception e){
                emitter.onError(new RuntimeException("Transaction failed"));
            }
        });
    }
}