package hr.ferit.matijasokol.gallery.network

import hr.ferit.matijasokol.gallery.models.UnsplashResponse
import hr.ferit.matijasokol.gallery.other.Constants.CLIENT_ID
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface UnsplashApi {

    @Headers("Accept-Version: v1", "Authorization: Client-ID $CLIENT_ID")
    @GET("search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): UnsplashResponse
}