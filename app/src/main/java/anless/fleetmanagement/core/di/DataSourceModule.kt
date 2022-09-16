package anless.fleetmanagement.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import anless.fleetmanagement.car_module.data.api.*
import anless.fleetmanagement.car_module.data.data_source.action.ActionDataSource
import anless.fleetmanagement.car_module.data.data_source.action.ActionDataSourceImpl
import anless.fleetmanagement.car_module.data.data_source.car.CarDataSource
import anless.fleetmanagement.car_module.data.data_source.car.CarDataSourceImpl
import anless.fleetmanagement.car_module.data.data_source.car_options.CarOptionsDataSource
import anless.fleetmanagement.car_module.data.data_source.car_options.CarOptionsDataSourceImpl
import anless.fleetmanagement.car_module.data.data_source.car_params.CarParamsDataSource
import anless.fleetmanagement.car_module.data.data_source.car_params.CarParamsDataSourceImpl
import anless.fleetmanagement.car_module.data.data_source.documents.DocumentsDataSource
import anless.fleetmanagement.car_module.data.data_source.documents.DocumentsDataSourceImpl
import anless.fleetmanagement.car_module.presentation.utils.CameraUtility
import anless.fleetmanagement.car_module.presentation.utils.FileUtility
import anless.fleetmanagement.core.utils.Constants.USER_PREFERENCES
import anless.fleetmanagement.core.utils.SettingsManagerPreferences
import anless.fleetmanagement.core.utils.authorization.AuthorizationManagerImpl
import anless.fleetmanagement.core.utils.authorization.HashManagerPreferences
import anless.fleetmanagement.station_module.data.api.StationApi
import anless.fleetmanagement.station_module.data.data_source.StationDataSource
import anless.fleetmanagement.station_module.data.data_source.StationDataSourceImpl
import anless.fleetmanagement.user_module.data.api.SessionApi
import anless.fleetmanagement.user_module.data.api.ShiftApi
import anless.fleetmanagement.user_module.data.api.UserApi
import anless.fleetmanagement.user_module.data.data_source.session.SessionDataSource
import anless.fleetmanagement.user_module.data.data_source.session.SessionDataSourceImpl
import anless.fleetmanagement.user_module.data.data_source.shift.ShiftDataSource
import anless.fleetmanagement.user_module.data.data_source.shift.ShiftDataSourceImpl
import anless.fleetmanagement.user_module.data.data_source.user.UserDataSource
import anless.fleetmanagement.user_module.data.data_source.user.UserDataSourceImpl
import anless.fleetmanagement.user_module.domain.common.AuthorizationManager
import anless.fleetmanagement.user_module.domain.repository.SessionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Provides
    @Singleton
    fun provideAuthorizationManager(
        sessionRepository: SessionRepository,
        hashManagerPreferences: HashManagerPreferences
    ): AuthorizationManager {
        return AuthorizationManagerImpl(sessionRepository, hashManagerPreferences)
    }

    @Provides
    @Singleton
    fun provideHashManagerPreferences(dataStore: DataStore<Preferences>): HashManagerPreferences {
        return HashManagerPreferences(dataStore)
    }

    @Provides
    @Singleton
    fun provideSettingsManagerPreferences(dataStore: DataStore<Preferences>): SettingsManagerPreferences {
        return SettingsManagerPreferences(dataStore)
    }

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { appContext.preferencesDataStoreFile(USER_PREFERENCES) }
        )
    }

    @Provides
    @Singleton
    fun provideFileUtility(@ApplicationContext appContext: Context): FileUtility {
        return FileUtility(context = appContext)
    }

    @Provides
    @Singleton
    fun provideUserDataSource(userApi: UserApi): UserDataSource {
        return UserDataSourceImpl(userApi)
    }

    @Provides
    @Singleton
    fun provideShiftDataSource(shiftApi: ShiftApi): ShiftDataSource {
        return ShiftDataSourceImpl(shiftApi)
    }

    @Provides
    @Singleton
    fun provideSessionDataSource(sessionApi: SessionApi): SessionDataSource {
        return SessionDataSourceImpl(sessionApi)
    }

    @Provides
    @Singleton
    fun provideStationDataSource(stationApi: StationApi): StationDataSource {
        return StationDataSourceImpl(stationApi)
    }

    @Provides
    @Singleton
    fun provideCarDataSource(carApi: CarApi): CarDataSource {
        return CarDataSourceImpl(carApi)
    }

    @Provides
    @Singleton
    fun provideActionDataSource(actionApi: ActionApi): ActionDataSource {
        return  ActionDataSourceImpl(actionApi)
    }

    @Provides
    @Singleton
    fun provideCarOptionsDataSource(carOptionsApi: CarOptionsApi): CarOptionsDataSource {
        return CarOptionsDataSourceImpl(carOptionsApi)
    }

    @Provides
    @Singleton
    fun provideDocumentsDataSource(documentsApi: DocumentsApi): DocumentsDataSource {
        return DocumentsDataSourceImpl(documentsApi)
    }

    @Provides
    @Singleton
    fun provideCarParamsDataSource(carParamsApi: CarParamsApi): CarParamsDataSource {
        return CarParamsDataSourceImpl(carParamsApi)
    }

    @Provides
    @Singleton
    fun provideCameraUtility(@ApplicationContext appContext: Context): CameraUtility {
        return CameraUtility(appContext)
    }
}