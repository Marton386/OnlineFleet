package anless.fleetmanagement.core.di

import anless.fleetmanagement.car_module.data.api.*
import anless.fleetmanagement.core.utils.Constants
import anless.fleetmanagement.station_module.data.api.StationApi
import anless.fleetmanagement.user_module.data.api.SessionApi
import anless.fleetmanagement.user_module.data.api.ShiftApi
import anless.fleetmanagement.user_module.data.api.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
    private const val BASE_URL = "https://onlinefleet.ru/"
    private const val RM_URL = "https://www.rentmotors.ru/"

    @Provides
    @Singleton
    @RetrofitBaseUrl
    fun provideBaseRetrofit(httpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @RetrofitCarParamsUrl
    fun provideRmRetrofit(httpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(RM_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        logging: HttpLoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(Constants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Constants.READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(Constants.WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    }

    @Provides
    @Singleton
    fun provideGetUserApi(@RetrofitBaseUrl retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSessionApi(@RetrofitBaseUrl retrofit: Retrofit): SessionApi {
        return retrofit.create(SessionApi::class.java)
    }

    @Provides
    @Singleton
    fun provideShiftApi(@RetrofitBaseUrl retrofit: Retrofit): ShiftApi {
        return retrofit.create(ShiftApi::class.java)
    }

    @Provides
    @Singleton
    fun provideStationApi(@RetrofitBaseUrl retrofit: Retrofit): StationApi {
        return retrofit.create(StationApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCarApi(@RetrofitBaseUrl retrofit: Retrofit): CarApi {
        return retrofit.create(CarApi::class.java)
    }

    @Provides
    @Singleton
    fun provideActionApi(@RetrofitBaseUrl retrofit: Retrofit): ActionApi {
        return retrofit.create(ActionApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCarOptionsApi(@RetrofitBaseUrl retrofit: Retrofit): CarOptionsApi {
        return retrofit.create(CarOptionsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDocumentsApi(@RetrofitBaseUrl retrofit: Retrofit): DocumentsApi {
        return retrofit.create(DocumentsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRmApi(@RetrofitCarParamsUrl retrofit: Retrofit): CarParamsApi {
        return retrofit.create(CarParamsApi::class.java)
    }
}