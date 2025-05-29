package com.example.dailymate;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.matcher.ViewMatchers;

import com.example.dailymate.Presentation.ui.LoginActivity;
import com.example.dailymate.Presentation.ui.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.allOf;

import android.widget.FrameLayout;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void loginFragment_shouldBeDisplayedInLoginActivity() {
        // Fokus ke FrameLayout parent saja
        onView(allOf(
                withId(R.id.fragmentContainer),
                isAssignableFrom(FrameLayout.class)))
                .check(matches(isDisplayed()));

        // Atau verifikasi ada komponen dari fragment
        onView(withId(R.id.btnLogin))  // Contoh komponen di fragment
                .check(matches(isDisplayed()));
    }
}

//@RunWith(AndroidJUnit4.class)
//public class ExampleInstrumentedTest {
//    @Test
//    public void useAppContext() {
//        // Context of the app under test.
//        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        assertEquals("com.example.dailymate", appContext.getPackageName());
//    }
//}