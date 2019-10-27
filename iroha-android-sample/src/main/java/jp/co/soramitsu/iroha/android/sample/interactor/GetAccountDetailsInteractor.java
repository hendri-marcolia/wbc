package jp.co.soramitsu.iroha.android.sample.interactor;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.protobuf.InvalidProtocolBufferException;

import java.math.BigInteger;
import java.security.KeyPair;

import javax.inject.Inject;
import javax.inject.Named;

import io.grpc.ManagedChannel;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import iroha.protocol.QryResponses;
import iroha.protocol.Queries;
import jp.co.soramitsu.iroha.android.sample.Constants;
import jp.co.soramitsu.iroha.android.sample.PreferencesUtil;
import jp.co.soramitsu.iroha.android.sample.injection.ApplicationModule;
import jp.co.soramitsu.iroha.java.IrohaAPI;
import jp.co.soramitsu.iroha.java.Query;

import static jp.co.soramitsu.iroha.android.sample.Constants.DOMAIN_ID;
import static jp.co.soramitsu.iroha.android.sample.Constants.QUERY_COUNTER;

public class GetAccountDetailsInteractor extends SingleInteractor<String, Void> {


    private final PreferencesUtil preferenceUtils;
    private final ManagedChannel channel;

    @Inject
    GetAccountDetailsInteractor(@Named(ApplicationModule.JOB) Scheduler jobScheduler,
                                @Named(ApplicationModule.UI) Scheduler uiScheduler,
                                PreferencesUtil preferenceUtils, ManagedChannel channel) {
        super(jobScheduler, uiScheduler);
        this.preferenceUtils = preferenceUtils;
        this.channel = channel;
    }

    @Override
    protected Single<String> build(Void v) {
        return Single.create(emitter -> {
            long currentTime = System.currentTimeMillis();
            try {
                KeyPair userKeys = preferenceUtils.retrieveKeys();
                String username = preferenceUtils.retrieveUsername();
                String domain = DOMAIN_ID;
                String USR = String.format("%s@%s",username,domain);
                IrohaAPI irohaAPI = getIrohaAPI();
                Queries.Query q = Query.builder(USR, 1).getAccountDetail(USR,null,Constants.ACCOUNT_DETAILS,10,null,null).buildSigned(userKeys);
                QryResponses.AccountDetailResponse response = irohaAPI.query(q).getAccountDetailResponse();
                JsonElement jsonElement = new Gson().fromJson(response.getDetail(), JsonObject.class).get(USR);
                emitter.onSuccess(jsonElement != null ? jsonElement.getAsJsonObject().get(Constants.ACCOUNT_DETAILS).getAsString() : "");
            }catch (Exception e){
                emitter.onError(new Throwable("Failed to get Account detail"));
            }
//            Keypair userKeys = preferenceUtils.retrieveKeys();
//            String username = preferenceUtils.retrieveUsername();
//
//            UnsignedQuery accountDetails = modelQueryBuilder.creatorAccountId(username + "@" + DOMAIN_ID)
//                    .queryCounter(BigInteger.valueOf(QUERY_COUNTER))
//                    .createdTime(BigInteger.valueOf(currentTime))
//                    .getAccountDetail(username + "@" + DOMAIN_ID)
//                    .build();
//
//            protoQueryHelper = new ModelProtoQuery(accountDetails);
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
//            JsonElement jsonElement = new Gson().fromJson(queryResponse.getAccountDetailResponse().getDetail(), JsonObject.class).get(username + "@" + DOMAIN_ID);
//            ;
//            String detail = jsonElement != null ? jsonElement.getAsJsonObject().get(Constants.ACCOUNT_DETAILS).getAsString() : "";
//
//            emitter.onSuccess(detail);
        });
    }
}