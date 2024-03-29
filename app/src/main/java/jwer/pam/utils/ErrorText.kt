package jwer.pam.utils

import android.content.Context
import jwer.pam.R

class ErrorText {

    companion object {

        fun tempoOutOfRange(app: Context?): String {
            return app?.resources?.getString(R.string.tempo_out_of_range_1) + " " + Tempo.MIN_BPM + " " + app?.resources?.getString(R.string.tempo_out_of_range_2) + " " + Tempo.MAX_BPM
        }

    }
}