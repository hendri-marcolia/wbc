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
import retrofit2.http.Body;
import retrofit2.http.POST;

public class CheckAccountIdInteractor extends SingleInteractor<HttpResult, String> {



    @Inject
    CheckAccountIdInteractor(@Named(ApplicationModule.JOB) Scheduler jobScheduler,
                             @Named(ApplicationModule.UI) Scheduler uiScheduler) {
        super(jobScheduler, uiScheduler);
    }

    @Override
    protected Single<HttpResult> build(String parameter) {
        return EndPoint.wbcApi().checkAccountId(parameter);
    }

}
