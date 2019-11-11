package jp.co.soramitsu.iroha.android.sample.view.main;


import javax.inject.Inject;

import jp.co.soramitsu.iroha.android.sample.MyUtils;
import jp.co.soramitsu.iroha.android.sample.PreferencesUtil;
import jp.co.soramitsu.iroha.android.sample.SampleApplication;
import jp.co.soramitsu.iroha.android.sample.data.Account;
import jp.co.soramitsu.iroha.android.sample.interactor.GetAccountBalanceInteractor;
import jp.co.soramitsu.iroha.android.sample.interactor.GetAccountDetailsInteractor;
import jp.co.soramitsu.iroha.android.sample.interactor.GetAccountInteractor;
import jp.co.soramitsu.iroha.android.sample.interactor.SetAccountDetailsInteractor;
import lombok.Setter;

public class MainPresenter {

    private final PreferencesUtil preferencesUtil;
    private final SetAccountDetailsInteractor setAccountDetails;
    private final GetAccountDetailsInteractor getAccountDetails;
    private final GetAccountInteractor getAccountInteractor;
    private final GetAccountBalanceInteractor getAccountBalanceInteractor;

    @Setter
    private MainView view;

    @Inject
    public MainPresenter(PreferencesUtil preferencesUtil,
                         SetAccountDetailsInteractor setAccountDetails,
                         GetAccountDetailsInteractor getAccountDetails,
                         GetAccountInteractor getAccountInteractor,
                         GetAccountBalanceInteractor getAccountBalanceInteractor) {
        this.preferencesUtil = preferencesUtil;
        this.setAccountDetails = setAccountDetails;
        this.getAccountDetails = getAccountDetails;
        this.getAccountInteractor = getAccountInteractor;
        this.getAccountBalanceInteractor = getAccountBalanceInteractor;
    }

    void onCreate() {
        updateData(false);
    }


    void updateData(boolean fromRefresh) {
        String username = preferencesUtil.retrieveUsername();
        view.setUsername(username);

        getAccountInteractor.execute(username,
                account -> {
                    SampleApplication.instance.account = new Account(account, -1);
                    getAccountDetails.execute(
                            details -> {
                                view.setAccountDetails(details);
                                view.hideRefresh();
                            },
                            throwable -> view.showError(throwable)
                    );
                    getAccountBalanceInteractor.execute(
                            balance -> {
                                if (fromRefresh) {
                                    view.hideRefresh();
                                }
                                view.setAccountBalance(MyUtils.formatIDR(balance));
                                SampleApplication.instance.account.setBalance(balance);
                            },
                            throwable -> view.showError(throwable));
                },
                throwable -> view.showError(throwable)
        );


    }

    void logout() {
        preferencesUtil.clear();
        view.showLoginScreen();
    }

    void setAccountDetails(String details) {
        view.showProgress();
        setAccountDetails.execute(details, () -> {
            view.hideProgress();
            view.setAccountDetails(details);
        }, throwable -> {
            view.showError(throwable);
            view.hideProgress();
        });
    }

    void onDestroy() {
        view = null;
        setAccountDetails.unsubscribe();
        getAccountDetails.unsubscribe();
        getAccountInteractor.unsubscribe();
        getAccountBalanceInteractor.unsubscribe();
    }
}