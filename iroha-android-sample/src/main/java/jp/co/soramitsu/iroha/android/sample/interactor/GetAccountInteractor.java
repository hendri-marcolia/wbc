package jp.co.soramitsu.iroha.android.sample.interactor;

import android.util.Log;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;

import java.math.BigInteger;
import java.security.KeyPair;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import io.grpc.ManagedChannel;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import iroha.protocol.QryResponses;
import iroha.protocol.Queries;
import jp.co.soramitsu.iroha.android.sample.PreferencesUtil;
import jp.co.soramitsu.iroha.android.sample.injection.ApplicationModule;
import jp.co.soramitsu.iroha.java.IrohaAPI;
import jp.co.soramitsu.iroha.java.Query;


public class GetAccountInteractor extends SingleInteractor<QryResponses.Account, String> {

    private final ManagedChannel channel;
    private final PreferencesUtil preferenceUtils;

    @Inject
    GetAccountInteractor(@Named(ApplicationModule.JOB) Scheduler jobScheduler,
                         @Named(ApplicationModule.UI) Scheduler uiScheduler, ManagedChannel channel,
                         PreferencesUtil preferencesUtil) {
        super(jobScheduler, uiScheduler);
        this.channel = channel;
        this.preferenceUtils = preferencesUtil;
    }

    @Override
    protected Single<QryResponses.Account> build(String accountId) {
        return Single.create(emitter -> {
            long currentTime = System.currentTimeMillis();
            try {
                KeyPair userKeys = preferenceUtils.retrieveKeys();
                String username = preferenceUtils.retrieveUsername();
                String domain = preferenceUtils.retrieveDomain();
                String USR = String.format("%s@%s",username,domain);
                IrohaAPI irohaAPI = getIrohaAPI();
                Queries.Query q = Query.builder(USR, currentTime, 1).getAccount(USR).buildSigned(userKeys);
                QryResponses.AccountResponse response = irohaAPI.query(q).getAccountResponse();
                Log.d(USR, "Has account: " + response.hasAccount());
                irohaAPI.terminate();
                if (response.hasAccount()){
                    emitter.onSuccess(response.getAccount());
                }

            }catch (Exception e){
                emitter.onError(new Throwable("Failed to get Account"));
            }
//            Keypair adminKeys = crypto.convertFromExisting(PUB_KEY, PRIV_KEY);
//
//            // GetAccount
//            UnsignedQuery query = modelQueryBuilder
//                    .createdTime(BigInteger.valueOf(currentTime))
//                    .queryCounter(BigInteger.valueOf(QUERY_COUNTER))
//                    .creatorAccountId(CREATOR)
//                    .getAccount(accountId + "@" + DOMAIN_ID)
//                    .build();
//
//
//            // sign transaction and get its binary representation (Blob)
//            protoQueryHelper = new ModelProtoQuery(query);
//            ByteVector queryBlob = protoQueryHelper.signAndAddSignature(adminKeys).finish().blob();
//            byte bquery[] = toByteArray(queryBlob);
//
//            Queries.Query protoQuery = null;
//            try {
//                protoQuery = Queries.Query.parseFrom(bquery);
//            } catch (InvalidProtocolBufferException e) {
//                emitter.onError(e);
//            }
//
//            QueryServiceGrpc.QueryServiceBlockingStub queryStub = QueryServiceGrpc.newBlockingStub(channel)
//                    .withDeadlineAfter(CONNECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
//            Responses.QueryResponse queryResponse = queryStub.find(protoQuery);
//
//            emitter.onSuccess(queryResponse.getAccountResponse().getAccount());
        });
    }
}