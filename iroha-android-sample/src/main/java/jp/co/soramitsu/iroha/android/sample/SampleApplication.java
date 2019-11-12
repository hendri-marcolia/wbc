package jp.co.soramitsu.iroha.android.sample;

import android.app.Application;
import androidx.annotation.VisibleForTesting;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.orm.SugarContext;

import jp.co.soramitsu.iroha.android.sample.data.Account;
import jp.co.soramitsu.iroha.android.sample.injection.ApplicationComponent;
import jp.co.soramitsu.iroha.android.sample.injection.DaggerApplicationComponent;
import lombok.Getter;

public class SampleApplication extends Application {
    public static SampleApplication instance;

    public Account account;

    @Getter
    @VisibleForTesting
    public ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        applicationComponent = DaggerApplicationComponent.builder().build();
        Logger.addLogAdapter(new AndroidLogAdapter());
        SugarContext.init(this);
    }
}