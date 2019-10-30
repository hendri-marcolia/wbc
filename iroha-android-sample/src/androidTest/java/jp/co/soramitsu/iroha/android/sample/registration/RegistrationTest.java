package jp.co.soramitsu.iroha.android.sample.registration;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import jp.co.soramitsu.iroha.android.sample.MatcherUtils;
import jp.co.soramitsu.iroha.android.sample.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static jp.co.soramitsu.iroha.android.sample.Const.INVALID_USERNAME;
import static jp.co.soramitsu.iroha.android.sample.Const.VALID_USERNAME;
import static jp.co.soramitsu.iroha.android.sample.PreferencesUtil.SAVED_USERNAME;
import static jp.co.soramitsu.iroha.android.sample.PreferencesUtil.SHARED_PREFERENCES_FILE;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RegistrationTest {

    @Rule
    public IntentsTestRule<RegistrationActivity> rule = new IntentsTestRule<>(
            RegistrationActivity.class, false, false);

    @Test
    public void ui_AllLabelsAreDisplayed() {
        onView(ViewMatchers.withId(R.id.title)).check(matches(isDisplayed()));
        onView(withId(R.id.subtitle)).check(matches(isDisplayed()));
        onView(withId(R.id.username)).check(matches(isDisplayed()));
        onView(withId(R.id.register_button)).check(matches(isDisplayed()));
        onView(withId(R.id.username_input)).check(matches(MatcherUtils.withHint(R.string.username_hint)));
    }

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
    }

    @Test
    public void changeText_invalidUsername() {
        onView(withId(R.id.username)).perform(typeText(INVALID_USERNAME), closeSoftKeyboard());

        onView(withId(R.id.username)).check(matches(withText("ulat")));
    }

    @Test
    public void changeText_validUsername() {
        onView(withId(R.id.username)).perform(typeText(VALID_USERNAME), closeSoftKeyboard());

        onView(withId(R.id.username)).check(matches(withText(VALID_USERNAME)));
    }

    @Test
    public void register_EmptyUsername() {
        onView(withId(R.id.register_button)).perform(click());

        onView(withText(R.string.error_dialog_title))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withText(R.string.username_empty_error_dialog_message))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
    }

    @Test
    public void register_GetAccountError() {

    }

    @Test
    public void register_GetAccountExists() {

    }

    @Test
    public void register_CreateAccountError() {

    }

    @Test
    public void register_AddAssetError() {

    }

    @Test
    public void register_Success() {

    }
}