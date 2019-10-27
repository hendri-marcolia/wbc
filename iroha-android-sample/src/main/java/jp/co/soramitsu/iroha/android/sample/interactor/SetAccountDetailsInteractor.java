package jp.co.soramitsu.iroha.android.sample.interactor;

import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;

import java.math.BigInteger;
import java.security.KeyPair;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import io.grpc.ManagedChannel;
import io.reactivex.Completable;
import io.reactivex.Scheduler;
import iroha.protocol.BlockOuterClass;
import iroha.protocol.Endpoint;
import jp.co.soramitsu.iroha.android.sample.Constants;
import jp.co.soramitsu.iroha.android.sample.PreferencesUtil;
import jp.co.soramitsu.iroha.android.sample.injection.ApplicationModule;
import jp.co.soramitsu.iroha.java.IrohaAPI;
import jp.co.soramitsu.iroha.java.TransactionBuilder;

import static jp.co.soramitsu.iroha.android.sample.Constants.ACCOUNT_DETAILS;
import static jp.co.soramitsu.iroha.android.sample.Constants.CONNECTION_TIMEOUT_SECONDS;
import static jp.co.soramitsu.iroha.android.sample.Constants.DOMAIN_ID;

public class SetAccountDetailsInteractor extends CompletableInteractor<String> {

    private final ManagedChannel channel;
    private final PreferencesUtil preferenceUtils;

    @Inject
    SetAccountDetailsInteractor(@Named(ApplicationModule.JOB) Scheduler jobScheduler,
                                @Named(ApplicationModule.UI) Scheduler uiScheduler,
                                ManagedChannel managedChannel, PreferencesUtil preferencesUtil) {
        super(jobScheduler, uiScheduler);
        this.channel = managedChannel;
        this.preferenceUtils = preferencesUtil;
    }

    @Override
    protected Completable build(String details) {
        return Completable.create(emitter -> {
            long currentTime = System.currentTimeMillis();
            try {
                KeyPair userKeys = preferenceUtils.retrieveKeys();
                String username = preferenceUtils.retrieveUsername();
                String domain = DOMAIN_ID;
                String USR = String.format("%s@%s",username,domain);
                IrohaAPI irohaAPI = getIrohaAPI();
                irohaAPI.transaction(
                        new TransactionBuilder(USR, new Date().getTime())
                                .setAccountDetail(USR, Constants.ACCOUNT_DETAILS, details).sign(userKeys).build()
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
//
//            UnsignedTx setDetailsTransaction = txBuilder.creatorAccountId(username + "@" + DOMAIN_ID)
//                    .createdTime(BigInteger.valueOf(currentTime))
//                    .setAccountDetail(username + "@" + DOMAIN_ID, ACCOUNT_DETAILS, details)
//                    .build();
//
//            protoTxHelper = new ModelProtoTransaction(setDetailsTransaction);
//            ByteVector txblob = protoTxHelper.signAndAddSignature(userKeys).finish().blob();
//            byte[] bs = toByteArray(txblob);
//            BlockOuterClass.Transaction protoTx = null;
//
//            try {
//                protoTx = BlockOuterClass.Transaction.parseFrom(bs);
//            } catch (InvalidProtocolBufferException e) {
//                emitter.onError(e);
//            }
//
//            CommandServiceGrpc.CommandServiceBlockingStub stub = CommandServiceGrpc.newBlockingStub(channel)
//                    .withDeadlineAfter(CONNECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
//
//            stub.torii(protoTx);
//
//            // Check if it was successful
//            if (!isTransactionSuccessful(stub, setDetailsTransaction)) {
//                emitter.onError(new RuntimeException("Transaction failed"));
//            } else {
//                emitter.onComplete();
//            }
        });
    }
}