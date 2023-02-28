package com.lttrung.notepro.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.lttrung.notepro.BuildConfig
import com.lttrung.notepro.database.data.locals.room.CurrentUserDao
import com.lttrung.notepro.database.data.locals.room.UserDatabase
import com.lttrung.notepro.database.data.networks.impl.UserRetrofitServiceImpl
import com.lttrung.notepro.database.data.networks.interceptors.AuthorizationInterceptor
import com.lttrung.notepro.database.data.networks.interceptors.NetworksInterceptor
import com.lttrung.notepro.utils.AppConstant.Companion.DEFAULT_PREFERENCES_NAME
import com.lttrung.notepro.utils.AppConstant.Companion.USER_DATABASE_NAME
import com.lttrung.notepro.utils.RetrofitUtils.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppProvidesModules {
    @Provides
    @Singleton
    fun providesCurrentUserDatabase(@ApplicationContext context: Context): UserDatabase {
        return Room.databaseBuilder(context, UserDatabase::class.java, USER_DATABASE_NAME).build()
    }

    @Provides
    @Singleton
    fun providesCurrentUserDao(database: UserDatabase): CurrentUserDao {
        return database.currentUserDao()
    }

    @Provides
    @Singleton
    fun providesLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    @Singleton
    fun providesOkHttp(
        authorizationInterceptor: AuthorizationInterceptor,
        networksInterceptor: NetworksInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(networksInterceptor)
            .addInterceptor(authorizationInterceptor)
            .addInterceptor(loggingInterceptor)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun providesGson(): Gson {
        return GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm").create()
    }

    @Provides
    @Singleton
    fun providesGsonConverterFactory(gson: Gson): GsonConverterFactory {
        return GsonConverterFactory.create(gson)
    }

    @Provides
    fun providesRxJava3CallAdapterFactory(): RxJava3CallAdapterFactory {
        return RxJava3CallAdapterFactory.create()
    }

    @Provides
    @Singleton
    fun providesRetrofit(
        gsonConverterFactory: GsonConverterFactory,
        rxJava3CallAdapterFactory: RxJava3CallAdapterFactory,
        okHttp: OkHttpClient
    ): Retrofit = Retrofit.Builder().baseUrl(BASE_URL)
        .addConverterFactory(gsonConverterFactory)
        .addCallAdapterFactory(rxJava3CallAdapterFactory)
        .client(okHttp).build()

    @Provides
    @Singleton
    fun providesSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(DEFAULT_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun providesUserService(retrofit: Retrofit): UserRetrofitServiceImpl.Service =
        retrofit.create(UserRetrofitServiceImpl.Service::class.java)
}