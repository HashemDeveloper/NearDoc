package com.project.neardoc

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry

@Suppress("UNCHECKED_CAST")
class ApplicationProvider {
    companion object {
        fun <T : Context?> getApplicationContext(): Context? {
            return InstrumentationRegistry.getInstrumentation().targetContext
                .applicationContext as T
        }
    }
}