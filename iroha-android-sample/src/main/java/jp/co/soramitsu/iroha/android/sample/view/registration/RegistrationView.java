package jp.co.soramitsu.iroha.android.sample.view.registration;

public interface RegistrationView {
    void setAccountIdValidation(boolean valid, String msg);

    void setFullNameValidation(boolean valid);

    void setKtpValidation(boolean valid);

    void setBankAccountValidation(boolean valid);

    void openMainView();

    void showError(Throwable e);

    void showInfo(String info);

    void createProgressDialog();

    void hideProgressDialog();

}
