package jp.co.soramitsu.iroha.android.sample.view.withdraw;

import android.content.Intent;

import com.orhanobut.logger.Logger;

import java.security.KeyPair;

import javax.inject.Inject;

import jp.co.soramitsu.crypto.ed25519.Ed25519Sha3;
import jp.co.soramitsu.iroha.android.sample.MyUtils;
import jp.co.soramitsu.iroha.android.sample.PreferencesUtil;
import jp.co.soramitsu.iroha.android.sample.data.Payload;
import jp.co.soramitsu.iroha.android.sample.data.Transaction;
import jp.co.soramitsu.iroha.android.sample.fragmentinterface.InteractorListener;
import jp.co.soramitsu.iroha.android.sample.interactor.GenerateTransactionQRInteractor;
import jp.co.soramitsu.iroha.android.sample.interactor.basicsavingaction.AddSignatoryInteractor;
import jp.co.soramitsu.iroha.android.sample.interactor.basicsavingaction.CreateWithdrawTransactionInteractor;
import jp.co.soramitsu.iroha.android.sample.qrscanner.QRScannerActivity;
import lombok.Setter;

public class WithdrawPresenter {
    public static final int REQUEST_CODE_QR_SCAN = 103;
    private final GenerateTransactionQRInteractor generateQRInteractor;

    private final AddSignatoryInteractor addSignatoryInteractor;

    private final CreateWithdrawTransactionInteractor createWithdrawTransactionInteractor;

    private final PreferencesUtil preferencesUtil;

    private static Transaction lastTransaction;

    @Setter
    private WithdrawFragment fragment;


    @Inject
    public WithdrawPresenter(GenerateTransactionQRInteractor generateQRInteractor,
                             AddSignatoryInteractor addSignatoryInteractor,
                             CreateWithdrawTransactionInteractor createWithdrawTransactionInteractor, PreferencesUtil preferencesUtil) {
        this.generateQRInteractor = generateQRInteractor;
        this.addSignatoryInteractor = addSignatoryInteractor;
        this.createWithdrawTransactionInteractor = createWithdrawTransactionInteractor;
        this.preferencesUtil = preferencesUtil;
    }

    void showQR(Long amount) {
        Payload payload = new Payload(amount, preferencesUtil.retrieveUsername(), Payload.PayloadType.WITHDRAW);
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

    boolean doWithdrawOnline(Transaction transaction, InteractorListener listener){
        fragment.showLoading();
        addSignatoryInteractor.execute(Ed25519Sha3.publicKeyFromBytes(MyUtils.stringToBytes(transaction.getAgentPublicKey())),
                () -> {
                    createWithdrawTransactionInteractor.execute(transaction,
                            val2 -> {
                                listener.onNext(val2);
                            },
                            () -> {
                                if (listener != null)
                                    listener.onComplete(transaction);
                            }, throwable -> {
                                if (listener != null)
                                    listener.onError(throwable);
                            });
                },throwable -> {
                    fragment.hideLoading();
                    fragment.showError(throwable);
                });


        return true;
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

    void onDestroy() {
        fragment = null;
        this.generateQRInteractor.unsubscribe();
    }

    void onStop() {
        fragment = null;
    }

}