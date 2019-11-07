package jp.co.soramitsu.iroha.android.sample.view.deposit;

import android.content.Intent;

import com.orhanobut.logger.Logger;

import javax.inject.Inject;

import jp.co.soramitsu.iroha.android.sample.interactor.GenerateQRInteractor;
import jp.co.soramitsu.iroha.android.sample.qrscanner.QRScannerActivity;
import lombok.Setter;

public class DepositPresenter {

    public static final int REQUEST_CODE_QR_SCAN = 101;
    @Setter
    private DepositFragment fragment;

    private final GenerateQRInteractor generateQRInteractor;

    @Inject
    public DepositPresenter(GenerateQRInteractor generateQRInteractor) {
        this.generateQRInteractor = generateQRInteractor;
    }

    void showQR(String amount) {
        generateQRInteractor.execute(amount, bitmap -> {
            fragment.didGenerateSuccess(bitmap);
        }, throwable -> {
            Logger.e(throwable.getMessage());
        });
    }

    void doScanQR(){
        Intent i = new Intent(fragment.getActivity(), QRScannerActivity.class);
        fragment.startActivityForResult(i, REQUEST_CODE_QR_SCAN);
    }

    void onStop() {
        fragment = null;
        this.generateQRInteractor.unsubscribe();
    }
}