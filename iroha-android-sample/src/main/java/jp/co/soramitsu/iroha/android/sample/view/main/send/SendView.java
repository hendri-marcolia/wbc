package jp.co.soramitsu.iroha.android.sample.view.main.send;

public interface SendView {

    void didSendSuccess();

    void didSendError(Throwable error);

}
