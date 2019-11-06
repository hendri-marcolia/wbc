package jp.co.soramitsu.iroha.android.sample.view.registration;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import javax.inject.Inject;

import jp.co.soramitsu.iroha.android.sample.R;
import jp.co.soramitsu.iroha.android.sample.SampleApplication;
import jp.co.soramitsu.iroha.android.sample.databinding.ActivityRegistrationBinding;
import jp.co.soramitsu.iroha.android.sample.view.main.MainActivity;

public class RegistrationActivity extends AppCompatActivity implements RegistrationView {

    private ActivityRegistrationBinding binding;

    @Inject
    RegistrationPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_registration);
        SampleApplication.instance.getApplicationComponent().inject(this);

        presenter.setView(this);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        binding.registerButton.setOnClickListener(v -> {
            String accountId = binding.accountIdText.getText().toString();
            String fullName = binding.fullnameText.getText().toString();
            String ktp = binding.ktpText.getText().toString();
            String bankAccount = binding.bankAccountText.getText().toString();

            presenter.createAccount(accountId, fullName, ktp, bankAccount);
        });
    }

    @Override
    public void setAccountIdValidation(boolean empty, boolean exist) {
        if (empty) {
            binding.accountIdValidator.setError(getText(R.string.username_empty_error_dialog_message));
        } else if (exist) {
            binding.accountIdValidator.setError(getText(R.string.username_already_exists_error_dialog_message));
        } else {
            binding.accountIdValidator.setErrorEnabled(false);
        }
    }

    @Override
    public void setFullNameValidation(boolean valid) {
        if (valid) {
            binding.fullnameValidator.setErrorEnabled(false);
        } else {
            binding.fullnameValidator.setError(getText(R.string.fields_cant_be_empty));
        }
    }

    @Override
    public void setKtpValidation(boolean valid) {
        if (valid) {
            binding.ktpValidation.setErrorEnabled(false);
        } else {
            binding.ktpValidation.setError(getText(R.string.fields_cant_be_empty));
        }
    }

    @Override
    public void setBankAccountValidation(boolean valid) {
        if (valid) {
            binding.bankAccountValidation.setErrorEnabled(false);
        } else {
            binding.bankAccountValidation.setError(getText(R.string.fields_cant_be_empty));
        }
    }

    @Override
    public void openMainView() {
        startActivity(new Intent(this, MainActivity.class));
        finishAffinity();
    }
}
