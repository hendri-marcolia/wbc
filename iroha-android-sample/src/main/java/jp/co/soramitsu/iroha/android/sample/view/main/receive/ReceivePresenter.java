package jp.co.soramitsu.iroha.android.sample.view.main.receive;

import com.orhanobut.logger.Logger;

import javax.inject.Inject;

import lombok.Setter;

public class ReceivePresenter {

    @Setter
    private ReceiveFragment fragment;


    @Inject
    public ReceivePresenter() {
    }

    void generateQR(String amount) {
        if (amount.isEmpty()) {
            amount = "0";
        }
    }

    void onStop() {
        fragment = null;
    }
}