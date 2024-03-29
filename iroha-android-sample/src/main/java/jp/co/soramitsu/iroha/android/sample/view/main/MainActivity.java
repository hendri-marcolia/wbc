package jp.co.soramitsu.iroha.android.sample.view.main;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.net.ConnectException;

import javax.inject.Inject;

import jp.co.soramitsu.iroha.android.sample.BuildConfig;
import jp.co.soramitsu.iroha.android.sample.Constants;
import jp.co.soramitsu.iroha.android.sample.PreferencesUtil;
import jp.co.soramitsu.iroha.android.sample.R;
import jp.co.soramitsu.iroha.android.sample.SampleApplication;
import jp.co.soramitsu.iroha.android.sample.adapter.MainAdapter;
import jp.co.soramitsu.iroha.android.sample.databinding.ActivityMainBinding;
import jp.co.soramitsu.iroha.android.sample.fragmentinterface.OnBackPressed;
import jp.co.soramitsu.iroha.android.sample.view.login.LoginActivity;

public class MainActivity extends AppCompatActivity implements MainView, SwipeRefreshLayout.OnRefreshListener {

    private ActivityMainBinding binding;

    private ProgressDialog dialog;

    private SwipeRefreshLayout swipeLayout;

    @Inject
    MainPresenter presenter;

    @Inject
    PreferencesUtil preferencesUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        SampleApplication.instance.getApplicationComponent().inject(this);
        presenter.setView(this);

        // TODO : dirty hack for hide amount at agent flavour
        if (BuildConfig.FLAVOR.equals("agent")){
            binding.balance.setVisibility(View.GONE);
        }

        //refresh layout
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        createProgressDialog();
        configureRefreshLayout();

        RxView.clicks(binding.logout)
                .subscribe(v -> {
                    AlertDialog alertDialog = new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.logout))
                            .setMessage(getString(R.string.logout_description))
                            .setCancelable(true)
                            .setPositiveButton(android.R.string.ok, (dialog, which) -> presenter.logout())
                            .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                            .create();
                    alertDialog.setOnShowListener(arg0 ->
                            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                                    .setTextColor(getResources().getColor(R.color.hint)));
                    alertDialog.setOnShowListener(arg0 ->
                            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                    .setTextColor(getResources().getColor(R.color.iroha)));
                    alertDialog.show();
                });
        if(BuildConfig.FLAVOR.equals("agent")) {
            RxView.clicks(binding.logo)
                    .subscribe(v -> {
                        presenter.generateUserQR();
                    });
            RxView.clicks(binding.screenBlocker)
                    .subscribe(view -> {
                        hideBottomSheet();
                    });
        }
        RxView.clicks(binding.bio)
                .subscribe(v -> {
                    LayoutInflater inflater = LayoutInflater.from(this);
                    @SuppressLint("InflateParams") final View view = inflater.inflate(R.layout.dialog_account_details, null);
                    final EditText details = view.findViewById(R.id.details);
                    final TextView symbolsLeft = view.findViewById(R.id.symbols_left);

                    symbolsLeft.setText(String.valueOf(Constants.MAX_ACCOUNT_DETAILS_SIZE));

                    RxTextView.textChanges(details)
                            .map(CharSequence::toString)
                            .subscribe(text ->
                                    symbolsLeft.setText(String.valueOf(Constants.MAX_ACCOUNT_DETAILS_SIZE - text.length())));

                    AlertDialog alertDialog = new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.account_details))
                            .setMessage(getString(R.string.bio))
                            .setCancelable(true)
                            .setPositiveButton(android.R.string.ok, (dialog, which) -> presenter.setAccountDetails(details.getText().toString()))
                            .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                            .create();


                    alertDialog.setOnShowListener(arg0 ->
                            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                                    .setTextColor(getResources().getColor(R.color.hint)));
                    alertDialog.setOnShowListener(arg0 ->
                            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                    .setTextColor(getResources().getColor(R.color.iroha)));

                    alertDialog.setView(view);
                    alertDialog.show();
                });

        setupViewPager();
        binding.tabs.setupWithViewPager(binding.content);

        presenter.onCreate();
    }

    private void configureRefreshLayout() {
//        binding.swiperefresh.setOnRefreshListener(() -> presenter.updateData(true));
    }

    private void setupViewPager() {
        MainAdapter adapter = new MainAdapter(getSupportFragmentManager());
        binding.content.setAdapter(adapter);

        binding.content.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
//                binding.swiperefresh.setEnabled(!(adapter.getItem(position) instanceof HistoryFragment));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void setUsername(String username) {
        binding.username.setText(username);
    }

    @Override
    public void setAccountDetails(String details) {
        binding.bio.setText(details.isEmpty() ? getString(R.string.bio) : details);
    }

    @Override
    public void setAccountBalance(String balance) {
        binding.balance.setText(balance);
    }

    @Override
    public void showLoginScreen() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void showProgress() {
        dialog.show();
    }

    @Override
    public void hideProgress() {
        dialog.dismiss();
    }

    @Override
    public void showError(Throwable throwable) {
        hideRefresh();
        hideProgress();
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.error_dialog_title))
                .setMessage(
                        throwable.getCause() instanceof ConnectException ?
                                getString(R.string.general_error) :
                                throwable.getLocalizedMessage()
                )
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    if (throwable.getCause() instanceof ConnectException) {
                        //  finish();
                    }
                })
                .create();
        alertDialog.show();
    }

    @Override
    public void showInfo(String info) {
        hideRefresh();
        hideProgress();
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
    public void hideRefresh() {
//        binding.swiperefresh.setRefreshing(false);
        swipeLayout.setRefreshing(false);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    public void showProfileQr(Bitmap bitmap) {

        binding.qrCodeImageView.setImageBitmap(bitmap);
        binding.bottomSheet.setVisibility(View.VISIBLE);
        binding.screenBlocker.setVisibility(View.VISIBLE);
        BottomSheetBehavior.from(binding.bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private boolean hideBottomSheet() {
        if (BottomSheetBehavior.from(binding.bottomSheet).getState() == BottomSheetBehavior.STATE_EXPANDED) {
            BottomSheetBehavior.from(binding.bottomSheet).setState(BottomSheetBehavior.STATE_COLLAPSED);
            BottomSheetBehavior.from(binding.bottomSheet).setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View view, int i) {
                    if (BottomSheetBehavior.STATE_COLLAPSED == i) {
                        binding.bottomSheet.setVisibility(View.GONE);
                        binding.screenBlocker.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onSlide(@NonNull View view, float v) {

                }
            });
            return false;
        }
        return true;
    }

    @Override
    public void refreshData(boolean animate) {
//        binding.swiperefresh.setRefreshing(animate);
        presenter.updateData(animate);
    }

    private void createProgressDialog() {
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage(getString(R.string.please_wait));
    }

    @Override
    public void onRefresh() {
        presenter.updateData(true);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = ((MainAdapter)binding.content.getAdapter()).getItem(binding.content.getCurrentItem());
        if (fragment instanceof OnBackPressed) {
            if(((OnBackPressed) fragment).onBackPressed()) super.onBackPressed();
        }else
        super.onBackPressed();
    }
}