package com.akhilasdeveloper.bored.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.akhilasdeveloper.bored.data.CardDao
import com.akhilasdeveloper.bored.data.mapper.BoredTableMapper
import com.akhilasdeveloper.bored.repositories.BoredApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivitiesViewModel
@Inject constructor(
    private val boredApiRepository: BoredApiRepository
) : ViewModel() {

    val activitiesPass: Flow<PagingData<CardDao>> = Pager(PagingConfig(pageSize = 6)) {
        boredApiRepository.fetchPassActivities()
    }.flow.cachedIn(viewModelScope).map {
        it.map { table ->
            BoredTableMapper().fromSourceToDestination(table)
        }
    }

    val activitiesAdded: Flow<PagingData<CardDao>> = Pager(PagingConfig(pageSize = 6)) {
        boredApiRepository.fetchAddActivities()
    }.flow.cachedIn(viewModelScope).map {
        it.map { table ->
            BoredTableMapper().fromSourceToDestination(table)
        }
    }

    fun updateIsCompleted(id:Int?, key:String, isCompleted:Boolean){
        viewModelScope.launch {
            id?.let {
                boredApiRepository.updateIsCompleted(id, key, isCompleted)
            }
        }
    }

    fun updatePosition(from: CardDao?, to: CardDao?){
        from?.let {
            to?.let {
                val createdDate = from.createdDate
                viewModelScope.launch {
                    boredApiRepository.updateCreatedDate(id = from.id!!,to.createdDate)
                    boredApiRepository.updateCreatedDate(id = to.id!!,createdDate)
                }
            }
        }
    }
}