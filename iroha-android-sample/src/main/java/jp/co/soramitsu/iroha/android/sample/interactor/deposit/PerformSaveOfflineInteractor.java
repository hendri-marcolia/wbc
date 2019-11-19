package jp.co.soramitsu.iroha.android.sample.interactor.deposit;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import jp.co.soramitsu.iroha.android.sample.EndPoint.EndPoint;
import jp.co.soramitsu.iroha.android.sample.data.HttpResult;
import jp.co.soramitsu.iroha.android.sample.data.PerformSavePayload;
import jp.co.soramitsu.iroha.android.sample.data.Registration;
import jp.co.soramitsu.iroha.android.sample.data.Transaction;
import jp.co.soramitsu.iroha.android.sample.injection.ApplicationModule;
import jp.co.soramitsu.iroha.android.sample.interactor.SingleInteractor;

public class PerformSaveOfflineInteractor extends SingleInteractor<HttpResult, Transaction> {


    @Inject
    PerformSaveOfflineInteractor(@Named(ApplicationModule.JOB) Scheduler jobScheduler,
                                 @Named(ApplicationModule.UI) Scheduler uiScheduler) {
        super(jobScheduler, uiScheduler);

    }

    @Override
    protected Single<HttpResult> build(Transaction parameter) {
        return EndPoint.wbcApi().performSaveOffline(parameter);
    }

}