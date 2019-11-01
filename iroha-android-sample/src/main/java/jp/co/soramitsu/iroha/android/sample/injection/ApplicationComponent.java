package jp.co.soramitsu.iroha.android.sample.injection;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjector;
import jp.co.soramitsu.iroha.android.sample.view.main.MainActivity;
import jp.co.soramitsu.iroha.android.sample.view.main.history.HistoryFragment;
import jp.co.soramitsu.iroha.android.sample.view.main.receive.ReceiveFragment;
import jp.co.soramitsu.iroha.android.sample.view.main.send.SendFragment;
import jp.co.soramitsu.iroha.android.sample.registration.RegistrationActivity;
import jp.co.soramitsu.iroha.android.sample.view.login.LoginActivity;

@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent extends AndroidInjector {

    void inject(HistoryFragment historyFragment);

    void inject(SendFragment sendFragment);

    void inject(ReceiveFragment receiveFragment);

    void inject(RegistrationActivity registrationActivity);

    void inject(MainActivity mainActivity);

    void inject(LoginActivity loginActivity);
}