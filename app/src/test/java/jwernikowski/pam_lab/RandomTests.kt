package jwernikowski.pam_lab

import jwernikowski.pam_lab.dto.RhythmLineDto
import jwernikowski.pam_lab.sound.Sound
import org.junit.Test

class RandomTests {

    @Test
    fun rhythmLineListsEqual() {
        val l1 = listOf(RhythmLineDto(Sound.WOOD, arrayOf(true, false)))
        val l2 = listOf(RhythmLineDto(Sound.WOOD, arrayOf(true, false)))
        val l3 = listOf(RhythmLineDto(Sound.WOOD, arrayOf(false, true)))
        assert(l1 == l2)
        assert(l1 != l3)
    }

}