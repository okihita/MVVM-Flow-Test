package com.okihita.accenture.data.remote

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class GitHubApiTest {

    lateinit var gitHubApi: GitHubApi
    lateinit var server: MockWebServer

    @Before
    fun setUp() {
        server = MockWebServer()
        gitHubApi = Retrofit.Builder()
            .baseUrl(server.url(""))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GitHubApi::class.java)
    }

    private fun enqueueMockResponse(fileName: String) {
        javaClass.classLoader?.let { classLoader ->
            val source = classLoader.getResourceAsStream(fileName).source().buffer()
            val mockResponse = MockResponse()
            mockResponse.setBody(source.readString(Charsets.UTF_8))
            server.enqueue(mockResponse)
        }
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun getSearchResult_sentRequest_receivedExpected() = runBlocking {

        enqueueMockResponse("CardcaptorSearchResult.json")
        val response = gitHubApi.getUsers("cardcaptor", 20, 1)

        val request = server.takeRequest()

        assertThat(response).isNotNull()
        assertThat(request.path).isEqualTo("/search/users?q=cardcaptor&per_page=20&page=1")
        assertThat(response.users.size).isAtMost(20)
    }

    @Test
    fun getSearchResult_emptyResult_receivedEmpty() = runBlocking {

        enqueueMockResponse("EmptySearchResult.json")
        val response = gitHubApi.getUsers("emptyResult")

        assertThat(response).isNotNull()
        assertThat(response.users).hasSize(0)
    }

    @Test
    fun getSearchResult_rateLimited_receivedError() = runBlocking {

        enqueueMockResponse("SearchRateLimitExceeded.json")
        val response = gitHubApi.getUsers("rateLimited", 10, 1)

        assertThat(response).isNotNull()
        assertThat(response.message).isNotNull()
    }

    @Test
    fun getUserProfile_sentRequest_receivedExpected() = runBlocking {

        enqueueMockResponse("UserProfileResult.json")
        val response = gitHubApi.getUserById(29698459)

        val request = server.takeRequest()

        assertThat(response).isNotNull()
        assertThat(request.path).isEqualTo("/user/29698459")
        assertThat(response.login.lowercase()).contains("cardcaptor")
    }
}