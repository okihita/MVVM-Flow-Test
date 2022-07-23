package com.okihita.accenture.ui.list

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.MediumTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.okihita.accenture.R
import com.okihita.accenture.ui.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
@MediumTest
class ListFragmentTest {

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
    }

    @Test
    fun onSearchClick_showsLoading() {

        onView(withId(R.id.etSearchQuery))
            .perform(typeText("hello"), closeSoftKeyboard())
        onView(withId(R.id.btSearch))
            .perform(click())
        onView(withId(R.id.pbLoading))
            .check(matches(isDisplayed()))
    }

    @Test
    fun onSearchClick_after3Seconds_searchResultShown() {

        val searchQuery = "hello"

        // Searching a common username
        onView(withId(R.id.etSearchQuery))
            .perform(typeText(searchQuery), closeSoftKeyboard())
        onView(withId(R.id.btSearch))
            .perform(click())

        Thread.sleep(3000) // May cause flaky test. TODO: Use IdlingResource instead

        // At least 5 items should be shown on screen, for a common search query
        // WARNING: This is a flaky test. May fail when the screen can't contain the RV items.
        onView(withId(R.id.rvUsers)).check(matches(hasMinimumChildCount(5)))

        // The displayed RV should contain items with "hello" in it.
        onView(withId(R.id.rvUsers)).check(matches(hasDescendant(withText(searchQuery))));
    }

    @Test
    fun onSearchResultClick_fragmentChanged() {

        val searchQuery = "hello"

        // Searching a common username
        onView(withId(R.id.etSearchQuery))
            .perform(typeText(searchQuery), closeSoftKeyboard())
        onView(withId(R.id.btSearch))
            .perform(click())

        Thread.sleep(3000) // May cause flaky test. TODO: Use IdlingResource instead

        // The displayed RV should contain items with "hello" in it.
        onView(withId(R.id.rvUsers))
            .check(matches(hasDescendant(withText(searchQuery))));

        onView(withId(R.id.rvUsers)).perform(
            RecyclerViewActions.actionOnItemAtPosition<GitHubUsersAdapter.GitHubUserVH>(
                0, click()
            )
        )

        Thread.sleep(3000)
    }
}