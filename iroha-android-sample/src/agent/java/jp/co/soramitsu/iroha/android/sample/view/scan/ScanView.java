package jp.co.soramitsu.iroha.android.sample.view.scan;

public interface ScanView {
    void showError(Throwable e);

    void showInfo(String msg);

    void showLoading();

}
