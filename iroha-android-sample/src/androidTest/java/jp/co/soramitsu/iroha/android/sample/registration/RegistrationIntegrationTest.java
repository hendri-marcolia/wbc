package jp.co.soramitsu.iroha.android.sample.registration;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.espresso.IdlingPolicies;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import jp.co.soramitsu.iroha.android.sample.NetworkRequestIdlingResources;
import jp.co.soramitsu.iroha.android.sample.R;
import jp.co.soramitsu.iroha.android.sample.main.MainActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static jp.co.soramitsu.iroha.android.sample.Const.EXISTING_USERNAME;
import static jp.co.soramitsu.iroha.android.sample.Const.NETWORK_TIMEOUT_SECONDS;
import static jp.co.soramitsu.iroha.android.sample.PreferencesUtil.SAVED_USERNAME;
import static jp.co.soramitsu.iroha.android.sample.PreferencesUtil.SHARED_PREFERENCES_FILE;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RegistrationIntegrationTest {

    private String username;

    @Rule
    public IntentsTestRule<RegistrationActivity> rule = new IntentsTestRule<>(
            RegistrationActivity.class, false, false);

    @Before
    public void setUp() {
        InstrumentationRegistry.getTargetContext()
                .getSharedPreferences(
                        SHARED_PREFERENCES_FILE,
                        Context.MODE_PRIVATE)
                .edit()
                .remove(SAVED_USERNAME)
                .apply();

        rule.launchActivity(null);

        username = String.valueOf(System.currentTimeMillis());
    }

    @Test
    public void register_existingUsername() {
        onView(withId(R.id.username)).perform(typeText(EXISTING_USERNAME), closeSoftKeyboard());
        onView(withId(R.id.register_button)).perform(click());

        IdlingPolicies.setIdlingResourceTimeout(NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        IdlingPolicies.setMasterPolicyTimeout(NETWORK_TIMEOUT_SECONDS * 2, TimeUnit.SECONDS);

        IdlingResource idlingResource = new NetworkRequestIdlingResources(rule.getActivity().registrationPresenter);
        IdlingRegistry.getInstance().register(idlingResource);

        onView(withText(R.string.error_dialog_title))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withText(R.string.username_already_exists_error_dialog_message))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withText(android.R.string.ok))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    @Test
    public void register_validUsername() {
        onView(withId(R.id.username)).perform(typeText(username), closeSoftKeyboard());
        onView(withId(R.id.register_button)).perform(click());

        IdlingPolicies.setIdlingResourceTimeout(NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        IdlingPolicies.setMasterPolicyTimeout(NETWORK_TIMEOUT_SECONDS * 2, TimeUnit.SECONDS);

        IdlingResource idlingResource = new NetworkRequestIdlingResources(rule.getActivity().registrationPresenter);
        IdlingRegistry.getInstance().register(idlingResource);

        intended(hasComponent(MainActivity.class.getName()));

        IdlingRegistry.getInstance().unregister(idlingResource);
    }
}