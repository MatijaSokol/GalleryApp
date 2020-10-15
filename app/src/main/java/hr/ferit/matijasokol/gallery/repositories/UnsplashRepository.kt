package hr.ferit.matijasokol.gallery.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import hr.ferit.matijasokol.gallery.network.UnsplashApi
import hr.ferit.matijasokol.gallery.ui.data.UnsplashPagingSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnsplashRepository @Inject constructor(
    private val unsplashApi: UnsplashApi
) {

    fun getSearchResults(query: String) = Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { UnsplashPagingSource(unsplashApi, query) }
    ).liveData
}