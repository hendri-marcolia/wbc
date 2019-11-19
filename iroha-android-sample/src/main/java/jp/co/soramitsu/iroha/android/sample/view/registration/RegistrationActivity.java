package jp.co.soramitsu.iroha.android.sample.view.registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import javax.inject.Inject;

import jp.co.soramitsu.iroha.android.sample.R;
import jp.co.soramitsu.iroha.android.sample.SampleApplication;
import jp.co.soramitsu.iroha.android.sample.databinding.ActivityRegistrationBinding;
import jp.co.soramitsu.iroha.android.sample.view.main.MainActivity;
import retrofit2.adapter.rxjava2.HttpException;

public class RegistrationActivity extends AppCompatActivity implements RegistrationView {

    private ActivityRegistrationBinding binding;

    private ProgressDialog dialog;

    @Inject
    RegistrationPresenter presenter;
    private static String agentID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        agentID = getIntent().getStringExtra("agentID");
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
            presenter.createAccount(agentID, accountId, fullName, ktp, bankAccount);
        });

        binding.buttonCheckId.setOnClickListener( v -> {
            String accountId = binding.accountIdText.getText().toString();
            presenter.validateAccountId(accountId);
        });

        binding.accountIdText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                presenter.accountInvalidate();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void setAccountIdValidation(boolean valid, String msg) {
        if (!valid) {
            binding.accountIdValidator.setError(msg);
        }else {
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

    @Override
    public void showError(Throwable e) {
        hideProgressDialog();
        AlertDialog alertDialog = null;
        try {
            alertDialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.error_dialog_title))
                    .setMessage(
                            (e instanceof HttpException)  ? ((HttpException) e).response().errorBody().string() :
                            e.getLocalizedMessage()
                    )
                    .setCancelable(true)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {

                    })
                    .create();
            alertDialog.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void showInfo(String info) {
        hideProgressDialog();
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.info_dialog_title))
                .setMessage(
                        info
                )
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {

                })
                .create();
        alertDialog.show();
    }


    @Override
    public void createProgressDialog() {
        hideProgressDialog();
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage(getString(R.string.please_wait));
        dialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if (dialog != null && dialog.isShowing()){
            try{
                dialog.dismiss();
            }catch (Exception e){
                // ignore
            }
        }
    }
}
