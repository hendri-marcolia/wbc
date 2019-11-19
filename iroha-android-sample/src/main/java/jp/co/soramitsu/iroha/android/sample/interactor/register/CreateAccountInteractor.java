package jp.co.soramitsu.iroha.android.sample.interactor.register;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import jp.co.soramitsu.iroha.android.sample.EndPoint.EndPoint;
import jp.co.soramitsu.iroha.android.sample.data.HttpResult;
import jp.co.soramitsu.iroha.android.sample.data.Registration;
import jp.co.soramitsu.iroha.android.sample.injection.ApplicationModule;
import jp.co.soramitsu.iroha.android.sample.interactor.SingleInteractor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

import static jp.co.soramitsu.iroha.android.sample.Constants.BANK_URL;

public class CreateAccountInteractor extends SingleInteractor<HttpResult, Registration> {


    @Inject
    CreateAccountInteractor(@Named(ApplicationModule.JOB) Scheduler jobScheduler,
                            @Named(ApplicationModule.UI) Scheduler uiScheduler) {
        super(jobScheduler, uiScheduler);

    }

    @Override
    protected Single<HttpResult> build(Registration parameter) {
        return EndPoint.wbcApi().registerAccount(parameter);
    }

}