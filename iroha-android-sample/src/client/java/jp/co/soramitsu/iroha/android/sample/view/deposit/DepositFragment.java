package jp.co.soramitsu.iroha.android.sample.view.deposit;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.jakewharton.rxbinding2.view.RxView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import jp.co.soramitsu.iroha.android.sample.R;
import jp.co.soramitsu.iroha.android.sample.SampleApplication;
import jp.co.soramitsu.iroha.android.sample.databinding.FragmentDepositBinding;
import jp.co.soramitsu.iroha.android.sample.fragmentinterface.OnBackPressed;

public class DepositFragment extends Fragment implements DepositView, OnBackPressed {
    private FragmentDepositBinding binding;

    @Inject
    DepositPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_deposit, container, false);
        SampleApplication.instance.getApplicationComponent().inject(this);

        presenter.setFragment(this);

        RxView.clicks(binding.deposit)
                .subscribe(view -> {
                    String amount = binding.amount.getText().toString().trim();
                    presenter.showQR(amount);
                    NumberFormat format = NumberFormat.getCurrencyInstance();
                    format.setMaximumFractionDigits(0);
                    Locale locale = new Locale("in", "ID");
                    format.setCurrency(Currency.getInstance(locale));
                    binding.confAmount.setText("Rp " + format.format(Integer.parseInt(amount)));
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



        return binding.getRoot();
    }

    public void didGenerateSuccess(Bitmap bitmap){
        binding.qrCodeImageView.setImageBitmap(bitmap);
        binding.bottomSheet.setVisibility(View.VISIBLE);
        binding.screenBlocker.setVisibility(View.VISIBLE);
        BottomSheetBehavior.from(binding.bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == DepositPresenter.REQUEST_CODE_QR_SCAN) {
                if (data == null) {
                    Toast.makeText(getContext(), "QR invalid", Toast.LENGTH_LONG);
                } else {
                    String result = data.getData().toString();
                    Toast.makeText(getContext(), "QR Payload", Toast.LENGTH_LONG);
                }
            }
        }
    }

    @Override
    public boolean onBackPressed() {
        if(BottomSheetBehavior.from(binding.bottomSheet).getState() == BottomSheetBehavior.STATE_EXPANDED){
            BottomSheetBehavior.from(binding.bottomSheet).setState(BottomSheetBehavior.STATE_COLLAPSED);
            BottomSheetBehavior.from(binding.bottomSheet).setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View view, int i) {
                    if (BottomSheetBehavior.STATE_COLLAPSED == i){
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
}