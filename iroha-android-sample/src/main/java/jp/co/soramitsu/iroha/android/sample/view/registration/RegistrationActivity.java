package jp.co.soramitsu.iroha.android.sample.view.registration;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import javax.inject.Inject;

import jp.co.soramitsu.iroha.android.sample.R;
import jp.co.soramitsu.iroha.android.sample.SampleApplication;
import jp.co.soramitsu.iroha.android.sample.databinding.ActivityRegistrationBinding;

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
            String email = binding.emailText.getText().toString();
            String birthDate = binding.birthdateText.getText().toString();
            String nationality = binding.nationalityText.getText().toString();
            String nationalId = binding.nationalIdText.getText().toString();
            String phone = binding.phoneText.getText().toString();

            boolean allValid = presenter.validateRegistrationForm(accountId, fullName, email, birthDate, nationality, nationalId, phone);

            if (allValid) {
                presenter.createAccount(accountId);
            } else {

            }
        });
    }

    @Override
    public void backToLogin() {
        finish();
    }

    @Override
    public void setAccountIdStatus(boolean empty, boolean exist) {
        if (empty) {
            binding.accountIdValidator.setError(getText(R.string.username_empty_error_dialog_message));
        } else if (exist) {
            binding.accountIdValidator.setError(getText(R.string.username_already_exists_error_dialog_message));
        } else {
            binding.accountIdValidator.setErrorEnabled(false);
        }
    }

    @Override
    public void setFullNameStatus(boolean valid) {
        if (valid) {
            binding.fullnameValidator.setErrorEnabled(false);
        } else {
            binding.fullnameValidator.setError(getText(R.string.fields_cant_be_empty));
        }
    }

    @Override
    public void setBirthDateStatus(boolean valid) {
        if (valid) {
            binding.birthdateValidation.setErrorEnabled(false);
        } else {
            binding.birthdateValidation.setError(getText(R.string.fields_cant_be_empty));
        }
    }

    @Override
    public void setNationalityStatus(boolean valid) {
        if (valid) {
            binding.nationalityValidation.setErrorEnabled(false);
        } else {
            binding.nationalityValidation.setError(getText(R.string.fields_cant_be_empty));
        }
    }

    @Override
    public void setNationalIdStatus(boolean valid) {
        if (valid) {
            binding.nationalIdValidation.setErrorEnabled(false);
        } else {
            binding.nationalIdValidation.setError(getText(R.string.fields_cant_be_empty));
        }
    }

    @Override
    public void setEmailStatus(boolean valid) {
        if (valid) {
            binding.emailValidator.setErrorEnabled(false);
        } else {
            binding.emailValidator.setError(getText(R.string.fields_cant_be_empty));
        }
    }

    @Override
    public void setPhoneStatus(boolean valid) {
        if (valid) {
            binding.phoneValidation.setErrorEnabled(false);
        } else {
            binding.phoneValidation.setError(getText(R.string.fields_cant_be_empty));
        }
    }
}
