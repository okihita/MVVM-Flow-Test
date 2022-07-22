package com.okihita.accenture.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.okihita.accenture.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
@MediumTest
internal class MainActivityTest {

    @Test
    fun onMainActivityLaunch_searchElementsShown() {
        ActivityScenario.launch(MainActivity::class.java)
        Espresso.onView(withId(R.id.clMain))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.tvList))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.etSearchQuery))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.btSearch))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}