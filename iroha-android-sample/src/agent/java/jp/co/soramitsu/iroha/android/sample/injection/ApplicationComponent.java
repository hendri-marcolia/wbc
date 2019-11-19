package jp.co.soramitsu.iroha.android.sample.injection;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjector;
import jp.co.soramitsu.iroha.android.sample.view.main.MainActivity;
import jp.co.soramitsu.iroha.android.sample.view.main.SplashScreenActivity;
import jp.co.soramitsu.iroha.android.sample.view.main.history.HistoryFragment;
import jp.co.soramitsu.iroha.android.sample.view.main.receive.ReceiveFragment;
import jp.co.soramitsu.iroha.android.sample.view.main.send.SendFragment;
import jp.co.soramitsu.iroha.android.sample.view.registration.RegistrationActivity;
import jp.co.soramitsu.iroha.android.sample.view.login.LoginActivity;
import jp.co.soramitsu.iroha.android.sample.view.scan.ScanFragment;

@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent extends AndroidInjector {

    void inject(HistoryFragment historyFragment);

    void inject(SendFragment sendFragment);

    void inject(ScanFragment scanFragment);

    void inject(ReceiveFragment receiveFragment);

    void inject(RegistrationActivity registrationActivity);

    void inject(MainActivity mainActivity);

    void inject(LoginActivity loginActivity);

    void inject(SplashScreenActivity splashScreenActivity);
}