package jp.co.soramitsu.iroha.android.sample.services;

import com.google.firebase.messaging.FirebaseMessagingService;

import androidx.annotation.NonNull;

public class AppFcmService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }
}
