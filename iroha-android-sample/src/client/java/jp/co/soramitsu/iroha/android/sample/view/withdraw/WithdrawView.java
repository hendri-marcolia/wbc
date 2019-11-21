package jp.co.soramitsu.iroha.android.sample.view.withdraw;

public interface WithdrawView {
    void showError(Throwable e);

    void showInfo(String msg);

    void showLoading();

    void hideLoading();


}
