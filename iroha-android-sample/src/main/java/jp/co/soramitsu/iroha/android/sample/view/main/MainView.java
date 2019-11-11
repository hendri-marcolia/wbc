package jp.co.soramitsu.iroha.android.sample.view.main;

interface MainView {
    void setUsername(String username);

    void setAccountDetails(String details);

    void setAccountBalance(String balance);

    void showLoginScreen();

    void showProgress();

    void hideProgress();

    void showError(Throwable throwable);

    void showInfo(String info);

    void hideRefresh();

    void refreshData(boolean animate);
}