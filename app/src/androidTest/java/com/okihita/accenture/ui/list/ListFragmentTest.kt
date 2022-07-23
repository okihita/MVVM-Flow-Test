package com.okihita.accenture.ui.list

import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.MediumTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.jakewharton.espresso.OkHttp3IdlingResource
import com.okihita.accenture.R
import com.okihita.accenture.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltAndroidTest
@RunWith(AndroidJUnit4ClassRunner::class)
@MediumTest
class ListFragmentTest {

    @Inject
    lateinit var okHttpClient: OkHttpClient

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val mockNavController: NavController = mock(NavController::class.java)
    private lateinit var idlingResource: IdlingResource

    @Before
    fun setup() {

        hiltRule.inject()

        idlingResource = OkHttp3IdlingResource.create("okhttp", okHttpClient)

        launchFragmentInHiltContainer<ListFragment> {
            mockNavController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(requireView(), mockNavController)
        }
    }

    @After
    fun teardown() {
        IdlingRegistry.getInstance().unregister()
    }

    @Test
    fun onSearchClick_showsLoading() {

        onView(ViewMatchers.withId(R.id.etSearchQuery))
            .perform(typeText("sakura"), closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.btSearch))
            .perform(click())
        onView(ViewMatchers.withId(R.id.pbLoading))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun onSearchClick_afterNetworkFinished_searchResultShown() {

        IdlingRegistry.getInstance().register(idlingResource)

        val searchQuery = "hello"

        // Searching a common username
        onView(ViewMatchers.withId(R.id.etSearchQuery))
            .perform(typeText(searchQuery), closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.btSearch))
            .perform(click())

        // At least 5 items should be shown on screen, for a common search query
        // WARNING: This is a flaky test. May fail when the screen can't contain the RV items.
        onView(ViewMatchers.withId(R.id.rvUsers))
            .check(matches(ViewMatchers.hasMinimumChildCount(5)))

        // The displayed RV should contain items with "Sakura" in it.
        onView(ViewMatchers.withId(R.id.rvUsers))
            .check(matches(ViewMatchers.hasDescendant(ViewMatchers.withText(searchQuery))))
    }

    @Test
    fun onSearchResultClick_mockNavControllerCalled() {

        IdlingRegistry.getInstance().register(idlingResource)

        val searchQuery = "hello"
        val helloUserId = 1836624

        // Searching a common username
        onView(ViewMatchers.withId(R.id.etSearchQuery))
            .perform(typeText(searchQuery), closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.btSearch))
            .perform(click())

        // The displayed RV should contain items with "hello" in it.
        onView(ViewMatchers.withId(R.id.rvUsers))
            .check(matches(ViewMatchers.hasDescendant(ViewMatchers.withText(searchQuery))));

        onView(ViewMatchers.withId(R.id.rvUsers)).perform(
            RecyclerViewActions.actionOnItemAtPosition<GitHubUsersAdapter.GitHubUserVH>(
                0, click()
            )
        )

        verify(mockNavController)
            .navigate(ListFragmentDirections.actionListFragmentToDetailsFragment(helloUserId))
    }
}