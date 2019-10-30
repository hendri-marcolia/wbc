package jp.co.soramitsu.iroha.android.sample.main;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.net.ConnectException;

import javax.inject.Inject;

import jp.co.soramitsu.iroha.android.sample.Constants;
import jp.co.soramitsu.iroha.android.sample.PreferencesUtil;
import jp.co.soramitsu.iroha.android.sample.R;
import jp.co.soramitsu.iroha.android.sample.SampleApplication;
import jp.co.soramitsu.iroha.android.sample.databinding.ActivityMainBinding;
import jp.co.soramitsu.iroha.android.sample.main.history.HistoryFragment;
import jp.co.soramitsu.iroha.android.sample.main.receive.ReceiveFragment;
import jp.co.soramitsu.iroha.android.sample.main.send.SendFragment;
import jp.co.soramitsu.iroha.android.sample.registration.RegistrationActivity;

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

        //refresh layout
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // test
        String username = preferencesUtil.retrieveUsername();
        if (username.length()<1){
            preferencesUtil.saveUsername("admin");
            preferencesUtil.saveKeys("cded824a3b0fc1de4e0a8e48d8d7c3cf26944ea396dce2743dcee4ae95c62e51");
        }
        // test

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
        Adapter adapter = new Adapter(getSupportFragmentManager());
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
    public void showRegistrationScreen() {
        startActivity(new Intent(this, RegistrationActivity.class));
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
    public void hideRefresh() {
//        binding.swiperefresh.setRefreshing(false);
        swipeLayout.setRefreshing(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
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

    public static class Adapter extends FragmentPagerAdapter {

        Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new SendFragment();
            } else if (position == 1) {
                return new ReceiveFragment();
            } else {
                return new HistoryFragment();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "SEND";
            } else if (position == 1) {
                return "RECEIVE";
            } else {
                return "HISTORY";
            }
        }
    }
}