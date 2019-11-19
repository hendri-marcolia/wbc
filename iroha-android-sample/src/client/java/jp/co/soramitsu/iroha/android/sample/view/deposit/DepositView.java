package jp.co.soramitsu.iroha.android.sample.view.deposit;

public interface DepositView {
    void showError(Throwable e);

    void showInfo(String msg);

    void showLoading();

    void hideLoading();

}
