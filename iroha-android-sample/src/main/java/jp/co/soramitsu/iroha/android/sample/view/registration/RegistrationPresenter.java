package jp.co.soramitsu.iroha.android.sample.view.registration;

import java.security.KeyPair;

import javax.inject.Inject;

import jp.co.soramitsu.crypto.ed25519.Ed25519Sha3;
import jp.co.soramitsu.iroha.android.sample.PreferencesUtil;
import jp.co.soramitsu.iroha.android.sample.data.Registration;
import jp.co.soramitsu.iroha.android.sample.data.Validate;
import jp.co.soramitsu.iroha.android.sample.interactor.CreateAccountInteractor;
import jp.co.soramitsu.iroha.android.sample.interactor.ValidateAccountInteractor;
import jp.co.soramitsu.iroha.java.Utils;
import lombok.Setter;

public class RegistrationPresenter {

    private final PreferencesUtil preferencesUtil;
    private final ValidateAccountInteractor validateAccountInteractor;
    private final CreateAccountInteractor createAccountInteractor;
    private final Ed25519Sha3 crypto;

    @Setter
    private RegistrationView view;

    @Inject
    public RegistrationPresenter(PreferencesUtil preferencesUtil,
                                 CreateAccountInteractor createAccountInteractor,
                                 ValidateAccountInteractor validateAccountInteractor) {
        this.preferencesUtil = preferencesUtil;
        this.createAccountInteractor = createAccountInteractor;
        this.validateAccountInteractor = validateAccountInteractor;
        this.crypto = new Ed25519Sha3();
    }

    public void createAccount(String accountId, String fullName, String ktp, String bankAccount) {
        boolean result = true;

        if (accountId.isEmpty()) {
            view.setAccountIdValidation(true, false);
            result = false;
        }

        if (fullName.isEmpty()) {
            view.setFullNameValidation(false);
            result = false;
        }

        if (ktp.isEmpty()) {
            view.setKtpValidation(false);
            result = false;
        }

        if (bankAccount.isEmpty()) {
            view.setBankAccountValidation(false);
            result = false;
        }

        if (result) {
            Validate validate = new Validate();
            validate.setAccountId(accountId);

            validateAccountInteractor.execute(validate, validateResult -> {
                if (validateResult.getHttpResult().equals("200")) {
                    KeyPair keyPair = crypto.generateKeypair();

                    preferencesUtil.saveUsername(accountId);
                    preferencesUtil.saveKeys(Utils.toHex(keyPair.getPrivate().getEncoded()));

                    Registration registration = new Registration();
                    registration.setAccountId(accountId);
                    registration.setFullName(fullName);
                    registration.setBankAccount(bankAccount);
                    registration.setKtp(ktp);
                    registration.setFcmId("");
                    registration.setAccountPublicKey(Utils.toHex(keyPair.getPublic().getEncoded()));

                    createAccountInteractor.execute(registration, registrationResult -> {
                        if (validateResult.getHttpResult().equals("200")) {
                            // success
                        } else {
                            // error
                        }
                    }, throwable -> {
                        // handling error when request server.
                    });
                } else {
                    view.setAccountIdValidation(false, true);
                }
            }, throwable -> {
                // handling error when request server.
            });
        }
    }
}
