package jp.co.soramitsu.iroha.android.sample.view.login;

import javax.inject.Inject;

import lombok.Setter;

public class LoginPresenter {

    @Setter
    private LoginView view;

    @Inject
    public LoginPresenter() {}

    public void loginFromBiometric() {
        view.openMainView();
    }

    public void loginFromPassword(String accountId, String privateKey) {
        view.openMainView();
    }

}
