package jp.co.soramitsu.iroha.android.sample.view.login;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import java.util.concurrent.Executor;

import javax.inject.Inject;

import jp.co.soramitsu.iroha.android.sample.R;
import jp.co.soramitsu.iroha.android.sample.SampleApplication;

public class LoginActivity extends AppCompatActivity implements LoginView {

    private ViewDataBinding binding;
    private AlertDialog passwordPopup;
    private AlertDialog.Builder passwordPopupBuilder;
    private View passwordView;

    private Button biometricButton, passwordButton, registerButton;

    private BiometricPrompt biometricPopup;
    private BiometricPrompt.PromptInfo biometricPopupBuilder;

    @Inject
    LoginPresenter presenter;

    private Handler handler = new Handler();
    private Executor executor = command -> handler.post(command);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        SampleApplication.instance.getApplicationComponent().inject(this);
        presenter.setView(this);

        biometricButton = findViewById(R.id.biometric_button);
        passwordButton = findViewById(R.id.password_button);
        registerButton = findViewById(R.id.register_button);

        biometricButton.setOnClickListener(v -> openBiometricPopup());
        passwordButton.setOnClickListener(v -> openPasswordPopup());
    }

    @Override
    public void openBiometricPopup() {
        biometricPopupBuilder = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.biometric_login))
                .setNegativeButtonText(getString(R.string.cancel))
                .build();

        biometricPopup = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        biometricPopup.authenticate(biometricPopupBuilder);
    }

    @Override
    public void openPasswordPopup() {
        passwordPopupBuilder = new AlertDialog.Builder(this);
        passwordView = getLayoutInflater().inflate(R.layout.dialog_login_password, null);
        passwordPopupBuilder.setView(passwordView);
        passwordPopupBuilder.setTitle(R.string.password_login);
        passwordPopupBuilder.setPositiveButton(R.string.login, (dialog, which) -> {
            dialog.dismiss();
        });
        passwordPopupBuilder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
        passwordPopup = passwordPopupBuilder.show();
    }
}
