package com.okihita.accenture.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.MediumTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.okihita.accenture.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
@MediumTest
internal class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun onMainActivityLaunch_searchElementsShown() {
        onView(withId(R.id.clMain))
            .check(matches(isDisplayed()))
        onView(withId(R.id.tvList))
            .check(matches(isDisplayed()))
        onView(withId(R.id.etSearchQuery))
            .check(matches(isDisplayed()))
        onView(withId(R.id.btSearch))
            .check(matches(isDisplayed()))
    }
}