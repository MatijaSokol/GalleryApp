package hr.ferit.matijasokol.gallery.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import hr.ferit.matijasokol.gallery.network.UnsplashApi
import hr.ferit.matijasokol.gallery.other.Constants.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGson(): GsonConverterFactory = GsonConverterFactory.create()

    @Provides
    @Singleton
    fun provideRetrofit(gsonConverterFactory: GsonConverterFactory): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(gsonConverterFactory)
        .build()

    @Provides
    @Singleton
    fun provideUnsplashApi(retrofit: Retrofit): UnsplashApi = retrofit.create(UnsplashApi::class.java)
}