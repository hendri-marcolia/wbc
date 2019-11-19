package jp.co.soramitsu.iroha.android.sample.view.registration;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.security.KeyPair;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import jp.co.soramitsu.crypto.ed25519.Ed25519Sha3;
import jp.co.soramitsu.iroha.android.sample.PreferencesUtil;
import jp.co.soramitsu.iroha.android.sample.R;
import jp.co.soramitsu.iroha.android.sample.SampleApplication;
import jp.co.soramitsu.iroha.android.sample.data.Registration;
import jp.co.soramitsu.iroha.android.sample.data.UglyPairing;
import jp.co.soramitsu.iroha.android.sample.data.Validate;
import jp.co.soramitsu.iroha.android.sample.interactor.register.CheckAccountIdInteractor;
import jp.co.soramitsu.iroha.android.sample.interactor.register.CreateAccountInteractor;
import jp.co.soramitsu.iroha.android.sample.interactor.register.GrantPermissionInteractor;
import jp.co.soramitsu.iroha.android.sample.interactor.register.ValidateAccountInteractor;
import jp.co.soramitsu.iroha.java.Utils;
import lombok.Setter;

public class RegistrationPresenter {

    private final PreferencesUtil preferencesUtil;
    private final ValidateAccountInteractor validateAccountInteractor;
    private final CheckAccountIdInteractor checkAccountIdInteractor;
    private final CreateAccountInteractor createAccountInteractor;
    private final GrantPermissionInteractor grantPermissionInteractor;
    private final Ed25519Sha3 crypto;
    private final String TAG = "RegistrationPresenter";
    private boolean isVerified = false;
    @Setter
    private RegistrationView view;

    @Inject
    public RegistrationPresenter(PreferencesUtil preferencesUtil,
                                 CreateAccountInteractor createAccountInteractor,
                                 ValidateAccountInteractor validateAccountInteractor,
                                 CheckAccountIdInteractor checkAccountIdInteractor,
                                 GrantPermissionInteractor grantPermissionInteractor) {
        this.preferencesUtil = preferencesUtil;
        this.createAccountInteractor = createAccountInteractor;
        this.validateAccountInteractor = validateAccountInteractor;
        this.checkAccountIdInteractor = checkAccountIdInteractor;
        this.grantPermissionInteractor = grantPermissionInteractor;
        this.crypto = new Ed25519Sha3();
    }

    public void validateAccountId(String accountId) {
        view.createProgressDialog();
        checkAccountIdInteractor.execute(accountId, httpResult -> {
            if (httpResult.getHttpResult() == 200) {
                this.isVerified = true;
                view.setAccountIdValidation(true, null);
                view.showInfo("Account Available");
            }
        }, throwable -> {
            // handling error when request server.
            view.showError(throwable);
        });
    }

    public void accountInvalidate() {
        this.isVerified = false;
    }

    public void createAccount(String agentId, String accountId, String fullName, String ktp, String bankAccount) {
        boolean result = true;

        if (!isVerified) {
            view.setAccountIdValidation(false, SampleApplication.instance.getText(R.string.username_not_yet_validated).toString());
            result = false;
        }

        if (accountId.isEmpty()) {
            view.setAccountIdValidation(false, SampleApplication.instance.getText(R.string.username_empty_error_dialog_message).toString());
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

            view.createProgressDialog();
            checkAccountIdInteractor.execute(accountId, checkResult -> {
                if (checkResult.getHttpResult() == 200) {
                    KeyPair keyPair = crypto.generateKeypair();
                    Registration registration = new Registration();
                    registration.setAccountId(accountId);
                    registration.setFullName(fullName);
                    registration.setBankAccount(bankAccount);
                    registration.setKtp(ktp);
                    registration.setFcmId("");
                    registration.setAccountPublicKey(Utils.toHex(keyPair.getPublic().getEncoded()));
                    FirebaseInstanceId.getInstance().getInstanceId()
                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                @Override
                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                    if (!task.isSuccessful()) {
                                        Log.w(TAG, "getInstanceId failed", task.getException());
                                        view.showError(new Throwable("Can't get FCM Token, Abort the registration"));
                                        return;
                                    }

                                    // Get new Instance ID token
                                    String token = task.getResult().getToken();

                                    // Log and toast
                                    registration.setFcmId(token);
                                    createAccountInteractor.execute(registration, registrationResult -> {
                                        if (registrationResult.getHttpResult() == 200) {

                                            grantPermissionInteractor.execute(new UglyPairing(accountId, keyPair, registrationResult.getBankInfo()),
                                                    () -> {
                                                        validateAccountInteractor.execute(new Validate(accountId), validateResult -> {
                                                            preferencesUtil.saveUsername(accountId);
                                                            preferencesUtil.saveKeys(Utils.toHex(keyPair.getPrivate().getEncoded()));
                                                            preferencesUtil.saveDomain(registrationResult.getBankInfo().getBankDomain());
                                                            preferencesUtil.saveAdminId(registrationResult.getBankInfo().getBankAccountId());
                                                            view.openMainView();
                                                        }, throwable -> {
                                                            view.showError(throwable);
                                                        });
                                                    }, throwable -> {
                                                        view.showError(throwable);
                                                    });
                                        } else {
                                            // error
                                            view.showError(new Throwable("Failed to create account, " + registrationResult.getMessage()));
                                        }
                                    }, throwable -> {
                                        // handling error when request server.
                                        view.showError(throwable);
                                    });
                                }
                            });

                } else {
                    view.setAccountIdValidation(false, SampleApplication.instance.getText(R.string.username_already_exists_error_dialog_message).toString());
                    view.showError(new Throwable("Account already exist"));
                }
            }, throwable -> {
                // handling error when request server.
                view.showError(throwable);
                view.setAccountIdValidation(false, SampleApplication.instance.getText(R.string.username_already_exists_error_dialog_message).toString());
            });
        }
    }
}
