package com.app.axisbankatm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.axisbankatm.room.dao.AxisDao
import java.lang.IllegalArgumentException

class ViewModelFactory(private val axisDao: AxisDao): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainViewModel::class.java)){
            return MainViewModel(axisDao) as T
        }
        throw IllegalArgumentException("Unknown Class Name")
    }
}