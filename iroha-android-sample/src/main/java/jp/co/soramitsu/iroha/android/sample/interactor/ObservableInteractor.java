package jp.co.soramitsu.iroha.android.sample.interactor;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public abstract class ObservableInteractor<ParameterType> extends Interactor {

    protected ObservableInteractor(Scheduler jobScheduler, Scheduler uiScheduler) {
        super(jobScheduler, uiScheduler);
    }

    protected abstract Observable build(ParameterType parameter);

    public void execute(ParameterType parameter,Consumer onNext, Action onSuccess, Consumer<Throwable> onError) {
        subscriptions.add(build(parameter)
                .subscribeOn(jobScheduler)
                .observeOn(uiScheduler)
                .subscribe(onNext, onError, onSuccess));
    }
}