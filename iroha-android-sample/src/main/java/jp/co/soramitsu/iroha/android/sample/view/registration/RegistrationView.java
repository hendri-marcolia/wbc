package jp.co.soramitsu.iroha.android.sample.view.registration;

public interface RegistrationView {
    void backToLogin();

    void setAccountIdStatus(boolean empty, boolean exist);

    void setFullNameStatus(boolean valid);

    void setBirthDateStatus(boolean valid);

    void setNationalityStatus(boolean valid);

    void setNationalIdStatus(boolean valid);

    void setEmailStatus(boolean valid);

    void setPhoneStatus(boolean valid);

}
