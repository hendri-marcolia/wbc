package jp.co.soramitsu.iroha.android.sample.interactor;

import com.google.protobuf.InvalidProtocolBufferException;

import java.math.BigInteger;
import java.security.KeyPair;

import javax.inject.Inject;
import javax.inject.Named;

import io.grpc.ManagedChannel;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import iroha.protocol.QryResponses;
import jp.co.soramitsu.iroha.android.sample.PreferencesUtil;
import jp.co.soramitsu.iroha.android.sample.injection.ApplicationModule;
import jp.co.soramitsu.iroha.java.IrohaAPI;
import jp.co.soramitsu.iroha.java.Query;

public class GetAccountBalanceInteractor extends SingleInteractor<Long, Void> {

    private final PreferencesUtil preferenceUtils;
    private final ManagedChannel channel;

    @Inject
    GetAccountBalanceInteractor(@Named(ApplicationModule.JOB) Scheduler jobScheduler,
                                @Named(ApplicationModule.UI) Scheduler uiScheduler,
                                PreferencesUtil preferenceUtils, ManagedChannel channel) {
        super(jobScheduler, uiScheduler);
        this.preferenceUtils = preferenceUtils;
        this.channel = channel;
    }

    @Override
    protected Single<Long> build(Void v) {
        return Single.create(emitter -> {
            try {
                long currentTime = System.currentTimeMillis();
                KeyPair userKeys = preferenceUtils.retrieveKeys();
                String username = preferenceUtils.retrieveUsername();
                String domain = preferenceUtils.retrieveDomain();
                String USR = String.format("%s@%s",username,domain);
                IrohaAPI api = getIrohaAPI();
                QryResponses.QueryResponse resp = api.query(Query.builder(USR, currentTime, 1)
                        .getAccountAssets(USR)
                        .buildSigned(userKeys));
                try {
                    if (resp.getAccountAssetsResponse().getAccountAssetsCount() < 1) {
                        emitter.onSuccess(0l);
                    } else {
                        resp.getAccountAssetsResponse().getAccountAssetsList().forEach(accountAsset -> {
                            if (accountAsset.getAssetId().startsWith("online")){
                                emitter.onSuccess(Long.parseLong(accountAsset.getBalance()));
                            }
                        });
                    }
                }catch (Exception e) {
                    emitter.onSuccess(0l);
                }
            }catch (Exception e) {
                emitter.onError(e);
            }

            //long currentTime = System.currentTimeMillis();

//            Keypair userKeys = preferenceUtils.retrieveKeys();
//            String username = preferenceUtils.retrieveUsername();
//
//            UnsignedQuery accountBalanceQuery = modelQueryBuilder.creatorAccountId(username + "@" + DOMAIN_ID)
//                    .queryCounter(BigInteger.valueOf(QUERY_COUNTER))
//                    .createdTime(BigInteger.valueOf(currentTime))
//                    .getAccountAssets(username + "@" + DOMAIN_ID)
//                    .build();
//
//            protoQueryHelper = new ModelProtoQuery(accountBalanceQuery);
//            ByteVector queryBlob = protoQueryHelper.signAndAddSignature(userKeys).finish().blob();
//            byte bquery[] = toByteArray(queryBlob);
//
//            Queries.Query protoQuery = null;
//            try {
//                protoQuery = Queries.Query.parseFrom(bquery);
//            } catch (InvalidProtocolBufferException e) {
//                emitter.onError(e);
//            }
//
//            QueryServiceGrpc.QueryServiceBlockingStub queryStub = QueryServiceGrpc.newBlockingStub(channel);
//            Responses.QueryResponse queryResponse = queryStub.find(protoQuery);
//
//            emitter.onSuccess(getIntBalance(queryResponse.getAccountAssetsResponse().getAccountAssets(0).getBalance()));
        });
    }
}
