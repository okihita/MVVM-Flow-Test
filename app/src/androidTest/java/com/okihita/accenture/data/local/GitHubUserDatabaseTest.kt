package com.okihita.accenture.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.okihita.accenture.data.model.GitHubUser
import com.okihita.accenture.generateUsers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class GitHubUserDatabaseTest {

    private lateinit var userDao: GitHubUserDao
    private lateinit var db: GitHubUserDatabase

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            GitHubUserDatabase::class.java
        ).build()
        userDao = db.userDao
    }

    @After
    fun teardown() {

        db.close()
    }

    @Test
    fun newDatabase_loadUsers_returnEmpty() = runBlocking {
        assertThat(userDao.getAllUsers()).isEqualTo(listOf<GitHubUser>())
    }

    @Test
    fun saveUsers_loadUsers_returnResult() = runBlocking {
        val users = generateUsers(15)
        userDao.insertAll(users)

        val dbUsers = userDao.getAllUsers()

        assertThat(dbUsers).isNotEmpty()
        assertThat(dbUsers).hasSize(15)
    }
}