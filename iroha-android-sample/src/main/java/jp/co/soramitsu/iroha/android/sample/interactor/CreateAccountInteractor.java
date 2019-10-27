package jp.co.soramitsu.iroha.android.sample.interactor;


import com.google.protobuf.InvalidProtocolBufferException;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import io.grpc.ManagedChannel;
import io.reactivex.Completable;
import io.reactivex.Scheduler;
import jp.co.soramitsu.iroha.android.sample.PreferencesUtil;
import jp.co.soramitsu.iroha.android.sample.injection.ApplicationModule;

import static jp.co.soramitsu.iroha.android.sample.Constants.CONNECTION_TIMEOUT_SECONDS;
import static jp.co.soramitsu.iroha.android.sample.Constants.CREATOR;
import static jp.co.soramitsu.iroha.android.sample.Constants.DOMAIN_ID;
import static jp.co.soramitsu.iroha.android.sample.Constants.PRIV_KEY;
import static jp.co.soramitsu.iroha.android.sample.Constants.PUB_KEY;

public class CreateAccountInteractor extends CompletableInteractor<String> {

    private final ManagedChannel channel;
    private final PreferencesUtil preferenceUtils;


    @Inject
    CreateAccountInteractor(@Named(ApplicationModule.JOB) Scheduler jobScheduler,
                            @Named(ApplicationModule.UI) Scheduler uiScheduler,
                            ManagedChannel managedChannel,
                            PreferencesUtil preferencesUtil) {
        super(jobScheduler, uiScheduler);
        this.channel = managedChannel;
        this.preferenceUtils = preferencesUtil;
    }

    @Override
    protected Completable build(String username) {
        return Completable.create(emitter -> {
            long currentTime = System.currentTimeMillis();
//            Keypair userKeys = crypto.generateKeypair();
//            Keypair adminKeys = crypto.convertFromExisting(PUB_KEY, PRIV_KEY);
//
//            // Create account
//            UnsignedTx createAccount = txBuilder.creatorAccountId(CREATOR)
//                    .createdTime(BigInteger.valueOf(currentTime))
//                    .createAccount(username, DOMAIN_ID, userKeys.publicKey())
//                    .build();
//
//            // sign transaction and get its binary representation (Blob)
//            protoTxHelper = new ModelProtoTransaction(createAccount);
//            ByteVector txblob = protoTxHelper.signAndAddSignature(adminKeys).finish().blob();
//            // Convert ByteVector to byte array
//            byte bs[] = toByteArray(txblob);
//
//            // create proto object
//            BlockOuterClass.Transaction protoTx = null;
//            try {
//                protoTx = BlockOuterClass.Transaction.parseFrom(bs);
//            } catch (InvalidProtocolBufferException e) {
//                emitter.onError(e);
//            }
//
//            // Send transaction to iroha
//            CommandServiceGrpc.CommandServiceBlockingStub stub = CommandServiceGrpc.newBlockingStub(channel)
//                    .withDeadlineAfter(CONNECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
//            stub.torii(protoTx);
//
//            // Check if it was successful
//            if (!isTransactionSuccessful(stub, createAccount)) {
//                emitter.onError(new RuntimeException("Transaction failed"));
//            } else {
//                preferenceUtils.saveKeys(userKeys);
//                preferenceUtils.saveUsername(username);
//                emitter.onComplete();
//            }
        });
    }
}