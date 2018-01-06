package com.aadimator.bakingapp;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;


import org.hamcrest.Description;
import org.hamcrest.Matcher;

import static org.hamcrest.core.Is.is;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import com.aadimator.bakingapp.activities.MainActivity;

/**
 * Instrumented test, which will execute on an Android device.
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    private static final String RECIPE_NAME = "Brownies";

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    //Clicks on a Recipe and checks if new activity's action bar matches recipe
    @Test
    public void clickRecyclerViewItem_OpensRecipeDetailActivity() {

        try {
            Thread.sleep(5000); // sleep for 5 sec so Recipes could be fetched from API
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.rv_recipe_list))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText(RECIPE_NAME)), click()));

        matchActionBarTitle(RECIPE_NAME);
    }


    private static void matchActionBarTitle(CharSequence title) {
        onView(isAssignableFrom(Toolbar.class))
                .check(matches(withActionBarTitle(is(title))));
    }

    private static Matcher<Object> withActionBarTitle(final Matcher<CharSequence> matcher) {
        return new BoundedMatcher<Object, Toolbar>(Toolbar.class) {
            @Override
            public boolean matchesSafely(Toolbar toolbar) {
                return matcher.matches(toolbar.getTitle());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("With Action Bar Title: ");
                matcher.describeTo(description);
            }
        };
    }
}
