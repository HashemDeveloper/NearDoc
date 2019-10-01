package com.project.neardoc.di.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.RuntimeException
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Suppress("UNCHECKED_CAST")
@Singleton
class ViewModelFactory @Inject constructor(private val creator: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
       var creator: Provider<out ViewModel>? = this.creator[modelClass]
        if (creator == null) {
            for ((key, value) in this.creator) {
                if (modelClass.isAssignableFrom(key)) {
                    creator = value
                    break
                }
            }
        }
        requireNotNull(creator) {
            "Unknown model class $modelClass"
        }
        try {
            return creator.get() as T
        } catch (e: Exception) {
            throw RuntimeException()
        }
    }

}