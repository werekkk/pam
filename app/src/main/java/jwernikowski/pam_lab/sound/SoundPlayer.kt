package jwernikowski.pam_lab.sound

import android.content.Context
import android.media.SoundPool
import jwernikowski.pam_lab.R
import jwernikowski.pam_lab.dto.RhythmLineDto

class SoundPlayer(context: Context) {

    companion object {
        private var wood_id : Int = 0
        private var triangle_id: Int = 0
        val soundPool : SoundPool = SoundPool.Builder().setMaxStreams(20).build()
    }

    init {
        wood_id = soundPool.load(context, R.raw.wood, 1)
        triangle_id = soundPool.load(context, R.raw.triangle, 1)
    }

    fun playRhythmLines(rhythmLines: Iterable<RhythmLineDto>, blockIndex: Int) {
        if (blockIndex < rhythmLines.first().beats.size) {
            rhythmLines.forEach {
                if (it.beats[blockIndex])
                    play(it.sound)
            }
        }
    }

    private fun play(sound : Sound) {
        when (sound) {
            Sound.WOOD -> soundPool.play(wood_id, 0.8f, 0.8f, 1, 0, 1f)
            Sound.TRIANGLE -> soundPool.play(triangle_id, 1f, 1f, 1, 0, 1f)
        }
    }

}