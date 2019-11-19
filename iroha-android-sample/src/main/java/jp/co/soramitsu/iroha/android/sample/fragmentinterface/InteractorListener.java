package jp.co.soramitsu.iroha.android.sample.fragmentinterface;

public interface InteractorListener<T> {
    long id = 0;
    void onNext(Object object);
    void onComplete(T t);
    void onError(Throwable throwable);
}
