package jwernikowski.pam_lab.utils

class Tempo {

    companion object {
        val MIN_BPM = 10
        val MAX_BPM = 300

        val DEFAULT_INITIAL = 60
        val DEFAULT_GOAL = 120

        fun bpmToProgress(tempo: Int) : Int {
            return (100 * (tempo - MIN_BPM).toFloat() / (MAX_BPM - MIN_BPM)).toInt()
        }

        fun progressToBpm(progress: Int): Int {
            return (MIN_BPM + (MAX_BPM - MIN_BPM) * (progress.toFloat() / 100)).toInt()
        }

        fun truncate(tempo: Int): Int {
            if (tempo < MIN_BPM)
                return MIN_BPM
            if (tempo > MAX_BPM)
                return MAX_BPM
            return tempo
        }

        fun isTempoValid(tempo: Int): Boolean {
            return tempo >= MIN_BPM && tempo <= MAX_BPM
        }
    }
}