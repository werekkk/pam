package jwer.pam.utils

import androidx.lifecycle.*

class MutableLiveDataWithDelayedInitialValue<T>(initialValue: T, delayedInitialValue: LiveData<T>) : MutableLiveData<T>(initialValue) {

        private val mediator = MediatorLiveData<T>()

        init {
            mediator.addSource(delayedInitialValue) {
                mediator.removeSource(delayedInitialValue)
                value = it
            }
        }

        override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
            mediator.observe(owner, observer)
            super.observe(owner, observer)
        }

        override fun observeForever(observer: Observer<in T>) {
            mediator.observeForever(observer)
            super.observeForever(observer)
        }

        override fun removeObserver(observer: Observer<in T>) {
            mediator.removeObserver(observer)
            super.removeObserver(observer)
        }

        override fun removeObservers(owner: LifecycleOwner) {
            mediator.removeObservers(owner)
            super.removeObservers(owner)
        }
}
