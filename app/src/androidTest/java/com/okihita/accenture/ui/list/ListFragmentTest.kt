package com.okihita.accenture.ui.list

import android.content.Context
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.jakewharton.espresso.OkHttp3IdlingResource
import com.okihita.accenture.R
import com.okihita.accenture.launchFragmentInHiltContainer
import com.okihita.accenture.util.PAGE_SIZE_PER_REQUEST
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.OkHttpClient
import org.hamcrest.Matchers.*
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltAndroidTest
@RunWith(AndroidJUnit4ClassRunner::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING) // To run tests according to name, ascending
@MediumTest
class ListFragmentTest {

    @Inject
    lateinit var okHttpClient: OkHttpClient

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val mockNavController: NavController = mock(NavController::class.java)
    private lateinit var networkIdlingResource: IdlingResource

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Before
    fun setup() {

        hiltRule.inject()

        networkIdlingResource = OkHttp3IdlingResource.create("okhttp", okHttpClient)

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
    fun test01_onSearchClick_blankQuery_errorShown() {

        onView(withId(R.id.etSearchQuery))
            .perform(typeText("  "), closeSoftKeyboard())
        onView(withId(R.id.btSearch))
            .perform(click())

        // Error TV for blank query is shown
        onView(withId(R.id.tvError))
            .check(matches(withText(containsString(context.getString(R.string.listFragment_blankQuery)))))

        // RV is hidden
        onView(withId(R.id.rvUsers))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun test02_onSearchClick_loadingShown() {

        onView(withId(R.id.etSearchQuery))
            .perform(typeText("sakura"), closeSoftKeyboard())
        onView(withId(R.id.btSearch))
            .perform(click())

        onView(withId(R.id.pbLoading))
            .check(matches(isDisplayed()))
    }

    @Test
    fun test03_onSearchResult_emptyResult_errorShown() {

        IdlingRegistry.getInstance().register(networkIdlingResource)

        // Search an unlikely username in GitHub
        onView(withId(R.id.etSearchQuery))
            .perform(typeText("asdliuhcliwehuwefbhawbcalsjdbc"), closeSoftKeyboard())
        onView(withId(R.id.btSearch))
            .perform(click())

        onView(withId(R.id.tvError))
            .check(matches(withText(containsString(context.getString(R.string.listFragment_emptyResult)))))
    }

    @Test
    fun test04_onSearchResult_notEmptyResult_searchResultShown() {

        IdlingRegistry.getInstance().register(networkIdlingResource)

        val searchQuery = "hello"

        // Searching a common username
        onView(withId(R.id.etSearchQuery))
            .perform(typeText(searchQuery), closeSoftKeyboard())
        onView(withId(R.id.btSearch))
            .perform(click())

        onView(withId(R.id.pbLoading)) // Make sure progress bar is not displayed
            .check(matches(not(isDisplayed())))

        // At least 5 items should be shown on screen, for a common search query
        // WARNING: This is a flaky test. May fail when the screen can't contain the RV items.
        onView(withId(R.id.rvUsers))
            .check(matches(hasMinimumChildCount(5)))

        onView(withId(R.id.rvUsers))
            .check(matches(hasDescendant(withText(searchQuery))))
    }

    @Test
    fun test04b_onSearchResult_exactlyOneResult_showNoMoreResultInfo() {

        val searchQuery = "okihita" // Only one user with this name

        // Searching a very specific username
        onView(withId(R.id.etSearchQuery))
            .perform(typeText(searchQuery), closeSoftKeyboard())
        onView(withId(R.id.btSearch))
            .perform(click())

        Thread.sleep(2_000)

        onView(withId(R.id.pbLoading)) // Make sure progress bar is not displayed
            .check(matches(not(isDisplayed())))

        // "No more search result" error message is shown
        onView(withId(R.id.tvAppendError))
            .check(matches(withText(containsString(context.getString(R.string.listFragment_noMoreResult)))))

    }

    @Test
    fun test05_onSearchResult_itemClick_mockNavControllerCalled() {

        IdlingRegistry.getInstance().register(networkIdlingResource)

        val searchQuery = "hello"
        val helloUserId = 1836624

        // Searching a common username
        onView(withId(R.id.etSearchQuery))
            .perform(typeText(searchQuery), closeSoftKeyboard())
        onView(withId(R.id.btSearch))
            .perform(click())

        // The displayed RV should contain items with "hello" in it.
        onView(withId(R.id.rvUsers))
            .check(matches(hasDescendant(withText(searchQuery))))

        onView(withId(R.id.rvUsers))
            .perform(actionOnItemAtPosition<GitHubUsersAdapter.GitHubUserVH>(0, click()))

        verify(mockNavController)
            .navigate(ListFragmentDirections.actionListFragmentToDetailsFragment(helloUserId))
    }

    @Test
    fun test06_onSearchResult_scrollToLastItem_morePageLoaded() {

        IdlingRegistry.getInstance().register(networkIdlingResource)

        // Searching a common username so we get potentially many paged results
        val searchQuery = "hello"
        onView(withId(R.id.etSearchQuery))
            .perform(typeText(searchQuery), closeSoftKeyboard())
        onView(withId(R.id.btSearch))
            .perform(click())

        // On original load, will fetch three pages, which is 30 users
        // Since it's zero-index, we will want to scroll to item at position 29
        onView(withId(R.id.rvUsers))
            .perform(
                actionOnItemAtPosition<GitHubUsersAdapter.GitHubUserVH>(
                    PAGE_SIZE_PER_REQUEST * 3 - 1,
                    scrollTo()
                )
            )

        // After waiting for network call, adapter should load another 10 items.
        // Now item at position 39 should exist (previously it didn't)
        onView(withId(R.id.rvUsers))
            .perform(
                actionOnItemAtPosition<GitHubUsersAdapter.GitHubUserVH>(
                    PAGE_SIZE_PER_REQUEST * 4 - 1,
                    scrollTo()
                )
            )

        // After waiting for network call, adapter should load another 10 items.
        // Now item at position 49 should exist (previously it didn't)
        onView(withId(R.id.rvUsers))
            .perform(
                actionOnItemAtPosition<GitHubUsersAdapter.GitHubUserVH>(
                    PAGE_SIZE_PER_REQUEST * 5 - 1,
                    scrollTo()
                )
            )
    }

    @Test
    fun test07_callSearchElevenTimes_showRateLimitError() {

        IdlingRegistry.getInstance().register(networkIdlingResource)

        repeat(11) {
            onView(withId(R.id.etSearchQuery))
                .perform(clearText(), typeText("nikita$it"), closeSoftKeyboard())
            onView(withId(R.id.btSearch))
                .perform(click())
        }

        onView(withId(R.id.rvUsers))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.tvError))
            .check(matches(isDisplayed()))
    }
}