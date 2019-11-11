package jp.co.soramitsu.iroha.android.sample.data;

import java.security.KeyPair;

import lombok.Getter;
import lombok.Setter;

public class UglyPairing {
    @Getter
    @Setter
    String accountId;
    @Getter
    @Setter
    KeyPair keyPair;
    @Getter
    @Setter
    BankInfo bankInfo;

    public UglyPairing(String accountId, KeyPair keyPair, BankInfo bankInfo) {
        this.accountId = accountId;
        this.keyPair = keyPair;
        this.bankInfo = bankInfo;
    }
}
