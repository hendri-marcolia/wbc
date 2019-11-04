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

        if (allValid) view.backToLogin();
    }

}
