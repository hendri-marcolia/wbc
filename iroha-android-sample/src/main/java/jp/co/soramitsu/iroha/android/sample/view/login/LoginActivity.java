package jp.co.soramitsu.iroha.android.sample.view.login;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.databinding.DataBindingUtil;

import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import jp.co.soramitsu.iroha.android.sample.PreferencesUtil;
import jp.co.soramitsu.iroha.android.sample.R;
import jp.co.soramitsu.iroha.android.sample.SampleApplication;
import jp.co.soramitsu.iroha.android.sample.databinding.ActivityLoginBinding;
import jp.co.soramitsu.iroha.android.sample.qrscanner.QRScannerActivity;
import jp.co.soramitsu.iroha.android.sample.view.main.MainActivity;
import jp.co.soramitsu.iroha.android.sample.view.registration.RegistrationActivity;

public class LoginActivity extends AppCompatActivity implements LoginView {
    private ActivityLoginBinding binding;

    private AlertDialog passwordPopup;
    private AlertDialog.Builder passwordPopupBuilder;
    private View passwordView;

    private BiometricPrompt biometricPopup;
    private BiometricPrompt.PromptInfo biometricPopupBuilder;
    private Context context;
    public static final int REQUEST_CODE_QR_SCAN = 100;
    @Inject
    LoginPresenter presenter;

    @Inject
    PreferencesUtil preferencesUtil;

    private Handler handler = new Handler();
    private Executor executor = command -> handler.post(command);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        SampleApplication.instance.getApplicationComponent().inject(this);
        presenter.setView(this);

        binding.biometricButton.setOnClickListener(v -> openBiometricPopup());
        binding.passwordButton.setOnClickListener(v -> openPasswordPopup());
        binding.registerButton.setOnClickListener(v -> openRegistrationView());
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
                presenter.loginFromBiometric();
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
            EditText accountId = passwordView.findViewById(R.id.account_id);
            EditText privateKey = passwordView.findViewById(R.id.password);

            presenter.loginFromPassword(accountId.getText().toString(), privateKey.getText().toString());
            dialog.dismiss();
        });
        passwordPopupBuilder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
        passwordPopup = passwordPopupBuilder.show();
    }

    @Override
    public void openMainView() {
        startActivity(new Intent(this, MainActivity.class));
        finishAffinity();
    }

    @Override
    public void openRegistrationView() {
        Dexter.withActivity(this)
        .withPermissions(Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            Intent i = new Intent(context, QRScannerActivity.class);
                            startActivityForResult(i, REQUEST_CODE_QR_SCAN);
                        } else {
                            Toast.makeText(context, "Permissions weren't granted", Toast.LENGTH_LONG);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_QR_SCAN) {
                if (data == null) {
                    Toast.makeText(context, "QR invalid", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(this, RegistrationActivity.class);
                    intent.putExtra("agentID", data.getData().toString());
                    startActivity(intent);
                }
            }
        }
    }
}
