package jp.co.soramitsu.iroha.android.sample.view.withdraw;

import javax.inject.Inject;

import jp.co.soramitsu.iroha.android.sample.interactor.GenerateQRInteractor;
import lombok.Setter;

public class WithdrawPresenter {

    @Setter
    private WithdrawFragment fragment;

    private final GenerateQRInteractor generateQRInteractor;

    @Inject
    public WithdrawPresenter(GenerateQRInteractor generateQRInteractor) {
        this.generateQRInteractor = generateQRInteractor;
    }


    void onStop() {
        fragment = null;
    }
}