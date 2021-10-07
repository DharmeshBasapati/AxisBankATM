package com.app.focusonatm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.focusonatm.room.dao.FocusDao
import java.lang.IllegalArgumentException

class ViewModelFactory(private val focusDao: FocusDao): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainViewModel::class.java)){
            return MainViewModel(focusDao) as T
        }
        throw IllegalArgumentException("Unknown Class Name")
    }
}