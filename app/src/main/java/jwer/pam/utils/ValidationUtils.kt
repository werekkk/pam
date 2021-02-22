package jwer.pam.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

fun <T> validateLiveData(liveData: LiveData<T>, validatorFn: (T) -> Boolean): MediatorLiveData<Boolean> {
    return MediatorLiveData<Boolean>().apply {
        addSource(liveData) {
            postValue(validatorFn(it))
        }
    }
}

fun checkValidators(validators: Array<MediatorLiveData<Boolean>>): Boolean {
    return validators.fold(true) { prev, validator ->
        val value = validator.value ?: false
        if (!value) validator.postValue(value)
        value && prev
    }
}