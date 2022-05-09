package com.bafoor.todolistmvvm.ui.deleteallcompleted

import androidx.lifecycle.ViewModel
import com.bafoor.todolistmvvm.data.ApplicationScope
import com.bafoor.todolistmvvm.data.ListDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteAllCompletedTaskViewModel @Inject constructor(
    private val taskDao: ListDao,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel() {

    fun onClickConfirmDeletion() =
        applicationScope.launch {
            taskDao.deleteAllCompletedTask()
        }
}