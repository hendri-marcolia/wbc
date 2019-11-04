package jp.co.soramitsu.iroha.android.sample.view.registration;

import javax.inject.Inject;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import jp.co.soramitsu.iroha.android.sample.PreferencesUtil;
import jp.co.soramitsu.iroha.android.sample.interactor.CreateAccountInteractor;
import lombok.Setter;

public class RegistrationPresenter {

    private final PreferencesUtil preferencesUtil;
    private final CreateAccountInteractor createAccountInteractor;

    @Setter
    private RegistrationView view;

    @Inject
    public RegistrationPresenter(PreferencesUtil preferencesUtil,
                                 CreateAccountInteractor createAccountInteractor) {
        this.preferencesUtil = preferencesUtil;
        this.createAccountInteractor = createAccountInteractor;
    }

    public void createAccount(String accountId) {
        createAccountInteractor.execute(accountId, () -> {

        }, throwable -> {

        });
    }

    public boolean validateRegistrationForm(String accountId,
                                            String fullName,
                                            String email,
                                            String birthDate,
                                            String nationality,
                                            String nationalId,
                                            String phone) {

        boolean allValid = true;

        if (accountId.isEmpty()) {
            view.setAccountIdStatus(true, false);
            allValid = false;
        } else {
            view.setAccountIdStatus(false, false);
        }

        if (fullName.isEmpty()) {
            view.setFullNameStatus(false);
            allValid = false;
        } else {
            view.setFullNameStatus(true);
        }

        if (email.isEmpty()) {
            view.setEmailStatus(false);
            allValid = false;
        } else {
            view.setEmailStatus(true);
        }

        if (birthDate.isEmpty()) {
            view.setBirthDateStatus(false);
            allValid = false;
        } else {
            view.setBirthDateStatus(true);
        }

        if (nationality.isEmpty()) {
            view.setNationalityStatus(false);
            allValid = false;
        } else {
            view.setNationalityStatus(true);
        }

        if (nationalId.isEmpty()) {
            view.setNationalIdStatus(false);
            allValid = false;
        } else {
            view.setNationalIdStatus(true);
        }

        if (phone.isEmpty()) {
            view.setPhoneStatus(false);
            allValid = false;
        } else {
            view.setPhoneStatus(true);
        }

        return allValid;
    }

}
