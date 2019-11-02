package jp.co.soramitsu.iroha.android.sample.view.registration;

import javax.inject.Inject;

import lombok.Setter;

public class RegistrationPresenter {

    @Setter
    private RegistrationView view;

    @Inject
    public RegistrationPresenter() {}

    public void ValidateRegistrationForm(String accountId,
                                         String fullName,
                                         String birthDate,
                                         String nationality,
                                         String nationalId) {

        boolean allValid = true;

        if (accountId.isEmpty()) {
            view.setAccountIdStatus(true, false);
            allValid = false;
        } else {
            view.setAccountIdStatus(false, false);
        }

        if (allValid) view.backToLogin();
    }

}
