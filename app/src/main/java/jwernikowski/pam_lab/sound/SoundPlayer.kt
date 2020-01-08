package jwernikowski.pam_lab.sound

import android.content.Context
import android.media.SoundPool
import jwernikowski.pam_lab.R
import jwernikowski.pam_lab.models.RhythmDto
import jwernikowski.pam_lab.models.RhythmLineDto

class SoundPlayer(context: Context) {

    companion object {
        private var wood_id : Int = 0
        private var triangle_id: Int = 0
        val soundPool : SoundPool = SoundPool.Builder().setMaxStreams(6).build()
    }

    init {
        wood_id = soundPool.load(context, R.raw.wood, 1)
        triangle_id = soundPool.load(context, R.raw.triangle, 1)
    }

    fun play(sound : Sound) {
        when (sound) {
            Sound.WOOD -> soundPool.play(wood_id, 1f, 1f, 1, 0, 1f)
            Sound.TRIANGLE -> soundPool.play(triangle_id, 0.5f, 0.5f, 1, 0, 1f)
        }
    }

    fun playRhythmLines(rhythmLines: Iterable<RhythmLineDto>, blockIndex: Int) {
        rhythmLines.forEach { line -> run{
            if (line.beats[blockIndex])
                play(line.sound)
        } }
    }

}