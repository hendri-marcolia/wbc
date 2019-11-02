package jp.co.soramitsu.iroha.android.sample.view.main.history;

public interface HistoryView {

    void finishRefresh();

    void didError(Throwable error);

}
