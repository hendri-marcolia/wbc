package jp.co.soramitsu.iroha.android.sample.view.withdraw;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.jakewharton.rxbinding2.view.RxView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.orm.SugarRecord;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import jp.co.soramitsu.iroha.android.sample.MyUtils;
import jp.co.soramitsu.iroha.android.sample.R;
import jp.co.soramitsu.iroha.android.sample.SampleApplication;
import jp.co.soramitsu.iroha.android.sample.data.Transaction;
import jp.co.soramitsu.iroha.android.sample.databinding.FragmentWithdrawBinding;
import jp.co.soramitsu.iroha.android.sample.entity.TransactionEntity;
import jp.co.soramitsu.iroha.android.sample.fragmentinterface.InteractorListener;
import jp.co.soramitsu.iroha.android.sample.fragmentinterface.OnBackPressed;
import jp.co.soramitsu.iroha.android.sample.view.main.MainActivity;

public class WithdrawFragment extends Fragment implements WithdrawView, OnBackPressed {
    private FragmentWithdrawBinding binding;

    @Inject
    WithdrawPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_withdraw, container, false);
        SampleApplication.instance.getApplicationComponent().inject(this);

        presenter.setFragment(this);

        RxView.clicks(binding.deposit)
                .subscribe(view -> {
                    String amount = binding.amount.getText().toString().trim();
                    if (amount.length() < 1 || Long.parseLong(amount) < 1)
                        return;
                    if (SampleApplication.instance.account == null ||Long.parseLong(amount) > SampleApplication.instance.account.getBalance()){
                        showInfo("Balance insufficient");
                        // Need node verification later
                        return;
                    }
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getRootView().getWindowToken(), 0);
                    presenter.showQR(Long.parseLong(amount));
                });
        RxView.clicks(binding.scanQR)
                .subscribe(view -> {
                    Dexter.withActivity(getActivity())
                            .withPermissions(Manifest.permission.CAMERA,
                                    Manifest.permission.READ_EXTERNAL_STORAGE)
                            .withListener(new MultiplePermissionsListener() {
                                @Override
                                public void onPermissionsChecked(MultiplePermissionsReport report) {
                                    if (report.areAllPermissionsGranted()) {
                                        presenter.doScanQR();
                                    } else {
                                        Toast.makeText(getContext(), "Permissions weren't granted", Toast.LENGTH_LONG);
                                    }
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                    token.continuePermissionRequest();
                                }
                            }).check();
                });
        RxView.clicks(binding.screenBlocker)
                .subscribe(view -> {
                    hideBottomSheet();
                });


        return binding.getRoot();
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
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.setFragment(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == WithdrawPresenter.REQUEST_CODE_QR_SCAN) {
                if (data == null) {
                    ((MainActivity) getActivity()).showError(new Throwable("QR Invalid"));
                } else {
                    try {
                        Transaction transaction = new Gson().fromJson(data.getData().toString(), Transaction.class);
                        if(presenter.validateTransaction(transaction)){

                                    presenter.doWithdrawOnline(transaction, new InteractorListener() {
                                        long id1 = 0;
                                        @Override
                                        public void onNext(Object object) {
                                            ((MainActivity)getActivity()).hideProgress();
                                            hideBottomSheet();
                                            id1 = new TransactionEntity(new Gson().toJson(transaction), false, true).save();
                                            showInfo("Success create Transaction,\nPlease wait for agent confirmation");
                                        }

                                        @Override
                                        public void onComplete(Object o) {
                                            showInfo("Transaction successfully synced");
                                            TransactionEntity t =  TransactionEntity.findById(TransactionEntity.class, id1);
                                            if (t != null){
                                                t.setCommited(true);
                                                t.save();
                                            }
                                            ((MainActivity)getActivity()).refreshData(false);
                                        }

                                        @Override
                                        public void onError(Throwable throwable) {
                                            hideLoading();
                                            showError(throwable);
                                        }
                                    });


                        }else {
                            showError(new Throwable("Unknown Error while validating the Transaction"));
                        }

                    } catch (Throwable e) {
                        showError(e);
                    }
                }
            }
        }
    }


    public void didGenerateSuccess(Bitmap bitmap, Long amount) {
        binding.qrCodeImageView.setImageBitmap(bitmap);
        binding.bottomSheet.setVisibility(View.VISIBLE);
        binding.screenBlocker.setVisibility(View.VISIBLE);
        binding.confAmount.setText(MyUtils.formatIDR(amount));
        BottomSheetBehavior.from(binding.bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
    }
    @Override
    public boolean onBackPressed() {
        return hideBottomSheet();
    }

    @Override
    public void showError(Throwable e) {
        ((MainActivity) getActivity()).showError(e);
    }

    @Override
    public void showInfo(String msg) {
        ((MainActivity) getActivity()).showInfo(msg);
    }

    @Override
    public void showLoading() {
        ((MainActivity) getActivity()).showProgress();
    }

    @Override
    public void hideLoading() {
        ((MainActivity) getActivity()).hideProgress();
    }
}