package jp.co.soramitsu.iroha.android.sample.EndPoint;

import io.reactivex.Single;
import jp.co.soramitsu.iroha.android.sample.data.HttpResult;
import jp.co.soramitsu.iroha.android.sample.data.PerformSavePayload;
import jp.co.soramitsu.iroha.android.sample.data.Registration;
import jp.co.soramitsu.iroha.android.sample.data.Transaction;
import jp.co.soramitsu.iroha.android.sample.data.Validate;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface WbcApi {
    @POST("validate_account")
    Single<HttpResult> validateAccount(@Body Validate validate);

    @GET("check_accountid")
    Single<HttpResult> checkAccountId(@Query("account_id") String accountId);

    @POST("register_account")
    Single<HttpResult> registerAccount(@Body Registration registration);

    @POST("perform_save")
    Single<HttpResult> performSave(@Body PerformSavePayload payload);

    @POST("perform_save_offline")
    Single<HttpResult> performSaveOffline(@Body Transaction transactionPayload);


    @POST("perform_withdraw")
    Single<HttpResult> performWithdraw(@Body PerformSavePayload payload);
}
