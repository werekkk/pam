package jwernikowski.pam_lab

import android.media.AudioManager
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import jwernikowski.pam_lab.db.repository.SongRepository
import jwernikowski.pam_lab.di.AppComponent
import jwernikowski.pam_lab.di.AppModule
import jwernikowski.pam_lab.di.DaggerAppComponent
import jwernikowski.pam_lab.di.RoomModule
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var component: AppComponent
    }

    @Inject
    lateinit var songRepository: SongRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        volumeControlStream = AudioManager.STREAM_MUSIC
        setContentView(R.layout.activity_main)

        component = DaggerAppComponent.builder()
            .appModule(AppModule(application))
            .roomModule(RoomModule(application))
            .build()

        component.inject(this)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_metronome, R.id.navigation_rhythms, R.id.navigation_songs
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}
