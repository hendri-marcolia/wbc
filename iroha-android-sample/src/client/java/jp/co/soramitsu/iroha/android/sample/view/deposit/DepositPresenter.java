package jp.co.soramitsu.iroha.android.sample.view.deposit;

import android.content.Intent;

import com.orhanobut.logger.Logger;

import java.security.KeyPair;

import javax.inject.Inject;

import jp.co.soramitsu.crypto.ed25519.Ed25519Sha3;
import jp.co.soramitsu.iroha.android.sample.MyUtils;
import jp.co.soramitsu.iroha.android.sample.PreferencesUtil;
import jp.co.soramitsu.iroha.android.sample.data.Payload;
import jp.co.soramitsu.iroha.android.sample.data.PerformSavePayload;
import jp.co.soramitsu.iroha.android.sample.data.Transaction;
import jp.co.soramitsu.iroha.android.sample.interactor.GenerateTransactionQRInteractor;
import jp.co.soramitsu.iroha.android.sample.interactor.deposit.AddSignatoryInteractor;
import jp.co.soramitsu.iroha.android.sample.interactor.deposit.CreateDepositTransactionInteractor;
import jp.co.soramitsu.iroha.android.sample.interactor.deposit.PerformSaveOfflineInteractor;
import jp.co.soramitsu.iroha.android.sample.qrscanner.QRScannerActivity;
import lombok.Setter;

public class DepositPresenter {

    public static final int REQUEST_CODE_QR_SCAN = 101;
    @Setter
    private DepositFragment fragment;

    private final GenerateTransactionQRInteractor generateQRInteractor;

    private final AddSignatoryInteractor addSignatoryInteractor;

    private final CreateDepositTransactionInteractor createDepositTransactionInteractor;

    private final PerformSaveOfflineInteractor performSaveOfflineInteractor;

    private final PreferencesUtil preferencesUtil;

    private static Transaction lastTransaction;

    @Inject
    public DepositPresenter(GenerateTransactionQRInteractor generateQRInteractor, AddSignatoryInteractor addSignatoryInteractor, CreateDepositTransactionInteractor createDepositTransactionInteractor, PerformSaveOfflineInteractor performSaveOfflineInteractor, PreferencesUtil preferencesUtil) {
        this.generateQRInteractor = generateQRInteractor;
        this.addSignatoryInteractor = addSignatoryInteractor;
        this.createDepositTransactionInteractor = createDepositTransactionInteractor;
        this.performSaveOfflineInteractor = performSaveOfflineInteractor;
        this.preferencesUtil = preferencesUtil;
    }

    void showQR(Long amount) {
        Payload payload = new Payload(amount, preferencesUtil.retrieveUsername());
        KeyPair keyPair = preferencesUtil.retrieveKeys();
        byte[] sign = MyUtils.sign(payload, keyPair);
        lastTransaction = new Transaction(payload, MyUtils.bytesToString(keyPair.getPublic().getEncoded()), MyUtils.bytesToString(sign));

        generateQRInteractor.execute(lastTransaction, bitmap -> {
            fragment.didGenerateSuccess(bitmap, amount);
        }, throwable -> {
            Logger.e(throwable.getMessage());
        });
    }

    void doScanQR(){
        Intent i = new Intent(fragment.getActivity(), QRScannerActivity.class);
        fragment.startActivityForResult(i, REQUEST_CODE_QR_SCAN);
    }

    void onDestroy() {
        fragment = null;
        this.generateQRInteractor.unsubscribe();
    }

    boolean doDepositOnline(Transaction transaction, Runnable runnable){
        fragment.showLoading();
        addSignatoryInteractor.execute(Ed25519Sha3.publicKeyFromBytes(MyUtils.stringToBytes(transaction.getAgentPublicKey())),
                () -> {
                    createDepositTransactionInteractor.execute(transaction,
                            () -> {
                                fragment.showInfo("Success create Transaction,\nPlease wait for agent confirmation");
                                runnable.run();
                            }, throwable -> {
                                fragment.hideLoading();
                                fragment.showError(throwable);
                            });
                },throwable -> {
                    fragment.hideLoading();
                    fragment.showError(throwable);
                });


        return true;
    }

    boolean doDepositOffline(Transaction transaction, Runnable runnable, Runnable runnable1){
        performSaveOfflineInteractor.execute(transaction, httpResult -> {
            runnable.run();
        }, throwable -> {
            runnable1.run();
        });
        return  true;
    }

    boolean validateTransaction(Transaction transaction) throws Throwable{
        Payload payload = transaction.getTransactionPayload().getPayload();
        KeyPair keyPair = preferencesUtil.retrieveKeys();

        if (!transaction.getTransactionPayload().equals(lastTransaction.getTransactionPayload())) {
            throw new Throwable("Payload somehow altered, abort the Transaction");
        }
        try {
            MyUtils.verify(payload,
                    MyUtils.stringToBytes(transaction.getTransactionPayload().getCustomerSigning()),
                    keyPair.getPublic());
        }catch (Exception e) {
            throw new Throwable("Failed to verify payload, " + e.getMessage());
        }
        // verify agent signing
        try {
            if (transaction.getAgentId() != null && transaction.getAgentId().length() > 0 &&
                transaction.getAgentPublicKey() != null && transaction.getAgentPublicKey().length() > 0 &&
                transaction.getAgentSinging() != null && transaction.getAgentSinging() != null){
                String tempSigning = transaction.getAgentSinging();
                transaction.setAgentSinging(null);
                MyUtils.verify(transaction,
                        MyUtils.stringToBytes(tempSigning),
                        Ed25519Sha3.publicKeyFromBytes(MyUtils.stringToBytes(transaction.getAgentPublicKey()))
                );
                transaction.setAgentSinging(tempSigning);
            }else throw new Exception("Agent signing missing");
        }catch (Exception e) {
            throw new Throwable("Failed to verify agent signing, " +e.getMessage());
        }


        return true;
    }

}