package com.okihita.accenture.ui.details

import androidx.core.os.bundleOf
import androidx.paging.ExperimentalPagingApi
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.MediumTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.google.gson.Gson
import com.jakewharton.espresso.OkHttp3IdlingResource
import com.okihita.accenture.R
import com.okihita.accenture.data.model.GitHubUserDetails
import com.okihita.accenture.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.OkHttpClient
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import javax.inject.Inject

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4ClassRunner::class)
@HiltAndroidTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING) // To run tests according to name, ascending
@MediumTest
class DetailsFragmentTest {

    @Inject
    lateinit var okHttpClient: OkHttpClient

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var networkIdlingResource: IdlingResource

    @Before
    fun setup() {
        hiltRule.inject()
        networkIdlingResource = OkHttp3IdlingResource.create("okhttp", okHttpClient)
    }

    @After
    fun teardown() {
        IdlingRegistry.getInstance().unregister()
    }

    @Test
    fun test01_onLoadPhilippLackner_noDatabaseResult_philipNameLoaded() {

        launchFragmentInHiltContainer<DetailsFragment>(
            bundleOf("userId" to 53933333) // GitHub user philipplackner
        )

        IdlingRegistry.getInstance().register(networkIdlingResource)

        Espresso.onView(ViewMatchers.withId(R.id.tvUsername))
            .check(ViewAssertions.matches(ViewMatchers.withText(userPhilipp.name)))
        Espresso.onView(ViewMatchers.withId(R.id.tvBio))
            .check(ViewAssertions.matches(ViewMatchers.withText(userPhilipp.bio)))
    }

    @Test
    fun test02_onLoadNonexistentUser_errorShown() {

        launchFragmentInHiltContainer<DetailsFragment>(
            bundleOf("userId" to 539333331287361) // Should return "HTTP 404"
        )

        IdlingRegistry.getInstance().register(networkIdlingResource)
        Thread.sleep(1_000)

        Espresso.onView(ViewMatchers.withId(R.id.tvUsername))
            .check(ViewAssertions.matches(ViewMatchers.withText("HTTP 404 ")))
    }


    private val philippJson = """
        {
            "login": "philipplackner",
            "id": 53933333,
            "node_id": "MDQ6VXNlcjUzOTMzMzMz",
            "avatar_url": "https://avatars.githubusercontent.com/u/53933333?v=4",
            "gravatar_id": "",
            "url": "https://api.github.com/users/philipplackner",
            "html_url": "https://github.com/philipplackner",
            "followers_url": "https://api.github.com/users/philipplackner/followers",
            "following_url": "https://api.github.com/users/philipplackner/following{/other_user}",
            "gists_url": "https://api.github.com/users/philipplackner/gists{/gist_id}",
            "starred_url": "https://api.github.com/users/philipplackner/starred{/owner}{/repo}",
            "subscriptions_url": "https://api.github.com/users/philipplackner/subscriptions",
            "organizations_url": "https://api.github.com/users/philipplackner/orgs",
            "repos_url": "https://api.github.com/users/philipplackner/repos",
            "events_url": "https://api.github.com/users/philipplackner/events{/privacy}",
            "received_events_url": "https://api.github.com/users/philipplackner/received_events",
            "type": "User",
            "site_admin": false,
            "name": "Philipp Lackner",
            "company": null,
            "blog": "",
            "location": "Germany",
            "email": null,
            "hireable": null,
            "bio": "I post awesome Android stuff on my Instagram page @philipplackner_official and on my YouTube channel Philipp Lackner.",
            "twitter_username": null,
            "public_repos": 142,
            "public_gists": 4,
            "followers": 4878,
            "following": 1,
            "created_at": "2019-08-09T07:42:14Z",
            "updated_at": "2022-05-05T09:43:30Z"
        }
    """.trimIndent()

    private val userPhilipp = Gson().fromJson(philippJson, GitHubUserDetails::class.java)

}