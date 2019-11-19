package jp.co.soramitsu.iroha.android.sample.view.scan;

import android.content.Intent;

import com.orhanobut.logger.Logger;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.EmptyStackException;
import java.util.List;

import javax.inject.Inject;

import iroha.protocol.TransactionOuterClass;
import jp.co.soramitsu.crypto.ed25519.Ed25519Sha3;
import jp.co.soramitsu.iroha.android.sample.EndPoint.EndPoint;
import jp.co.soramitsu.iroha.android.sample.MyUtils;
import jp.co.soramitsu.iroha.android.sample.PreferencesUtil;
import jp.co.soramitsu.iroha.android.sample.data.Payload;
import jp.co.soramitsu.iroha.android.sample.data.PerformSavePayload;
import jp.co.soramitsu.iroha.android.sample.data.Transaction;
import jp.co.soramitsu.iroha.android.sample.interactor.GenerateTransactionQRInteractor;
import jp.co.soramitsu.iroha.android.sample.interactor.GetPendingTransactionInteractor;
import jp.co.soramitsu.iroha.android.sample.interactor.deposit.AgentPerformSaveOnlineInteractor;
import jp.co.soramitsu.iroha.android.sample.interactor.deposit.AgentSignPendingTransactionInteractor;
import jp.co.soramitsu.iroha.android.sample.qrscanner.QRScannerActivity;
import lombok.Setter;

public class ScanPresenter {

    public static final int REQUEST_CODE_QR_SCAN = 102;
    @Setter
    private ScanFragment fragment;

    private final GenerateTransactionQRInteractor generateQRInteractor;

    private final GetPendingTransactionInteractor getPendingTransactionInteractor;

    private final AgentSignPendingTransactionInteractor agentSignPendingTransactionInteractor;

    private final AgentPerformSaveOnlineInteractor agentPerformSaveOnlineInteractor;

    private final PreferencesUtil preferencesUtil;


    @Inject
    public ScanPresenter(GenerateTransactionQRInteractor generateQRInteractor,
                         GetPendingTransactionInteractor getPendingTransactionInteractor,
                         AgentSignPendingTransactionInteractor agentSignPendingTransactionInteractor, AgentPerformSaveOnlineInteractor agentPerformSaveOnlineInteractor, PreferencesUtil preferencesUtil) {
        this.generateQRInteractor = generateQRInteractor;
        this.agentSignPendingTransactionInteractor = agentSignPendingTransactionInteractor;
        this.agentPerformSaveOnlineInteractor = agentPerformSaveOnlineInteractor;
        this.preferencesUtil = preferencesUtil;
        this.getPendingTransactionInteractor = getPendingTransactionInteractor;
    }

    void showQR(Transaction transaction, Transaction.TransactionType type) {
        try {
            KeyPair keyPair = preferencesUtil.retrieveKeys();
            transaction.setAgentPublicKey(MyUtils.bytesToString(keyPair.getPublic().getEncoded()));
            transaction.setAgentId(preferencesUtil.retrieveUsername());
            transaction.setAgentSinging(null);
            transaction.setTransactionType(type);
            byte[] sign = MyUtils.sign(transaction, keyPair);
            transaction.setAgentSinging(MyUtils.bytesToString(sign));
            validateTransaction(transaction, true);
            generateQRInteractor.execute(transaction, bitmap -> {
                fragment.didGenerateSuccess(bitmap, transaction);
            }, throwable -> {
                Logger.e(throwable.getMessage());
                fragment.showError(throwable);
            });
        } catch (Throwable e) {
            fragment.showError(e);
        }

    }

    void doScanQR() {
        Intent i = new Intent(fragment.getActivity(), QRScannerActivity.class);
        fragment.startActivityForResult(i, REQUEST_CODE_QR_SCAN);
    }

    void onDestroy() {
        fragment = null;
        this.generateQRInteractor.unsubscribe();
    }

    List<TransactionOuterClass.Transaction> getPendingTransaction(Transaction transaction) {
        String accountId = transaction.getTransactionPayload().getPayload().getCustomerId();
        getPendingTransactionInteractor.execute(accountId, transactions -> {
            agentSignPendingTransactionInteractor.execute(transactions, o -> {

                    },
                    () -> {
                        // Perform Save
                        PerformSavePayload payload = new PerformSavePayload();
                        payload.setAmount(String.valueOf(transaction.getTransactionPayload().getPayload().getAmount()));
                        payload.setAgentId(transaction.getAgentId());
                        payload.setAgentPublicKey(transaction.getAgentPublicKey());
                        payload.setAccountId(transaction.getTransactionPayload().getPayload().getCustomerId());
                        payload.setAccountPublicKey(transaction.getAgentPublicKey());
                        agentPerformSaveOnlineInteractor.execute(payload, httpResult -> {
                            fragment.showInfo("Success Sign the transaction for account : " + accountId);
                        }, throwable -> {
                            fragment.showError(throwable);
                        });
                    }, throwable -> {
                        fragment.showError(throwable);
                    });
        }, throwable -> {
            if (throwable instanceof EmptyStackException)
                fragment.getPendingTransaction(transaction);
            else
                fragment.showError(throwable);
        });
        return null;
    }

    boolean validateTransaction(Transaction transaction, boolean validateAgent) throws Throwable {
        Payload payload = transaction.getTransactionPayload().getPayload();
        KeyPair agentkeyPair = preferencesUtil.retrieveKeys();
        PublicKey customerPublicKey = Ed25519Sha3.publicKeyFromBytes(MyUtils.stringToBytes(transaction.getTransactionPayload().getCustomerPublicKey()));
        try {
            MyUtils.verify(payload,
                    MyUtils.stringToBytes(transaction.getTransactionPayload().getCustomerSigning()),
                    customerPublicKey);
        } catch (Exception e) {
            throw new Throwable("Failed to verify payload, " + e.getMessage());
        }
        // verify agent signing
        if (!validateAgent) return true;
        try {
            if (transaction.getAgentId() != null && transaction.getAgentId().length() > 0 &&
                    transaction.getAgentPublicKey() != null && transaction.getAgentPublicKey().length() > 0 &&
                    transaction.getAgentSinging() != null && transaction.getAgentSinging() != null) {
                String tempSigning = transaction.getAgentSinging();
                transaction.setAgentSinging(null);
                MyUtils.verify(transaction,
                        MyUtils.stringToBytes(tempSigning),
                        agentkeyPair.getPublic()
                );
                transaction.setAgentSinging(tempSigning);
            } else throw new Exception("Agent signing missing");
        } catch (Exception e) {
            throw new Throwable("Failed to verify agent signing, " + e.getMessage());
        }


        return true;
    }

}