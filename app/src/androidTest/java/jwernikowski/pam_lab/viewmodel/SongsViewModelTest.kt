package jwernikowski.pam_lab.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.jraska.livedata.test
import jwernikowski.pam_lab.ui.activity.MainActivity
import jwernikowski.pam_lab.db.AppDatabase
import jwernikowski.pam_lab.db.data.entity.Song
import jwernikowski.pam_lab.db.repository.SongRepository
import jwernikowski.pam_lab.ui.fragment.songs.SongsViewModel
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SongsViewModelTest {

    private lateinit var db: AppDatabase
    private lateinit var songDao: SongDao
    private lateinit var songRepository: SongRepository

    private lateinit var songViewModel: SongsViewModel

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).allowMainThreadQueries().build()
        songDao = db.songDao()
        songRepository = SongRepository(songDao)
        songViewModel = SongsViewModel()
        songViewModel.songsRepository = songRepository
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertingSongsWorks() {
        val s1 = Song("test", 123, 145)
        val s2 = Song("", 10, 300)
        runBlocking { songViewModel.insert(s1) }
        val all = songDao.getAll()
        all.test()
            .awaitValue()
            .assertHasValue()
            .assertValue(listOf(s1))
    }

}