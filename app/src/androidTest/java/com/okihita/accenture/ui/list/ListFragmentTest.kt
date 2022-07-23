package com.okihita.accenture.ui.list

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.MediumTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.okihita.accenture.R
import com.okihita.accenture.ui.MainActivity
import org.hamcrest.CoreMatchers.containsString
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
        onView(withId(R.id.tvList))
            .check(matches(withText(containsString("Loading..."))))
    }

    @Test
    fun onSearchClick_after3Seconds_showsSearchResults() {

        onView(withId(R.id.etSearchQuery))
            .perform(typeText("hello"), closeSoftKeyboard())
        onView(withId(R.id.btSearch))
            .perform(click())

        Thread.sleep(3000) // May cause flaky test. TODO: Use IdlingResource instead

        onView(withId(R.id.tvList))
            .check(matches(withText(containsString("hello"))))
    }
}