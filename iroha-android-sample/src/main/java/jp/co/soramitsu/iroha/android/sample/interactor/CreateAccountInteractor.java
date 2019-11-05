package jp.co.soramitsu.iroha.android.sample.interactor;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import jp.co.soramitsu.iroha.android.sample.data.HttpResult;
import jp.co.soramitsu.iroha.android.sample.data.Registration;
import jp.co.soramitsu.iroha.android.sample.injection.ApplicationModule;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

import static jp.co.soramitsu.iroha.android.sample.Constants.BANK_URL;

public class CreateAccountInteractor extends SingleInteractor<HttpResult, Registration> {

    private Retrofit retrofit;
    private CreateAccountApi api;

    @Inject
    CreateAccountInteractor(@Named(ApplicationModule.JOB) Scheduler jobScheduler,
                            @Named(ApplicationModule.UI) Scheduler uiScheduler) {
        super(jobScheduler, uiScheduler);

        retrofit = new Retrofit
                .Builder()
                .baseUrl(BANK_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        api = retrofit.create(CreateAccountApi.class);
    }

    @Override
    protected Single<HttpResult> build(Registration parameter) {
        return api.registerAccount(parameter);
    }

    private interface CreateAccountApi {
        @POST("register_account")
        Single<HttpResult> registerAccount(@Body Registration registration);
    }
}