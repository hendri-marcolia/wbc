package jp.co.soramitsu.iroha.android.sample.interactor.register;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import jp.co.soramitsu.iroha.android.sample.EndPoint.EndPoint;
import jp.co.soramitsu.iroha.android.sample.data.HttpResult;
import jp.co.soramitsu.iroha.android.sample.data.Validate;
import jp.co.soramitsu.iroha.android.sample.injection.ApplicationModule;
import jp.co.soramitsu.iroha.android.sample.interactor.SingleInteractor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

import static jp.co.soramitsu.iroha.android.sample.Constants.BANK_URL;

public class ValidateAccountInteractor extends SingleInteractor<HttpResult, Validate> {


    @Inject
    ValidateAccountInteractor(@Named(ApplicationModule.JOB) Scheduler jobScheduler,
                              @Named(ApplicationModule.UI) Scheduler uiScheduler) {
        super(jobScheduler, uiScheduler);

    }

    @Override
    protected Single<HttpResult> build(Validate parameter) {
        return EndPoint.wbcApi().validateAccount(parameter);
    }

}
