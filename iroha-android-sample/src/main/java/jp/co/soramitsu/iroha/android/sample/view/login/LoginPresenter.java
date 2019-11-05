package jp.co.soramitsu.iroha.android.sample.view.login;

import javax.inject.Inject;

import jp.co.soramitsu.iroha.android.sample.PreferencesUtil;
import jp.co.soramitsu.iroha.android.sample.data.Validate;
import jp.co.soramitsu.iroha.android.sample.interactor.ValidateAccountInteractor;
import lombok.Setter;

public class LoginPresenter {

    private final PreferencesUtil preferencesUtil;
    private final ValidateAccountInteractor validateAccountInteractor;

    @Setter
    private LoginView view;

    @Inject
    public LoginPresenter(PreferencesUtil preferencesUtil,
                          ValidateAccountInteractor validateAccountInteractor) {
        this.preferencesUtil = preferencesUtil;
        this.validateAccountInteractor = validateAccountInteractor;
    }

    public void loginFromBiometric() {
        view.openMainView();
    }

    public void loginFromPassword(String accountId, String privateKey) {
        Validate validate = new Validate();
        validate.setAccountId(accountId);

        validateAccountInteractor.execute(validate, validateResult -> {
            if (validateResult.getHttpResult().equals("200")) {
                view.openMainView();
            } else {
                // user not found;
            }
        }, throwable -> {
            // Connection error
        });
    }

}
