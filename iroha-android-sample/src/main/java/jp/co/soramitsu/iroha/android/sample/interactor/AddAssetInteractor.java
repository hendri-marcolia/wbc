package jp.co.soramitsu.iroha.android.sample.interactor;



import javax.inject.Inject;
import javax.inject.Named;

import io.grpc.ManagedChannel;
import io.reactivex.Completable;
import io.reactivex.Scheduler;
import jp.co.soramitsu.iroha.android.sample.PreferencesUtil;
import jp.co.soramitsu.iroha.android.sample.injection.ApplicationModule;

import static jp.co.soramitsu.iroha.android.sample.Constants.ASSET_ID;
import static jp.co.soramitsu.iroha.android.sample.Constants.CONNECTION_TIMEOUT_SECONDS;
import static jp.co.soramitsu.iroha.android.sample.Constants.CREATOR;
import static jp.co.soramitsu.iroha.android.sample.Constants.DOMAIN_ID;
import static jp.co.soramitsu.iroha.android.sample.Constants.PRIV_KEY;
import static jp.co.soramitsu.iroha.android.sample.Constants.PUB_KEY;

public class AddAssetInteractor extends CompletableInteractor<String> {

    private final ManagedChannel channel;
    private final PreferencesUtil preferenceUtils;

    @Inject
    AddAssetInteractor(@Named(ApplicationModule.JOB) Scheduler jobScheduler,
                       @Named(ApplicationModule.UI) Scheduler uiScheduler,
                       ManagedChannel managedChannel, PreferencesUtil preferencesUtil) {
        super(jobScheduler, uiScheduler);
        this.channel = managedChannel;
        this.preferenceUtils = preferencesUtil;
    }

    @Override
    protected Completable build(String details) {
        return Completable.create(emitter -> {
//            long currentTime = System.currentTimeMillis();
//            Keypair adminKeys = crypto.convertFromExisting(PUB_KEY, PRIV_KEY);
//            String username = preferenceUtils.retrieveUsername();
//
//            //Adding asset
//            UnsignedTx addAssetTx = txBuilder.creatorAccountId(CREATOR)
//                    .createdTime(BigInteger.valueOf(currentTime))
//                    .addAssetQuantity(CREATOR, ASSET_ID, "100")
//                    .transferAsset(CREATOR, username + "@" + DOMAIN_ID, ASSET_ID, "initial", "100")
//                    .build();
//
//            protoTxHelper = new ModelProtoTransaction(addAssetTx);
//            ByteVector txblob = protoTxHelper.signAndAddSignature(adminKeys).finish().blob();
//            byte[] bsq = toByteArray(txblob);
//            BlockOuterClass.Transaction protoTx = null;
//
//            try {
//                protoTx = BlockOuterClass.Transaction.parseFrom(bsq);
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
//            if (!isTransactionSuccessful(stub, addAssetTx)) {
//                emitter.onError(new RuntimeException("Transaction failed"));
//            } else {
//                emitter.onComplete();
//            }
        });
    }
}