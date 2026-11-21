package com.nachrichten.assessment.di

import android.content.Context
import androidx.room.Room
import com.nachrichten.assessment.data.local.database.NewsDatabase
import com.nachrichten.assessment.data.remote.api.NewsApiService
import com.nachrichten.assessment.data.repository.NewsRepositoryImpl
import com.nachrichten.assessment.domain.repository.NewsRepository
import com.nachrichten.assessment.domain.usecase.GetArticlesUseCase
import com.nachrichten.assessment.domain.usecase.GetCategoriesUseCase
import com.nachrichten.assessment.domain.usecase.GetSourcesUseCase
import com.nachrichten.assessment.domain.usecase.SearchArticlesUseCase
import com.nachrichten.assessment.domain.usecase.SearchSourcesUseCase
import com.nachrichten.assessment.util.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

interface AppContainer {
    val newsDatabase: NewsDatabase
    val newsApiService: NewsApiService
    val newsRepository: NewsRepository
    val getCategoriesUseCase: GetCategoriesUseCase
    val getSourcesUseCase: GetSourcesUseCase
    val searchSourcesUseCase: SearchSourcesUseCase
    val getArticlesUseCase: GetArticlesUseCase
    val searchArticlesUseCase: SearchArticlesUseCase
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    override val newsApiService: NewsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApiService::class.java)
    }

    override val newsDatabase: NewsDatabase by lazy {
        Room.databaseBuilder(
            context.applicationContext,
            NewsDatabase::class.java,
            Constants.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    override val newsRepository: NewsRepository by lazy {
        NewsRepositoryImpl(
            apiService = newsApiService,
            sourceDao = newsDatabase.sourceDao,
            articleDao = newsDatabase.articleDao
        )
    }

    override val getCategoriesUseCase: GetCategoriesUseCase by lazy {
        GetCategoriesUseCase(newsRepository)
    }

    override val getSourcesUseCase: GetSourcesUseCase by lazy {
        GetSourcesUseCase(newsRepository)
    }

    override val searchSourcesUseCase: SearchSourcesUseCase by lazy {
        SearchSourcesUseCase(newsRepository)
    }

    override val getArticlesUseCase: GetArticlesUseCase by lazy {
        GetArticlesUseCase(newsRepository)
    }

    override val searchArticlesUseCase: SearchArticlesUseCase by lazy {
        SearchArticlesUseCase(newsRepository)
    }
}
