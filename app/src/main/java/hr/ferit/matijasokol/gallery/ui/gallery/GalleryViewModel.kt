package hr.ferit.matijasokol.gallery.ui.gallery

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.cachedIn
import hr.ferit.matijasokol.gallery.repositories.UnsplashRepository

class GalleryViewModel @ViewModelInject constructor(
    private val repository: UnsplashRepository,
    @Assisted state: SavedStateHandle
) : ViewModel() {

    val currentQuery = state.getLiveData(CURRENT_QUERY, DEFAULT_QUERY)

    val photos = currentQuery.switchMap { queryString ->
        repository.getSearchResults(queryString).cachedIn(viewModelScope)
    }

    fun searchPhotos(query: String) {
        currentQuery.value = query
    }

    companion object {
        const val DEFAULT_QUERY = "golden retriever"
        const val CURRENT_QUERY = "current_query"
    }
}