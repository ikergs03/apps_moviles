package com.example.mylibrary.ui.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.mylibrary.data.model.ItemStatus
import com.example.mylibrary.data.model.ItemType
import com.example.mylibrary.data.repository.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    repository: LibraryRepository
) : ViewModel() {

    val stats: LiveData<StatisticsData> = repository.getAllItems().map { items ->
        StatisticsData(
            total = items.size,
            books = items.count { it.type == ItemType.BOOK },
            movies = items.count { it.type == ItemType.MOVIE },
            videogames = items.count { it.type == ItemType.VIDEOGAME },
            pending = items.count { it.status == ItemStatus.PENDING },
            inProgress = items.count { it.status == ItemStatus.IN_PROGRESS },
            completed = items.count { it.status == ItemStatus.COMPLETED },
            abandoned = items.count { it.status == ItemStatus.ABANDONED }
        )
    }
}

data class StatisticsData(
    val total: Int,
    val books: Int,
    val movies: Int,
    val videogames: Int,
    val pending: Int,
    val inProgress: Int,
    val completed: Int,
    val abandoned: Int
)
