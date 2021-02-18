package jwernikowski.pam_lab.ui.dialog.meter_edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jwernikowski.pam_lab.db.data.Meter
import jwernikowski.pam_lab.dto.RhythmLineDto

class EditMeterViewModel : ViewModel() {

    val measure = MutableLiveData(4)
    val length = MutableLiveData(4)

    val meterChange = MediatorLiveData<Meter>()

    init {
        meterChange.addSource(measure) { meterChange.postValue(getMeter()) }
        meterChange.addSource(length) { meterChange.postValue(getMeter()) }
    }

    fun setMeter(meter: Meter) {
        measure.value = meter.measure
        length.value = meter.length
    }

    fun getMeter(): Meter {
        return Meter(measure.value!!, length.value!!)
    }

    fun updateMeter(measureChange: Int, lengthChange: Int) {
        measure.value = Meter.truncateMeasure(measure.value!! + measureChange)
        length.value = Meter.truncateLength(length.value!! + lengthChange)
    }

}