package jp.co.soramitsu.iroha.android.sample.view.withdraw;

import javax.inject.Inject;

import lombok.Setter;

public class WithdrawPresenter {

    @Setter
    private WithdrawFragment fragment;


    @Inject
    public WithdrawPresenter() {
    }


    void onStop() {
        fragment = null;
    }
}