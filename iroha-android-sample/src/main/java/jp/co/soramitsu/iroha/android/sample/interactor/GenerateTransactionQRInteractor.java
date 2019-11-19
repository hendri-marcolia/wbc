package jp.co.soramitsu.iroha.android.sample.interactor;


import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.gson.Gson;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import jp.co.soramitsu.iroha.android.sample.PreferencesUtil;
import jp.co.soramitsu.iroha.android.sample.data.Transaction;
import jp.co.soramitsu.iroha.android.sample.injection.ApplicationModule;

public class GenerateTransactionQRInteractor extends SingleInteractor<Bitmap, Transaction> {

    private final PreferencesUtil preferenceUtils;

    @Inject
    GenerateTransactionQRInteractor(@Named(ApplicationModule.JOB) Scheduler jobScheduler,
                                    @Named(ApplicationModule.UI) Scheduler uiScheduler,
                                    PreferencesUtil preferencesUtil) {
        super(jobScheduler, uiScheduler);
        this.preferenceUtils = preferencesUtil;
    }

    private final int SIZE = 1000;

    @Override
    protected Single<Bitmap> build(Transaction t) {
        return Single.create(emitter -> {
            String qrText = new Gson().toJson(t);
                        Map<EncodeHintType, String> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            QRCode qrCode = Encoder.encode(qrText, ErrorCorrectionLevel.L, hints);
            final ByteMatrix byteMatrix = qrCode.getMatrix();
            final int width = byteMatrix.getWidth();
            final int height = byteMatrix.getHeight();
            final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    byte val = byteMatrix.get(x, y);
                    bitmap.setPixel(x, y, val == 1 ? Color.BLACK : Color.WHITE);
                }
            }
            emitter.onSuccess(Bitmap.createScaledBitmap(bitmap, SIZE, SIZE, false));
        });
    }
}