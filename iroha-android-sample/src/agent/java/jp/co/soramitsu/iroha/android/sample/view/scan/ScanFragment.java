package jp.co.soramitsu.iroha.android.sample.view.scan;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
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
import jp.co.soramitsu.iroha.android.sample.data.Payload;
import jp.co.soramitsu.iroha.android.sample.data.Transaction;
import jp.co.soramitsu.iroha.android.sample.databinding.FragmentDepositBinding;
import jp.co.soramitsu.iroha.android.sample.databinding.FragmentScanBinding;
import jp.co.soramitsu.iroha.android.sample.entity.TransactionEntity;
import jp.co.soramitsu.iroha.android.sample.fragmentinterface.OnBackPressed;
import jp.co.soramitsu.iroha.android.sample.view.main.MainActivity;

public class ScanFragment extends Fragment implements ScanView, OnBackPressed {
    private FragmentScanBinding binding;

    private Handler rHandler = new Handler();

    @Inject
    ScanPresenter presenter;

    private static Integer transactionTypeId = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_scan, container, false);
        SampleApplication.instance.getApplicationComponent().inject(this);

        presenter.setFragment(this);

        RxView.clicks(binding.scanBtn)
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

    public void didGenerateSuccess(Bitmap bitmap, Transaction transaction) {
        binding.qrCodeImageView.setImageBitmap(bitmap);
        binding.bottomSheet.setVisibility(View.VISIBLE);
        binding.screenBlocker.setVisibility(View.VISIBLE);
        binding.confAmount.setText("Transaction : " + (transaction.getTransactionType() == Transaction.TransactionType.ONLINE ? "Online" : "Offline"));
        binding.saveTx.setVisibility((transaction.getTransactionType() == Transaction.TransactionType.ONLINE ? View.INVISIBLE : View.VISIBLE));
        binding.saveTx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Save pending tx here ( we only need userID)
                //new TransactionEntity(new Gson().toJson(transaction), false, false).save();
                hideBottomSheet();
            }
        });
        BottomSheetBehavior.from(binding.bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        getPendingTransaction(transaction);
    }

    public void getPendingTransaction(Transaction transaction) {
        rHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                presenter.getPendingTransaction(transaction);
            }
        }, 5000);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ScanPresenter.REQUEST_CODE_QR_SCAN) {
                if (data == null) {
                    ((MainActivity) getActivity()).showError(new Throwable("QR Invalid"));
                } else {
                    try {
                        Transaction transaction = new Gson().fromJson(data.getData().toString(), Transaction.class);
                        try {
                            if(presenter.validateTransaction(transaction, false)){
                                transactionTypeId = 0;
                                Payload payload = transaction.getTransactionPayload().getPayload();
                                String transactionType[] = new String[]{"Online", "Offline"};
                                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                                        .setTitle("Transaction Amount : " + payload.getAmount() + "\nCustomer ID : " + payload.getCustomerId())
                                        .setSingleChoiceItems(transactionType, 0, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    transactionTypeId = i;
                                                }
                                            })
                                        .setPositiveButton("OK", (dialogInterface, i) -> {
                                            if (transactionTypeId < 0) {
                                                Toast.makeText(getActivity(),"Please Select transaction type", Toast.LENGTH_LONG).show();
                                                return;
                                            }else {
                                                presenter.showQR(transaction, transactionTypeId == 0 ? Transaction.TransactionType.ONLINE : Transaction.TransactionType.OFFLINE);
                                                dialogInterface.dismiss();
                                            }
                                        })
                                        .setCancelable(false)
                                        .create();
                                dialog.show();

                            }else showError(new Throwable("Unknown Error while validating the Transaction"));
                        }catch (Exception e) {
                            showError(e);
                        }

//                        transaction.setAgentId("agent1@test6");
//                        transaction.setAgentPublicKey(
//                               MyUtils.bytesToString(MyUtils.hexToBytes("a05154253901769b97036883020546540298d6a6fb453e54b0404fe5c49a1cfa"))
//                        );
//                        transaction.setAgentSinging(
//                                MyUtils.bytesToString(
//                                MyUtils.sign(transaction.getTransactionPayload(),
//                                       MyUtils.keyPairFromPrivate(MyUtils.privateKeyFromString("78356528af93e4b03cad19ec0ee44a45c41e68375789a827cd93f911b57b6a9b"))
//                                ))
//                        );
//                        transaction.setTransactionType(Transaction.TransactionType.ONLINE);
//                        if(presenter.validateTransaction(transaction)){
//                            switch (transaction.getTransactionType()){
//                                case ONLINE:
//                                    presenter.doDepositOnline(transaction,
//                                            new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    hideBottomSheet();
//                                                }
//                                            });
//                                    break;
//                                case OFFLINE:
//                                    break;
//                            }
//                        }else {
//                            showError(new Throwable("Unknown Error while validating the Transaction"));
//                        }

                    } catch (Throwable e) {
                        showError(e);
                    }
                }
            }
        }
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
}