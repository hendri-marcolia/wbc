package jp.co.soramitsu.iroha.android.sample.view.login;

import android.util.Log;

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
            Log.d(LoginPresenter.class.getSimpleName(), "HTTP: " + validateResult.getHttpResult() + ", MSG: " + validateResult.getMessage());
            if (validateResult.getHttpResult() == 200) {
                preferencesUtil.saveUsername(accountId);
                preferencesUtil.saveKeys(privateKey);
                view.openMainView();
            } else {
                // user not found;
            }
        }, throwable -> {
            // Connection error
            Log.d(LoginPresenter.class.getSimpleName(), throwable.getMessage());
        });
    }

}
