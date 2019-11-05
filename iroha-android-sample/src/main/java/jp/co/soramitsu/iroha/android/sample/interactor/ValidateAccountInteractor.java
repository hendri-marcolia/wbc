package jp.co.soramitsu.iroha.android.sample.interactor;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import jp.co.soramitsu.iroha.android.sample.data.HttpResult;
import jp.co.soramitsu.iroha.android.sample.data.Validate;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

import static jp.co.soramitsu.iroha.android.sample.Constants.BANK_URL;

public class ValidateAccountInteractor extends SingleInteractor<HttpResult, Validate> {

    private Retrofit retrofit;
    private ValidateAccountApi api;

    @Inject
    ValidateAccountInteractor(Scheduler jobScheduler, Scheduler uiScheduler) {
        super(jobScheduler, uiScheduler);
        retrofit = new Retrofit
                .Builder()
                .baseUrl(BANK_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        api = retrofit.create(ValidateAccountApi.class);
    }

    @Override
    protected Single<HttpResult> build(Validate parameter) {
        return api.validateAccount(parameter);
    }

    private interface ValidateAccountApi {
        @POST("validate_account")
        Single<HttpResult> validateAccount(@Body Validate validate);
    }
}
