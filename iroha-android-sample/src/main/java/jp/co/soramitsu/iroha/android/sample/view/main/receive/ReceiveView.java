package jp.co.soramitsu.iroha.android.sample.view.main.receive;

import android.graphics.Bitmap;

public interface ReceiveView {

    void didGenerateSuccess(Bitmap bitmap);

    void didError(Throwable error);
}