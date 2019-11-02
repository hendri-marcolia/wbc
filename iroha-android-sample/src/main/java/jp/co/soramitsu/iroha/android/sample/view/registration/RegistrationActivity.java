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
            String birthDate = binding.birthdateText.getText().toString();
            String nationality = binding.nationalityText.getText().toString();
            String nationalId = binding.nationalIdText.getText().toString();

            presenter.ValidateRegistrationForm(accountId, fullName, birthDate, nationality, nationalId);
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

    }

    @Override
    public void setBirthDateStatus(boolean valid) {

    }

    @Override
    public void setNationalityStatus(boolean valid) {

    }

    @Override
    public void setNationalIdStatus(boolean valid) {

    }
}
