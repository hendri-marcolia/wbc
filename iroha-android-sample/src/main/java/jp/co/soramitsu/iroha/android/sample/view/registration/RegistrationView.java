package jp.co.soramitsu.iroha.android.sample.view.registration;

public interface RegistrationView {
    void backToLogin();

    void setAccountIdValidation(boolean empty, boolean exist);

    void setFullNameValidation(boolean valid);

    void setKtpValidation(boolean valid);

    void setBankAccountValidation(boolean valid);
}
